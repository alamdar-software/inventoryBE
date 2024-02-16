package com.inventory.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.project.model.Brand;
import com.inventory.project.repository.BrandRepository;

import jakarta.servlet.http.HttpSession;

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
@PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")
public ResponseEntity<List<Brand>> viewAllBrands() {
    List<Brand> brandList = brandRepository.findAll();
    return ResponseEntity.ok(brandList);
}
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

    @PostMapping("/add")
    public ResponseEntity<Object> addAndSaveBrand(@RequestBody @Validated Brand brand, BindingResult result, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        int page = 1;

        try {
            if (result.hasErrors()) {
                response.put("error", "Please fill all fields correctly");
                return ResponseEntity.badRequest().body(response);
            }

            if (brandRepository.findByBrandName(brand.getBrandName()) != null) {
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
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

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

            if (brandRepository.findByBrandNameAndIdNot(brand.getBrandName(), id).isPresent()) {
                response.put("error", "Brand with this name already exists");
                return ResponseEntity.badRequest().body(response);
            }

            Brand updatedBrand = existingBrand.get();
            updatedBrand.setBrandName(brand.getBrandName()); // Modify other properties as needed

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
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getBrandById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Brand> brand = brandRepository.findById(id);

            if (brand.isPresent()) {
                return ResponseEntity.ok(brand.get());
            } else {
                response.put("error", "Brand not found for ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("error", "Error fetching brand: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @PreAuthorize("hasRole('SUPERADMIN')")

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
