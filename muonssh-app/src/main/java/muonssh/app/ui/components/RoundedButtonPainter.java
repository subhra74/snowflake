package muonssh.app.ui.components;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.Painter;
import javax.swing.UIDefaults;

public class RoundedButtonPainter {
	private final Painter<AbstractButton> normalPainter;
    private final Painter<AbstractButton> hotPainter;
    private final Painter<AbstractButton> pressedPainter;
	private final GradientPaint normalGradient;
    private final GradientPaint hotGradient;
    private final GradientPaint pressedGradient;
	private final Color borderColor;

	public RoundedButtonPainter(UIDefaults defaults) {
		this.normalGradient = new GradientPaint(0, 0,
				defaults.getColor("button.normalGradient1"), 0, 50,
				defaults.getColor("button.normalGradient2"));
		this.hotGradient = new GradientPaint(0, 0,
				defaults.getColor("button.hotGradient1"), 0, 50,
				defaults.getColor("button.hotGradient2"));
		this.pressedGradient = new GradientPaint(0, 0,
				defaults.getColor("button.pressedGradient1"), 0, 50,
				defaults.getColor("button.pressedGradient2"));
		this.borderColor = defaults.getColor("nimbusBorder");

		normalPainter = (Graphics2D g, AbstractButton object, int width,
				int height) -> {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			if (object.isEnabled()) {
				g.setPaint(normalGradient);
			} else {
				g.setPaint(pressedGradient);
			}
			g.fillRoundRect(1, 1, width - 2, height - 2, 7, 7);
			g.setColor(borderColor);
			g.drawRoundRect(1, 1, width - 2, height - 2, 7, 7);
		};

		hotPainter = (Graphics2D g, AbstractButton object, int width, int height) -> {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setPaint(hotGradient);
			g.fillRoundRect(1, 1, width - 2, height - 2, 7, 7);
			g.setColor(borderColor);
			g.drawRoundRect(1, 1, width - 2, height - 2, 7, 7);
		};

		pressedPainter = (Graphics2D g, AbstractButton object, int width,
				int height) -> {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setPaint(pressedGradient);
			g.fillRoundRect(1, 1, width - 2, height - 2, 7, 7);
			g.setColor(borderColor);
			g.drawRoundRect(1, 1, width - 2, height - 2, 7, 7);
		};
	}

	public Painter<AbstractButton> getNormalPainter() {
		return normalPainter;
	}

	public Painter<AbstractButton> getHotPainter() {
		return hotPainter;
	}

	public Painter<AbstractButton> getPressedPainter() {
		return pressedPainter;
	}
}
