package prezwiz.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import prezwiz.server.repository.ContactRepository;
import prezwiz.server.repository.MemberRepository;
import prezwiz.server.service.contact.ContactService;
import prezwiz.server.service.contact.DBContactService;
import prezwiz.server.service.contact.DiscordContactService;

@Configuration
public class ContactConfig {

    @Value("${web_hook.contact}")
    private String CONTACT_WEBHOOK_URI;

    @Bean
    public ContactService contactService(MemberRepository memberRepository, ContactRepository contactRepository) {
        return new DiscordContactService(
                memberRepository,
                contactRepository,
                CONTACT_WEBHOOK_URI
                );
    }
}
