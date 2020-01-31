package snowflake.components.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ModalGlassPanel extends JPanel {
	private AlphaComposite alphaComposite = AlphaComposite.SrcOver
			.derive(0.65f);

	public ModalGlassPanel(JButton btnOk, JButton btnCancel,
			List<JComponent> components) {
		BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(layout);
		setOpaque(false);
		add(Box.createVerticalGlue());

		Box box = Box.createVerticalBox();
		box.setBorder(new EmptyBorder(10, 10, 10, 10));
		box.setAlignmentX(Box.CENTER_ALIGNMENT);
		box.setOpaque(true);
		for (JComponent c : components) {
			c.setAlignmentX(Box.LEFT_ALIGNMENT);
			box.add(c);
			box.add(Box.createVerticalStrut(10));
		}

		Box b1 = Box.createHorizontalBox();
		b1.add(Box.createHorizontalGlue());
		b1.add(btnOk);
		if (btnCancel != null) {
			b1.add(Box.createHorizontalStrut(10));
			b1.add(btnCancel);
		}

		b1.setAlignmentX(Box.LEFT_ALIGNMENT);
		box.add(b1);

		box.setMaximumSize(box.getPreferredSize());

		add(box);

		add(Box.createVerticalGlue());

		addMouseListener(new MouseAdapter() {
		});
		addMouseMotionListener(new MouseAdapter() {
		});
		addKeyListener(new KeyAdapter() {
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				requestFocusInWindow();
				// rootPane.requestFocusInWindow();
				for (JComponent c : components) {
					if (c instanceof JPasswordField) {
						// System.out.println("Requesting focus");
						c.requestFocusInWindow();
						if (btnOk != null) {
							((JPasswordField) c).addActionListener(ev -> {
								btnOk.doClick();
							});
						}
						break;
					}
				}
			}
		});
		setFocusTraversalKeysEnabled(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// g2.setComposite(alphaComposite);
		g2.setColor(new Color(0, 0, 0, 0.5f));
		g2.fillRect(0, 0, getWidth(), getHeight());
	}
}
