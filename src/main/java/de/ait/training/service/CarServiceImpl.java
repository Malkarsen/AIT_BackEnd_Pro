package de.ait.training.service;

import de.ait.training.model.Car;
import de.ait.training.repository.CarRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
public class CarServiceImpl implements CarService {
    private final CarRepository repositiry;
    private final String uploadDirName;
    private final String hostUrl;

    public CarServiceImpl(
            CarRepository repositiry,
            @Value("${upload.dir}") String uploadDirName,
            @Value("${host.url}")  String hostUrl
    ) {
        this.repositiry = repositiry;
        this.uploadDirName = uploadDirName;
        this.hostUrl = hostUrl;
    }

    @Override
    @Transactional
    public void attachImage(Long id, MultipartFile file) {
        File uploadDir = new File(uploadDirName);
        uploadDir.mkdirs();

        String uniqueFileName = generateUniqueFileName(file);
        File targetFile = new File(uploadDir, uniqueFileName);

        try (FileOutputStream out = new FileOutputStream(targetFile)) {
            out.write(file.getBytes());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        // Чтобы получить загруженную картинку обратно, клиент отправляет запрос:
        // GET -> http://localhost:8080/77936a2c-5c09-4914-aa6c-f8a84f9ac955-bmw-x5-black.jpg
        Car car = repositiry.findById(id).orElseThrow(
                // По хорошему здесь нужно выбрасывать пользовательский эксепшен
                // и обрабатывать его в глобальном обработчике эксепшенов
                () -> new IllegalArgumentException("Car with id " + id + " not found")
        );
        car.setImageUrl(hostUrl + uniqueFileName);
    }

    private String generateUniqueFileName(MultipartFile file) {
        String origFileName = file.getOriginalFilename();
        String randomUuid = UUID.randomUUID().toString();

        return randomUuid + "-" +  origFileName;
    }
}
