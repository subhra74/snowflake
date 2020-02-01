/**
 *
 */
package snowflake.components.sysinfo;

import snowflake.utils.GraphicsUtils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * @author subhro
 *
 */
public class ServicePanel extends JPanel {
    private ServiceTableModel model = new ServiceTableModel();
    private JTable table;
    private static final Pattern SERVICE_PATTERN = Pattern
            .compile("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+([\\S]+.*)");
    private static final Pattern UNIT_PATTERN = Pattern
            .compile("(\\S+)\\s+([\\S]+.*)");
    private JButton btnStart, btnStop, btnRestart, btnReload, btnEnable,
            btnDisable, btnRefresh;
    private JTextField txtFilter;
    private JCheckBox chkRunAsSuperUser;
    private static final String SEP = UUID.randomUUID().toString();
    private JButton btnFilter;
    private List<ServiceEntry> list;

    public static final String SYSTEMD_COMMAND = "systemctl list-unit-files -t service -a " +
            "--plain --no-pager --no-legend --full; echo "
            + SEP
            + "; systemctl list-units -t service -a --plain --no-pager --no-legend --full";

    /**
     *
     */
    public ServicePanel() {
        super(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(5, 5, 5, 5));

        ServiceTableCellRenderer r = new ServiceTableCellRenderer();

        table = new JTable(model);
        table.setDefaultRenderer(Object.class, r);
        table.setShowGrid(false);
        table.setRowHeight(r.getPreferredSize().height);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        JLabel lbl1 = new JLabel("Search");
        txtFilter = GraphicsUtils.createTextField(30);//new JTextField(30);
        txtFilter.addActionListener(e -> {
            filter();
        });
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

        btnStart = new JButton("Start");
        btnStop = new JButton("Stop");
        btnRestart = new JButton(
                "Restart");
        btnReload = new JButton("Reload");
        btnEnable = new JButton("Enable");
        btnDisable = new JButton(
                "Disable");

        chkRunAsSuperUser = new JCheckBox(
                "Perform actions as super user (sudo)");
        box.add(chkRunAsSuperUser);

        box.add(Box.createHorizontalGlue());
        box.add(btnStart);
        box.add(Box.createHorizontalStrut(5));
        box.add(btnStop);
        box.add(Box.createHorizontalStrut(5));
        box.add(btnRestart);
        box.add(Box.createHorizontalStrut(5));
        box.add(btnReload);
        box.add(Box.createHorizontalStrut(5));
        box.add(btnEnable);
        box.add(Box.createHorizontalStrut(5));
        box.add(btnDisable);
        box.add(Box.createHorizontalStrut(5));

        add(box, BorderLayout.SOUTH);
    }

    public void setElevationActionListener(ActionListener a) {
        chkRunAsSuperUser.addActionListener(a);
    }

//    public void setRefreshActionListener(ActionListener a) {
//        btnRefresh.addActionListener(a);
//    }

    public void setStartServiceActionListener(ActionListener a) {
        btnStart.addActionListener(a);
    }

    public void setStopServiceActionListener(ActionListener a) {
        btnStop.addActionListener(a);
    }

    public void setRestartServiceActionListener(ActionListener a) {
        btnRestart.addActionListener(a);
    }

    public void setReloadServiceActionListener(ActionListener a) {
        btnReload.addActionListener(a);
    }

    public void setEnableServiceActionListener(ActionListener a) {
        btnEnable.addActionListener(a);
    }

    public void setDisableServiceActionListener(ActionListener a) {
        btnDisable.addActionListener(a);
    }

    private void filter() {
        String text = txtFilter.getText();
        model.clear();
        if (text.length() > 0) {
            List<ServiceEntry> filteredList = new ArrayList<>();
            for (ServiceEntry entry : list) {
                if (entry.getName().contains(text) ||
                        entry.getDesc().contains(text) ||
                        entry.getUnitStatus().contains(text)) {
                    filteredList.add(entry);
                }
            }
            model.addEntries(filteredList);
        } else {
            model.addEntries(list);
        }
    }

    private String getSelectedService() {
        int r = table.getSelectedRow();
        if (r < 0) {
            return null;
        }
        return (String) model.getValueAt(table.convertRowIndexToModel(r), 0);
    }

    public String getStartServiceCommand() {
        String cmd = getSelectedService();
        if (cmd == null) {
            return null;
        }
        return "systemctl start " + cmd;
    }

    public String getStopServiceCommand() {
        String cmd = getSelectedService();
        if (cmd == null) {
            return null;
        }
        return "systemctl stop " + cmd;
    }

    public String getRestartServiceCommand() {
        String cmd = getSelectedService();
        if (cmd == null) {
            return null;
        }
        return "systemctl restart " + cmd;
    }

    public String getReloadServiceCommand() {
        String cmd = getSelectedService();
        if (cmd == null) {
            return null;
        }
        return "systemctl reload " + cmd;
    }

    public String getEnableServiceCommand() {
        String cmd = getSelectedService();
        if (cmd == null) {
            return null;
        }
        return "systemctl enable " + cmd;
    }

    public String getDisableServiceCommand() {
        String cmd = getSelectedService();
        if (cmd == null) {
            return null;
        }
        return "systemctl disable " + cmd;
    }

    public void setUseSuperUser(boolean select) {
        chkRunAsSuperUser.setSelected(select);
    }

    public boolean getUseSuperUser() {
        return chkRunAsSuperUser.isSelected();
    }

    public static List<ServiceEntry> parseServiceEntries(StringBuilder data) {
        List<ServiceEntry> list = new ArrayList<>();
        Map<String, String> unitMap = new HashMap<>();
        //Map<String, ServiceEntry> unitMap = new HashMap<>();
        boolean parsingUnit = true;
        for (String s : data.toString().split("\n")) {
            if (parsingUnit && s.equals(SEP)) {
                parsingUnit = false;
                continue;
            }

            if (parsingUnit) {
                parseUnitFile(s, unitMap);
            } else {
                ServiceEntry ent = parseUnit(s, unitMap);
                if (ent != null) {
                    list.add(ent);
                }
            }
        }
//        System.out.println(unitMap);
//        System.out.println(list);

        return list;
//        return unitMap.entrySet().stream().map(e -> e.getValue())
//                .collect(Collectors.toList());
    }

    public void setServiceData(List<ServiceEntry> list) {
        this.list = list;
        filter();
    }

    private static void parseUnitFile(String data, Map<String, String> map) {
        Matcher m = UNIT_PATTERN.matcher(data);
        if (m.find() && m.groupCount() == 2) {
            map.put(m.group(1).trim(), m.group(2).trim());
//            ServiceEntry e = new ServiceEntry();
//            e.setName(m.group(1));
//            e.setUnitStatus("");
//            e.setDesc("");
//            e.setUnitFileStatus(m.group(2));
//            return e;
        }
//        return null;
    }

    private static ServiceEntry parseUnit(String data, Map<String, String> unitMap) {
        ServiceEntry ent = new ServiceEntry();
        Matcher m = SERVICE_PATTERN.matcher(data);
        if (m.find() && m.groupCount() == 5) {
            String name = m.group(1).trim();
            if (unitMap.get(name) != null) {
                String status = unitMap.get(name);
                ent.setName(name);
                ent.setUnitFileStatus(status);
                ent.setUnitStatus(m.group(3) + "(" + m.group(4) + ")");
                ent.setDesc(m.group(5).trim());
                return ent;
            }


//            ServiceEntry e = unitMap.get(m.group(1));
//            if (e != null) {
//                e.setDesc(m.group(5));
//                e.setUnitStatus(m.group(3) + "(" + m.group(4) + ")");
//            }
        }
        return null;
    }

    public String getServiceListCommand() {
        return SYSTEMD_COMMAND;
    }
}
