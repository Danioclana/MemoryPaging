package com.softwarefactory.memorypaging.Page;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RestController
@RequestMapping("/page")
public class PageController {

    public static ArrayList<Page> pages = new ArrayList<Page>();

    @PostMapping("/create")
    public ResponseEntity<?> createPage() {

        Page page = new Page();
        page.setId(pages.size() + 1);
        page.setAge(-1);
        page.setTimeLastUsed(-1);
        page.setFutureAcess(0);
        pages.add(page);

        return ResponseEntity.status(200).body("Page " + pages.size() + " created successfully");
    }

    @PostMapping("/createAll")
    public ResponseEntity<?> createPagesRandom(@RequestBody int numberOfPages) {
        
        pages.clear();

        for (int i = 0; i < numberOfPages; i++) {

            Page page = new Page();
            page.setId(pages.size() + 1);
            page.setAge(-1);
            page.setTimeLastUsed(-1);
            page.setFutureAcess(0);
            pages.add(page);
        }

        return ResponseEntity.status(200).body(pages.size() + " pages created successfully");
    }

    @GetMapping("/delete")
    public ResponseEntity<?> deletePage() {
        
        pages.remove(pages.size() - 1);

        return ResponseEntity.status(200).body("Page" + " deleted successfully");
    }

    @GetMapping("/deleteAll")
    public ResponseEntity<?> deleteAll() {

        pages.clear();

        return ResponseEntity.status(200).body("Page deleted successfully");
    }

    public Page isExists(int id) {
        for (Page page : pages) {
            if (page.getId() == id) {
                return page;
            }
        }
        return null;
    }
}
