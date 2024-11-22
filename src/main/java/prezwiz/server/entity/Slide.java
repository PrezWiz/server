package prezwiz.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Slide {

    public Slide(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Id
    @GeneratedValue
    @Column(name = "SLIDE_ID")
    private Long id;

    @Column
    private String title;

    @Column
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SLIDES_ID")
    private Slides slides;

    public void setSlides(Slides slides) {
        this.slides = slides;
    }

}
