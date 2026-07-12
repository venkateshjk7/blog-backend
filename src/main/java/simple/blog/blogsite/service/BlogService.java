package simple.blog.blogsite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import simple.blog.blogsite.repository.BlogRepository;
import simple.blog.blogsite.model.Post;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class BlogService {
    @Autowired
    private BlogRepository repository;

    @Autowired
    private StorageService storageService;

    public List<Post> getAllPosts() {
        return repository.findAll();
    }

    public Post createPost(String title, String description, String email, MultipartFile image) throws IOException {
        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setEmail(email);

        // Save image and set URL
        String imageUrl = storageService.save(image);
        post.setImageUrl(imageUrl);

        return repository.save(post);
    }

    public String deletePostById(long id){
        repository.deleteById(id);
        return "Post is successfully deleted";
    }

    public Post updateNewPost(Long id, String title, String description, MultipartFile imageFile) {
        Post existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        existing.setTitle(title);
        existing.setDescription(description);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Save file to a folder (e.g., "uploads") and store the path/URL
                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                Path path = Paths.get("uploads/" + fileName);
                Files.write(path, imageFile.getBytes());

                // Store the relative path or URL in the entity
                existing.setImageUrl(path.toString());
            } catch (IOException e) {
                throw new RuntimeException("Failed to process image", e);
            }
        }

        return repository.save(existing);
    }

}
