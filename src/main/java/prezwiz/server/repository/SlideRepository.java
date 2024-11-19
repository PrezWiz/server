package prezwiz.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prezwiz.server.entity.Slide;

public interface SlideRepository extends JpaRepository<Slide, Long> {
}
