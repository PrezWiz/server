package prezwiz.server.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Slides {

    @Id
    @GeneratedValue
    @Column(name = "SLIDES_ID")
    private Long id;

    @OneToMany(mappedBy = "slides")
    private List<Slide> slideList = new ArrayList<>();

    public void addSlide(Slide slide) {
        this.slideList.add(slide);
        if (slide.getSlides() != this) { //무한루프에 빠지지 않게 체크
            slide.setSlides(this);
        }
    }
}
