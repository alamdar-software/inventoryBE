package com.inventory.project.controller;

import com.inventory.project.model.Item;
import com.inventory.project.model.Location;
import com.inventory.project.repository.ItemRepository;
import com.inventory.project.repository.LocationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("/item")
@RestController
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private LocationRepository locationRepository;

    @GetMapping("/add")
    public ResponseEntity<String> add(Model model, HttpSession session) {
        if (!model.asMap().containsKey("item")) {
            model.addAttribute("item", new Item());
        }

        List<Location> locations = locationRepository.findAll();

        return ResponseEntity.ok("Add item operation.");
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Item item) {
        try {
            Item savedItem = itemRepository.save(item);
            if (savedItem != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Item saved successfully");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to save item");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving item");
        }
    }


    @GetMapping("/view")
    public ResponseEntity<List<Item>> viewItems() {
        List<Item> items = itemRepository.findAll();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/view/pageno={page}")
    public ResponseEntity<List<Item>> viewItemsPaginated(@PathVariable("page") int page, HttpSession session) {
        // Use pagination logic from the original code
        return ResponseEntity.ok(null);
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<Item> editItem(@PathVariable("id") Long id) {
        Item item = itemRepository.getReferenceById(id);
        if (item != null) {
            return ResponseEntity.ok(item);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestBody Item item) {
        try {
            Item updatedItem = itemRepository.findById(item);
            if (updatedItem != null) {
                return ResponseEntity.status(HttpStatus.OK).body("Item updated successfully");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating item");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable(value = "id") Long id) {
        try {
            itemRepository.deleteById(id);
            return ResponseEntity.ok("Item deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting item");
        }
    }




}

