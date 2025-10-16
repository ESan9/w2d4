package emanuelesanna.w2d4.repositories;

import emanuelesanna.w2d4.entities.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BlogsRepository extends JpaRepository<Blog, UUID> {
    List<Blog> findBlogByAuthorId(UUID authorId);
}
