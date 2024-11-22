package prezwiz.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prezwiz.server.entity.Member;
import prezwiz.server.entity.Slides;

import java.util.Optional;

public interface SlidesRepository extends JpaRepository<Slides, Long> {
}
