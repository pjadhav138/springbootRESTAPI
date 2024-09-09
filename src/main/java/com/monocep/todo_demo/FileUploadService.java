package com.monocep.todo_demo;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
	String uploadfile(String path, MultipartFile file) throws IOException;
}
