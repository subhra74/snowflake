package snowflake.components.main;

import snowflake.components.files.editor.ExternalEditor;
import snowflake.components.newsession.SessionInfo;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SessionContentPanel extends JPanel {
    CardLayout card;
    Map<String, SessionContent> componentHashMap = new HashMap<>();
    ExternalEditor externalEditor;

    public SessionContentPanel() {
        init();
    }

    private void init() {
        card = new CardLayout();
        setLayout(card);
        externalEditor = new ExternalEditor(a -> {
            List<ExternalEditor.FileModificationInfo> list = a;
            for (ExternalEditor.FileModificationInfo f : list) {
                List<ExternalEditor.FileModificationInfo> lst = new ArrayList<>();
                for (Map.Entry<String, SessionContent> ent : componentHashMap.entrySet()) {
                    if (ent.getValue().getFileComponentHolder().hashCode() == f.activeSessionId) {
                        lst.add(f);
                    }
                    ent.getValue().getFileComponentHolder().filesChanged(lst);
                }
            }
        }, 2000);
        externalEditor.startWatchingForChanges();
    }

    public void addNewSession(SessionInfo info) {
        SessionContent content = new SessionContent(info, externalEditor);
        content.setOpaque(false);
        componentHashMap.put(info.hashCode() + "", content);
        add(content, info.hashCode() + "");
    }

    public void selectSession(SessionInfo info) {
        System.out.println("Selecting " + info.hashCode());
        card.show(this, info.hashCode() + "");
    }

    public boolean removeSession(SessionInfo info) {
        SessionContent content = componentHashMap.get(info.hashCode() + "");
        remove(content);
        //card.removeLayoutComponent(content);
        return true;
    }
}
