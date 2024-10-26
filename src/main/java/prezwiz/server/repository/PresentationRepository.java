package prezwiz.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prezwiz.server.entity.Presentation;

public interface PresentationRepository extends JpaRepository<Presentation, Long> {
}
