package com.example.guestbook.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@ToString
public class GuestBookEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String authorName;
    @Email(message = "Некорректный email")
    private String email;
    @NotBlank(message = "Сообщение не может быть пустым")
    @Size(min = 10, max = 1000, message = "Сообщение должно быть от 10 до 1000 символов")
    private String message;
    private LocalDateTime createdAt;


    public String getAvatarUrl() {
        if (email != null && !email.isEmpty()) {
            String hash = DigestUtils.md5DigestAsHex(email.toLowerCase().getBytes());
            return "https://www.gravatar.com/avatar/" + hash + "?d=identicon";
        } else {
            return "https://api.dicebear.com/7.x/identicon/svg?seed=" + authorName;
        }
    }

}
