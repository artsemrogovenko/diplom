package com.artsemrogovenko.diplom.diagrams.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class FileHashCalculator {
    private final String tempDirectory = "./tempDiagrams/";

    public String calculateHash(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");

        // Создаем поток для чтения файла
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);

        // Читаем содержимое файла блоками и обновляем хэш
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = bis.read(buffer)) != -1) {
            digest.update(buffer, 0, bytesRead);
        }
        // Закрываем потоки
        bis.close();
        fis.close();

        // Получаем хэш в виде массива байт
        byte[] hashBytes = digest.digest();

        // Преобразуем хэш в строку шестнадцатеричного представления
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static String calculateHash(MultipartFile file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] fileBytes = file.getBytes();
        byte[] hashBytes = digest.digest(fileBytes);

        // Преобразование байтов хэша в строку hex
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static String getHash(File file) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");

            try (FileInputStream fis = new FileInputStream(file);
                 DigestInputStream dis = new DigestInputStream(fis, digest)) {

                // Считываем файл блоками для вычисления хэша
                byte[] buffer = new byte[8192];
                while (dis.read(buffer) != -1) {
                    // Чтение файла и вычисление хэша
                }

                // Получаем вычисленный хэш
                byte[] hash = digest.digest();

                // Преобразование байтов хэша в строку hex
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }

                System.out.println("SHA-512 хэш файла: " + hexString);
                return hexString.toString();
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
