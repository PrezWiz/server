package prezwiz.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import prezwiz.server.repository.ContactRepository;
import prezwiz.server.repository.MemberRepository;
import prezwiz.server.service.contact.ContactService;
import prezwiz.server.service.contact.DBContactService;
import prezwiz.server.service.contact.DiscordContactService;

@Configuration
public class ContactConfig {

    @Bean
    public ContactService contactService(MemberRepository memberRepository, ContactRepository contactRepository) {
        return new DiscordContactService(
                memberRepository,
                contactRepository,
                "https://discord.com/api/webhooks/1308299894376759316/PXX6Az0qeNXb6zewcUtBQoZQ8sy-bhcNGkknXIck-tpNFSjExhZsQT1y6jL-xppdvVwJ"
                );
    }
}
