package simple.blog.blogsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import simple.blog.blogsite.service.BlogService;
import simple.blog.blogsite.model.Post;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class BlogController {
    @Autowired
    private BlogService service;

    /*@GetMapping
    public ResponseEntity<List<Post>> getPosts() {
        List<Post> posts = service.getAllPosts();
        posts.forEach(post -> {
            if (post.getImageUrl() != null) {
                // Convert disk path to URL
                String fileName = new File(post.getImageUrl()).getName();
                post.setImageUrl("/uploads/" + fileName);
            }
        });
        return ResponseEntity.ok(posts);
    }*/

    @GetMapping
    public ResponseEntity<List<Post>> getPosts() {
        return ResponseEntity.ok(service.getAllPosts());
    }


    @PostMapping("/create")
    public ResponseEntity<?> addPost(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String email,
            @RequestParam(value="image", required = false) MultipartFile image) {
        try {
            Post savedPost = service.createPost(title, description, email, image);
            return ResponseEntity.ok(savedPost);
        } catch (Exception e) {
            System.out.println("error: "+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<String> deletePost(@PathVariable long id){
        return new ResponseEntity<>(service.deletePostById(id), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        Post updatedPost = service.updateNewPost(id, title, description, imageFile);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }


}
