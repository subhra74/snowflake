package snowflake.components.main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.MatteBorder;

import snowflake.components.common.TabHeader;
import snowflake.components.files.editor.ExternalEditor;
import snowflake.components.newsession.SessionInfo;

public class SessionContentPanel extends JPanel {
	CardLayout card;
	Map<String, SessionContent> componentHashMap = new HashMap<>();
	ExternalEditor externalEditor;
	private JTabbedPane tabs;

	public SessionContentPanel() {
		init();
	}

	private void init() {
		setLayout(new BorderLayout());
		setBorder(new MatteBorder(1, 0, 0, 0, new Color(24, 26, 31)));
		tabs = new JTabbedPane();

		tabs.putClientProperty("override.tab-background",
				new Color(33, 37, 43));
		tabs.putClientProperty("override.tab-selection", new Color(40, 44, 52));

		tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabs.addChangeListener(e -> {
			if (tabs.getSelectedIndex() != -1 && tabs.getTabCount() > 0) {

				for (int i = 0; i < tabs.getTabCount(); i++) {
					TabHeader header1 = (TabHeader) tabs.getTabComponentAt(i);
					if (header1 != null) {
						header1.getBtnClose().setSelected(false);
					}
				}

				TabHeader header = (TabHeader) tabs
						.getTabComponentAt(tabs.getSelectedIndex());
				if (header != null) {
					header.getBtnClose().setSelected(true);
				}
			}
		});

		this.add(tabs);

//        card = new CardLayout();
//        setOpaque(true);
//        setBackground(new Color(240, 240, 240));
//        setLayout(card);
		externalEditor = new ExternalEditor(a -> {
			List<ExternalEditor.FileModificationInfo> list = a;
			for (ExternalEditor.FileModificationInfo f : list) {
				List<ExternalEditor.FileModificationInfo> lst = new ArrayList<>();
				for (Map.Entry<String, SessionContent> ent : componentHashMap
						.entrySet()) {
					if (ent.getValue().getFileComponentHolder()
							.hashCode() == f.activeSessionId) {
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
		createNewTab(info, content);
		// add(content, info.hashCode() + "");
	}

	private void createNewTab(SessionInfo info, SessionContent content) {
		int index = tabs.getTabCount();
		TabHeader tabHeader = new TabHeader(info.getName());
		tabHeader.getLblTitle().setForeground(new Color(210, 213, 219));
		tabHeader.getBtnClose().setForeground(new Color(20, 20, 20));
		tabHeader.getBtnClose().setBackground(new Color(150, 150, 150));
		tabHeader.getBtnClose().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = tabs.indexOfTabComponent(tabHeader);
				System.out.println("Closing tab at: " + index);
				closeTab(index);
			}
		});
		tabs.addTab(null, content);
		tabs.setTabComponentAt(index, tabHeader);
		tabs.setSelectedIndex(index);
	}

	public void closeTab(int index) {
		SessionContent content = (SessionContent) tabs.getComponentAt(index);
		tabs.removeTabAt(index);
		content.close();
	}

	public void selectSession(SessionInfo info) {
//        System.out.println("Selecting " + info.hashCode());
//        card.show(this, info.hashCode() + "");
	}

	public boolean removeSession(SessionInfo info) {
		return true;
//        SessionContent content = componentHashMap.get(info.hashCode() + "");
//        //remove(content);
//        content.close();
//        //card.removeLayoutComponent(content);
//        return true;
	}
}
