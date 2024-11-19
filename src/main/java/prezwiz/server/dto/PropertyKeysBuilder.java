package prezwiz.server.dto;

import java.util.ArrayList;
import java.util.List;

public class PropertyKeysBuilder {
    private List<String> store = new ArrayList<>();

    public void add(String property) {
        store.add("\"" + property + "\"");
    }

    public String build() {
        String result = "[";
        result += String.join(",", store);
        result += "]";
        return result;
    }

    @Override
    public String toString() {
        String result = "[";
        result += String.join(",", store);
        result += "]";
        return result;
    }
}
