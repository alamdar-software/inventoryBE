package com.inventory.project.controller;

import com.inventory.project.model.Entity;
import com.inventory.project.repository.EntityRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/entity")
public class EntityController {
    @Autowired
    private EntityRepository entityRepository;


    @PostMapping ("/add")
    public ResponseEntity<Map<String, Object>> addEntityModel(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        int page = 1;

        try {
            Entity entityModel = new Entity();

            response.put("entityModel", entityModel);

            pagination(response, session, page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error fetching data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/save")
    public ResponseEntity<String> saveEntityModel(@RequestBody Entity entityModel) {
        try {
            if (entityRepository.findByName(entityModel.getName()) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Entity already exists");
            } else {
                entityRepository.save(entityModel);
                return ResponseEntity.status(HttpStatus.CREATED).body("Entity saved successfully");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving entity: " + e.getMessage());
        }
    }


    @GetMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> editEntityModel(@PathVariable("id") Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        int page = (int) session.getAttribute("page");

        try {
            Entity entityModel = entityRepository.findById(id).orElse(null);
            response.put("entityModel", entityModel);
            response.put("edit", true);
            pagination(response, session, page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error fetching data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PutMapping ("/update")
    public ResponseEntity<String> updateEntityModel(@RequestBody Entity entityModel) {
        try {
            if (entityRepository.findByNameAndId(entityModel.getName(), entityModel.getId()) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Entity with this name already exists");
            } else {
                entityRepository.save(entityModel);
                return ResponseEntity.ok("Entity Updated Successfully!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating entity: " + e.getMessage());
        }
    }


    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>> viewEntityModels(@RequestParam(defaultValue = "0") int page, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            pagination(response, session, page);
            response.put("entityModel", new Entity());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error fetching data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEntityModel(@PathVariable("id") Long id, HttpSession session) {
        try {
             entityRepository.deleteById(id);
            return ResponseEntity.ok("Entity deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deletion Unsuccessful: " + e.getMessage());
        }
    }

    private void pagination(Map<String, Object> model, HttpSession session, int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<Entity> list = entityRepository.findAll(pageable);

        model.put("entityList", list.getContent());
        session.setAttribute("page", page);
        model.put("currentPage", page);
        model.put("totalPages", list.getTotalPages());
        model.put("totalItems", list.getTotalElements());
    }
}
