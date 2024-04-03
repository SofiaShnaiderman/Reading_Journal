package com.example.readingjournal.service;

import com.example.readingjournal.entity.Book;
import com.example.readingjournal.exception.CannotFindBookException;
import com.example.readingjournal.exception.CannotParseBookFromUrlException;
import com.example.readingjournal.repository.BookRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ParseService parseService;

    public List<Book> getByListId(long listId) {
        return bookRepository.findAllByListId(listId);
    }

    public Book getById(long id) throws CannotFindBookException {
        return bookRepository.findById(id)
                .orElseThrow(() -> new CannotFindBookException("Cannot find book with id " + id));
    }

    public Book addNewBook(String url) throws CannotParseBookFromUrlException {
        Book newBook = parseService.parse(url);
        newBook.setListId(1);
        return bookRepository.save(newBook);
    }

    public boolean updateList(long bookId, int newListId) throws CannotFindBookException {
        bookRepository.findById(bookId).orElseThrow(() -> new CannotFindBookException("Cannot find book with id " + bookId));
        bookRepository.updateListIdById(bookId, newListId);
        return bookRepository.findById(bookId).get().getListId() == newListId;
    }

    public void deleteBook(long id) {
        bookRepository.deleteById(id);
    }

    public byte[] makeExcel(long listId) {
        List<Book> booksForList = getByListId(listId);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Books");
        int rowNum = 0;

        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Name", "Author", "Description", "Image URL", "Pages"};
        int colNum = 0;
        for (String header : headers) {
            Cell cell = headerRow.createCell(colNum++);
            cell.setCellValue(header);
        }

        for (Book book : booksForList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(book.getName());
            row.createCell(1).setCellValue(book.getAuthor());
            row.createCell(2).setCellValue(book.getDescription());
            row.createCell(3).setCellValue(book.getImageUrl());
            row.createCell(4).setCellValue(book.getPages());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
