package by.itacademy.hibernate.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserInfo implements Serializable {
    private String name;
    private String email;

    public UserInfo(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public UserInfo() {
    }
}
