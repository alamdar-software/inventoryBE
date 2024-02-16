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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/entity")
@CrossOrigin("*")
public class EntityController {
    @Autowired
    private EntityRepository entityRepository;

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

    @PostMapping("/add")
    public ResponseEntity<Entity> addEntity(@RequestBody Entity entity) {
        if (entity == null || entity.getEntityName() == null || entity.getEntityName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Entity existingEntity = entityRepository.findByEntityName(entity.getEntityName());
        if (existingEntity != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(existingEntity);
        }

        Entity savedEntity = entityRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEntity);
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

    @PutMapping("/update/{id}")
    public ResponseEntity<Entity> updateEntity(@PathVariable("id") Long id, @RequestBody Entity updatedEntity) {
        Optional<Entity> optionalEntity = entityRepository.findById(id);
        if (optionalEntity.isPresent()) {
            Entity entity = optionalEntity.get();
            entity.setEntityName(updatedEntity.getEntityName());
            Entity savedEntity = entityRepository.save(entity);
            return ResponseEntity.ok(savedEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

    @GetMapping("get/{id}")
    public ResponseEntity<Entity> getEntityById(@PathVariable("id") Long id) {
        Optional<Entity> optionalEntity = entityRepository.findById(id);
        return optionalEntity.map(entity -> ResponseEntity.ok().body(entity))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

    @GetMapping("/view")
    public ResponseEntity<List<Entity>> getAllEntities() {
        List<Entity> entities = entityRepository.findAll();
        return ResponseEntity.ok(entities);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEntity(@PathVariable("id") Long id) {
        if (entityRepository.existsById(id)) {
            entityRepository.deleteById(id);
            return ResponseEntity.ok("Entity deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
