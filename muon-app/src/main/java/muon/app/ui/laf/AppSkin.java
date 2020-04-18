/**
 * 
 */
package muon.app.ui.laf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import muon.app.ui.components.RoundedButtonPainter;

/**
 * @author subhro
 *
 */
public abstract class AppSkin {
	protected UIDefaults defaults;
	protected NimbusLookAndFeel laf;

	/**
	 * 
	 */
	public AppSkin() {
		initDefaults();
	}

	private void initDefaults() {
		this.laf = new NimbusLookAndFeel();
		this.defaults = this.laf.getDefaults();

		this.defaults.put("defaultFont", loadFonts());
		this.defaults.put("iconFont", loadFontAwesomeFonts());
		this.defaults.put("defaultStroke", new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		this.defaults.put("ScrollPane.contentMargins", new Insets(0, 0, 0, 0));
		// this.defaults.put("PopupMenu.contentMargins", new Insets(0, 0, 0,
		// 0));

		Painter<? extends JComponent> scrollPaneBorderPainter = (graphics, component, width, height) -> {
			graphics.setColor(defaults.getColor("control"));
			graphics.setStroke((BasicStroke) defaults.get("defaultStroke"));
			graphics.drawRect(0, 0, width, height);
		};

		this.defaults.put("ScrollPane[Enabled+Focused].borderPainter", scrollPaneBorderPainter);
		this.defaults.put("ScrollPane[Enabled].borderPainter", scrollPaneBorderPainter);

		this.defaults.put("ScrollBar.width", 7);

		Painter<? extends JComponent> treeCellFocusPainter = (g, object, width, height) -> {
		};
		this.defaults.put("Tree:TreeCell[Enabled+Focused].backgroundPainter", treeCellFocusPainter);
	}

	/**
	 * @return the laf
	 */
	public NimbusLookAndFeel getLaf() {
		return laf;
	}

	protected Font loadFonts() {
		try (InputStream is = AppSkin.class

				// .getResourceAsStream("/fonts/Verdana.ttf")) {
				.getResourceAsStream("/fonts/Helvetica.ttf")) {
			// .getResourceAsStream("/fonts/DejaVuSans.ttf")) {
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			// System.out.println("App font: "+font.getFamily()+"
			// "+font.getFontName());
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
			return font.deriveFont(Font.PLAIN, 12.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected Font loadFontAwesomeFonts() {
		try (InputStream is = AppSkin.class.getResourceAsStream("/fonts/fontawesome-webfont.ttf")) {
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			return font.deriveFont(Font.PLAIN, 14f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Color getDefaultBackground() {
		return this.defaults.getColor("control");
	}

	public Color getDefaultForeground() {
		return this.defaults.getColor("text");
	}

	public Color getDefaultSelectionForeground() {
		return this.defaults.getColor("nimbusSelectedText");
	}

	public Color getDefaultSelectionBackground() {
		return this.defaults.getColor("nimbusSelectionBackground");
	}

	public Color getDefaultBorderColor() {
		return this.defaults.getColor("nimbusBorder");
	}

	public Font getIconFont() {
		return UIManager.getFont("iconFont");
	}

	public Font getDefaultFont() {
		return UIManager.getFont("defaultFont");
	}

	public Color getInfoTextForeground() {
		return this.defaults.getColor("infoText");
	}

	public Color getAddressBarSelectionBackground() {
		return this.defaults.getColor("scrollbar");
	}

	public Color getAddressBarRolloverBackground() {
		return this.defaults.getColor("scrollbar-hot");
	}

	public UIDefaults getSplitPaneSkin() {
		UIDefaults uiDefaults = new UIDefaults();
		Painter<? extends Object> painter = new Painter<Object>() {
			@Override
			public void paint(Graphics2D g, Object object, int width, int height) {
				g.setColor(defaults.getColor("control"));
				g.fill(new Rectangle(0, 0, width, height));
			}
		};

		for (String key : new String[] { "SplitPane:SplitPaneDivider[Enabled].backgroundPainter",
				"SplitPane:SplitPaneDivider[Enabled+Vertical].foregroundPainter",
				"SplitPane:SplitPaneDivider[Enabled].backgroundPainter",
				"SplitPane:SplitPaneDivider[Enabled].foregroundPainter",
				"SplitPane:SplitPaneDivider[Focused].backgroundPainter",
				"SplitPane:SplitPaneDivider[Enabled].foregroundPainter",
				"SplitPane:SplitPaneDivider[Enabled].foregroundPainter" }) {
			uiDefaults.put(key, painter);
		}

		uiDefaults.put("SplitPane.contentMargins", new Insets(0, 0, 0, 0));
		uiDefaults.put("SplitPane.background", defaults.getColor("control"));

		uiDefaults.put("background", defaults.getColor("control"));
		uiDefaults.put("controlDkShadow", defaults.getColor("control"));
		uiDefaults.put("controlHighlight", defaults.getColor("control"));

		uiDefaults.put("menu", defaults.getColor("control"));
		uiDefaults.put("nimbusBlueGrey", defaults.getColor("control"));
		uiDefaults.put("controlHighlight", defaults.getColor("control"));

		return uiDefaults;
	}

	public void createSkinnedButton(UIDefaults btnSkin) {
		RoundedButtonPainter cs = new RoundedButtonPainter(btnSkin);
		btnSkin.put("Button.contentMargins", new Insets(8, 15, 8, 15));
		btnSkin.put("Button[Default+Focused+MouseOver].backgroundPainter", cs.getHotPainter());
		btnSkin.put("Button[Default+Focused+Pressed].backgroundPainter", cs.getPressedPainter());
		btnSkin.put("Button[Default+Focused].backgroundPainter", cs.getNormalPainter());
		btnSkin.put("Button[Default+MouseOver].backgroundPainter", cs.getHotPainter());
		btnSkin.put("Button[Default+Pressed].backgroundPainter", cs.getPressedPainter());
		btnSkin.put("Button[Default].backgroundPainter", cs.getNormalPainter());
		btnSkin.put("Button[Enabled].backgroundPainter", cs.getNormalPainter());
		btnSkin.put("Button[Focused+MouseOver].backgroundPainter", cs.getHotPainter());
		btnSkin.put("Button[Focused+Pressed].backgroundPainter", cs.getPressedPainter());
		btnSkin.put("Button[Focused].backgroundPainter", cs.getNormalPainter());
		btnSkin.put("Button[MouseOver].backgroundPainter", cs.getHotPainter());
		btnSkin.put("Button[Pressed].backgroundPainter", cs.getPressedPainter());
		btnSkin.put("Button[Default+Pressed].textForeground", defaults.getColor("control"));
		btnSkin.put("Button.foreground", defaults.getColor("control"));
		btnSkin.put("Button[Disabled].textForeground", Color.GRAY);
		btnSkin.put("Button[Disabled].backgroundPainter", cs.getNormalPainter());
	}

	public void createTextFieldSkin(UIDefaults uiDefaults) {
		final Color borderColor = defaults.getColor("nimbusBorder");// new
		// Color(230,
		// 230, 230);
		final Color focusedColor = defaults.getColor("nimbusSelectionBackground");
		Painter<? extends JComponent> focusedBorder = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(focusedColor);
				g.drawRoundRect(1, 1, width - 2, height - 2, 5, 5);
			}
		};

		Painter<? extends JComponent> normalBorder = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(borderColor);
				g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g.drawRoundRect(1, 1, width - 2, height - 2, 5, 5);
			}
		};

		uiDefaults.put("FormattedTextField[Disabled].borderPainter", normalBorder);
		uiDefaults.put("FormattedTextField[Enabled].borderPainter", normalBorder);
		uiDefaults.put("FormattedTextField[Focused].borderPainter", focusedBorder);

		uiDefaults.put("PasswordField[Disabled].borderPainter", normalBorder);
		uiDefaults.put("PasswordField[Enabled].borderPainter", normalBorder);
		uiDefaults.put("PasswordField[Focused].borderPainter", focusedBorder);

		uiDefaults.put("TextField[Disabled].borderPainter", normalBorder);
		uiDefaults.put("TextField[Enabled].borderPainter", normalBorder);
		uiDefaults.put("TextField[Focused].borderPainter", focusedBorder);

		uiDefaults.put("TextField.contentMargins", new Insets(8, 8, 8, 8));
		uiDefaults.put("PasswordField.contentMargins", new Insets(8, 8, 8, 8));
	}

	public void createSpinnerSkin(UIDefaults uiDefaults) {
		Color c1 = this.defaults.getColor("TextField.background");
		Color c2 = this.defaults.getColor("nimbusBorder");

		Painter<? extends JComponent> painter1 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(c1);
				g.fillRoundRect(1, 1, width - 2 + 20, height - 2, 5, 5);
				g.setColor(c2);
				g.drawRoundRect(1, 1, width - 2 + 20, height - 2, 5, 5);
			}
		};

		Painter<? extends JComponent> painter2 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(c1);
				g.fillRoundRect(1 - 20, 1, width - 2 + 20, height - 2 + 20, 5, 5);
				g.setColor(c2);
				g.drawRoundRect(1 - 20, 1, width - 2 + 20, height - 2 + 20, 5, 5);
			}
		};

		Painter<? extends JComponent> painter3 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(c1);
				g.fillRoundRect(1 - 20, 1 - 20, width - 2 + 20, height - 2 + 20, 5, 5);
				g.setColor(c2);
				g.drawRoundRect(1 - 20, 1 - 20, width - 2 + 20, height - 2 + 20, 5, 5);
			}
		};

		uiDefaults.put("Spinner:\"Spinner.nextButton\"[Disabled].backgroundPainter", painter2);
		uiDefaults.put("Spinner:\"Spinner.nextButton\"[Enabled].backgroundPainter", painter2);
		uiDefaults.put("Spinner:\"Spinner.nextButton\"[Focused+MouseOver].backgroundPainter", painter2);
		uiDefaults.put("Spinner:\"Spinner.nextButton\"[Focused+Pressed].backgroundPainter", painter2);
		uiDefaults.put("Spinner:\"Spinner.nextButton\"[Focused].backgroundPainter", painter2);
		uiDefaults.put("Spinner:\"Spinner.nextButton\"[MouseOver].backgroundPainter", painter2);
		uiDefaults.put("Spinner:\"Spinner.nextButton\"[Pressed].backgroundPainter", painter2);

		uiDefaults.put("Spinner:\"Spinner.previousButton\"[Disabled].backgroundPainter", painter3);
		uiDefaults.put("Spinner:\"Spinner.previousButton\"[Enabled].backgroundPainter", painter3);
		uiDefaults.put("Spinner:\"Spinner.previousButton\"[Focused+MouseOver].backgroundPainter", painter3);
		uiDefaults.put("Spinner:\"Spinner.previousButton\"[Focused+Pressed].backgroundPainter", painter3);
		uiDefaults.put("Spinner:\"Spinner.previousButton\"[Focused].backgroundPainter", painter3);
		uiDefaults.put("Spinner:\"Spinner.previousButton\"[MouseOver].backgroundPainter", painter3);
		uiDefaults.put("Spinner:\"Spinner.previousButton\"[Pressed].backgroundPainter", painter3);

		uiDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\"[Enabled].backgroundPainter", painter1);
		uiDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\"[Focused].backgroundPainter", painter1);
		uiDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\"[Focused+Selected].backgroundPainter", painter1);
		uiDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\".contentMargins", new Insets(7, 7, 7, 7));
	}

	public void createComboBoxSkin(UIDefaults uiDefaults) {
		Color c1 = this.defaults.getColor("nimbusBorder");
		Color c2 = this.defaults.getColor("TextField.background");
		Color c3 = this.defaults.getColor("nimbusSelectionBackground");
		Painter<? extends JComponent> painter1 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(c2);
				g.fillRoundRect(1, 1, width - 2, height - 2, 5, 5);
				g.setColor(c1);
				g.drawRoundRect(1, 1, width - 2, height - 2, 5, 5);
			}
		};

		Painter<? extends JComponent> painter2 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(c2);
				g.fillRoundRect(1, 1, width - 2, height - 2, 5, 5);
				g.setColor(c1);
				g.drawRoundRect(1, 1, width - 2, height - 2, 5, 5);
			}
		};

		Painter<? extends JComponent> painter3 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(c2);
				g.fillRoundRect(1, 1, width - 2, height - 2, 5, 5);
				g.setColor(c1);
				g.drawRoundRect(1, 1, width - 2, height - 2, 5, 5);
			}
		};

		Painter<? extends JComponent> painter4 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(c2);
				g.fillRoundRect(1, 1, width - 2, height - 2, 5, 5);
				g.setColor(c1);
				g.drawRoundRect(1, 1, width - 2, height - 2, 5, 5);
			}
		};

		Painter<? extends JComponent> painter5 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(c2);
				g.fillRoundRect(1, 1, width - 2, height - 2, 5, 5);
				g.setColor(c1);
				g.drawRoundRect(1, 1, width - 2, height - 2, 5, 5);
			}
		};
		uiDefaults.put("ComboBox:\"ComboBox.textField\"[Enabled].backgroundPainter", painter3);
		uiDefaults.put("ComboBox:\"ComboBox.textField\"[Selected].backgroundPainter", painter3);
		uiDefaults.put("ComboBox[Enabled].backgroundPainter", painter4);
		uiDefaults.put("ComboBox[Focused+MouseOver].backgroundPainter", painter5);
		uiDefaults.put("ComboBox[Focused+Pressed].backgroundPainter", painter5);
		uiDefaults.put("ComboBox[Focused].backgroundPainter", painter4);
		uiDefaults.put("ComboBox[MouseOver].backgroundPainter", painter1);
		uiDefaults.put("ComboBox[Pressed].backgroundPainter", painter1);
		uiDefaults.put("ComboBox[Editable+Focused].backgroundPainter", painter4);
		uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Editable+Enabled].backgroundPainter", painter3);
		uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Editable+MouseOver].backgroundPainter", painter2);
		uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Editable+Pressed].backgroundPainter", painter2);
		uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Editable+Selected].backgroundPainter", painter3);
		uiDefaults.put("ComboBox.contentMargins", new Insets(3, 5, 3, 5));
		uiDefaults.put("ComboBox:\"ComboBox.listRenderer\".contentMargins", new Insets(3, 5, 3, 5));
		uiDefaults.put("ComboBox.rendererUseListColors", Boolean.TRUE);
	}

	public void createTreeSkin(UIDefaults uiDefaults) {
		uiDefaults.put("Tree[Enabled].closedIconPainter", new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				Font font = g.getFont();
				g.setColor(defaults.getColor("Tree.textForeground"));
				g.setFont(getIconFont().deriveFont(16));
				int h = g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();

				g.drawString("\uf07b", 0, h);
				g.setFont(font);
			}
		});

		uiDefaults.put("Tree[Enabled].openIconPainter", new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setColor(defaults.getColor("Tree.textForeground"));
				Font font = g.getFont();
				g.setFont(getIconFont().deriveFont(16));
				int h = g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();

				g.drawString("\uf07c", 0, h);
				g.setFont(font);
			}
		});

		uiDefaults.put("Tree[Enabled].leafIconPainter", new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setColor(defaults.getColor("Tree.textForeground"));
				Font font = g.getFont();
				g.setFont(getIconFont().deriveFont(16));
				int h = g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();

				g.drawString("\uf15b", 0, h);
				g.setFont(font);
			}
		});
		uiDefaults.put("Tree.rendererMargins", new Insets(5, 5, 5, 5));
	}

	public UIDefaults createToolbarSkin() {
		UIDefaults toolBarButtonSkin = new UIDefaults();
		Painter<JButton> toolBarButtonPainterNormal = new Painter<JButton>() {
			@Override
			public void paint(Graphics2D g, JButton object, int width, int height) {
				g.setColor(UIManager.getColor("control"));
				g.fillRect(0, 0, width, height);
			}
		};

		Painter<JButton> toolBarButtonPainterHot = new Painter<JButton>() {
			@Override
			public void paint(Graphics2D g, JButton object, int width, int height) {
				g.setColor(UIManager.getColor("scrollbar-hot"));
				g.fillRect(0, 0, width, height);
			}
		};

		Painter<JButton> toolBarButtonPainterPressed = new Painter<JButton>() {
			@Override
			public void paint(Graphics2D g, JButton object, int width, int height) {
				g.setColor(UIManager.getColor("scrollbar"));
				g.fillRect(0, 0, width, height);
			}
		};

		toolBarButtonSkin.put("Button.contentMargins", new Insets(5, 8, 5, 8));

		toolBarButtonSkin.put("Button[Disabled].backgroundPainter", toolBarButtonPainterNormal);
		toolBarButtonSkin.put("Button[Disabled].textForeground", Color.LIGHT_GRAY);

		toolBarButtonSkin.put("Button.foreground", UIManager.getColor("scrollbar"));

		toolBarButtonSkin.put("Button[Enabled].backgroundPainter", toolBarButtonPainterNormal);
		toolBarButtonSkin.put("Button[Focused].backgroundPainter", toolBarButtonPainterNormal);
		toolBarButtonSkin.put("Button[Default].backgroundPainter", toolBarButtonPainterNormal);
		toolBarButtonSkin.put("Button[Default+Focused].backgroundPainter", toolBarButtonPainterNormal);

		toolBarButtonSkin.put("Button[Pressed].backgroundPainter", toolBarButtonPainterPressed);
		toolBarButtonSkin.put("Button[Focused+Pressed].backgroundPainter", toolBarButtonPainterPressed);
		toolBarButtonSkin.put("Button[Default+Focused+Pressed].backgroundPainter", toolBarButtonPainterPressed);
		toolBarButtonSkin.put("Button[Default+Pressed].backgroundPainter", toolBarButtonPainterPressed);

		toolBarButtonSkin.put("Button[MouseOver].backgroundPainter", toolBarButtonPainterHot);
		toolBarButtonSkin.put("Button[Focused+MouseOver].backgroundPainter", toolBarButtonPainterHot);
		toolBarButtonSkin.put("Button[Default+MouseOver].backgroundPainter", toolBarButtonPainterHot);
		toolBarButtonSkin.put("Button[Default+Focused+MouseOver].backgroundPainter", toolBarButtonPainterHot);

		return toolBarButtonSkin;
	}

	public UIDefaults createTabButtonSkin() {
		UIDefaults toolBarButtonSkin = new UIDefaults();
		Painter<JButton> toolBarButtonPainterNormal = new Painter<JButton>() {
			@Override
			public void paint(Graphics2D g, JButton object, int width, int height) {
				g.setColor(getDefaultBackground());
				g.fillRect(0, 0, width, height);
			}
		};

		Painter<JButton> toolBarButtonPainterHot = new Painter<JButton>() {
			@Override
			public void paint(Graphics2D g, JButton object, int width, int height) {
				g.setColor(getSelectedTabColor());
				g.fillRect(0, 0, width, height);
			}
		};

		Painter<JButton> toolBarButtonPainterPressed = new Painter<JButton>() {
			@Override
			public void paint(Graphics2D g, JButton object, int width, int height) {
				g.setColor(UIManager.getColor("scrollbar"));
				g.fillRect(0, 0, width, height);
			}
		};

		toolBarButtonSkin.put("Button.contentMargins", new Insets(5, 8, 5, 8));

		toolBarButtonSkin.put("Button[Disabled].backgroundPainter", toolBarButtonPainterNormal);
		toolBarButtonSkin.put("Button[Disabled].textForeground", Color.LIGHT_GRAY);

		toolBarButtonSkin.put("Button[Enabled].backgroundPainter", toolBarButtonPainterNormal);
		toolBarButtonSkin.put("Button[Focused].backgroundPainter", toolBarButtonPainterNormal);
		toolBarButtonSkin.put("Button[Default].backgroundPainter", toolBarButtonPainterNormal);
		toolBarButtonSkin.put("Button[Default+Focused].backgroundPainter", toolBarButtonPainterNormal);

		toolBarButtonSkin.put("Button[Pressed].backgroundPainter", toolBarButtonPainterPressed);
		toolBarButtonSkin.put("Button[Focused+Pressed].backgroundPainter", toolBarButtonPainterPressed);
		toolBarButtonSkin.put("Button[Default+Focused+Pressed].backgroundPainter", toolBarButtonPainterPressed);
		toolBarButtonSkin.put("Button[Default+Pressed].backgroundPainter", toolBarButtonPainterPressed);

		toolBarButtonSkin.put("Button[MouseOver].backgroundPainter", toolBarButtonPainterHot);
		toolBarButtonSkin.put("Button[Focused+MouseOver].backgroundPainter", toolBarButtonPainterHot);
		toolBarButtonSkin.put("Button[Default+MouseOver].backgroundPainter", toolBarButtonPainterHot);
		toolBarButtonSkin.put("Button[Default+Focused+MouseOver].backgroundPainter", toolBarButtonPainterHot);

		return toolBarButtonSkin;
	}

	public void createTableHeaderSkin(UIDefaults uiDefaults) {
		// Color c1 = defaults.getColor("nimbusBorder");
		Painter<? extends Object> painterNormal = (Graphics2D g, Object object, int width, int height) -> {
//			g.setColor(c1);
//			g.drawLine(width - 1, 3, width - 1, height - 6);
		};
		uiDefaults.put("TableHeader.font", new Font(Font.DIALOG, Font.PLAIN, 14));
		uiDefaults.put("TableHeader.background", defaults.getColor("control"));// new
																				// Color(240,
		// 240, 240));
		uiDefaults.put("TableHeader.foreground", defaults.getColor("text"));
		uiDefaults.put("TableHeader:\"TableHeader.renderer\".opaque", false);
		uiDefaults.put("TableHeader:\"TableHeader.renderer\"[Enabled+Focused+Sorted].backgroundPainter", painterNormal);
		uiDefaults.put("TableHeader:\"TableHeader.renderer\"[Enabled+Focused].backgroundPainter", painterNormal);
		uiDefaults.put("TableHeader:\"TableHeader.renderer\"[Enabled+Sorted].backgroundPainter", painterNormal);
		uiDefaults.put("TableHeader:\"TableHeader.renderer\"[Enabled].backgroundPainter", painterNormal);
		uiDefaults.put("TableHeader:\"TableHeader.renderer\"[MouseOver].backgroundPainter", painterNormal);
		uiDefaults.put("TableHeader:\"TableHeader.renderer\"[Pressed].backgroundPainter", painterNormal);
	}

	public void createPopupMenuSkin(UIDefaults uiDefaults) {
		Color controlColor = this.defaults.getColor("control");
		Color textColor = this.defaults.getColor("text");
		Color selectedTextColor = this.defaults.getColor("nimbusSelectedText");

		uiDefaults.put("PopupMenu.background", controlColor);
		uiDefaults.put("PopupMenu.foreground", textColor);

		uiDefaults.put("Menu.foreground", textColor);
		uiDefaults.put("Menu[Enabled].textForeground", textColor);
		uiDefaults.put("Menu[Enabled+Selected].textForeground", selectedTextColor);

		uiDefaults.put("Menu.contentMargins", new Insets(5, 10, 5, 10));
		uiDefaults.put("MenuItem.contentMargins", new Insets(5, 10, 5, 10));
		uiDefaults.put("MenuItem.foreground", textColor);
		uiDefaults.put("MenuItem[Enabled].textForeground", textColor);
		uiDefaults.put("MenuItem[MouseOver].textForeground", selectedTextColor);
		uiDefaults.put("MenuItem:MenuItemAccelerator[Disabled].textForeground", textColor);
		uiDefaults.put("MenuItem:MenuItemAccelerator[MouseOver].textForeground", textColor);

		Painter<? extends JComponent> popupPainter = (graphics, component, width, height) -> {
			graphics.setColor(this.defaults.getColor("control"));
			graphics.fillRect(0, 0, width, height);
		};

		uiDefaults.put("PopupMenu[Enabled].backgroundPainter", popupPainter);

		Painter<? extends JComponent> menuSelectionPainter = (graphics, component, width, height) -> {
			graphics.setColor(this.defaults.getColor("nimbusSelectionBackground"));
			graphics.fillRect(0, 0, width, height);
		};

		uiDefaults.put("MenuItem[MouseOver].backgroundPainter", menuSelectionPainter);
	}

	public Color getTableBackgroundColor() {
		return defaults.getColor("Table.background");
	}

	public Color getSelectedTabColor() {
		return defaults.getColor("button.pressedGradient2");
	}

	public Color getTextFieldBackground() {
		return defaults.getColor("TextField.background");
	}

	public void createCheckboxSkin(UIDefaults uiDefaults) {
		Color c1 = defaults.getColor("text");
		Color c2 = defaults.getColor("text");

		Painter<? extends JComponent> painter1 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g.setColor(c1);
				g.drawRect(2, 2, width - 4, height - 4);
			}
		};

		Painter<? extends JComponent> painter2 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g.setColor(c2);
				g.drawRect(2, 2, width - 4, height - 4);
				g.fillRect(4, 4, width - 8, height - 8);
			}
		};

		uiDefaults.put("CheckBox[Disabled].iconPainter", painter1);
		uiDefaults.put("CheckBox[Enabled].iconPainter", painter1);
		uiDefaults.put("CheckBox[Focused+MouseOver+Selected].iconPainter", painter2);
		uiDefaults.put("CheckBox[Focused+Pressed+Selected].iconPainter", painter2);
		uiDefaults.put("CheckBox[Focused+Selected].iconPainter", painter2);
		uiDefaults.put("CheckBox[MouseOver+Selected].iconPainter", painter2);
		uiDefaults.put("CheckBox[Pressed+Selected].iconPainter", painter2);

		uiDefaults.put("CheckBox[Focused+MouseOver].iconPainter", painter1);
		uiDefaults.put("CheckBox[Focused+Pressed].iconPainter", painter1);
		uiDefaults.put("CheckBox[Pressed].iconPainter", painter1);
		uiDefaults.put("CheckBox[Selected].iconPainter", painter2);
		uiDefaults.put("CheckBox[Focused].iconPainter", painter1);
		uiDefaults.put("CheckBox[MouseOver].iconPainter", painter1);
	}

	public void createRadioButtonSkin(UIDefaults uiDefaults) {
		Color c1 = defaults.getColor("text");
		Color c2 = defaults.getColor("text");

		Painter<? extends JComponent> painter1 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g.setColor(c1);
				g.drawOval(0, 0, width, height);
			}
		};

		Painter<? extends JComponent> painter2 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(c2);
				g.drawOval(0, 0, width, height);
				g.fillOval(4, 4, width - 8, height - 8);
			}
		};

		uiDefaults.put("RadioButton[Disabled].iconPainter", painter1);
		uiDefaults.put("RadioButton[Enabled].iconPainter", painter1);
		uiDefaults.put("RadioButton[Focused+MouseOver+Selected].iconPainter", painter2);
		uiDefaults.put("RadioButton[Focused+Pressed+Selected].iconPainter", painter2);
		uiDefaults.put("RadioButton[Focused+Selected].iconPainter", painter2);
		uiDefaults.put("RadioButton[MouseOver+Selected].iconPainter", painter2);
		uiDefaults.put("RadioButton[Pressed+Selected].iconPainter", painter2);

		uiDefaults.put("RadioButton[Focused+MouseOver].iconPainter", painter1);
		uiDefaults.put("RadioButton[Focused+Pressed].iconPainter", painter1);
		uiDefaults.put("RadioButton[Pressed].iconPainter", painter1);
		uiDefaults.put("RadioButton[Selected].iconPainter", painter2);
		uiDefaults.put("RadioButton[Focused].iconPainter", painter1);
		uiDefaults.put("RadioButton[MouseOver].iconPainter", painter1);
	}

	public void createTooltipSkin(UIDefaults uiDefaults) {
		Color c1 = defaults.getColor("Tree.background");
		Painter<? extends JComponent> painter2 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width, int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(c1);
				g.fillRect(0, 0, width, height);
			}
		};
		uiDefaults.put("ToolTip[Enabled].backgroundPainter", painter2);
		uiDefaults.put("ToolTip.background", defaults.getColor("control"));
		uiDefaults.put("ToolTip.foreground", defaults.getColor("text"));
	}

}
