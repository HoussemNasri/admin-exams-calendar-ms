package fsb.aoc.adminexamscalendarms.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface FileStorageService {
  void saveFile(MultipartFile file) throws IOException;

  Resource loadFileAsResource(String filename);

  void deleteFile(String filename);

  Stream<Path> loadAllFiles();
}
