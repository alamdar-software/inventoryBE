package com.inventory.project.controller;

import com.inventory.project.model.Brand;
import com.inventory.project.repository.BrandRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/brand")
@CrossOrigin("*")
public class BrandController {
    @Autowired
    private BrandRepository brandRepository;

//    @GetMapping("/view/{page}")
//    public ResponseEntity<Map<String, Object>> viewBrands(@PathVariable("page") int page, HttpSession session) {
//        Map<String, Object> response = new HashMap<>();
//        Pageable pageable = PageRequest.of(page - 1, 10);
//        Page<Brand> brandPage = brandRepository.findAll(pageable);
//        response.put("brandList", brandPage.getContent());
//        response.put("currentPage", page);
//        response.put("totalPages", brandPage.getTotalPages());
//        response.put("totalItems", brandPage.getTotalElements());
//        session.setAttribute("page", page);
//        return ResponseEntity.ok(response);
//    }
@GetMapping("/view")
public ResponseEntity<List<Brand>> viewAllBrands() {
    List<Brand> brandList = brandRepository.findAll();
    return ResponseEntity.ok(brandList);
}
    @PostMapping("/add")
    public ResponseEntity<Object> addAndSaveBrand(@RequestBody @Validated Brand brand, BindingResult result, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        int page = 1;

        try {
            if (result.hasErrors()) {
                response.put("error", "Please fill all fields correctly");
                return ResponseEntity.badRequest().body(response);
            }

            if (brandRepository.findByName(brand.getName()) != null) {
                response.put("error", "Brand already exists");
                return ResponseEntity.badRequest().body(response);
            }

            Brand savedBrand = brandRepository.save(brand);
            response.put("brand", savedBrand);


            pagination((Model) response, session, page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error saving brand: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    public void pagination(Model model, HttpSession session, int page) {

        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<Brand> list = brandRepository.findAll(pageable);
        model.addAttribute("brandList", list.getContent());
        session.setAttribute("page", page);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", list.getTotalPages());
        model.addAttribute("totalItems", list.getTotalElements());
    }
    @PutMapping("/edit/{id}")
    public ResponseEntity<Object> editAndUpdateBrand(
            @PathVariable("id") Long id,
            @RequestBody @Validated Brand brand,
            BindingResult result,
            HttpSession session
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (result.hasErrors()) {
                response.put("error", "Please enter all fields correctly");
                return ResponseEntity.badRequest().body(response);
            }

            Optional<Brand> existingBrand = brandRepository.findById(id);
            if (!existingBrand.isPresent()) {
                response.put("error", "Brand not found");
                return ResponseEntity.notFound().build();
            }

            if (brandRepository.findByNameAndIdNot(brand.getName(), id).isPresent()) {
                response.put("error", "Brand with this name already exists");
                return ResponseEntity.badRequest().body(response);
            }

            Brand updatedBrand = existingBrand.get();
            updatedBrand.setName(brand.getName()); // Modify other properties as needed

            brandRepository.save(updatedBrand);


            int page = session.getAttribute("page") != null ? (int) session.getAttribute("page") : 1;

            response.put("success", "Brand Updated Successfully!");
            pagination((Model) response, session, page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error updating brand: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable Long id) {
        try {
            brandRepository.deleteById(id);
            return ResponseEntity.ok("Brand deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deletion unsuccessful");
        }
    }
}
