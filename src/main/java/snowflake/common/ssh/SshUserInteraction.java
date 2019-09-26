package snowflake.common.ssh;

import com.jcraft.jsch.*;
import snowflake.App;
import snowflake.components.newsession.SessionInfo;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class SshUserInteraction extends AbstractUserInteraction {
    private JRootPane rootPane;

    public SshUserInteraction(SessionInfo info, JRootPane rootPane) {
        super(info);
        this.rootPane = rootPane;
    }

    protected boolean showModal(List<JComponent> components, boolean yesNo) {
        JPanel panel = new JPanel();
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }
        });
        Box box = Box.createVerticalBox();
        box.setOpaque(true);
        for (JComponent c : components) {
            c.setAlignmentX(Box.LEFT_ALIGNMENT);
            box.add(c);
        }

        JButton btnOk = new JButton("OK");
        JButton btnCancel = null;

        if (yesNo) {
            btnCancel = new JButton("Cancel");
        }

        final AtomicBoolean isOk = new AtomicBoolean(false);

        btnOk.addActionListener(e -> {
            synchronized (this) {
                panel.setVisible(false);
                isOk.set(true);
                this.notify();
            }
        });

        if (yesNo) {
            btnCancel.addActionListener(e -> {
                synchronized (this) {
                    panel.setVisible(false);
                    isOk.set(false);
                    this.notify();
                }
            });
        }


        Box b1 = Box.createHorizontalBox();
        b1.add(Box.createHorizontalGlue());
        b1.add(btnOk);
        if (yesNo) {
            b1.add(btnCancel);
        }

        b1.setAlignmentX(Box.LEFT_ALIGNMENT);
        box.add(b1);


        panel.add(box);
        panel.setOpaque(false);

        SwingUtilities.invokeLater(() -> {
            System.out.println("Root pane: " + rootPane);
            rootPane.getGlassPane().setVisible(false);
            rootPane.setGlassPane(panel);
            panel.setVisible(true);
            System.out.println("Prompt made visible");
        });

        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return isOk.get();
    }
}
