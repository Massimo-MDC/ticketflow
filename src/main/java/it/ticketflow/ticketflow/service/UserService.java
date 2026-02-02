package it.ticketflow.ticketflow.service;

import it.ticketflow.ticketflow.dto.user.UserAdminUpdateDTO;
import it.ticketflow.ticketflow.dto.user.UserResponseDTO;
import it.ticketflow.ticketflow.dto.user.UserUpdateDTO;
import it.ticketflow.ticketflow.entity.User;
import it.ticketflow.ticketflow.entity.enums.Role;
import it.ticketflow.ticketflow.exception.NotFoundException;
import it.ticketflow.ticketflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public Page<UserResponseDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponseDTO::fromEntity);
    }

    public Page<UserResponseDTO> findByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable)
                .map(UserResponseDTO::fromEntity);
    }

    public UserResponseDTO findById(UUID id) {
        User user = getUserById(id);
        return UserResponseDTO.fromEntity(user);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User", id));
    }

    @Transactional
    public UserResponseDTO updateProfile(UUID id, UserUpdateDTO dto) {
        User user = getUserById(id);

        if (dto.firstName() != null) {
            user.setFirstName(dto.firstName());
        }
        if (dto.lastName() != null) {
            user.setLastName(dto.lastName());
        }
        if (dto.phoneNumber() != null) {
            user.setPhoneNumber(dto.phoneNumber());
        }
        if (dto.dateOfBirth() != null) {
            user.setDateOfBirth(dto.dateOfBirth());
        }

        User savedUser = userRepository.save(user);
        return UserResponseDTO.fromEntity(savedUser);
    }

    @Transactional
    public UserResponseDTO uploadProfileImage(UUID id, MultipartFile file) {
        User user = getUserById(id);

        // Elimina l'immagine precedente se esiste
        if (user.getProfileImageUrl() != null) {
            cloudinaryService.deleteImage(user.getProfileImageUrl());
        }

        // Carica la nuova immagine
        String imageUrl = cloudinaryService.uploadImage(file, "users");
        user.setProfileImageUrl(imageUrl);

        User savedUser = userRepository.save(user);
        return UserResponseDTO.fromEntity(savedUser);
    }

    @Transactional
    public UserResponseDTO adminUpdate(UUID id, UserAdminUpdateDTO dto) {
        User user = getUserById(id);

        if (dto.role() != null) {
            user.setRole(dto.role());
        }
        if (dto.enabled() != null) {
            user.setEnabled(dto.enabled());
        }

        User savedUser = userRepository.save(user);
        return UserResponseDTO.fromEntity(savedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = getUserById(id);

        // Elimina l'immagine profilo se esiste
        if (user.getProfileImageUrl() != null) {
            cloudinaryService.deleteImage(user.getProfileImageUrl());
        }

        userRepository.delete(user);
    }
}