package muon.util;

import java.awt.*;
import java.io.InputStream;

public final class IconFont {
    public static final String RI_ADD_LINE= "\uea13";
    public static final String RI_MENU_LINE= "\uef3e";
    public static final String RI_INSTANCE_LINE= "\uf383";
    public static final String RI_ARROW_LEFT_LINE= "\uea60";
    public static final String RI_ARROW_RIGHT_LINE= "\uea6c";
    public static final String RI_ARROW_UP_LINE= "\uea76";
    public static final String RI_LOOP_RIGHT_LINE= "\uf33f";
    public static final String RI_MORE_2_LINE= "\uef77";
    public static final String RI_CLOSE_LINE= "\ueb99";

    public static IconFont getSharedInstance() {
        return IconFontHolder.me;
    }

    public Font getIconFont(float size) {
        return this.font.deriveFont(size);
    }

    private Font loadIconFont() {
        try (InputStream is = IconFont.class.getResourceAsStream("/fonts/remixicon.ttf")) {
            return Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Font font;

    private IconFont() {
        this.font = this.loadIconFont();
    }

    private static final class IconFontHolder {
        private static final IconFont me = new IconFont();
    }
}
