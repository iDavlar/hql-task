package by.itacademy.hibernate.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(schema = "public")
public class Profile {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String street;

    private String language;

    public Profile(Long id, User user, String street, String language) {
        this.id = id;
        this.user = user;
        this.street = street;
        this.language = language;
    }

    public Profile() {
    }

    public void setUser(User user) {
        user.setProfile(this);
        this.user = user;
    }
}