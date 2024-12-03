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

    public Presentation(String topic) {
        this.topic = topic;
    }

    @Id
    @GeneratedValue
    @Column(name = "PRESENTATION_ID")
    private Long id;

    @Column
    private String topic;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "SCRIPT_ID")
    private Script script;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "SLIDES_ID")
    private Slides slides;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @CreatedDate
    private LocalDateTime createdAt;

    // add method의 경우 초기에 한번만 사용함
    public void addSlides(Slides slides) {
        this.slides = slides;
    }

    public void addScript(Script script) {
        this.script = script;
    }

    public void addTopic(String topic) {
        this.topic = topic;
    }

    public void updateSlides(Slides newSlides) {
        this.slides = newSlides;
    }

    public void updateScript(Script newScript) {
        this.script = newScript;
    }
}
