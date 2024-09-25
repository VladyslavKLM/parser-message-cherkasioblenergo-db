package innovateiu.task.parsermessagecherkasioblenergodb;

import innovateiu.task.parsermessagecherkasioblenergodb.config.BotConfig;
import innovateiu.task.parsermessagecherkasioblenergodb.consumer.domain.Consumer;
import innovateiu.task.parsermessagecherkasioblenergodb.consumer.service.ConsumerService;
import innovateiu.task.parsermessagecherkasioblenergodb.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.*;

@Component
public class ParserMessageBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final ScheduleService scheduleService;
    private final ConsumerService consumerService;


    @Autowired
    public ParserMessageBot(BotConfig botConfig, ScheduleService scheduleService, ConsumerService consumerService) {
        this.botConfig = botConfig;
        this.scheduleService = scheduleService;
        this.consumerService = consumerService;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            User user = message.getFrom();
            if (message.getText().equals("/start")) {
                if (!consumerService.checkUserIsExist(user.getId())) {
                    consumerService.crateUser(getDataUser(user));
                }
                sendQueueButtons(user.getId());
            } else {
                try {
                    sendMessage(message.getChatId(), scheduleService.getScheduleForTurn(Long.valueOf(message.getText())));
                } catch (NumberFormatException e) {
                    if (Objects.equals(checkRole(message.getFrom().getId()), "USER")) {
                        sendMessage(message.getChatId(), "Введіть правильний номер черги.");
                    }
                }
            }
            if (Objects.equals(checkRole(message.getFrom().getId()), "ADMIN")) {
                scheduleService.parserMessage(message.getText());
                notifyUsers();
            }

        } else if (update.hasCallbackQuery()) {
            long dataWithButton = Long.parseLong(update.getCallbackQuery().getData());
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            consumerService.updateUserTurn(chatId, dataWithButton);
            sendMessageWithGraphs(chatId, dataWithButton);
        }
    }

    private void notifyUsers() {
        List<Consumer> consumers = consumerService.getAllUsers();
        for (Consumer consumer : consumers) {
            sendMessage(consumer.getChatId(), "ОНОВЛЕННЯ ГРАФІКІВ");
            sendMessage(consumer.getChatId(), scheduleService.getScheduleForTurn(consumer.getNumberTurn()));
        }
    }

    private void sendMessageWithGraphs(long chatId, long data) {
        String schedule = scheduleService.getScheduleForTurn(data);
        sendMessage(chatId, "Графіки відключення світла " + data + " черги");
        sendMessage(chatId, schedule);
    }

    private String checkRole(Long chatId) {
        return consumerService.getUserByChatId(chatId).getRole();
    }

    private Consumer getDataUser(User user) {
        Consumer consumer = new Consumer();
        consumer.setChatId(user.getId());
        consumer.setUsername(user.getUserName());
        return consumer;

    }

    private void sendQueueButtons(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<Long> queues = scheduleService.getAllTurns();

        for (Long queue : queues) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("Черга " + queue);
            button.setCallbackData(queue.toString());
            rows.add(List.of(button));
        }

        inlineKeyboardMarkup.setKeyboard(rows);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Виберіть чергу для перегляду графіка відключень:");
        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException ignored) {
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException ignored) {
        }
    }
}
