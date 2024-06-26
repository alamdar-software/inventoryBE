package com.inventory.project.serviceImpl;

import com.inventory.project.model.Entity;
import com.inventory.project.repository.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class EntityService {
    @Autowired
    private EntityRepository entityRepository;

    public List<Entity> findByEntityName(String entityName) {
        return Collections.singletonList(entityRepository.findByEntityName(entityName));
    }

    public List<Entity> getAllEntities() {
        return entityRepository.findAll();
    }
}
