package com.example.readingjournal.repository;

import com.example.readingjournal.entity.Book;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findAll();
    List<Book> findAllByListId(long listId);

    @Transactional
    @Modifying
    @Query("UPDATE Book b SET b.listId = :listId WHERE b.id = :bookId")
    void updateListIdById(Long bookId, int listId);
}
