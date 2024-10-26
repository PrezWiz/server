package prezwiz.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Presentation {

    public Presentation(String topic, String pptLocation, String scriptLocation) {
        this.topic = topic;
        this.pptLocation = pptLocation;
        this.scriptLocation = scriptLocation;
    }

    @Id
    @GeneratedValue
    @Column(name = "PRESENTATION_ID")
    private Long id;

    @Column
    private String topic;

    @Column
    private String pptLocation;

    @Column
    private String scriptLocation;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @CreatedDate
    private LocalDateTime createdAt;
}
