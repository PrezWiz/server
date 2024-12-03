package prezwiz.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor
public class Contact {

    public Contact(String message, Member member, LocalDateTime createdAt) {
        this.message = message;
        this.member = member;
        this.createdAt = createdAt;
    }

    @Id
    @GeneratedValue
    @Column(name = "CONTACT_ID")
    private Long id;

    @Column
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    Member member;

    @Column
    private LocalDateTime createdAt;

}
