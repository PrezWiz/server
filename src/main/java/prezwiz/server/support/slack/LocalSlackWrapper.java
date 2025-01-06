package prezwiz.server.support.slack;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

//@Profile("local")
//@Component
public class LocalSlackWrapper implements SlackWrapper {
    @Override
    public void success(String message) {

    }

    @Override
    public void warning(String message) {

    }
}
