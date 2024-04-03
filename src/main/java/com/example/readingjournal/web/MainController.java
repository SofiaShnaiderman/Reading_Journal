package com.example.readingjournal.web;

import com.example.readingjournal.exception.CannotFindBookException;
import com.example.readingjournal.exception.CannotParseBookFromUrlException;
import com.example.readingjournal.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private BookService bookService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("list1", bookService.getByListId(1));
        model.addAttribute("list2", bookService.getByListId(2));
        model.addAttribute("list3", bookService.getByListId(3));
        return "index";
    }

    @PostMapping("/add")
    public String addNew(@RequestParam("url") String url, Model model)  {
        try {
            bookService.addNewBook(url);
            return "redirect:/";
        } catch (CannotParseBookFromUrlException e) {
            model.addAttribute("error_msg", e.getMessage());
            logger.warn("Cannot process url", e);
            return index(model);
        }
    }

    @GetMapping("/books")
    public String index(@RequestParam long id, Model model) {
        try {
            model.addAttribute("book", bookService.getById(id));
            return "bookinfo";
        } catch (CannotFindBookException e) {
            model.addAttribute("error_msg", e.getMessage());
            logger.warn("Cannot get book with id {}", id, e);
            return index(model);
        }
    }

    @PostMapping("/start")
    public String startReading(@RequestParam long id, Model model)  {
        try {
            bookService.updateList(id, 2);
            return "redirect:/";
        } catch (CannotFindBookException e) {
            model.addAttribute("error_msg", e.getMessage());
            logger.warn("Cannot start reading for book with id {}", id, e);
            return index(model);
        }
    }

    @PostMapping("/finish")
    public String finishReading(@RequestParam long id, Model model) {
        try {
            bookService.updateList(id, 3);
            return "redirect:/";
        } catch (CannotFindBookException e) {
            model.addAttribute("error_msg", e.getMessage());
            logger.warn("Cannot finish reading for book with id {}", id, e);
            return index(model);
        }
    }

    @PostMapping("/delete")
    public String deleteBook(@RequestParam long id) {
        bookService.deleteBook(id);
        return "redirect:/";
    }
}
