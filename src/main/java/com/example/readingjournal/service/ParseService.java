package com.example.readingjournal.service;

import com.example.readingjournal.entity.Book;
import com.example.readingjournal.exception.CannotParseBookFromUrlException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ParseService {

    public Book parse(String url) throws CannotParseBookFromUrlException {
        try {
            Document doc = Jsoup.connect(url).get();

            // extracting book details
            String name = doc.selectFirst("h1.Text__title1").text();
            String author = doc.selectFirst("span.ContributorLink__name").text();
            String description = doc.selectFirst("span.Formatted").text();
            int pages = Integer.parseInt(doc.selectFirst("p[data-testid=pagesFormat]").text().replaceAll("\\D", ""));
            String imageUrl = doc.selectFirst("img[role=presentation]").attr("src");

            Book book = new Book();
            book.setName(name);
            book.setAuthor(author);
            book.setDescription(description);
            book.setImageUrl(imageUrl);
            book.setPages(pages);

            return book;
        } catch (IOException e) {
            throw new CannotParseBookFromUrlException("Cannot parse url: " + url);
        }
    }
}
