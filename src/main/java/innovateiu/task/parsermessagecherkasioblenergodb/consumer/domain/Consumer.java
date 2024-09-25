package innovateiu.task.parsermessagecherkasioblenergodb.consumer.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "consumer")
public class Consumer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consumer_id_gen")
    @SequenceGenerator(name = "consumer_id_gen", sequenceName = "consumer_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Size(max = 100)
    @NotNull
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "number_turn")
    private Long numberTurn;

    @Size(max = 100)
    @NotNull
    @ColumnDefault("'USER'")
    @Column(name = "role", nullable = false, length = 100)
    private String role;

}