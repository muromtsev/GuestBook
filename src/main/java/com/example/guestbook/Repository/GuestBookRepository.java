package com.example.guestbook.Repository;

import com.example.guestbook.Entity.GuestBookEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestBookRepository extends JpaRepository<GuestBookEntry, Long> {

}
