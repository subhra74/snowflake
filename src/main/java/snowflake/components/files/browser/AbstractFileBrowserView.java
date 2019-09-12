package snowflake.components.files.browser;

import snowflake.App;
import snowflake.components.common.AddressBar;
import snowflake.components.common.NavigationHistory;
import snowflake.components.files.DndTransferData;
import snowflake.components.files.FileComponentHolder;
import snowflake.components.files.browser.folderview.FolderView;
import snowflake.components.files.browser.folderview.FolderViewEventListener;
import snowflake.utils.LayoutUtils;
import snowflake.utils.PathUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

public abstract class AbstractFileBrowserView extends JPanel implements FolderViewEventListener {
    protected AddressBar addressBar;
    protected FolderView folderView;
    protected JRootPane rootPane;
    private AbstractAction upAction, reloadAction;
    protected String path;
    protected FileComponentHolder holder;
    private NavigationHistory history;
    private JButton btnBack, btnNext;
    private OverflowMenuHandler overflowMenuHandler;

    protected PanelOrientation orientation;

    public enum PanelOrientation {
        Left, Right
    }

    public AbstractFileBrowserView(JRootPane rootPane, FileComponentHolder holder, PanelOrientation orientation) {
        super(new BorderLayout());
        this.orientation = orientation;
        this.rootPane = rootPane;
        this.holder = holder;

        overflowMenuHandler = new OverflowMenuHandler(holder, this);
        history = new NavigationHistory();
        JPanel toolBar = new JPanel(new BorderLayout());
        createAddressBar();
        addressBar.addActionListener(e -> {
            String text = e.getActionCommand();
            System.out.println("Address changed: " + text + " old: " + this.path);
            if (PathUtils.isSamePath(this.path, text)) {
                System.out.println("Same text");
                return;
            }
            if (text != null && text.length() > 0) {
                addBack(this.path);
                render(text);
            }
        });
        Box smallToolbar = Box.createHorizontalBox();

        upAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBack(path);
                up();
            }
        };
        reloadAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reload();
            }
        };

        btnBack = new JButton();
        btnBack.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        btnBack.setForeground(Color.DARK_GRAY);
        btnBack.setFont(App.getFontAwesomeFont());
        btnBack.setText("\uf060");
        btnBack.addActionListener(e -> {
            String item = history.prevElement();
            addNext(this.path);
            render(item);
        });


        btnNext = new JButton();
        btnNext.setForeground(Color.DARK_GRAY);
        btnNext.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        btnNext.setFont(App.getFontAwesomeFont());
        btnNext.setText("\uf061");
        btnNext.addActionListener(e -> {
            String item = history.nextElement();
            addBack(this.path);
            render(item);
        });


        JButton btnHome = new JButton();
        btnHome.setForeground(Color.DARK_GRAY);
        btnHome.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        btnHome.setFont(App.getFontAwesomeFont());
        btnHome.setText("\uf015");
        btnHome.addActionListener(e -> {
                    addBack(this.path);
                    home();
                }
        );


        JButton btnUp = new JButton();
        btnUp.setForeground(Color.DARK_GRAY);
        btnUp.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        btnUp.addActionListener(upAction);
        btnUp.setFont(App.getFontAwesomeFont());
        btnUp.setText("\uf062");


        smallToolbar.add(Box.createHorizontalStrut(5));

        JButton btnReload = new JButton();
        btnReload.setForeground(Color.DARK_GRAY);
        btnReload.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        btnReload.addActionListener(reloadAction);
        btnReload.setFont(App.getFontAwesomeFont());
        btnReload.setText("\uf021");

        JButton btnMore = new JButton();
        btnMore.setForeground(Color.DARK_GRAY);
        btnMore.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        btnMore.setFont(App.getFontAwesomeFont());
        btnMore.setText("\uf142");
        btnMore.addActionListener(e -> {
            JPopupMenu popupMenu = overflowMenuHandler.getOverflowMenu();
            popupMenu.pack();
            Dimension d = popupMenu.getPreferredSize();
            int x = btnMore.getWidth() - d.width;
            int y = btnMore.getHeight();
            popupMenu.show(btnMore, x, y);
        });

        LayoutUtils.makeSameSize(btnMore, btnReload, btnUp, btnHome, btnNext, btnBack);

        smallToolbar.add(btnBack);
        smallToolbar.add(btnNext);
        smallToolbar.add(btnHome);
        smallToolbar.add(btnUp);

        Box b2 = Box.createHorizontalBox();
        b2.add(btnReload);
        b2.setBorder(new EmptyBorder(3, 0, 3, 0));
        b2.add(btnReload);
        b2.add(btnMore);

        toolBar.add(smallToolbar, BorderLayout.WEST);
        toolBar.add(addressBar);
        toolBar.add(b2, BorderLayout.EAST);

        add(toolBar, BorderLayout.NORTH);

        folderView = new FolderView(this);

        this.overflowMenuHandler.setFolderView(folderView);

        add(folderView);

        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "up");
        this.getActionMap().put("up", upAction);

        updateNavButtons();

    }


    protected abstract void createAddressBar();

    public abstract String toString();

    public void close() {
//        if (fs != null) {
//            synchronized (fileViewMap) {
//                int c = fileViewMap.get(fs);
//                if (c > 1) {
//                    fileViewMap.put(fs, c - 1);
//                } else if (c == 1) {
//                    try {
//                        fs.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
    }

    public String getCurrentDirectory() {
        return this.path;
    }

    public abstract boolean handleDrop(DndTransferData transferData);

    protected abstract void up();

    protected abstract void home();

    @Override
    public void reload() {
        this.render(this.path);
    }

    public PanelOrientation getOrientation() {
        return orientation;
    }

    @Override
    public void addBack(String path) {
        history.addBack(path);
        updateNavButtons();
    }

    private void addNext(String path) {
        history.addForward(this.path);
        updateNavButtons();
    }

    private void updateNavButtons() {
        btnBack.setEnabled(history.hasPrevElement());
        btnNext.setEnabled(history.hasNextElement());
    }

    public OverflowMenuHandler getOverflowMenuHandler() {
        return this.overflowMenuHandler;
    }
}
