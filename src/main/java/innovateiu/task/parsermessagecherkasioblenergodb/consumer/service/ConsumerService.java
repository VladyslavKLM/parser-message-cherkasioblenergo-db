package innovateiu.task.parsermessagecherkasioblenergodb.consumer.service;

import innovateiu.task.parsermessagecherkasioblenergodb.consumer.domain.Consumer;
import innovateiu.task.parsermessagecherkasioblenergodb.consumer.repository.ConsumerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumerService {
    private final ConsumerRepository consumerRepository;

    @Autowired
    public ConsumerService(ConsumerRepository consumerRepository) {
        this.consumerRepository = consumerRepository;

    }

    public void crateUser(Consumer consumer) {
        consumer.setRole("USER");
        consumerRepository.save(consumer);
    }

    public Consumer getUserByChatId(Long id) {
        return consumerRepository.findByChatId(id);
    }

    public void updateUserTurn(Long chatId, Long newNumberTurn) {
        Consumer consumer = consumerRepository.findByChatId(chatId);
        if (consumer != null) {
            consumer.setNumberTurn(newNumberTurn);
            consumerRepository.save(consumer);
        } else {
            throw new EntityNotFoundException("Користувача з chatId " + chatId + " не знайдено.");
        }
    }

    public boolean checkUserIsExist(Long chatId) {
        return consumerRepository.existsByChatId(chatId);
    }

    public List<Consumer> getAllUsers() {
        return consumerRepository.findAll();
    }
}
