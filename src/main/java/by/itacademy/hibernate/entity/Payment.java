package by.itacademy.hibernate.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Data
@Builder
@Entity
public class Payment implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer amount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    public Payment(Long id, Integer amount, User receiver) {
        this.id = id;
        this.amount = amount;
        this.receiver = receiver;
    }

    public Payment() {
    }
}