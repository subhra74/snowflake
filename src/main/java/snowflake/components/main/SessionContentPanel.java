package snowflake.components.main;

import snowflake.components.newsession.SessionInfo;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class SessionContentPanel extends JPanel {
    CardLayout card;
    Map<String, SessionContent> componentHashMap = new HashMap<>();

    public SessionContentPanel(){
        init();
    }

    private void init() {
        card=new CardLayout();
        setLayout(card);
    }

    public void addNewSession(SessionInfo info) {
        SessionContent content = new SessionContent(info);
        componentHashMap.put(info.hashCode() + "", content);
        add(content, info.hashCode() + "");
    }

    public void selectSession(SessionInfo info) {
        System.out.println("Selecting "+info.hashCode());
        card.show(this, info.hashCode() + "");
    }

    public boolean removeSession(SessionInfo info) {
        SessionContent content = componentHashMap.get(info.hashCode() + "");
        remove(content);
        //card.removeLayoutComponent(content);
        return true;
    }
}
