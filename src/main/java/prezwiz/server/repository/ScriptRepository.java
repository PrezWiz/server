package prezwiz.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prezwiz.server.entity.Script;

public interface ScriptRepository extends JpaRepository<Script, Long> {
}
