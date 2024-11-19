package prezwiz.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prezwiz.server.entity.Slides;

public interface SlidesRepository extends JpaRepository<Slides, Long> {
}
