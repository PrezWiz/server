package prezwiz.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Member {

    public Member(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String role;

    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "member")
    private List<Presentation> presentations = new ArrayList<>();

    // 발표자료 추가 메서드
    public void addPresentation(Presentation presentation) {
        this.presentations.add(presentation);
        presentation.setMember(this);
    }

}
