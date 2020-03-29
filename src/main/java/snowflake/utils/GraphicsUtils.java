package snowflake.utils;

import snowflake.App;
import snowflake.components.common.CustomButtonPainter;
import snowflake.components.common.CustomScrollBarUI;
import snowflake.components.common.RoundedButtonPainter;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class GraphicsUtils {
	private static Map<String, Color> colorMapLight = new HashMap<>();
	private static Map<String, Color> colorMapDark = new HashMap<>();
	private static Map<String, Color> colorMapCommon = new HashMap<>();

	static {
		colorMapCommon.put("selection-highlight", new Color(3, 155, 229));

		colorMapLight.put("tab-background", Color.WHITE);
		colorMapLight.put("tab-selection", new Color(240, 240, 240));
		colorMapLight.put("control", Color.WHITE);

		colorMapLight.put("text", Color.BLACK);
		colorMapLight.put("menuText", Color.BLACK);
		colorMapLight.put("controlText", Color.BLACK);
		colorMapLight.put("textForeground", Color.BLACK);
		colorMapLight.put("infoText", Color.BLACK);
		colorMapLight.put("List.foreground", Color.BLACK);
		colorMapLight.put("Label.foreground", Color.BLACK);

		colorMapLight.put("Tree.background", Color.WHITE);
		colorMapLight.put("Tree.textForeground", Color.GRAY);
		colorMapLight.put("nimbusBorder", new Color(230, 230, 230));
		colorMapLight.put("scrollbar", new Color(220, 220, 220));
		colorMapLight.put("scrollbar-hot", new Color(200, 200, 200));

		colorMapLight.put("button.normalGradient1", new Color(245, 245, 245));
		colorMapLight.put("button.normalGradient2", new Color(235, 235, 235));
		colorMapLight.put("button.hotGradient1", new Color(255, 255, 255));
		colorMapLight.put("button.hotGradient2", new Color(230, 230, 230));
		colorMapLight.put("button.pressedGradient1", new Color(230, 230, 230));
		colorMapLight.put("button.pressedGradient2", new Color(180, 180, 180));
		// colorMapLight.put("button.borderColor", new Color(235, 235, 235));

		colorMapDark.put("tab-background", new Color(33, 37, 43));
		colorMapDark.put("tab-selection", new Color(40, 44, 52));
		colorMapDark.put("control", new Color(40, 44, 52));

		colorMapDark.put("text", new Color(240, 240, 240));
		colorMapDark.put("menuText", new Color(240, 240, 240));
		colorMapDark.put("controlText", new Color(240, 240, 240));
		colorMapDark.put("textForeground", new Color(240, 240, 240));
		colorMapDark.put("infoText", new Color(240, 240, 240));
		colorMapDark.put("List.foreground", new Color(240, 240, 240));
		colorMapDark.put("Label.foreground", new Color(240, 240, 240));

		colorMapDark.put("Tree.background", new Color(40, 44, 52));
		colorMapDark.put("Tree.textForeground",
				new Color(75 + 20, 83 + 20, 98 + 20));
		colorMapDark.put("nimbusBorder", new Color(24, 26, 31));
		colorMapDark.put("scrollbar", new Color(75, 83, 98));
		colorMapDark.put("scrollbar-hot", new Color(75 + 20, 83 + 20, 98 + 20));

		colorMapDark.put("button.normalGradient1", new Color(57, 62, 74));
		colorMapDark.put("button.normalGradient2",
				new Color(55 - 10, 61 - 10, 72 - 10));
		colorMapDark.put("button.hotGradient1",
				new Color(57 + 20, 62 + 20, 74 + 20));
		colorMapDark.put("button.hotGradient2",
				new Color(57 + 10, 62 + 10, 74 + 10));
		colorMapDark.put("button.pressedGradient1",
				new Color(57 - 20, 62 - 20, 74 - 20));
		colorMapDark.put("button.pressedGradient2",
				new Color(57 - 10, 62 - 10, 74 - 10));
		// colorMapDark.put("button.borderColor", new Color(24, 26, 31));
	}

	public static UIDefaults createSkinnedButton(UIDefaults btnSkin) {
//        Color c1 = new Color(3, 155, 229);
//        Color c2 = new Color(2, 132, 195);
//        Color c3 = new Color(70, 130, 180);
//        Color c1 = new Color(3, 155, 229);
//        Color c2 = new Color(2, 132, 195);
//        Color c3 = new Color(70, 130, 180);
//        CustomButtonPainter cs = new CustomButtonPainter(c1, c2, c3);
		RoundedButtonPainter cs = new RoundedButtonPainter();
		btnSkin.put("Button.contentMargins", new Insets(8, 15, 8, 15));
		btnSkin.put("Button[Default+Focused+MouseOver].backgroundPainter",
				cs.getHotPainter());
		btnSkin.put("Button[Default+Focused+Pressed].backgroundPainter",
				cs.getPressedPainter());
		btnSkin.put("Button[Default+Focused].backgroundPainter",
				cs.getNormalPainter());
		btnSkin.put("Button[Default+MouseOver].backgroundPainter",
				cs.getHotPainter());
		btnSkin.put("Button[Default+Pressed].backgroundPainter",
				cs.getPressedPainter());
		btnSkin.put("Button[Default].backgroundPainter", cs.getNormalPainter());
		btnSkin.put("Button[Enabled].backgroundPainter", cs.getNormalPainter());
		btnSkin.put("Button[Focused+MouseOver].backgroundPainter",
				cs.getHotPainter());
		btnSkin.put("Button[Focused+Pressed].backgroundPainter",
				cs.getPressedPainter());
		btnSkin.put("Button[Focused].backgroundPainter", cs.getNormalPainter());
		btnSkin.put("Button[MouseOver].backgroundPainter", cs.getHotPainter());
		btnSkin.put("Button[Pressed].backgroundPainter",
				cs.getPressedPainter());
		btnSkin.put("Button[Default+Pressed].textForeground",
				getThemeColor("control"));
		btnSkin.put("Button.foreground", getThemeColor("control"));
		btnSkin.put("Button[Disabled].textForeground", Color.GRAY);
		return btnSkin;
	}

	public static JButton createSkinnedButton(Color c1, Color c2, Color c3) {
		UIDefaults btnSkin = new UIDefaults();
		CustomButtonPainter cs = new CustomButtonPainter(c1, c2, c3);
		btnSkin.put("Button[Default+Focused+MouseOver].backgroundPainter",
				cs.getHotPainter());
		btnSkin.put("Button[Default+Focused+Pressed].backgroundPainter",
				cs.getPressedPainter());
		btnSkin.put("Button[Default+Focused].backgroundPainter",
				cs.getNormalPainter());
		btnSkin.put("Button[Default+MouseOver].backgroundPainter",
				cs.getHotPainter());
		btnSkin.put("Button[Default+Pressed].backgroundPainter",
				cs.getPressedPainter());
		btnSkin.put("Button[Default].backgroundPainter", cs.getNormalPainter());
		btnSkin.put("Button[Enabled].backgroundPainter", cs.getNormalPainter());
		btnSkin.put("Button[Focused+MouseOver].backgroundPainter",
				cs.getHotPainter());
		btnSkin.put("Button[Focused+Pressed].backgroundPainter",
				cs.getPressedPainter());
		btnSkin.put("Button[Focused].backgroundPainter", cs.getNormalPainter());
		btnSkin.put("Button[MouseOver].backgroundPainter", cs.getHotPainter());
		btnSkin.put("Button[Pressed].backgroundPainter",
				cs.getPressedPainter());
		JButton btn = new JButton();
		btn.putClientProperty("Nimbus.Overrides", btnSkin);
		return btn;
	}

//    public static UIDefaults creatTextFieldSkin(UIDefaults uiDefaults) {
//        Painter<JTextField> focusedBorder = new Painter<JTextField>() {
//            @Override
//            public void paint(Graphics2D g, JTextField object, int width, int height) {
//                g.setColor(new Color(200, 200, 200));
//                g.drawRect(0, 0, width - 1, height - 1);
//            }
//        };
//
//        Painter<JTextField> normalBorder = new Painter<JTextField>() {
//            @Override
//            public void paint(Graphics2D g, JTextField object, int width, int height) {
//                g.setColor(new Color(240, 240, 240));
//                g.drawRect(0, 0, width - 1, height - 1);
//            }
//        };
//        uiDefaults.put("TextField[Disabled].borderPainter", normalBorder);
//        uiDefaults.put("TextField[Enabled].borderPainter", normalBorder);
//        uiDefaults.put("TextField[Focused].borderPainter", focusedBorder);
//        return uiDefaults;
//    }

//    public static JTextField createTextField() {
//        JTextField txt = new JTextField();
//        txt.putClientProperty("Nimbus.Overrides", creatTextFieldSkin());
//        return txt;
//    }

//    public static JTextField createTextField(int col) {
//        JTextField txt = new JTextField(col);
//        txt.putClientProperty("Nimbus.Overrides", creatTextFieldSkin());
//        return txt;
//    }

	public static UIDefaults createTextFieldSkin(UIDefaults uiDefaults) {
		final Color borderColor = getThemeColor("nimbusBorder");// new
																// Color(230,
																// 230, 230);
		final Color focusedColor = getCommonColor("selection-highlight");
		Painter<? extends JComponent> focusedBorder = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width,
					int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(focusedColor);
				g.drawRect(1, 1, width - 2, height - 2);
			}
		};

		Painter<? extends JComponent> normalBorder = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width,
					int height) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(borderColor);
				g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND));
				g.drawRect(0, 0, width - 1, height - 1);
			}
		};

		uiDefaults.put("TextField.background", getThemeColor("control"));
		uiDefaults.put("FormattedTextField.background",
				getThemeColor("control"));
		uiDefaults.put("PasswordField.background", getThemeColor("control"));

		uiDefaults.put("FormattedTextField[Disabled].borderPainter",
				normalBorder);
		uiDefaults.put("FormattedTextField[Enabled].borderPainter",
				normalBorder);
		uiDefaults.put("FormattedTextField[Focused].borderPainter",
				focusedBorder);

		uiDefaults.put("PasswordField[Disabled].borderPainter", normalBorder);
		uiDefaults.put("PasswordField[Enabled].borderPainter", normalBorder);
		uiDefaults.put("PasswordField[Focused].borderPainter", focusedBorder);

		uiDefaults.put("TextField[Disabled].borderPainter", normalBorder);
		uiDefaults.put("TextField[Enabled].borderPainter", normalBorder);
		uiDefaults.put("TextField[Focused].borderPainter", focusedBorder);
		return uiDefaults;
	}

	public static UIDefaults createSpinnerSkin(UIDefaults uiDefaults) {
		Color c1 = getThemeColor("scrollbar");
		Color c2 = getThemeColor("scrollbar-hot");
		Painter<? extends JComponent> painter1 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width,
					int height) {
				g.setColor(c1);
				g.fillRect(0, 0, width - 1, height - 1);
			}
		};

		Painter<? extends JComponent> painter2 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width,
					int height) {
				g.setColor(c1);
				g.fillRect(0, 0, width - 1, height - 1);
			}
		};

		uiDefaults.put(
				"Spinner:\"Spinner.nextButton\"[Disabled].backgroundPainter",
				painter2);
		uiDefaults.put(
				"Spinner:\"Spinner.nextButton\"[Enabled].backgroundPainter",
				painter2);
		uiDefaults.put(
				"Spinner:\"Spinner.nextButton\"[Focused+MouseOver].backgroundPainter",
				painter1);
		uiDefaults.put(
				"Spinner:\"Spinner.nextButton\"[Focused+Pressed].backgroundPainter",
				painter1);
		uiDefaults.put(
				"Spinner:\"Spinner.nextButton\"[Focused].backgroundPainter",
				painter2);
		uiDefaults.put(
				"Spinner:\"Spinner.nextButton\"[MouseOver].backgroundPainter",
				painter1);
		uiDefaults.put(
				"Spinner:\"Spinner.nextButton\"[Pressed].backgroundPainter",
				painter1);

		uiDefaults.put(
				"Spinner:\"Spinner.previousButton\"[Disabled].backgroundPainter",
				painter2);
		uiDefaults.put(
				"Spinner:\"Spinner.previousButton\"[Enabled].backgroundPainter",
				painter2);
		uiDefaults.put(
				"Spinner:\"Spinner.previousButton\"[Focused+MouseOver].backgroundPainter",
				painter1);
		uiDefaults.put(
				"Spinner:\"Spinner.previousButton\"[Focused+Pressed].backgroundPainter",
				painter1);
		uiDefaults.put(
				"Spinner:\"Spinner.previousButton\"[Focused].backgroundPainter",
				painter2);
		uiDefaults.put(
				"Spinner:\"Spinner.previousButton\"[MouseOver].backgroundPainter",
				painter1);
		uiDefaults.put(
				"Spinner:\"Spinner.previousButton\"[Pressed].backgroundPainter",
				painter1);

		uiDefaults.put(
				"Spinner:Panel:\"Spinner.formattedTextField\"[Enabled].backgroundPainter",
				painter2);
		uiDefaults.put(
				"Spinner:Panel:\"Spinner.formattedTextField\"[Focused].backgroundPainter",
				painter1);
		uiDefaults.put(
				"Spinner:Panel:\"Spinner.formattedTextField\"[Focused+Selected].backgroundPainter",
				painter1);
		return uiDefaults;
	}

	public static UIDefaults createPopupSkin(UIDefaults uiDefaults) {
		uiDefaults.put("nimbusLightBackground", getThemeColor("scrollbar"));
		uiDefaults.put("PopupMenu.background", getThemeColor("scrollbar"));
		uiDefaults.put("PopupMenu.foreground", getThemeColor("text"));

		uiDefaults.put("PopupMenu[Enabled].backgroundPainter",
				new Painter<JComponent>() {
					@Override
					public void paint(Graphics2D g, JComponent object,
							int width, int height) {
						g.setColor(getThemeColor("scrollbar"));
						g.fillRect(0, 0, width - 1, height - 1);
					}
				});
		return uiDefaults;
	}

	public static UIDefaults createComboBoxSkin(UIDefaults uiDefaults) {
		Color c1 = getThemeColor("nimbusBorder");
		Color c2 = getThemeColor("nimbusBorder");
		Color c3 = getThemeColor("nimbusBorder");
		Color c4 = getThemeColor("nimbusBorder");
		Painter<? extends JComponent> painter1 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width,
					int height) {
				g.setColor(c1);
				g.fillRect(0, 0, width - 1, height - 1);
			}
		};

		Painter<? extends JComponent> painter2 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width,
					int height) {
				g.setColor(c2);
				g.fillRect(0, 0, width - 1, height - 1);
			}
		};

		Painter<? extends JComponent> painter3 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width,
					int height) {
//                g.setColor(new Color(200, 200, 200));
//                g.drawRect(0, 0, width - 1, height - 1);
			}
		};

		Painter<? extends JComponent> painter4 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width,
					int height) {
				g.setColor(c3);
				g.drawRect(0, 0, width - 1, height - 1);
			}
		};

		Painter<? extends JComponent> painter5 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width,
					int height) {
				g.setColor(c4);
				g.drawRect(0, 0, width - 1, height - 1);
			}
		};

//        Painter<? extends JComponent> painter6 = new Painter<JComponent>() {
//            @Override
//            public void paint(Graphics2D g, JComponent object, int width, int height) {
//                int midx = width / 2;
//                int midy = height / 2;
//
//                g.setColor(new Color(200, 200, 200));
//                g.setFont(App.getFontAwesomeFont());
//                g.drawString("\uf0d9", midx, midy + g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent());
//
//            }
//        };

		uiDefaults.put(
				"ComboBox:\"ComboBox.textField\"[Enabled].backgroundPainter",
				painter3);
		uiDefaults.put(
				"ComboBox:\"ComboBox.textField\"[Selected].backgroundPainter",
				painter3);
		uiDefaults.put("ComboBox[Enabled].backgroundPainter", painter4);
		uiDefaults.put("ComboBox[Focused+MouseOver].backgroundPainter",
				painter5);
		uiDefaults.put("ComboBox[Focused+Pressed].backgroundPainter", painter5);
		uiDefaults.put("ComboBox[Focused].backgroundPainter", painter4);
		uiDefaults.put("ComboBox[MouseOver].backgroundPainter", painter1);
		uiDefaults.put("ComboBox[Pressed].backgroundPainter", painter1);
		uiDefaults.put("ComboBox[Editable+Focused].backgroundPainter",
				painter4);
		uiDefaults.put(
				"ComboBox:\"ComboBox.arrowButton\"[Editable+Enabled].backgroundPainter",
				painter3);
		uiDefaults.put(
				"ComboBox:\"ComboBox.arrowButton\"[Editable+MouseOver].backgroundPainter",
				painter2);
		uiDefaults.put(
				"ComboBox:\"ComboBox.arrowButton\"[Editable+Pressed].backgroundPainter",
				painter2);
		uiDefaults.put(
				"ComboBox:\"ComboBox.arrowButton\"[Editable+Selected].backgroundPainter",
				painter3);
		// Painter painter = (Painter)
		// uiDefaults.get("ComboBox:\"ComboBox.arrowButton\"[MouseOver].foregroundPainter");
//        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[MouseOver].foregroundPainter",
//                painter6);
//
//        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Pressed].foregroundPainter",
//                painter6);
//        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Selected].foregroundPainter",
//                painter6);
		return uiDefaults;
	}

//    public static JButton createButton(String text) {
//        Color c1 = new Color(3, 155, 229);
//        Color c2 = new Color(2, 132, 195);
//        Color c3 = new Color(70, 130, 180);
//        JButton btn = GraphicsUtils.createSkinnedButton(c1, c2, c3);
//        btn.setText(text);
//        btn.setForeground(Color.WHITE);
//        return btn;
//    }

	public static JScrollPane createScrollPane(Component component) {
		JScrollPane scrollPane = new JScrollPane(component);
		JScrollBar verticalScroller = new JScrollBar(JScrollBar.VERTICAL);
		verticalScroller.setUI(new CustomScrollBarUI());

		// verticalScroller.putClientProperty("Nimbus.Overrides",
		// App.scrollBarSkin);
		scrollPane.setVerticalScrollBar(verticalScroller);

		JScrollBar horizontalScroller = new JScrollBar(JScrollBar.HORIZONTAL);
		horizontalScroller.setUI(new CustomScrollBarUI());
		scrollPane.setHorizontalScrollBar(horizontalScroller);
		return scrollPane;
	}

	public static UIDefaults createCheckboxSkin(UIDefaults uiDefaults) {
		Color c1 = new Color(200, 200, 200);
		Color c2 = new Color(200, 200, 200);

		Painter<? extends JComponent> painter1 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width,
					int height) {
				g.setColor(c1);
				g.drawRect(2, 2, width - 5, height - 5);
			}
		};

		Painter<? extends JComponent> painter2 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width,
					int height) {
				g.setColor(c2);
				g.drawRect(2, 2, width - 5, height - 5);
				g.fillRect(4, 4, width - 8, height - 8);
			}
		};

		uiDefaults.put("CheckBox[Disabled].iconPainter", painter1);
		uiDefaults.put("CheckBox[Enabled].iconPainter", painter1);
		uiDefaults.put("CheckBox[Focused+MouseOver+Selected].iconPainter",
				painter2);
		uiDefaults.put("CheckBox[Focused+Pressed+Selected].iconPainter",
				painter2);
		uiDefaults.put("CheckBox[Focused+Selected].iconPainter", painter2);
		uiDefaults.put("CheckBox[MouseOver+Selected].iconPainter", painter2);
		uiDefaults.put("CheckBox[Pressed+Selected].iconPainter", painter2);

		uiDefaults.put("CheckBox[Focused+MouseOver].iconPainter", painter1);
		uiDefaults.put("CheckBox[Focused+Pressed].iconPainter", painter1);
		uiDefaults.put("CheckBox[Pressed].iconPainter", painter1);
		uiDefaults.put("CheckBox[Selected].iconPainter", painter2);
		uiDefaults.put("CheckBox[Focused].iconPainter", painter1);
		uiDefaults.put("CheckBox[MouseOver].iconPainter", painter1);

		return uiDefaults;
	}

	public static UIDefaults createTreeSkin(UIDefaults uiDefaults) {
		uiDefaults.put("Tree[Enabled].closedIconPainter",
				new Painter<JComponent>() {
					@Override
					public void paint(Graphics2D g, JComponent object,
							int width, int height) {
						Font font = g.getFont();
						g.setColor(getThemeColor("Tree.textForeground"));
						g.setFont(App.getFontAwesomeFont().deriveFont(16));
						int h = g.getFontMetrics().getAscent()
								+ g.getFontMetrics().getDescent();

						g.drawString("\uf07b", 0, h);
						g.setFont(font);
					}
				});

		uiDefaults.put("Tree[Enabled].openIconPainter",
				new Painter<JComponent>() {
					@Override
					public void paint(Graphics2D g, JComponent object,
							int width, int height) {
						g.setColor(getThemeColor("Tree.textForeground"));
						Font font = g.getFont();
						g.setFont(App.getFontAwesomeFont().deriveFont(16));
						int h = g.getFontMetrics().getAscent()
								+ g.getFontMetrics().getDescent();

						g.drawString("\uf07c", 0, h);
						g.setFont(font);
					}
				});

		uiDefaults.put("Tree[Enabled].leafIconPainter",
				new Painter<JComponent>() {
					@Override
					public void paint(Graphics2D g, JComponent object,
							int width, int height) {
						g.setColor(getThemeColor("Tree.textForeground"));
						Font font = g.getFont();
						g.setFont(App.getFontAwesomeFont().deriveFont(16));
						int h = g.getFontMetrics().getAscent()
								+ g.getFontMetrics().getDescent();

						g.drawString("\uf15b", 0, h);
						g.setFont(font);
					}
				});
		return uiDefaults;
	}

	public static UIDefaults createSplitPaneSkin(UIDefaults uiDefaults) {
		Painter<? extends JComponent> painter = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width,
					int height) {
				g.setColor(getThemeColor("control"));
				g.fill(new Rectangle(0, 0, width, height));
			}
		};

		for (String key : new String[] {
				"SplitPane:SplitPaneDivider[Enabled].backgroundPainter",
				"SplitPane:SplitPaneDivider[Enabled+Vertical].foregroundPainter",
				"SplitPane:SplitPaneDivider[Enabled].backgroundPainter",
				"SplitPane:SplitPaneDivider[Enabled].foregroundPainter",
				"SplitPane:SplitPaneDivider[Focused].backgroundPainter",
				"SplitPane:SplitPaneDivider[Enabled].foregroundPainter",
				"SplitPane:SplitPaneDivider[Enabled].foregroundPainter" }) {
			uiDefaults.put(key, painter);
		}

		uiDefaults.put("SplitPane.contentMargins", new Insets(0, 0, 0, 0));

		return uiDefaults;
	}

	public static UIDefaults createTabbedPaneSkin(UIDefaults uiDefaults) {
//		Color c1 = new Color(3, 155, 229);
//
//		Color c2 = new Color(33, 37, 43);
//		Color c3 = new Color(40, 44, 52);// , 200, 200);
//		Color c4 = new Color(24, 26, 31);

//		Color c2 = new Color(255, 255, 255);
//		Color c3 = new Color(240, 240, 240);

		Painter<? extends JComponent> painter1 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width,
					int height) {
				Color c2 = GraphicsUtils.getThemeColor(object,
						"tab-background");
				g.setColor(c2);
				g.fillRect(0, 0, width, height);
			}
		};

		Painter<? extends JComponent> painter2 = new Painter<JComponent>() {
			@Override
			public void paint(Graphics2D g, JComponent object, int width,
					int height) {

				Color c3 = GraphicsUtils.getThemeColor(object, "tab-selection");
				g.setColor(c3);
				g.fillRect(0, 0, width, height);
				Color c1 = GraphicsUtils.getCommonColor(object,
						"selection-highlight");
				g.setColor(c1);
				g.fillRect(0, 0, width, 2);
			}
		};

		uiDefaults.put("TabbedPane:TabbedPaneTabArea.contentMargins",
				new Insets(0, 0, 0, 0));

		for (String str : new String[] {
				"TabbedPane:TabbedPaneTabArea[Disabled].backgroundPainter",
				"TabbedPane:TabbedPaneTabArea[Enabled+MouseOver].backgroundPainter",
				"TabbedPane:TabbedPaneTabArea[Enabled+Pressed].backgroundPainter",
				"TabbedPane:TabbedPaneTabArea[Enabled].backgroundPainter",
				"TabbedPane:TabbedPaneTab[Enabled+MouseOver].backgroundPainter",
				"TabbedPane:TabbedPaneTab[Enabled].backgroundPainter" }) {
			uiDefaults.put(str, painter1);
		}

		for (String str : new String[] {
				"TabbedPane:TabbedPaneTab[Selected].backgroundPainter",
				"TabbedPane:TabbedPaneTab[Pressed+Selected].backgroundPainter",
				"TabbedPane:TabbedPaneTab[MouseOver+Selected].backgroundPainter",
				"TabbedPane:TabbedPaneTab[Focused+Pressed+Selected].backgroundPainter",
				"TabbedPane:TabbedPaneTab[Focused+MouseOver+Selected].backgroundPainter",
				"TabbedPane:TabbedPaneTab[Enabled+Pressed].backgroundPainter",
				"TabbedPane:TabbedPaneTab[Focused+Selected].backgroundPainter" }) {
			uiDefaults.put(str, painter2);
		}
//		uiDefaults.put(
//				"TabbedPane:TabbedPaneTabArea[Disabled].backgroundPainter",
//				painter1);
//		uiDefaults.put(
//				"TabbedPane:TabbedPaneTabArea[Enabled].backgroundPainter",
//				painter1);
//		uiDefaults.put(
//				"TabbedPane:TabbedPaneTabArea[Enabled+Pressed].backgroundPainter",
//				painter1);
//		uiDefaults.put(
//				"TabbedPane:TabbedPaneTabArea[Enabled+MouseOver].backgroundPainter",
//				painter1);

//		uiDefaults.put("TabbedPane:TabbedPaneTab[Selected].backgroundPainter",
//				painter2);
//		uiDefaults.put(
//				"TabbedPane:TabbedPaneTab[Pressed+Selected].backgroundPainter",
//				painter2);
//		uiDefaults.put(
//				"TabbedPane:TabbedPaneTab[MouseOver+Selected].backgroundPainter",
//				painter2);
//		uiDefaults.put(
//				"TabbedPane:TabbedPaneTab[Focused+Pressed+Selected].backgroundPainter",
//				painter2);
//		uiDefaults.put(
//				"TabbedPane:TabbedPaneTab[Focused+MouseOver+Selected].backgroundPainter",
//				painter2);
//		uiDefaults.put("TabbedPane:TabbedPaneTab[Enabled].backgroundPainter",
//				painter1);
//		uiDefaults.put(
//				"TabbedPane:TabbedPaneTab[Enabled+Pressed].backgroundPainter",
//				painter2);
//		uiDefaults.put(
//				"TabbedPane:TabbedPaneTab[Enabled+MouseOver].backgroundPainter",
//				painter1);
//		uiDefaults.put(
//				"TabbedPane:TabbedPaneTab[Focused+Selected].backgroundPainter",
//				painter2);

		return uiDefaults;
	}

	public static void customizeTableHeader(UIDefaults uiDefaults) {
		Color c1 = new Color(240, 240, 240);
		Painter painterNormal = (Graphics2D g, Object object, int width,
				int height) -> {
			g.setColor(c1);
			g.drawLine(width - 1, 3, width - 1, height - 6);
		};
		uiDefaults.put("TableHeader.font",
				new Font(Font.DIALOG, Font.PLAIN, 14));
		uiDefaults.put("TableHeader.background", Color.WHITE);// new Color(240,
																// 240, 240));
		uiDefaults.put("TableHeader.foreground", new Color(190, 150, 150));
		uiDefaults.put("TableHeader:\"TableHeader.renderer\".opaque", false);
		uiDefaults.put(
				"TableHeader:\"TableHeader.renderer\"[Enabled+Focused+Sorted].backgroundPainter",
				painterNormal);
		uiDefaults.put(
				"TableHeader:\"TableHeader.renderer\"[Enabled+Focused].backgroundPainter",
				painterNormal);
		uiDefaults.put(
				"TableHeader:\"TableHeader.renderer\"[Enabled+Sorted].backgroundPainter",
				painterNormal);
		uiDefaults.put(
				"TableHeader:\"TableHeader.renderer\"[Enabled].backgroundPainter",
				painterNormal);
		uiDefaults.put(
				"TableHeader:\"TableHeader.renderer\"[MouseOver].backgroundPainter",
				painterNormal);
		uiDefaults.put(
				"TableHeader:\"TableHeader.renderer\"[Pressed].backgroundPainter",
				painterNormal);
	}

	public static UIDefaults createSkinnedMenu(UIDefaults btnSkin) {
		Color c1 = new Color(3, 155, 229);
		Painter painter = new Painter() {
			@Override
			public void paint(Graphics2D g, Object object, int width,
					int height) {
				g.setColor(c1);
				g.fillRect(0, 0, width, height);
			}
		};
		btnSkin.put("MenuItem[MouseOver].backgroundPainter", painter);
		return btnSkin;
	}

	private static JPopupMenu popup(JTextComponent c) {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem mCut = new JMenuItem("Cut");
		JMenuItem mCopy = new JMenuItem("Copy");
		JMenuItem mPaste = new JMenuItem("Paste");
		JMenuItem mSelect = new JMenuItem("Select all");

		popup.add(mCut);
		popup.add(mCopy);
		popup.add(mPaste);
		popup.add(mSelect);

		mCut.addActionListener(e -> {
			c.cut();
		});

		mCopy.addActionListener(e -> {
			c.copy();
		});

		mPaste.addActionListener(e -> {
			c.paste();
		});

		mSelect.addActionListener(e -> {
			c.selectAll();
		});

		return popup;
	}

	private static void installPopUp(JTextComponent c) {
		c.putClientProperty("flat.popup", popup(c));
		c.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("Right click on text field");
				if (e.getButton() == MouseEvent.BUTTON3 || e.isPopupTrigger()) {

					JPopupMenu pop = (JPopupMenu) c
							.getClientProperty("flat.popup");
					if (pop != null) {
						pop.show(c, e.getX(), e.getY());
					}
				}
			}
		});
	}

	public static JTextField createTextField() {
		JTextField c = new JTextField();
		installPopUp(c);
		return c;
	}

	public static JTextField createTextField(int n) {
		JTextField c = new JTextField(n);
		installPopUp(c);
		return c;
	}

	public static JTextArea createTextArea() {
		JTextArea c = new JTextArea();
		installPopUp(c);
		return c;
	}

	public static Color getCommonColor(String key) {
		return getCommonColor(null, key);
	}

	public static Color getCommonColor(JComponent object, String key) {
		Color c2 = colorMapCommon.get(key);
		if (object == null)
			return c2;
		Color override = (Color) object.getClientProperty("override." + key);
		if (override != null) {
			c2 = override;
		}
		return c2;
	}

	public static Color getThemeColor(String key) {
		return getThemeColor(null, key);
	}

	public static Color getThemeColor(JComponent object, String key) {
		Color c2 = App.useDarkTheme() ? colorMapDark.get(key)
				: colorMapLight.get(key);
		if (object == null)
			return c2;
		Color override = (Color) object.getClientProperty("override." + key);
		if (override != null) {
			c2 = override;
		}
		return c2;
	}
}
