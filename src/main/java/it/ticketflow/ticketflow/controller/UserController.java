package it.ticketflow.ticketflow.controller;

import it.ticketflow.ticketflow.dto.user.UserAdminUpdateDTO;
import it.ticketflow.ticketflow.dto.user.UserResponseDTO;
import it.ticketflow.ticketflow.dto.user.UserUpdateDTO;
import it.ticketflow.ticketflow.entity.User;
import it.ticketflow.ticketflow.entity.enums.Role;
import it.ticketflow.ticketflow.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // === Endpoint per utente autenticato ===

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(UserResponseDTO.fromEntity(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMyProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserUpdateDTO dto) {
        UserResponseDTO response = userService.updateProfile(user.getId(), dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/image")
    public ResponseEntity<UserResponseDTO> uploadMyProfileImage(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        UserResponseDTO response = userService.uploadProfileImage(user.getId(), file);
        return ResponseEntity.ok(response);
    }

    // === Endpoint ADMIN ===

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable) {
        Page<UserResponseDTO> users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> getUsersByRole(
            @PathVariable Role role,
            Pageable pageable) {
        Page<UserResponseDTO> users = userService.findByRole(role, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        UserResponseDTO user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> adminUpdateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserAdminUpdateDTO dto) {
        UserResponseDTO response = userService.adminUpdate(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}