package prezwiz.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Script {

    public Script(String content) {
        this.content = content;
    }

    @Id
    @GeneratedValue
    @Column(name = "SCRIPT_ID")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

}
