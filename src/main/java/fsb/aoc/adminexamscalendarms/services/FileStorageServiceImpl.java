package fsb.aoc.adminexamscalendarms.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import fsb.aoc.adminexamscalendarms.exceptions.FileStorageException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServiceImpl implements FileStorageService {
  public static final Path CALENDAR_UPLOADS_FOLDER = Paths.get("uploads");

  @Override
  public void saveFile(MultipartFile file) throws IOException {
    if (file.getOriginalFilename() == null) {
      throw new FileStorageException("Bad file name");
    }
    Files.copy(file.getInputStream(), CALENDAR_UPLOADS_FOLDER.resolve(file.getOriginalFilename()));
  }

  @Override
  public Resource loadFileAsResource(String filename) {
    try {
      return new UrlResource(CALENDAR_UPLOADS_FOLDER.resolve(filename).toUri());
    } catch (MalformedURLException e) {
      throw new FileStorageException("Error while loading " + filename + ", " + e);
    }
  }

  @Override
  public void deleteFile(String filename) {}

  @Override
  public Stream<Path> loadAllFiles() {
    return null;
  }
}
