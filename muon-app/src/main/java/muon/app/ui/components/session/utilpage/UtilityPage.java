/**
 * 
 */
package muon.app.ui.components.session.utilpage;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

import muon.app.App;
import muon.app.ui.components.SkinnedScrollPane;
import muon.app.ui.components.session.Page;
import muon.app.ui.components.session.SessionContentPanel;
import muon.app.ui.components.session.utilpage.keys.KeyPage;
import muon.app.ui.components.session.utilpage.nettools.NetworkToolsPage;
import muon.app.ui.components.session.utilpage.portview.PortViewer;
import muon.app.ui.components.session.utilpage.services.ServicePanel;
import muon.app.ui.components.session.utilpage.sysinfo.SysInfoPanel;
import muon.app.ui.components.session.utilpage.sysload.SysLoadPage;
import util.FontAwesomeContants;
import util.LayoutUtilities;

/**
 * @author subhro
 *
 */
public class UtilityPage extends Page {
	private CardLayout cardLayout;
	private JPanel cardPanel;
	private AtomicBoolean init = new AtomicBoolean(false);
	private SessionContentPanel holder;

	/**
	 * 
	 */
	public UtilityPage(SessionContentPanel holder) {
		super(new BorderLayout());
		this.holder = holder;
	}

	@Override
	public void onLoad() {
		if (!init.get()) {
			init.set(true);
			createUI();
		}
	}

	@Override
	public String getIcon() {
		return FontAwesomeContants.FA_BRIEFCASE;
		// return FontAwesomeContants.FA_SLIDERS;
	}

	@Override
	public String getText() {
		return "Toolbox";
	}

	/**
	 * 
	 */
	private void createUI() {
		ButtonGroup bg = new ButtonGroup();
		Box vbox = Box.createVerticalBox();
		UtilityPageButton b1 = new UtilityPageButton("System info",
				FontAwesomeContants.FA_LINUX);

		UtilityPageButton b2 = new UtilityPageButton("System load",
				FontAwesomeContants.FA_AREA_CHART);

		UtilityPageButton b3 = new UtilityPageButton("Services - systemd",
				FontAwesomeContants.FA_SERVER);

		UtilityPageButton b4 = new UtilityPageButton("Process and ports",
				FontAwesomeContants.FA_DATABASE);

		UtilityPageButton b5 = new UtilityPageButton("SSH keys",
				FontAwesomeContants.FA_KEY);

		UtilityPageButton b6 = new UtilityPageButton("Network tools",
				FontAwesomeContants.FA_WRENCH);

		LayoutUtilities.equalizeSize(b1, b2, b3, b4, b5, b6);

		vbox.setBorder(
				new MatteBorder(0, 0, 0, 1, App.SKIN.getDefaultBorderColor()));

		b1.setAlignmentX(Box.LEFT_ALIGNMENT);
		vbox.add(b1);

		b2.setAlignmentX(Box.LEFT_ALIGNMENT);
		vbox.add(b2);

		b3.setAlignmentX(Box.LEFT_ALIGNMENT);
		vbox.add(b3);

		b5.setAlignmentX(Box.LEFT_ALIGNMENT);
		vbox.add(b4);

		b5.setAlignmentX(Box.LEFT_ALIGNMENT);
		vbox.add(b5);

		b6.setAlignmentX(Box.LEFT_ALIGNMENT);
		vbox.add(b6);

		vbox.add(Box.createVerticalGlue());

		bg.add(b1);
		bg.add(b2);
		bg.add(b3);
		bg.add(b4);
		bg.add(b5);
		bg.add(b6);

		JScrollPane jsp = new SkinnedScrollPane(vbox);
		jsp.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		this.add(jsp, BorderLayout.WEST);

		b1.setSelected(true);

		revalidate();
		repaint();

		b1.addActionListener(e -> {
			cardLayout.show(cardPanel, "SYS_INFO");
		});

		b2.addActionListener(e -> {
			cardLayout.show(cardPanel, "SYS_LOAD");
		});

		b3.addActionListener(e -> {
			cardLayout.show(cardPanel, "SYSTEMD_SERVICES");
		});

		b4.addActionListener(e -> {
			cardLayout.show(cardPanel, "PROC_PORT");
		});

		b5.addActionListener(e -> {
			cardLayout.show(cardPanel, "SSH_KEYS");
		});

		b6.addActionListener(e -> {
			cardLayout.show(cardPanel, "NET_TOOLS");
		});

		JPanel p1 = new SysInfoPanel(holder);
		JPanel p2 = new SysLoadPage(holder);
		JPanel p3 = new ServicePanel(holder);
		JPanel p4 = new PortViewer(holder);
		JPanel p5 = new KeyPage(holder);
		JPanel p6 = new NetworkToolsPage(holder);

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);

		cardPanel.add(p1, "SYS_INFO");
		cardPanel.add(p2, "SYS_LOAD");
		cardPanel.add(p3, "SYSTEMD_SERVICES");
		cardPanel.add(p4, "PROC_PORT");
		cardPanel.add(p5, "SSH_KEYS");
		cardPanel.add(p6, "NET_TOOLS");

		this.add(cardPanel);
	}

}
