package muon.app.ui.components.session.utilpage.sysload;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import util.FormatUtils;

import java.awt.*;

public class SystemLoadPanel extends JPanel {
	private LineGraph cpuGraph, memGraph, swpGraph;
	private double cpuStats[] = new double[10];
	private double memStats[] = new double[10];
	private double swpStats[] = new double[10];
	private long totalMemory, usedMemory, totalSwap, usedSwap;
	private double cpuUsage, memoryUsage, swapUsage;
	private JLabel cpuLabel, memoryLabel, swapLabel;

	public SystemLoadPanel() {
		super(new BorderLayout(5, 5));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setMinimumSize(new Dimension(200, 100));
		setPreferredSize(new Dimension(300, 400));
		Box b1 = Box.createVerticalBox();

		cpuLabel = new JLabel("Cpu usage");
		cpuLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		cpuLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
		b1.add(cpuLabel);

		cpuGraph = new LineGraph();
		cpuGraph.setValues(cpuStats);
		cpuGraph.setAlignmentX(Box.LEFT_ALIGNMENT);
		b1.add(cpuGraph);

		memoryLabel = new JLabel("Memory usage");
		memoryLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
		memoryLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
		b1.add(memoryLabel);

		memGraph = new LineGraph();
		memGraph.setValues(memStats);
		memGraph.setAlignmentX(Box.LEFT_ALIGNMENT);
		b1.add(memGraph);

		swapLabel = new JLabel("Swap usage");
		swapLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
		swapLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
		b1.add(swapLabel);

		swpGraph = new LineGraph();
		swpGraph.setValues(swpStats);
		swpGraph.setAlignmentX(Box.LEFT_ALIGNMENT);
		b1.add(swpGraph);

//        add(new JLabel("System performance"), BorderLayout.NORTH);
		add(b1);
	}

	public void setCpuUsage(double cpuUsage) {
		this.cpuUsage = cpuUsage;
		if (this.cpuUsage != 0) {
			System.arraycopy(cpuStats, 1, cpuStats, 0, cpuStats.length - 1);
			cpuStats[cpuStats.length - 1] = cpuUsage;
		}
	}

	public void setMemoryUsage(double memoryUsage) {
		this.memoryUsage = memoryUsage;
		if (this.memoryUsage != 0) {
			System.arraycopy(memStats, 1, memStats, 0, memStats.length - 1);
			memStats[memStats.length - 1] = memoryUsage;
		}
	}

	public void setSwapUsage(double swapUsage) {
		this.swapUsage = swapUsage;
		if (this.swapUsage != 0) {
			System.arraycopy(swpStats, 1, swpStats, 0, swpStats.length - 1);
			swpStats[swpStats.length - 1] = swapUsage;
		}
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}

	public void setUsedMemory(long usedMemory) {
		this.usedMemory = usedMemory;
	}

	public void setTotalSwap(long totalSwap) {
		this.totalSwap = totalSwap;
	}

	public void setUsedSwap(long usedSwap) {
		this.usedSwap = usedSwap;
	}

	public void refreshUi() {
		this.cpuLabel
				.setText(String.format("Cpu usage: %.1f", cpuUsage) + "% ");
		this.memoryLabel.setText(String.format("Memory usage: %.1f",
				memoryUsage)
				+ "%"
				+ (totalMemory != 0
						? (", (Total: " + FormatUtils
								.humanReadableByteCount(totalMemory, true)
								+ ", Used: "
								+ FormatUtils.humanReadableByteCount(usedMemory,
										true)
								+ ")")
						: ""));
		this.swapLabel.setText(String.format("Swap usage: %.1f", swapUsage)
				+ "% "
				+ (totalSwap != 0
						? (", ( Total: "
								+ FormatUtils.humanReadableByteCount(totalSwap,
										true)
								+ ", Used: " + FormatUtils
										.humanReadableByteCount(usedSwap, true)
								+ ")")
						: ""));
		this.revalidate();
		this.repaint();
	}
}
