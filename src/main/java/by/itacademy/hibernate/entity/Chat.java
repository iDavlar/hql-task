package by.itacademy.hibernate.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(of = "name")
@ToString(exclude = "userChats")
@Builder
@Entity
@Table(schema = "public")
public class Chat implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "chat")
    private List<UserChat> userChats = new ArrayList<>();

    public Chat(Long id, String name, List<UserChat> userChats) {
        this.id = id;
        this.name = name;
        this.userChats = userChats;
    }

    public Chat() {
    }
}