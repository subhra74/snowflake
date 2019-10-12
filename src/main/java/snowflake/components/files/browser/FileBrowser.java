package snowflake.components.files.browser;

import snowflake.App;
import snowflake.common.FileSystem;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.components.files.FileComponentHolder;
import snowflake.components.files.browser.local.LocalFileBrowserView;
import snowflake.components.files.browser.sftp.SftpFileBrowserView;
import snowflake.components.files.browser.ssh.SshFileBrowserView;
import snowflake.components.newsession.NewSessionDlg;
import snowflake.components.newsession.SessionInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileBrowser extends JPanel {
    private DefaultComboBoxModel<Object> leftList, rightList;
    private JSplitPane horizontalSplitter;
    private JComboBox<Object> leftDropdown, rightDropdown;
    private CardLayout leftCard, rightCard;
    private JPanel leftPanel, rightPanel;
    private AtomicBoolean closeRequested;
    private FileComponentHolder holder;
    private JRootPane rootPane;
    private boolean ignoreEvent = false;


    public FileBrowser(SessionInfo info,
                       SshUserInteraction source,
                       Map<SessionInfo, FileSystem> fileSystemMap,
                       Map<FileSystem, Integer> fileViewMap,
                       AtomicBoolean closeRequested,
                       FileComponentHolder holder,
                       JRootPane rootPane) {
        super(new BorderLayout());
        this.closeRequested = closeRequested;
        this.holder = holder;
        this.rootPane = rootPane;
        leftList = new DefaultComboBoxModel<>();
        leftList.addElement("New tab - Home server");
        rightList = new DefaultComboBoxModel<>();
        rightList.addElement("New tab");

        horizontalSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        horizontalSplitter.putClientProperty("Nimbus.Overrides", App.splitPaneSkin);
        horizontalSplitter.setResizeWeight(0.5);

        leftCard = new CardLayout();
        rightCard = new CardLayout();

        leftPanel = new JPanel(leftCard);
        rightPanel = new JPanel(rightCard);

        leftDropdown = new JComboBox<>(leftList);
        leftDropdown.setRenderer(new SessionDropDownRenderer());
        //leftDropdown.putClientProperty("Nimbus.Overrides", App.comboBoxSkin);
        rightDropdown = new JComboBox<>(rightList);
        rightDropdown.setRenderer(new SessionDropDownRenderer());
        //rightDropdown.putClientProperty("Nimbus.Overrides", App.comboBoxSkin);

        leftDropdown.addActionListener(e -> {
            System.out.println("Left drop down changed");
            int index = leftDropdown.getSelectedIndex();
            if (index != -1) {
                Object obj = leftList.getElementAt(index);
                if (obj instanceof String) {
                    if (ignoreEvent) {
                        ignoreEvent = false;
                        return;
                    }
                    openSshFileBrowserView(null, AbstractFileBrowserView.PanelOrientation.Left);
                } else {
                    leftCard.show(leftPanel, obj.hashCode() + "");
                }
            }
        });

//        leftDropdown.addItemListener(e -> {
//            System.out.println("Left drop down changed");
//            int index = leftDropdown.getSelectedIndex();
//            if (index != -1) {
//                Object obj = leftList.getElementAt(index);
//                if (obj instanceof String) {
//                } else {
//                    leftCard.show(leftPanel, obj.hashCode() + "");
//                }
//            }
//        });

        rightDropdown.addActionListener(e -> {
            int index = rightDropdown.getSelectedIndex();
            if (index != -1) {
                Object obj = rightList.getElementAt(index);
                if (obj instanceof String) {
                    if (ignoreEvent) {
                        ignoreEvent = false;
                        return;
                    }
                    JComboBox<String> cmbList = new JComboBox<>(new String[]{"Local files", "SFTP server", "FTP server"});
                    if (JOptionPane.showOptionDialog(this, new Object[]{"Please select a server to open in this tab", cmbList},
                            "New tab", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, null, null) == JOptionPane.OK_OPTION) {
                        int selectedOption = cmbList.getSelectedIndex();
                        if (selectedOption == 0) {
                            openLocalFileBrowserView(null, AbstractFileBrowserView.PanelOrientation.Right);
                        } else if (selectedOption == 1) {
                            openSftpFileBrowserView(null, AbstractFileBrowserView.PanelOrientation.Right);
                        }
                    }
                } else {
                    rightCard.show(rightPanel, obj.hashCode() + "");
                }
            }
        });

//        rightDropdown.addItemListener(e -> {
//            int index = rightDropdown.getSelectedIndex();
//            if (index != -1) {
//                Object obj = rightList.getElementAt(index);
//                if (obj instanceof String) {
//                } else {
//                    rightCard.show(rightPanel, obj.hashCode() + "");
//                }
//            }
//        });

        JPanel leftPanelHolder = new JPanel(new BorderLayout());
        JPanel rightPanelHolder = new JPanel(new BorderLayout());

        leftPanelHolder.setBorder(new EmptyBorder(10, 10, 10, 0));
        rightPanelHolder.setBorder(new EmptyBorder(10, 0, 10, 10));

        leftPanelHolder.add(leftDropdown, BorderLayout.NORTH);
        rightPanelHolder.add(rightDropdown, BorderLayout.NORTH);
        leftPanelHolder.add(leftPanel);
        rightPanelHolder.add(rightPanel);

        horizontalSplitter.setLeftComponent(leftPanelHolder);
        horizontalSplitter.setRightComponent(rightPanelHolder);
        horizontalSplitter.setDividerSize(10);

        add(horizontalSplitter);

        SshFileBrowserView fv1 = new SshFileBrowserView(this, rootPane, holder,
                null, AbstractFileBrowserView.PanelOrientation.Left);
        leftList.addElement(fv1);
        leftDropdown.setSelectedIndex(1);
        leftPanel.add(fv1, fv1.hashCode() + "");
        leftCard.show(leftPanel, fv1.hashCode() + "");

        LocalFileBrowserView fv2 = new LocalFileBrowserView(this, rootPane, holder,
                null, AbstractFileBrowserView.PanelOrientation.Right);
        rightList.addElement(fv2);
        rightDropdown.setSelectedIndex(1);
        rightPanel.add(fv2, fv2.hashCode() + "");
        rightCard.show(rightPanel, fv2.hashCode() + "");
    }

    public void close() {
        this.closeRequested.set(true);
    }

    public boolean isCloseRequested() {
        return closeRequested.get();
    }

    public void disableUi() {
        holder.disableUi();
    }

    public void disableUi(AtomicBoolean stopFlag) {
        holder.disableUi(stopFlag);
    }

    public void enableUi() {
        holder.enableUi();
    }

    public void openSshFileBrowserView(String path, AbstractFileBrowserView.PanelOrientation orientation) {
        SshFileBrowserView fv1 = new SshFileBrowserView(this, rootPane, holder, path, orientation);
        int c = leftList.getSize();
        leftList.addElement(fv1);
        leftPanel.add(fv1, fv1.hashCode() + "");
        leftDropdown.setSelectedIndex(c);
        leftCard.show(leftPanel, fv1.hashCode() + "");
    }

    public void openLocalFileBrowserView(String path, AbstractFileBrowserView.PanelOrientation orientation) {
        int c = rightList.getSize();
        LocalFileBrowserView fv1 = new LocalFileBrowserView(this, rootPane, holder, path, orientation);
        rightList.addElement(fv1);
        rightPanel.add(fv1, fv1.hashCode() + "");
        rightDropdown.setSelectedIndex(c);
        rightCard.show(rightPanel, fv1.hashCode() + "");
    }

    public void openSftpFileBrowserView(String path, AbstractFileBrowserView.PanelOrientation orientation) {
        SessionInfo info = new NewSessionDlg().newSession();
        if (info != null) {
            int c = rightList.getSize();
            SftpFileBrowserView fv1 = new SftpFileBrowserView(this, rootPane, holder, path, orientation, info);
            rightList.addElement(fv1);
            rightPanel.add(fv1, fv1.hashCode() + "");
            rightDropdown.setSelectedIndex(c);
            rightCard.show(rightPanel, fv1.hashCode() + "");
        }
    }

    public FileSystem getFs(int sessionCode) {
        try {
            for (int i = 0; i < rightList.getSize(); i++) {
                Object obj = rightList.getElementAt(i);
                if (obj.hashCode() == sessionCode) {
                    return ((AbstractFileBrowserView) obj).getFileSystem();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public void newFileTransfer(FileSystem sourceFs,
//                                FileSystem targetFs,
//                                FileInfo[] files,
//                                String sourceFolder,
//                                String targetFolder,
//                                int dragsource) {
//        holder.newFileTransfer(sourceFs, targetFs, files, sourceFolder, targetFolder, dragsource);
//    }

    public void requestReload(int sourceHashcode) {
        for (int i = 0; i < this.leftList.getSize(); i++) {
            Object obj = leftList.getElementAt(i);
            if (obj.hashCode() == sourceHashcode) {
                ((AbstractFileBrowserView) obj).reload();
                return;
            }
        }
        for (int i = 0; i < this.rightList.getSize(); i++) {
            Object obj = rightList.getElementAt(i);
            if (obj.hashCode() == sourceHashcode) {
                ((AbstractFileBrowserView) obj).reload();
            }
        }
    }

    public void removeFileView(AbstractFileBrowserView fileBrowserView) {
        if (fileBrowserView instanceof SshFileBrowserView) {
            removeRemoteFileView(fileBrowserView);
        } else {
            removeLocalOrForeignFileView(fileBrowserView);
        }
    }

    public void removeRemoteFileView(AbstractFileBrowserView fileBrowserView) {
        for (int i = 0; i < this.leftList.getSize(); i++) {
            Object obj = leftList.getElementAt(i);
            if (obj == fileBrowserView) {
                ignoreEvent = true;
                System.out.println("Remove remote");
                leftPanel.remove((Component) obj);
                leftList.removeElement(obj);
                revalidate();
                repaint();
                return;
            }
        }
    }

    public void removeLocalOrForeignFileView(AbstractFileBrowserView fileBrowserView) {
        for (int i = 0; i < this.rightList.getSize(); i++) {
            Object obj = rightList.getElementAt(i);
            if (obj == fileBrowserView) {
                ignoreEvent = true;
                System.out.println("Remove local or foreign");
                rightPanel.remove((Component) obj);
                rightList.removeElement(obj);
                fileBrowserView.close();
                revalidate();
                repaint();
                return;
            }
        }
    }
}
