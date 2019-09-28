package snowflake.components.sysinfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SocketPanel extends JPanel {
    private SocketTableModel model = new SocketTableModel();
    private JTable table;

    private JButton btnRefresh;
    private JTextField txtFilter;
    private JCheckBox chkRunAsSuperUser;
    private JButton btnFilter;
    private List<SocketEntry> list;
    private static final String SEPARATOR = UUID.randomUUID().toString();

    public static final String LSOF_COMMAND = "sh -c \"echo " + SEPARATOR + ";lsof -b -n -i tcp -P -s tcp:LISTEN -F cn\"";

    public SocketPanel() {
        super(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(5, 5, 5, 5));
        table = new JTable(model);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        JLabel lbl1 = new JLabel("Search");
        txtFilter = new JTextField(30);
        btnFilter = new JButton("Search");

        Box b1 = Box.createHorizontalBox();
        b1.add(lbl1);
        b1.add(Box.createHorizontalStrut(5));
        b1.add(txtFilter);
        b1.add(Box.createHorizontalStrut(5));
        b1.add(btnFilter);

        add(b1, BorderLayout.NORTH);

        btnFilter.addActionListener(e -> {
            filter();
        });
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table));

        Box box = Box.createHorizontalBox();

        btnRefresh = new JButton("Refresh");

        chkRunAsSuperUser = new JCheckBox(
                "Perform actions as super user (sudo)");
        box.add(chkRunAsSuperUser);

        box.add(Box.createHorizontalGlue());
        box.add(btnRefresh);
        box.add(Box.createHorizontalStrut(5));

        add(box, BorderLayout.SOUTH);
    }

    public void setRefreshActionListener(ActionListener a) {
        btnRefresh.addActionListener(a);
    }

    private void filter() {
        String text = txtFilter.getText();
        model.clear();
        if (text.length() > 0) {
            List<SocketEntry> filteredList = new ArrayList<>();
            for (SocketEntry entry : list) {
                if (entry.getApp().contains(text) ||
                        (entry.getPort() + "").contains(text) ||
                        entry.getHost().contains(text) ||
                        (entry.getPid() + "").contains(text)) {
                    filteredList.add(entry);
                }
            }
            model.addEntries(filteredList);
        } else {
            model.addEntries(list);
        }
    }

    public boolean getUseSuperUser() {
        return chkRunAsSuperUser.isSelected();
    }

    public static List<SocketEntry> parseSocketList(String text) {
        List<SocketEntry> list = new ArrayList<>();
        SocketEntry ent = null;
        boolean start = false;
        for (String line : text.split("\n")) {
            if (!start) {
                if (line.trim().equals(SEPARATOR)) {
                    start = true;
                }
                continue;
            }
            char ch = line.charAt(0);
            if (ch == 'p') {
                if (ent != null) {
                    list.add(ent);
                }
                ent = new SocketEntry();
                ent.setPid(Integer.parseInt(line.substring(1)));
            }
            if (ch == 'c') {
                ent.setApp(line.substring(1));
            }
            if (ch == 'n') {
                String hostStr = line.substring(1);
                int index = hostStr.lastIndexOf(":");
                if (index != -1) {
                    int port = Integer.parseInt(hostStr.substring(index) + 1);
                    String host = hostStr.substring(0, index);
                    if (ent.getHost() != null) {
                        //if listening on multiple interfaces, ports
                        SocketEntry ent1 = new SocketEntry();
                        ent1.setPort(port);
                        ent1.setHost(host);
                        ent1.setApp(ent.getApp());
                        ent1.setPid(ent.getPid());
                        list.add(ent1);
                    }
                    ent.setPort(port);
                    ent.setHost(host);
                }
            }
        }
        if (ent != null) {
            list.add(ent);
        }
        return list;
    }

    public void setSocketData(List<SocketEntry> list) {
        this.list = list;
        filter();
    }
}
