package snowflake.components.diskusage;

import snowflake.components.diskusage.treetable.AbstractTreeTableModel;

public class DiskUsageTableModel extends AbstractTreeTableModel {
    private String[] columns = new String[]{"Name", "Size"};

    public DiskUsageTableModel(Object root) {
        super(root);
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(Object node, int column) {
        DiskUsageEntry entry = (DiskUsageEntry) node;
        if (column == 0) {
            return entry.getName();
        } else {
            return entry.getSize();
        }
    }

    @Override
    public Object getChild(Object parent, int index) {
        DiskUsageEntry entry = (DiskUsageEntry) parent;
        return entry.getChildren().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        DiskUsageEntry entry = (DiskUsageEntry) parent;
        System.out.println(entry);
        System.out.println("Child count: " + entry.getChildren().size());
        return entry.getChildren().size();
    }
}
