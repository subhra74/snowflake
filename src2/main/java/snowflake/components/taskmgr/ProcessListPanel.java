package snowflake.components.taskmgr;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProcessListPanel extends JPanel {
    private ProcessTableModel model;
    private JTable table;
    private JTextField txtFilter;
    private JButton btnKill, btnChangePriority;

    public ProcessListPanel() {
        super(new BorderLayout());
        JPanel pan = new JPanel(new BorderLayout());
        model = new ProcessTableModel();
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane jsp = new JScrollPane(table);
        pan.add(jsp);

        Box b1 = Box.createHorizontalBox();
        b1.add(new JLabel("Filter process"));
        txtFilter = new JTextField(30);
        b1.add(txtFilter);
        JButton btnFilter = new JButton("Filter");
        b1.add(btnFilter);
        JButton btnClearFilter = new JButton("Clear");
        b1.add(btnClearFilter);

        Box b2 = Box.createHorizontalBox();
        btnKill = new JButton("Kill process");
        btnChangePriority = new JButton("Change priority");
        b2.add(Box.createHorizontalGlue());
        b2.add(btnKill);
        b2.add(btnChangePriority);

        pan.add(b1, BorderLayout.NORTH);
        pan.add(b2, BorderLayout.SOUTH);

        add(new JLabel("Processes"), BorderLayout.NORTH);
        add(pan);
    }

    public void setProcessList(List<ProcessTableEntry> list) {
        this.model.setProcessList(list);
    }
}
