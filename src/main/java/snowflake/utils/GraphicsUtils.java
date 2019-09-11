package snowflake.utils;

import snowflake.App;
import snowflake.components.common.CustomButtonPainter;
import snowflake.components.common.CustomScrollBarUI;

import javax.swing.*;
import java.awt.*;

public class GraphicsUtils {
    public static JButton createSkinnedButton(Color c1, Color c2, Color c3) {
        UIDefaults btnSkin = new UIDefaults();
        CustomButtonPainter cs = new CustomButtonPainter(c1, c2, c3);
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
        JButton btn = new JButton();
        btn.putClientProperty("Nimbus.Overrides", btnSkin);
        return btn;
    }

    public static UIDefaults creatTextFieldSkin(UIDefaults uiDefaults) {
        Painter<JTextField> focusedBorder = new Painter<JTextField>() {
            @Override
            public void paint(Graphics2D g, JTextField object, int width, int height) {
                g.setColor(new Color(200, 200, 200));
                g.drawRect(0, 0, width - 1, height - 1);
            }
        };

        Painter<JTextField> normalBorder = new Painter<JTextField>() {
            @Override
            public void paint(Graphics2D g, JTextField object, int width, int height) {
                g.setColor(new Color(240, 240, 240));
                g.drawRect(0, 0, width - 1, height - 1);
            }
        };
        uiDefaults.put("TextField[Disabled].borderPainter", normalBorder);
        uiDefaults.put("TextField[Enabled].borderPainter", normalBorder);
        uiDefaults.put("TextField[Focused].borderPainter", focusedBorder);
        return uiDefaults;
    }

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
        Painter<? extends JComponent> focusedBorder = new Painter<JComponent>() {
            @Override
            public void paint(Graphics2D g, JComponent object, int width, int height) {
                g.setColor(new Color(200, 200, 200));
                g.drawRect(0, 0, width - 1, height - 1);
            }
        };

        Painter<? extends JComponent> normalBorder = new Painter<JComponent>() {
            @Override
            public void paint(Graphics2D g, JComponent object, int width, int height) {
                g.setColor(new Color(240, 240, 240));
                g.drawRect(0, 0, width - 1, height - 1);
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
        return uiDefaults;
    }

    public static UIDefaults createSpinnerSkin(UIDefaults uiDefaults) {
        Painter<? extends JComponent> painter1 = new Painter<JComponent>() {
            @Override
            public void paint(Graphics2D g, JComponent object, int width, int height) {
                g.setColor(new Color(200, 200, 200));
                g.fillRect(0, 0, width - 1, height - 1);
            }
        };

        Painter<? extends JComponent> painter2 = new Painter<JComponent>() {
            @Override
            public void paint(Graphics2D g, JComponent object, int width, int height) {
                g.setColor(new Color(240, 240, 240));
                g.fillRect(0, 0, width - 1, height - 1);
            }
        };

        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Disabled].backgroundPainter", painter2);
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Enabled].backgroundPainter", painter2);
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Focused+MouseOver].backgroundPainter", painter1);
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Focused+Pressed].backgroundPainter", painter1);
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Focused].backgroundPainter", painter2);
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[MouseOver].backgroundPainter", painter1);
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Pressed].backgroundPainter", painter1);

        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Disabled].backgroundPainter", painter2);
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Enabled].backgroundPainter", painter2);
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Focused+MouseOver].backgroundPainter", painter1);
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Focused+Pressed].backgroundPainter", painter1);
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Focused].backgroundPainter", painter2);
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[MouseOver].backgroundPainter", painter1);
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Pressed].backgroundPainter", painter1);

        uiDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\"[Enabled].backgroundPainter", painter2);
        uiDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\"[Focused].backgroundPainter", painter1);
        uiDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\"[Focused+Selected].backgroundPainter", painter1);
        return uiDefaults;
    }

    public static UIDefaults createComboBoxSkin(UIDefaults uiDefaults) {
        Painter<? extends JComponent> painter1 = new Painter<JComponent>() {
            @Override
            public void paint(Graphics2D g, JComponent object, int width, int height) {
                g.setColor(new Color(200, 200, 200));
                g.fillRect(0, 0, width - 1, height - 1);
            }
        };

        Painter<? extends JComponent> painter2 = new Painter<JComponent>() {
            @Override
            public void paint(Graphics2D g, JComponent object, int width, int height) {
                g.setColor(new Color(240, 240, 240));
                g.fillRect(0, 0, width - 1, height - 1);
            }
        };

        Painter<? extends JComponent> painter3 = new Painter<JComponent>() {
            @Override
            public void paint(Graphics2D g, JComponent object, int width, int height) {
//                g.setColor(new Color(200, 200, 200));
//                g.drawRect(0, 0, width - 1, height - 1);
            }
        };

        Painter<? extends JComponent> painter4 = new Painter<JComponent>() {
            @Override
            public void paint(Graphics2D g, JComponent object, int width, int height) {
                g.setColor(new Color(240, 240, 240));
                g.drawRect(0, 0, width - 1, height - 1);
            }
        };

        Painter<? extends JComponent> painter5 = new Painter<JComponent>() {
            @Override
            public void paint(Graphics2D g, JComponent object, int width, int height) {
                g.setColor(new Color(200, 200, 200));
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
        //Painter painter = (Painter) uiDefaults.get("ComboBox:\"ComboBox.arrowButton\"[MouseOver].foregroundPainter");
//        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[MouseOver].foregroundPainter",
//                painter6);
//
//        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Pressed].foregroundPainter",
//                painter6);
//        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Selected].foregroundPainter",
//                painter6);
        return uiDefaults;
    }

    public static JButton createButton(String text) {
        Color c1 = new Color(3, 155, 229);
        Color c2 = new Color(2, 132, 195);
        Color c3 = new Color(70, 130, 180);
        JButton btn = GraphicsUtils.createSkinnedButton(c1, c2, c3);
        btn.setText(text);
        btn.setForeground(Color.WHITE);
        return btn;
    }

    public static JScrollPane createScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        JScrollBar verticalScroller = new JScrollBar(JScrollBar.VERTICAL);
        verticalScroller.setUI(new CustomScrollBarUI());

        //verticalScroller.putClientProperty("Nimbus.Overrides", App.scrollBarSkin);
        scrollPane.setVerticalScrollBar(verticalScroller);

        JScrollBar horizontalScroller = new JScrollBar(JScrollBar.HORIZONTAL);
        horizontalScroller.setUI(new CustomScrollBarUI());
        scrollPane.setHorizontalScrollBar(horizontalScroller);
        return scrollPane;
    }
}
