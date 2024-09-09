package com.monocep.todo_demo;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class MyController {
	UserRepository repository;
	@Autowired
	FileUploadUtil fileUploadUtil;
	@Value("${project.image}")
	String path;

	public MyController(UserRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public String hello() {
		return "Hello World";
	}

	@GetMapping("/user/{id}")
	public EntityModel<User> retriveUser(@PathVariable("id") int id) {
		System.out.println("test");
		Optional<User> user = repository.findById(id);
		if (user.isEmpty())
			throw new UserNotFoundException("Error");
		EntityModel<User> entityModel = EntityModel.of(user.get());
		return entityModel;
	}

	@GetMapping("/users")
	public List<User> retriveAllUsers() {
		return repository.findAll();
	}

	@PostMapping("/addUser")
	public ResponseEntity<User> createUser(@RequestBody User user) {
		System.out.println("test" + user.getUsername());
		try {
			User savedUser = repository.save(user);
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
					.buildAndExpand(savedUser.getId()).toUri();
			System.out.println("test - location" + location);
			return ResponseEntity.created(location).build();
		} catch (Exception e) {
			return null;
		}

	}

	@PostMapping("/single-file-upload")
	public ResponseEntity<Map<String, String>> handleFileUploadUsingCurl(@RequestParam("file") MultipartFile file) {
		Map<String, String> map = new HashMap<>();
		// Populate the map with file details

		map.put("fileSize", file.getSize() + "");
		map.put("fileContentType", file.getContentType());
		try {
			String name = this.fileUploadUtil.uploadfile(path, file);
			map.put("fileName", name);
			System.out.println(name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}

		// File upload is successful
		try {
			file.transferTo(new File("/Users/pankaj/Desktop/Screenshots/" + UUID.randomUUID().toString()
					.concat(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")))));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		map.put("message", "File upload done");

		return ResponseEntity.ok(map);
	}

	@PostMapping("/uploadMultiple")
	public ResponseEntity<String> handleFileUploadMultiple(@RequestParam("files") MultipartFile[] files)
			throws IOException {

		Arrays.stream(files).forEach(multipartFile -> {

			try {
//				multipartFile.transferTo(
//						new File("${project.image}" + UUID.randomUUID().toString().concat(multipartFile
//								.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".")))));
				String name = this.fileUploadUtil.uploadfile(path, multipartFile);
				System.out.println(name);
			} catch (IllegalStateException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
		// handle uploaded files
		return ResponseEntity.ok("Files uploaded successfully!");
	}
}
