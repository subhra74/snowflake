package snowflake;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import snowflake.common.Settings;
import snowflake.components.common.CustomScrollBarUI;
import snowflake.components.main.MainContent;
import snowflake.components.terminal.snippets.SnippetItem;
import snowflake.utils.GraphicsUtils;
import snowflake.utils.PathUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.plaf.synth.SynthScrollBarUI;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class App {
	public static final String APP_VERSION = "104";
	public static final String APP_VERSION_STR = "1.0.4";
	public static UIDefaults comboBoxSkin = new UIDefaults();
	public static UIDefaults toolBarButtonSkin = new UIDefaults();
	public static UIDefaults scrollBarSkin = new UIDefaults();
	public static UIDefaults splitPaneSkin = new UIDefaults();
	public static UIDefaults splitPaneSkin1 = new UIDefaults();
	public static UIDefaults splitPaneSkin2 = new UIDefaults();
	private static Properties config = new Properties();
	private static Font fontAwesomeFont;
	private static Settings settings;

	private static List<SnippetItem> snippetItems;

	public static String getConfig(String key) {
		return config.getProperty(key);
	}

	public static Font getFontAwesomeFont() {
		return fontAwesomeFont;
	}

	public static Settings getGlobalSettings() {
		return settings;
	}

//    class MySynthFactory extends SynthStyleFactory {
//
//        @Override
//        public SynthStyle getStyle(JComponent c, Region id) {
//            return null;
//        }
//    }

	public static void main(String[] args)
			throws UnsupportedLookAndFeelException {

		Security.addProvider(new BouncyCastleProvider());

		NimbusLookAndFeel nimbusLookAndFeel = new NimbusLookAndFeel();
		GraphicsUtils.createTextFieldSkin(nimbusLookAndFeel.getDefaults());
		GraphicsUtils.createSpinnerSkin(nimbusLookAndFeel.getDefaults());
		GraphicsUtils.createComboBoxSkin(nimbusLookAndFeel.getDefaults());
		GraphicsUtils.createCheckboxSkin(nimbusLookAndFeel.getDefaults());
		GraphicsUtils.createTabbedPaneSkin(nimbusLookAndFeel.getDefaults());
		GraphicsUtils.customizeTableHeader(nimbusLookAndFeel.getDefaults());
		GraphicsUtils.createSkinnedButton(nimbusLookAndFeel.getDefaults());
		GraphicsUtils.createSkinnedMenu(nimbusLookAndFeel.getDefaults());
		nimbusLookAndFeel.getDefaults().put("ScrollBarUI",
				CustomScrollBarUI.class.getName());

		UIManager.setLookAndFeel(nimbusLookAndFeel);
		UIManager.put("control", Color.WHITE);
		UIManager.put("nimbusSelectionBackground", new Color(3, 155, 229));

		// UIManager.put("nimbusBase", new Color(200, 200, 200));
//        UIManager.put("text", new Color(208, 208, 208));

		// UIManager.put("ScrollBar.thumbHeight", 8);
		// UIManager.put("ScrollBar:\"ScrollBar.button\".size", 5);
		// UIManager.put("Panel.background", new Color(245, 245, 245));
		splitPaneSkin.put(
				"SplitPane:SplitPaneDivider[Enabled].backgroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(Color.WHITE);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
		splitPaneSkin.put(
				"SplitPane:SplitPaneDivider[Enabled+Vertical].foregroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(Color.WHITE);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
		splitPaneSkin.put(
				"SplitPane:SplitPaneDivider[Enabled].backgroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(Color.WHITE);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
		splitPaneSkin.put(
				"SplitPane:SplitPaneDivider[Enabled].foregroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(Color.WHITE);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
		splitPaneSkin.put(
				"SplitPane:SplitPaneDivider[Focused].backgroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(Color.WHITE);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
		splitPaneSkin.put(
				"SplitPane:SplitPaneDivider[Enabled].foregroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(Color.WHITE);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});

		splitPaneSkin.put("SplitPane.contentMargins", new Insets(0, 0, 0, 0));

		createVerticalScrollSkin();
		createVerticalScrollSkin1();

		Painter<JComboBox> comboBoxPainterNormal = new Painter<JComboBox>() {
			@Override
			public void paint(Graphics2D g, JComboBox object, int width,
					int height) {
				g.setColor(new Color(240, 240, 240));
				g.drawRect(0, 0, width - 1, height - 1);
//                g.setColor(Color.BLACK);
//                g.fill(new Rectangle(0,0,width,height));
			}
		};

		Painter<JComboBox> comboBoxPainterFocused = new Painter<JComboBox>() {
			@Override
			public void paint(Graphics2D g, JComboBox object, int width,
					int height) {
				g.setColor(new Color(220, 220, 220));
				g.drawRect(0, 0, width - 1, height - 1);
//                g.setColor(Color.BLACK);
//                g.fill(new Rectangle(0,0,width,height));
			}
		};

		Painter<JComboBox> comboBoxPainterHot = new Painter<JComboBox>() {
			@Override
			public void paint(Graphics2D g, JComboBox object, int width,
					int height) {
				g.setColor(new Color(230, 230, 230));
				g.drawRect(0, 0, width - 1, height - 1);
//                g.setColor(Color.BLACK);
//                g.fill(new Rectangle(0,0,width,height));
			}
		};

		Painter<JComboBox> comboBoxPainterPressed = new Painter<JComboBox>() {
			@Override
			public void paint(Graphics2D g, JComboBox object, int width,
					int height) {
				g.setColor(new Color(220, 220, 220));
				g.fillRect(0, 0, width - 1, height - 1);
//                g.setColor(Color.BLACK);
//                g.fill(new Rectangle(0,0,width,height));
			}
		};

		comboBoxSkin.put("ComboBox[Enabled].backgroundPainter",
				comboBoxPainterNormal);
		comboBoxSkin.put("ComboBox[Focused].backgroundPainter",
				comboBoxPainterFocused);
		comboBoxSkin.put("ComboBox[MouseOver].backgroundPainter",
				comboBoxPainterHot);
		comboBoxSkin.put("ComboBox[Pressed].backgroundPainter",
				comboBoxPainterPressed);

		comboBoxSkin.put("ComboBox[Focused+Pressed].backgroundPainter",
				comboBoxPainterPressed);
		comboBoxSkin.put("ComboBox[Focused+MouseOver].backgroundPainter",
				comboBoxPainterHot);
		comboBoxSkin.put("ComboBox[Enabled+Selected].backgroundPainter",
				comboBoxPainterNormal);

		Painter<JButton> toolBarButtonPainterNormal = new Painter<JButton>() {
			@Override
			public void paint(Graphics2D g, JButton object, int width,
					int height) {

			}
		};

		Painter<JButton> toolBarButtonPainterHot = new Painter<JButton>() {
			@Override
			public void paint(Graphics2D g, JButton object, int width,
					int height) {
				g.setColor(new Color(240, 240, 240));
				g.fillRect(0, 0, width - 1, height - 1);
			}
		};

		Painter<JButton> toolBarButtonPainterPressed = new Painter<JButton>() {
			@Override
			public void paint(Graphics2D g, JButton object, int width,
					int height) {
				g.setColor(new Color(230, 230, 230));
				g.fillRect(0, 0, width - 1, height - 1);
			}
		};

		toolBarButtonSkin.put("Button.contentMargins", new Insets(5, 8, 5, 8));

		toolBarButtonSkin.put("Button[Disabled].backgroundPainter",
				toolBarButtonPainterNormal);
		toolBarButtonSkin.put("Button[Disabled].textForeground",
				Color.LIGHT_GRAY);

		toolBarButtonSkin.put("Button[Enabled].backgroundPainter",
				toolBarButtonPainterNormal);
		toolBarButtonSkin.put("Button[Focused].backgroundPainter",
				toolBarButtonPainterNormal);
		toolBarButtonSkin.put("Button[Default].backgroundPainter",
				toolBarButtonPainterNormal);
		toolBarButtonSkin.put("Button[Default+Focused].backgroundPainter",
				toolBarButtonPainterNormal);

		toolBarButtonSkin.put("Button[Pressed].backgroundPainter",
				toolBarButtonPainterPressed);
		toolBarButtonSkin.put("Button[Focused+Pressed].backgroundPainter",
				toolBarButtonPainterPressed);
		toolBarButtonSkin.put(
				"Button[Default+Focused+Pressed].backgroundPainter",
				toolBarButtonPainterPressed);
		toolBarButtonSkin.put("Button[Default+Pressed].backgroundPainter",
				toolBarButtonPainterPressed);

		toolBarButtonSkin.put("Button[MouseOver].backgroundPainter",
				toolBarButtonPainterHot);
		toolBarButtonSkin.put("Button[Focused+MouseOver].backgroundPainter",
				toolBarButtonPainterHot);
		toolBarButtonSkin.put("Button[Default+MouseOver].backgroundPainter",
				toolBarButtonPainterHot);
		toolBarButtonSkin.put(
				"Button[Default+Focused+MouseOver].backgroundPainter",
				toolBarButtonPainterHot);

		Painter scrollButtonPainter = new Painter() {
			@Override
			public void paint(Graphics2D g, Object object, int width,
					int height) {
				g.setColor(Color.RED);
				g.fillRect(0, 0, width, height);
			}
		};

		scrollBarSkin.put("ScrollBar.button.foregroundPainter",
				scrollButtonPainter);
		scrollBarSkin.put("ScrollBar.button.backgroundPainter",
				scrollButtonPainter);

		scrollBarSkin.put("ScrollBar.button[Enabled].foregroundPainter",
				scrollButtonPainter);
		scrollBarSkin.put("ScrollBar.button[Enabled].backgroundPainter",
				scrollButtonPainter);

		scrollBarSkin.put(
				"ScrollBar:\"ScrollBar.button\"[Enabled].foregroundPainter",
				scrollButtonPainter);
		scrollBarSkin.put(
				"ScrollBar:\"ScrollBar.button\"[MouseOver].foregroundPainter",
				scrollButtonPainter);
		scrollBarSkin.put(
				"ScrollBar:\"ScrollBar.button\"[Pressed].foregroundPainter",
				scrollButtonPainter);

		scrollBarSkin.put(
				"ScrollBar:\"ScrollBar.button\"[Enabled].backgroundPainter",
				scrollButtonPainter);
		scrollBarSkin.put(
				"ScrollBar:\"ScrollBar.button\"[MouseOver].backgroundPainter",
				scrollButtonPainter);
		scrollBarSkin.put(
				"ScrollBar:\"ScrollBar.button\"[Pressed].backgroundPainter",
				scrollButtonPainter);

		scrollBarSkin.put(
				"ScrollBar:ScrollBarTrack[Disabled].backgroundPainter",
				scrollButtonPainter);
		scrollBarSkin.put("ScrollBar:ScrollBarTrack[Enabled].backgroundPainter",
				scrollButtonPainter);
		scrollBarSkin.put("ScrollBar:\"ScrollBar.button\".size",
				Integer.valueOf(0));

		UIManager.put("ScrollBar.width", 7);

		//SynthScrollBarUI basic = new SynthScrollBarUI();
//        BasicTableHeaderUI headerUI=new BasicTableHeaderUI();
//
//        UIManager.put("ScrollBarUI",basic);
//        UIManager.put("TableHeaderUI",headerUI);

//        UIManager.put("ComboBox[Enabled+Selected].backgroundPainter", new Painter() {
//            @Override
//            public void paint(Graphics2D g, Object object, int width, int height) {
//                g.setColor(new Color(240,240,240));
//                g.drawRect(0,0,width-1,height-1);
////                g.setColor(Color.BLACK);
////                g.fill(new Rectangle(0,0,width,height));
//            }
//        });

//        UIManager.put("ComboBox[Focused+MouseOver].backgroundPainter", new Painter() {
//            @Override
//            public void paint(Graphics2D g, Object object, int width, int height) {
//                g.setColor(new Color(240,240,240));
//                g.fillRect(0,0,width-1,height-1);
////                g.setColor(Color.BLACK);
////                g.fill(new Rectangle(0,0,width,height));
//            }
//        });

//        UIManager.put("ComboBox[Focused+Pressed].backgroundPainter", new Painter() {
//            @Override
//            public void paint(Graphics2D g, Object object, int width, int height) {
//                g.setColor(new Color(240,240,240));
//                g.fillRect(0,0,width-1,height-1);
////                g.setColor(Color.BLACK);
////                g.fill(new Rectangle(0,0,width,height));
//            }
//        });

		splitPaneSkin.put(
				"SplitPane:SplitPaneDivider[Enabled].foregroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(Color.WHITE);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});

		config.put("temp.dir",
				PathUtils.combine(System.getProperty("user.home"),
						"snowflake-ssh" + File.separator + "temp",
						File.separator));

		config.put("app.dir", PathUtils.combine(System.getProperty("user.home"),
				"snowflake-ssh", File.separator));

		new File(config.get("app.dir").toString()).mkdirs();
		new File(config.get("temp.dir").toString()).mkdirs();

		loadFonts();

		loadSettings();

		loadSnippets();

		JFrame f = new JFrame("Snowflake");
		try {
			f.setIconImage(
					ImageIO.read(App.class.getResource("/snowflake-logo.png")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setSize(800, 600);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);

		f.add(new MainContent(f));
		f.setLocationRelativeTo(null);
		f.setVisible(true);

//        testDraw();

//        createSampleWindow();
//        createSampleWindow1();
	}

	private static void createVerticalScrollSkin() {
		Color c = new Color(240, 240, 240);
		splitPaneSkin1.put("SplitPane.contentMargins", new Insets(0, 0, 0, 0));
		splitPaneSkin1.put(
				"SplitPane:SplitPaneDivider[Enabled].backgroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(c);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
		splitPaneSkin1.put(
				"SplitPane:SplitPaneDivider[Enabled+Vertical].foregroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(c);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
		splitPaneSkin1.put(
				"SplitPane:SplitPaneDivider[Enabled].backgroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(c);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
		splitPaneSkin1.put(
				"SplitPane:SplitPaneDivider[Enabled].foregroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(c);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
		splitPaneSkin1.put(
				"SplitPane:SplitPaneDivider[Focused].backgroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(c);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
		splitPaneSkin1.put(
				"SplitPane:SplitPaneDivider[Enabled].foregroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(c);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
	}

	private static void createVerticalScrollSkin1() {
		Color c = Color.WHITE;
		splitPaneSkin2.put("SplitPane.contentMargins", new Insets(0, 0, 0, 0));
		splitPaneSkin2.put(
				"SplitPane:SplitPaneDivider[Enabled].backgroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(c);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
		splitPaneSkin2.put(
				"SplitPane:SplitPaneDivider[Enabled+Vertical].foregroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(c);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
		splitPaneSkin2.put(
				"SplitPane:SplitPaneDivider[Enabled].backgroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(c);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
		splitPaneSkin2.put(
				"SplitPane:SplitPaneDivider[Enabled].foregroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(c);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
		splitPaneSkin2.put(
				"SplitPane:SplitPaneDivider[Focused].backgroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(c);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
		splitPaneSkin2.put(
				"SplitPane:SplitPaneDivider[Enabled].foregroundPainter",
				new Painter() {
					@Override
					public void paint(Graphics2D g, Object object, int width,
							int height) {
						g.setColor(c);
						g.fill(new Rectangle(0, 0, width, height));
					}
				});
	}

	static JButton createFontAwesomeButton(String text, Color foreColor) {
		JButton btn = new JButton();
		btn.setForeground(foreColor);
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(false);
		btn.setFont(getFontAwesomeFont());
		btn.setText(text);
		return btn;
	}

	public static void loadFonts() {
		try (InputStream is = App.class
				.getResourceAsStream("/fontawesome-webfont.ttf")) {
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			fontAwesomeFont = font.deriveFont(Font.PLAIN, 14f);
			System.out.println("Font loaded");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//    static void testDraw() {
//        JComponent component = new JComponent() {
//            BufferedImage bufferedImage;
//
//            {
//                bufferedImage = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
//                Graphics2D g3 = bufferedImage.createGraphics();
//                g3.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
//                g3.setColor(Color.BLACK);
//                g3.setFont(getFontAwesomeFont().deriveFont(150.0f));
//                g3.drawString("\uf07b", 20, g3.getFontMetrics().getAscent() + 20);
//                g3.dispose();
//            }
//
//            @Override
//            protected void paintComponent(Graphics g) {
//
//
//                Graphics2D g2 = (Graphics2D) g;
//                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//                g2.setColor(Color.PINK);
//                g2.fillRect(0, 0, this.getWidth(), this.getHeight());
//                g2.drawImage(bufferedImage, 0, 0, this);
//            }
//        };
//
//        JFrame f = new JFrame("Title");
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        f.setSize(640, 480);
//        f.setLocationRelativeTo(null);
//        f.add(component);
//        f.setVisible(true);
//    }

	public synchronized static void loadSettings() {
		File file = new File(App.getConfig("app.dir"),
				AppConstants.CONFIG_DB_FILE);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		if (file.exists()) {
			try {
				settings = objectMapper.readValue(file,
						new TypeReference<Settings>() {
						});
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		settings = new Settings();
	}

	public synchronized static void saveSettings() {
		File file = new File(App.getConfig("app.dir"),
				AppConstants.CONFIG_DB_FILE);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.writeValue(file, settings);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized static void loadSnippets() {
		File file = new File(App.getConfig("app.dir"),
				AppConstants.SNIPPETS_FILE);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		if (file.exists()) {
			try {
				snippetItems = objectMapper.readValue(file,
						new TypeReference<List<SnippetItem>>() {
						});
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		snippetItems = new ArrayList<>();
	}

	public synchronized static void saveSnippets() {
		File file = new File(App.getConfig("app.dir"),
				AppConstants.SNIPPETS_FILE);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.writeValue(file, snippetItems);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<SnippetItem> getSnippetItems() {
		return snippetItems;
	}
}
