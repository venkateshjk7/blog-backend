package simple.blog.blogsite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import simple.blog.blogsite.model.Post;

@Component
public interface BlogRepository extends JpaRepository<Post,Long> {
}
