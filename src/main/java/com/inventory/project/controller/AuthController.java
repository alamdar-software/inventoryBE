package com.inventory.project.controller;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.inventory.project.exception.ErrorResponse;
import com.inventory.project.payload.request.AddUser;
import com.inventory.project.payload.request.UpdatePasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.project.constants.ERole;
import com.inventory.project.model.Role;
import com.inventory.project.model.User;
import com.inventory.project.payload.request.LoginRequest;
import com.inventory.project.payload.response.JwtResponse;
import com.inventory.project.payload.response.MessageResponse;
import com.inventory.project.repository.RoleRepository;
import com.inventory.project.repository.UserRepository;
import com.inventory.project.security.jwt.JwtUtils;
import com.inventory.project.serviceImpl.UserDetailsImpl;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt, 
                         userDetails.getId(), 
                         userDetails.getUsername(), 
                         userDetails.getEmail(), 
                         roles));
  }

  @PreAuthorize("hasRole('SUPERADMIN')")
  @PostMapping("/addUser")
  public ResponseEntity<?> registerUser(@Valid @RequestBody AddUser addUser,
                                        BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      List<String> errors = bindingResult.getFieldErrors().stream().map(error -> error.getDefaultMessage())
              .collect(Collectors.toList());
      return ResponseEntity.badRequest().body(new ErrorResponse(errors));
    }
    if (userRepository.existsByUsername(addUser.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Username is already taken!"));
    }

    if (userRepository.existsByEmail(addUser.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use!"));
    }

    // Create new user's account
    User user = new User(addUser.getName(), addUser.getUsername(), addUser.getEmail(),
            encoder.encode(addUser.getPassword()));


    String roleName = addUser.getRole();
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
        roles.add(userRole);
    }

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
  @PostMapping("/updatePassword")
  public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,
                                          Authentication authentication, Principal principal) {
    User user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));

    if (!encoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Old password is incorrect!"));
    }

    user.setPassword(encoder.encode(updatePasswordRequest.getNewPassword()));
    userRepository.save(user);
    return ResponseEntity.ok(new MessageResponse("Password updated successfully!"));
  }

}
