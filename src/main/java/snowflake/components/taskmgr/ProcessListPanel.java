package snowflake.components.taskmgr;

import snowflake.components.taskmgr.plaformsupport.ProcessListRenderer;
import snowflake.utils.GraphicsUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ProcessListPanel extends JPanel {
    private ProcessTableModel model;
    private JTable table;
    private JTextField txtFilter;
    private JButton btnKill, btnChangePriority, btnCopyArgs, btnStop;
    private RowFilter<ProcessTableModel, Integer> rowFilter;
    private String filterText = "";
    private JPopupMenu killPopup, prioPopup;
    private BiConsumer<String, Boolean> consumer;
    private AtomicBoolean hasPendingOperation = new AtomicBoolean(false);

    public ProcessListPanel(BiConsumer<String, Boolean> consumer, Consumer<Boolean> consumer1) {
        super(new BorderLayout());
        this.consumer = consumer;
        JPanel pan = new JPanel(new BorderLayout(5, 5));
        model = new ProcessTableModel();
        table = new JTable(model);

        table.getSelectionModel().addListSelectionListener(e -> {
            btnKill.setEnabled(table.getSelectedRows().length > 0 && !hasPendingOperation.get());
            btnChangePriority.setEnabled(table.getSelectedRows().length > 0 && !hasPendingOperation.get());
            btnCopyArgs.setEnabled(table.getSelectedRows().length > 0 && !hasPendingOperation.get());
        });

        // table.setAutoCreateRowSorter(true);
        ProcessListRenderer renderer = new ProcessListRenderer();
        table.setDefaultRenderer(Object.class, renderer);
        table.setRowHeight(renderer.getPreferredSize().height);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(200);

        rowFilter = new RowFilter<ProcessTableModel, Integer>() {
            @Override
            public boolean include(
                    Entry<? extends ProcessTableModel, ? extends Integer> entry) {
                if (filterText.length() < 1) {
                    return true;
                }
                Integer index = entry.getIdentifier();
                int c = entry.getModel().getColumnCount();
                for (int i = 0; i < c; i++) {
                    if ((entry.getModel().getValueAt(index, i)).toString()
                            .toLowerCase(Locale.ENGLISH)
                            .contains(filterText
                                    .toLowerCase(Locale.ENGLISH))) {
                        return true;
                    }
                }
                return false;
            }
        };

        TableRowSorter<ProcessTableModel> sorter = new TableRowSorter<ProcessTableModel>(
                model);
        sorter.setRowFilter(rowFilter);
        table.setRowSorter(sorter);

        JScrollPane jsp = new JScrollPane(table);
        pan.add(jsp);

        Box b1 = Box.createHorizontalBox();
//        b1.setBorder(new EmptyBorder(5, 5, 5, 5));
        b1.add(new JLabel("Processes"));
        b1.add(Box.createHorizontalStrut(10));
        txtFilter = new JTextField(30);
        b1.add(txtFilter);
        b1.add(Box.createHorizontalStrut(5));
        JButton btnFilter = new JButton("Filter");
        btnFilter.addActionListener(e -> {
            this.filterText = getProcessFilterText();
            model.fireTableDataChanged();
        });
        b1.add(btnFilter);
        b1.add(Box.createHorizontalStrut(5));

        JButton btnClearFilter = new JButton("Clear");
        b1.add(btnClearFilter);
        btnClearFilter.addActionListener(e -> {
            this.txtFilter.setText("");
            this.filterText = getProcessFilterText();
            model.fireTableDataChanged();
        });

        killPopup = new JPopupMenu();

        JMenuItem mKill = new JMenuItem("Kill");
        JMenuItem mKillAsRoot = new JMenuItem("Kill using sudo");

        mKill.addActionListener(e -> {
            int c = table.getSelectedRow();
            if (c != -1) {
                hasPendingOperation.set(true);
                btnKill.setEnabled(false);
                ProcessTableEntry ent = model.get(table.convertRowIndexToModel(c));
                this.consumer.accept("kill -9 " + ent.getPid(), Boolean.FALSE);
            }
        });

        mKillAsRoot.addActionListener(e -> {
            int c = table.getSelectedRow();
            if (c != -1) {
                hasPendingOperation.set(true);
                ProcessTableEntry ent = model.get(table.convertRowIndexToModel(c));
                btnKill.setEnabled(false);
                this.consumer.accept("kill -9 " + ent.getPid(), Boolean.TRUE);
            }
        });

        killPopup.add(mKill);
        killPopup.add(mKillAsRoot);

        killPopup.pack();

        prioPopup = new JPopupMenu();
        JMenuItem mPrio = new JMenuItem("Change priority");
        JMenuItem mPrioAsRoot = new JMenuItem("Change priority using sudo");
        prioPopup.add(mPrio);
        prioPopup.add(mPrioAsRoot);
        prioPopup.pack();

        btnStop = new JButton("Stop monitoring");
        btnStop.addActionListener(e -> {
            consumer1.accept(Boolean.TRUE);
        });

        Box b2 = Box.createHorizontalBox();
        btnCopyArgs = new JButton("Copy command");
        btnCopyArgs.addActionListener(e -> {
            int c = table.getSelectedRow();
            if (c != -1) {
                ProcessTableEntry ent = model.get(table.convertRowIndexToModel(c));
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(ent.getArgs()), null);
            }
        });

        btnKill = new JButton("Kill process");
        btnKill.addActionListener(e -> {
            Dimension d = killPopup.getPreferredSize();
            killPopup.show(btnKill, 0, -d.height);
        });
        btnChangePriority = new JButton("Change priority");
        btnChangePriority.addActionListener(e -> {
            Dimension d = prioPopup.getPreferredSize();
            prioPopup.show(btnChangePriority, 0, -d.height);
        });
        b2.add(btnStop);
        b2.add(Box.createHorizontalGlue());
        b2.add(btnCopyArgs);
        b2.add(Box.createHorizontalStrut(5));
        b2.add(btnKill);
        b2.add(Box.createHorizontalStrut(5));
        b2.add(btnChangePriority);

        pan.add(b1, BorderLayout.NORTH);
        pan.add(b2, BorderLayout.SOUTH);
        add(pan);

        btnKill.setEnabled(false);
        btnChangePriority.setEnabled(false);
        btnCopyArgs.setEnabled(false);
    }

    public String getProcessFilterText() {
        return txtFilter.getText();
    }

    public void setProcessList(List<ProcessTableEntry> list) {
        int x = table.getSelectedRow();
        int selectedPid = -1;
        if (x != -1) {
            int xc = table.convertRowIndexToModel(x);
            selectedPid = this.model.get(xc).getPid();
        }
        this.model.setProcessList(list);
        if (selectedPid != -1) {
            int i = 0;
            for (ProcessTableEntry ent : this.model.getProcessList()) {
                if (ent.getPid() == selectedPid) {
                    int r = table.convertRowIndexToView(i);
                    table.setRowSelectionInterval(r, r);
                    break;
                }
                i++;
            }
        }
    }

    public void activateProcessListPanel() {
        this.hasPendingOperation.set(false);
    }

    public void enableStop() {
        btnStop.setEnabled(true);
    }

    public void disableStop() {
        btnStop.setEnabled(false);
    }
}
