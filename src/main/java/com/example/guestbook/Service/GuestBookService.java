package com.example.guestbook.Service;

import com.example.guestbook.Entity.GuestBookEntry;

import java.util.List;

public interface GuestBookService {

    List<GuestBookEntry> getGuestBookEntries();
    GuestBookEntry getGuestBookEntryById(long id);
    GuestBookEntry saveGuestBookEntry(GuestBookEntry guestBookEntry);
    void deleteGuestBookEntryById(long id);


}
