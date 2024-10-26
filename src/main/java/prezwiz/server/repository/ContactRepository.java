package prezwiz.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prezwiz.server.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long> {
}
