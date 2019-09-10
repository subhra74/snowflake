package snowflake.components.files.transfer;

import javax.swing.*;

public class TransferProgressPanel extends JPanel {
    private JProgressBar prg;
    private JButton stop;
    private FileTransfer transfer;
    private int dragSource;

    public TransferProgressPanel(FileTransfer transfer, int dragSource) {
        super();
        this.transfer = transfer;
        this.dragSource = dragSource;
        prg = new JProgressBar();
        add(prg);
        stop = new JButton("Stop");
        stop.addActionListener(e -> {
            transfer.stop();
        });
        add(stop);
    }

    public void clear() {
        prg.setValue(0);
    }

    public void setProgress(int prg) {
        this.prg.setValue(prg);
    }

    public int getSource() {
        return dragSource;
    }

    public void setSource(int source) {
        this.dragSource = source;
    }
}
