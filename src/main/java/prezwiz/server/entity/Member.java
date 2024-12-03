package prezwiz.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

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

        this.isActive = true;
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

    @Column
    private boolean isActive;

    @CreatedDate
    private LocalDateTime createdAt;

    // 영속성 전이를 위한 cascade = All,
    // 고아객체 삭제를 위한 orphanRemoval = true
    @OneToMany(mappedBy = "member",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Presentation> presentations = new ArrayList<>();

    // 발표자료 추가 메서드
    public void addPresentation(Presentation presentation) {
        this.presentations.add(presentation);
        presentation.setMember(this);
    }

    public void withdraw(){
        this.isActive = false;
    }

    public void modifyPassword(String password){
        if (!StringUtils.hasText(password)) throw new IllegalArgumentException("password must not empty");
        this.password = password;
    }
}
