package muonssh.app.ui.components.session.files;

import java.util.*;

public class NavigationHistory {
    private final Deque<String> back = new LinkedList<>();
    private final Deque<String> forward = new LinkedList<>();

    public boolean hasPrevElement() {
        return !back.isEmpty();
    }

    public boolean hasNextElement() {
        return !forward.isEmpty();
    }

    public String prevElement() {
        String item = back.pop();
        return item;
    }

    public String nextElement() {
        String item = forward.pop();
        return item;
    }

    public void addBack(String item) {
        back.push(item);
    }

    public void addForward(String item) {
        forward.push(item);
    }
}
