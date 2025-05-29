package com.example.guestbook.Service;

import com.example.guestbook.Entity.GuestBookEntry;
import com.example.guestbook.Repository.GuestBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestBookServiceImpl implements GuestBookService {

    private final GuestBookRepository guestBookRepository;

    @Override
    public List<GuestBookEntry> getGuestBookEntries() {
        return guestBookRepository.findAll().reversed();
    }

    @Override
    public GuestBookEntry getGuestBookEntryById(long id) {
        return guestBookRepository.findById(id).orElse(null);
    }

    @Override
    public GuestBookEntry saveGuestBookEntry(GuestBookEntry guestBookEntry) {
        return guestBookRepository.save(guestBookEntry);
    }

    @Override
    public void deleteGuestBookEntryById(long id) {
        guestBookRepository.deleteById(id);
    }
}
