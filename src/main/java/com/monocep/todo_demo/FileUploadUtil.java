package com.monocep.todo_demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadUtil implements FileUploadService {

	public String uploadfile(String path, MultipartFile file) throws IOException {
		// TODO Auto-generated method stub

		String name = file.getOriginalFilename();
		File f = new File(path);
		if (!f.exists()) {
			f.mkdir();
		}
		String fileName = path.concat(UUID.randomUUID().toString().concat(name.substring(name.lastIndexOf("."))));
		Files.copy(file.getInputStream(), Paths.get(fileName));
		return fileName;
	}

}
