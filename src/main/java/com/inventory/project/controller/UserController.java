package com.inventory.project.controller;

import com.inventory.project.constants.ERole;
import com.inventory.project.model.Role;
import com.inventory.project.model.User;
import com.inventory.project.payload.request.AddUser;
import com.inventory.project.repository.RoleRepository;
import com.inventory.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("getById/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/view")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUserById(@PathVariable Long userId, @RequestBody Map<String, Object> requestBody) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (requestBody.containsKey("username")) {
            existingUser.setUsername((String) requestBody.get("username"));
        }
        if (requestBody.containsKey("email")) {
            existingUser.setEmail((String) requestBody.get("email"));
        }
        if (requestBody.containsKey("name")) {
            existingUser.setName((String) requestBody.get("name"));
        }
        if (requestBody.containsKey("contactNumber")) {
            existingUser.setContactNumber(Long.valueOf((Integer) requestBody.get("contactNumber")));
        }
        if (requestBody.containsKey("roles")) {
            String roleName = (String) requestBody.get("roles");
            Set<Role> roles = new HashSet<>();
            switch (roleName) {
                case "ROLE_SUPERAMDIN":
                    Role superadminRole = roleRepository.findByName(ERole.ROLE_SUPERADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(superadminRole);

                    break;
                case "ROLE_PREPARER":
                    Role preparerRole = roleRepository.findByName(ERole.ROLE_PREPARER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(preparerRole);

                    break;
                case "ROLE_VERIFIER":
                    Role verifierRole = roleRepository.findByName(ERole.ROLE_VERIFIER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(verifierRole);

                    break;
                case "ROLE_APPROVER":
                    Role approverRole = roleRepository.findByName(ERole.ROLE_APPROVER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(approverRole);

                    break;
                default:
                    Role userRole = roleRepository.findByName(ERole.ROLE_OTHER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);}
            existingUser.setRoles(roles);
        }

        User savedUser = userRepository.save(existingUser);
        return ResponseEntity.ok(savedUser);

    }


    @DeleteMapping("delete/{userId}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long userId) {
        userRepository.deleteById(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

}
