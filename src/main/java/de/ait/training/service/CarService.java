package de.ait.training.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface CarService {
    void attachImage(Long id, MultipartFile file);
}
