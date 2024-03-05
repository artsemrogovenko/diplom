package com.artsemrogovenko.diplom.diagrams.service;

import com.artsemrogovenko.diplom.diagrams.model.ContractNumber;
import com.artsemrogovenko.diplom.diagrams.model.DiagramDescription;
import com.artsemrogovenko.diplom.diagrams.model.SchemeForModule;
import com.artsemrogovenko.diplom.diagrams.repository.ContractNumberRepository;
import com.artsemrogovenko.diplom.diagrams.repository.SchemeFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiagramService {
    private final FileHashCalculator fileHashCalculator;
    private final ContractNumberRepository contractNumberRepository;
    private final SchemeFileRepository schemeFileRepository;
    private final String FOLDER_PATH = "./myDiagrams/";
    private String tempDirectory = "./tempDiagrams/";

    public ResponseEntity<SchemeForModule> saveToRepositiry(MultipartFile file, DiagramDescription description, String contractNumber) {
        return saveToRepositiry(writeMultipartFile(file), description, contractNumber);
    }


    private SchemeForModule createFileAndGenerateEntity(File file, DiagramDescription description, String contractNumber) {
        Path destination = Path.of(FOLDER_PATH + file.getName());
        if (!fileSearcher(file.getName(), Path.of(FOLDER_PATH))) {
            try {
                if (!Files.exists(Path.of(FOLDER_PATH))) {
                    Files.createDirectories(Path.of(FOLDER_PATH));
                }
                Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                log.warn(e.getLocalizedMessage());
            }
        }
        Pattern pattern = Pattern.compile("\\s\\r\\n\\t");
        Matcher matcher = pattern.matcher(contractNumber);
        //если номеров несколько сделать массив, убрать лишнее
        String[] formatNumbers = matcher.replaceAll("").split(",");

        List<ContractNumber> contractNumbers = new ArrayList<>();

        for (String n : formatNumbers) {

            ContractNumber number = new ContractNumber(n);
            // если нет такого номера то записать в базу, и записать в список
            if (contractNumberRepository.findByNumber(n) == null) {
                contractNumbers.add(contractNumberRepository.save(number));
            } else {
                //если такой номер уже был, то добавить в список
                contractNumbers.add(contractNumberRepository.findByNumber(n));
            }
        }

        SchemeForModule element = SchemeForModule.builder()
                .filePath(destination.toString())
                .moduleName(description.getModuleName())
                .modification(description.getModification())
                .versionAssembly(description.getVersionAssembly())
                .contractNumbers(contractNumbers)
                .build();
        return element;
    }

    private boolean notDuplicate(DiagramDescription description, String contractNumber) {
        List<SchemeForModule> verify = null;
        try {
            verify = schemeFileRepository.findByModuleNameAndModificationAndVersionAssembly(
                    description.getModuleName(), description.getModification(), description.getVersionAssembly()).get();
        } catch (NoSuchElementException e) {
            return true;
        }
        for (SchemeForModule schemeForModule : verify) {
            for (ContractNumber number : schemeForModule.getContractNumbers()) {
                if (number.getNumber().equals(contractNumber)) {
                    return false;
                }
            }
        }
        return true;
    }

    public ResponseEntity<SchemeForModule> saveToRepositiry(File file, DiagramDescription description, String contractNumber) {
        if (file == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        SchemeForModule savedScheme = null;
        if (notDuplicate(description, contractNumber)) {
            savedScheme = createFileAndGenerateEntity(file, description, contractNumber);
            schemeFileRepository.save(savedScheme);
            return new ResponseEntity<>(savedScheme, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(savedScheme, HttpStatus.CONFLICT);
    }

    public File init(File file) {
        // Путь к директории, куда нужно скопировать файл
        Path targetDirectory = Paths.get(tempDirectory);

        try {

            if (!Files.exists(targetDirectory)) {
                // Создаем целевую директорию, если ее не существует
                Files.createDirectories(targetDirectory);
            }

            // Путь к исходному файлу
            Path sourceFile = file.toPath();
            File temp = file;
            // Вычисляем хэш файла
            String hashstring = fileHashCalculator.calculateHash(temp);

//            String extension = "";
            String extension = getExtension(file.getName());
            int lastIndex = temp.getName().lastIndexOf('.');

            // Проверяем, чтобы убедиться, что точка найдена и что она не находится в начале или в конце имени файла
//            if (lastIndex != -1 && lastIndex != 0 && lastIndex != temp.getName().length() - 1) {
//                // Извлекаем подстроку, начиная с индекса точки + 1 (чтобы исключить точку из результата)
//                extension = temp.getName().substring(lastIndex);
//                System.out.println("Расширение файла: " + extension);
//            } else {
//                System.out.println("Невозможно определить расширение файла.");
//            }

            File renamedFile = new File(temp.getParentFile(), hashstring + extension);
            if (!fileSearcher(hashstring + extension, Path.of(file.getParent()))) {
                // Переименовываем файл если нет такого в папке
                temp.renameTo(renamedFile);
            } else {
//                temp.delete();
                file.delete();
            }

            return renamedFile;

        } catch (AccessDeniedException e) {
            System.out.println("Снимите защиту на запись");
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getExtension(String filename) {
        int lastIndex = filename.lastIndexOf('.');
        // Проверяем, чтобы убедиться, что точка найдена и что она не находится в начале или в конце имени файла
        if (lastIndex != -1 && lastIndex != 0 && lastIndex != filename.length() - 1) {
            // Извлекаем подстроку, начиная с индекса точки + 1 (чтобы исключить точку из результата)
            System.out.println("Расширение файла: " + filename.substring(lastIndex));
            return filename.substring(lastIndex);
        } else {
            System.out.println("Невозможно определить расширение файла.");
        }
        return "";
    }

    public File writeMultipartFile(MultipartFile multipartFile) {
        Path targetDirectory = Paths.get(tempDirectory);
        Path targetFile = targetDirectory.resolve(multipartFile.getOriginalFilename());


        try {
            if (multipartFile.isEmpty()) {
                log.warn("файл не выбран");
                throw new IOException("выберите файл");
            }

            if (!Files.exists(targetDirectory)) {
                // Создаем целевую директорию, если ее не существует
                Files.createDirectories(targetDirectory);
            }

            String multipartNewName = FileHashCalculator.calculateHash(multipartFile)
                    .concat(getExtension(multipartFile.getOriginalFilename()));
            // чтобы не писать уже существующий файл, высчитал заранее хеш и вернул ссылку на существующий
            if (fileSearcher(multipartNewName, Path.of(tempDirectory))) {
                return new File(tempDirectory, multipartNewName);
            }

            byte[] bytes = new byte[0];
            bytes = multipartFile.getBytes();
            Path path = Paths.get(targetFile.toUri());
            Files.write(path, bytes);

            log.info("Файл успешно загружен: " + multipartFile.getOriginalFilename());
            return init(targetFile.toFile());

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            log.info("Ошибка загрузки файла");
        }
        return null;
    }

    private boolean fileSearcher(String filename, Path directory) {
        try {
            List<Path> foundFiles = new ArrayList<>();
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    foundFiles.add(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    // Обработка ошибки доступа к файлу
                    return FileVisitResult.CONTINUE;
                }
            });
            for (Path path : foundFiles) {
                if (String.valueOf(path.getFileName()).equals(filename)) {
//                if (path.getFileName().equals(filename)) {
                    return true;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<SchemeForModule> getAllByModuleName(String name) {
        return schemeFileRepository.findAllByModuleName(name);
    }

    public List<String> getAllmodulesNames() {
        return schemeFileRepository.findAllModuleNames();
    }

    public ResponseEntity<Path> getScheme(String moduleName, String modification, String versionAssembly, String number) {
        List<SchemeForModule> list = schemeFileRepository.findByModuleNameAndModificationAndVersionAssembly(
                moduleName, modification, versionAssembly).get();
        Optional<SchemeForModule> result = list.stream()
                .filter(schemeForModule -> schemeForModule.getContractNumbers().stream()
                        .anyMatch(contractNumber -> contractNumber.getNumber().equals(number)))
                .findFirst();
        if (result.isPresent()) {
            // Найден элемент, удовлетворяющий условию
            return new ResponseEntity<Path>(Path.of(result.get().getFilePath()), HttpStatus.OK);
        }
        return new ResponseEntity<Path>(Path.of(null), HttpStatus.BAD_REQUEST);
    }
}
