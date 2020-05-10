/**
 * 
 */
package muon.app.ui.components.session.processview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import muon.app.App;
import muon.app.ssh.RemoteSessionInstance;
import muon.app.ui.components.session.Page;
import muon.app.ui.components.session.SessionContentPanel;
import muon.app.ui.components.session.processview.ProcessListPanel.CommandMode;
import util.FontAwesomeContants;
import util.ScriptLoader;
import util.SudoUtils;

/**
 * @author subhro
 *
 */
public class ProcessViewer extends Page {
	private SessionContentPanel holder;
	private AtomicBoolean init = new AtomicBoolean(false);
	private ProcessListPanel processListPanel;
	private AtomicBoolean processListLoaded = new AtomicBoolean(false);

	/**
	 * 
	 */
	public ProcessViewer(SessionContentPanel holder) {
		super(new BorderLayout());
		this.holder = holder;
	}

	@Override
	public void onLoad() {
		if (!init.get()) {
			init.set(true);
			createUI();
		}
		if (!processListLoaded.get()) {
			processListLoaded.set(true);
			runCommand(null, CommandMode.LIST_PROCESS);
		}
	}

	/**
	 * 
	 */
	private void createUI() {
		processListPanel = new ProcessListPanel((cmd, mode) -> {
			this.runCommand(cmd, mode);
		});
		processListPanel.setMinimumSize(new Dimension(10, 10));
		this.add(processListPanel);
	}

	@Override
	public String getIcon() {
		return FontAwesomeContants.FA_COGS;
	}

	@Override
	public String getText() {
		return "Processes";
	}

	private void updateProcessList(AtomicBoolean stopFlag) {
		try {
			List<ProcessTableEntry> list = getProcessList(holder.getRemoteSessionInstance(), stopFlag);
			SwingUtilities.invokeLater(() -> {
				// update ui ps
				processListPanel.setProcessList(list);
				// processListPanel.activateProcessListPanel();
			});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void runCommand(String cmd, CommandMode mode) {
		AtomicBoolean stopFlag = new AtomicBoolean(false);
		holder.disableUi(stopFlag);
		switch (mode) {
		case KILL_AS_USER:
			holder.EXECUTOR.execute(() -> {
				try {
					if (holder.getRemoteSessionInstance().exec(cmd, stopFlag, new StringBuilder(),
							new StringBuilder()) != 0) {
						if (!holder.isSessionClosed()) {
							JOptionPane.showMessageDialog(null, "Operation failed");
						}
						// JOptionPane.showMessageDialog(this, "Operation
						// failed");
					} else {
						updateProcessList(stopFlag);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				holder.enableUi();
			});

			break;
		case KILL_AS_ROOT:
			holder.EXECUTOR.execute(() -> {
				if (SudoUtils.runSudo(cmd, holder.getRemoteSessionInstance()) != 0) {
					if (!holder.isSessionClosed()) {
						JOptionPane.showMessageDialog(null, "Operation failed");
					}
				} else {
					updateProcessList(stopFlag);
				}
				holder.enableUi();
			});

			break;
		case LIST_PROCESS:
			holder.EXECUTOR.execute(() -> {
				updateProcessList(stopFlag);
				holder.enableUi();
			});
			break;
		}
	}

	public List<ProcessTableEntry> getProcessList(RemoteSessionInstance instance, AtomicBoolean stopFlag)
			throws Exception {
		StringBuilder out = new StringBuilder(), err = new StringBuilder();
		int ret = instance.exec(ScriptLoader.loadShellScript("/scripts/ps.sh"),
				// "ps -e -o pid=pid -o pcpu -o rss -o etime -o ppid -o user -o nice -o args -ww
				// --sort pid",
				stopFlag, out, err);
		if (ret != 0)
			throw new Exception("Error while getting metrics");
		// System.out.println(new String(bout.toByteArray()));
		return parseProcessList(out.toString());
	}

	private List<ProcessTableEntry> parseProcessList(String text) {
		List<ProcessTableEntry> list = new ArrayList<>();
		String lines[] = text.split("\n");
		boolean first = true;
		for (String line : lines) {
			if (first) {
				first = false;
				continue;
			}
			String p[] = line.trim().split("\\s+");
			if (p.length < 8) {
				continue;
			}

			ProcessTableEntry ent = new ProcessTableEntry();
			try {
				ent.setPid(Integer.parseInt(p[0].trim()));
			} catch (Exception e) {
			}
			try {
				ent.setCpu(Float.parseFloat(p[1].trim()));
			} catch (Exception e) {
			}
			try {
				ent.setMemory(Float.parseFloat(p[2].trim()));
			} catch (Exception e) {
			}
			ent.setTime(p[3]);
			try {
				ent.setPpid(Integer.parseInt(p[4].trim()));
			} catch (Exception e) {
			}
			ent.setUser(p[5]);
			try {
				ent.setNice(Integer.parseInt(p[6].trim()));
			} catch (Exception e) {
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 7; i < p.length; i++) {
				sb.append(p[i] + " ");
			}
			ent.setArgs(sb.toString().trim());
			list.add(ent);
		}
		return list;
	}

}
