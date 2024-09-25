package innovateiu.task.parsermessagecherkasioblenergodb.schedule.repository;

import innovateiu.task.parsermessagecherkasioblenergodb.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Schedule s WHERE s.date NOT IN (:today, :tomorrow)")
    void deleteOldSchedules(@Param("today") LocalDate today, @Param("tomorrow") LocalDate tomorrow);

    List<Schedule> findByNumberTurn(Long numberTurn);

    @Query("SELECT DISTINCT s.numberTurn FROM Schedule s")
    List<Long> findAllDistinctTurns();

    @Modifying
    @Transactional
    @Query("DELETE FROM Schedule s WHERE s.date = :date")
    void deleteByDate(@Param("date") LocalDate date);
}

