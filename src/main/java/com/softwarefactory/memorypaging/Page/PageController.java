package com.softwarefactory.memorypaging.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RestController
@RequestMapping("/page")
public class PageController {

    public static final ArrayList<Page> pages = new ArrayList<>();

    @PostMapping("/createAll")
    public ResponseEntity<Map<String, String>> createPages(@RequestBody int numberOfPages) {

        pages.clear();

        for (int i = 0; i < numberOfPages; i++) {
            createNewPage();
        }

        return ResponseEntity.status(200).body(createResponse(pages.size() + " pages created successfully"));
    }

    @GetMapping("/deleteAll")
    public ResponseEntity<Map<String, String>> deleteAll() {
        pages.clear();
        return ResponseEntity.status(200).body(createResponse("All pages deleted successfully"));
    }

    public Page isExists(int id) {
        for (Page page : pages) {
            if (page.getId() == id) {
                return page;
            }
        }
        return null;
    }

    private Page createNewPage() {
        Page page = new Page();
        page.setId(pages.size() + 1);
        page.setAge(-1);
        page.setTimeLastUsed(-1);
        pages.add(page);
        return page;
    }

    private Map<String, String> createResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }

    public void addFutureAcess(int pageId, int futureAcess) {
        Page page = isExists(pageId);
        if (page != null) {
            page.getFutureAcess().add(futureAcess);
        }
    }
}
