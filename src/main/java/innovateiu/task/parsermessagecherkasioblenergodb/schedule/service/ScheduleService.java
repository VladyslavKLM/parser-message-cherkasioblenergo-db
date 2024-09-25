package innovateiu.task.parsermessagecherkasioblenergodb.schedule.service;

import innovateiu.task.parsermessagecherkasioblenergodb.schedule.domain.Schedule;
import innovateiu.task.parsermessagecherkasioblenergodb.schedule.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public void parserMessage(String message) {

        LocalDate date = getDateWithMessage(message);
        scheduleRepository.deleteByDate(date);
        cleanUpSchedules();
        String[] lines = message.split("\n");
        for (String line : lines) {
            if (line.matches("\\d{2}:\\d{2}-\\d{2}:\\d{2}.*")) {
                String[] parts = line.split(" ");
                String hours = parts[0];

                for (int i = 1; i < parts.length; i++) {
                    String turn = parts[i];
                    String numberTurn = turn.replaceAll("[^0-9]", "");
                    if (!numberTurn.isEmpty()) {
                        try {
                            long number = Integer.parseInt(numberTurn);
                            createScheduleInDB(date, hours, number);

                        } catch (NumberFormatException e) {
                            System.out.println("extractShutdownHours");
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    private void cleanUpSchedules() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        scheduleRepository.deleteOldSchedules(today, tomorrow);
    }

    private void createScheduleInDB(LocalDate date, String hour, long number) {
        Schedule schedule = new Schedule();
        schedule.setDate(date);
        schedule.setHour(hour);
        schedule.setNumberTurn(number);
        scheduleRepository.save(schedule);
    }

    private LocalDate getDateWithMessage(String message) {
        String[] months = {"січня", "лютого", "березня", "квітня", "травня", "червня",
                "липня", "серпня", "вересня", "жовтня", "листопада", "грудня"};
        for (String month : months) {
            if (message.contains(month)) {
                String[] parts = message.split(month);
                String dayStr = parts[0].replaceAll(".*?(\\d{1,2}).*", "$1");
                int day = Integer.parseInt(dayStr);
                int monthIndex = Arrays.asList(months).indexOf(month) + 1;
                try {
                    return LocalDate.of(LocalDate.now().getYear(), monthIndex, day);
                } catch (DateTimeParseException e) {
                    System.out.println("extractDate");
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    public String getScheduleForTurn(Long numberTurn) {
        List<Schedule> schedules = scheduleRepository.findByNumberTurn(numberTurn);

        if (schedules.isEmpty()) {
            return "Немає графіку для зазначеного номеру черги.";
        }

        Map<LocalDate, List<Schedule>> groupedByDate = schedules.stream()
                .collect(Collectors.groupingBy(Schedule::getDate));

        StringBuilder scheduleStringBuilder = new StringBuilder();

        for (Map.Entry<LocalDate, List<Schedule>> entry : groupedByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<Schedule> dailySchedules = entry.getValue();

            scheduleStringBuilder.append("Графік на ")
                    .append(date.format(DateTimeFormatter.ofPattern("dd.MM")))
                    .append(":\n");

            for (Schedule schedule : dailySchedules) {
                scheduleStringBuilder.append(schedule.getHour()).append("\n");
            }
        }

        return scheduleStringBuilder.toString();
    }


    public List<Long> getAllTurns() {
        List<Long> turns = scheduleRepository.findAllDistinctTurns();
        Collections.sort(turns);
        return turns;
    }
}
