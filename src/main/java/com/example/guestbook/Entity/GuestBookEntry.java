package com.example.guestbook.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private String authorName;
    private String email;
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
