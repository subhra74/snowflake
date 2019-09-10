package snowflake.utils;

import snowflake.components.common.CustomButtonPainter;

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

    public static JButton createButton(String text) {
        Color c1 = new Color(3, 155, 229);
        Color c2 = new Color(2, 132, 195);
        Color c3 = new Color(70, 130, 180);
        JButton btn=GraphicsUtils.createSkinnedButton(c1, c2, c3);
        btn.setText(text);
        btn.setForeground(Color.WHITE);
        return btn;
    }
}
