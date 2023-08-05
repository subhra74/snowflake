package muon.app.ui.components.session.files;

import java.util.*;

public class NavigationHistory {
    private Deque<String> back = new LinkedList<>(),
            forward = new LinkedList<>();

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
