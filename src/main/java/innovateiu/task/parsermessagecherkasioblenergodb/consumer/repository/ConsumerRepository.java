package innovateiu.task.parsermessagecherkasioblenergodb.consumer.repository;

import innovateiu.task.parsermessagecherkasioblenergodb.consumer.domain.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    Consumer findByChatId(Long chatId);

    boolean existsByChatId(Long chatId);
}
