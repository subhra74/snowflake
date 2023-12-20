package muon.screens.appwin.tabs.terminal;

import com.jediterm.core.Color;
import com.jediterm.terminal.*;
import com.jediterm.terminal.model.TerminalTypeAheadSettings;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
import com.jediterm.terminal.emulator.*;
import muon.styles.AppTheme;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class CustomizedSettingsProvider extends DefaultSettingsProvider {
    private int[] palleteColors = {0x000000, 0xcd0000, 0x00cd00, 0xcdcd00, 0x1e90ff, 0xcd00cd, 0x00cdcd, 0xe5e5e5,
            0x4c4c4c, 0xff0000, 0x00ff00, 0xffff00, 0x4682b4, 0xff00ff, 0x00ffff, 0xffffff};
    private ColorPalette palette;

    /**
     *
     */
    public CustomizedSettingsProvider() {
        palette = ColorPaletteImpl.WINDOWS_PALETTE;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jediterm.terminal.ui.settings.DefaultSettingsProvider#
     * getTerminalColorPalette()
     */
    @Override
    public ColorPalette getTerminalColorPalette() {
        return palette;
    }

    @Override
    public @NotNull TerminalTypeAheadSettings getTypeAheadSettings() {
        return TerminalTypeAheadSettings.DEFAULT;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jediterm.terminal.ui.settings.DefaultSettingsProvider#
     * useAntialiasing()
     */
    @Override
    public boolean useAntialiasing() {
        return true;
    }

    public final TerminalColor getTerminalColor(int rgb) {
        return TerminalColor.fromColor(new Color(rgb));
    }

    @Override
    public TextStyle getDefaultStyle() {
        return new TextStyle(getTerminalColor(AppTheme.INSTANCE.getForeground().getRGB()),
                getTerminalColor(AppTheme.INSTANCE.getBackground().getRGB()));
        // return terminalTheme.getDefaultStyle();
    }
//
//    @Override
//    public TextStyle getFoundPatternColor() {
//        return new TextStyle(getTerminalColor(App.getGlobalSettings().getDefaultFoundFg()),
//                getTerminalColor(App.getGlobalSettings().getDefaultFoundBg()));
//        // return terminalTheme.getFoundPatternColor();
//    }
//
//    @Override
//    public TextStyle getSelectionColor() {
//        return new TextStyle(getTerminalColor(App.getGlobalSettings().getDefaultSelectionFg()),
//                getTerminalColor(App.getGlobalSettings().getDefaultSelectionBg()));
//        //
//    }
//
//    @Override
//    public TextStyle getHyperlinkColor() {
//        return new TextStyle(getTerminalColor(App.getGlobalSettings().getDefaultHrefFg()),
//                getTerminalColor(App.getGlobalSettings().getDefaultHrefBg()));
//        // return
//        // terminalTheme.getHyperlinkColor();
//    }
//
//    @Override
//    public boolean emulateX11CopyPaste() {
//        return App.getGlobalSettings().isPuttyLikeCopyPaste();
//    }
//
//    @Override
//    public boolean enableMouseReporting() {
//        return true;
//    }
//
////	@Override
////	public Font getTerminalFont() {
////		return UIManager.getFont("Terminal.font");
////	}
//
//    @Override
//    public boolean pasteOnMiddleMouseClick() {
//        return App.getGlobalSettings().isPuttyLikeCopyPaste();
//    }
//
//    @Override
//    public boolean copyOnSelect() {
//        return App.getGlobalSettings().isPuttyLikeCopyPaste();
//    }
//
//    @Override
//    public Font getTerminalFont() {
//        System.out.println("Called terminal font: " + App.getGlobalSettings().getTerminalFontName());
//        return FontUtils.loadTerminalFont(App.getGlobalSettings().getTerminalFontName()).deriveFont(Font.PLAIN,
//                App.getGlobalSettings().getTerminalFontSize());
////
////		return new Font(App.getGlobalSettings().getTerminalFontName(), Font.PLAIN,
////				App.getGlobalSettings().getTerminalFontSize());
//    }
//
//    @Override
//    public float getTerminalFontSize() {
//        return App.getGlobalSettings().getTerminalFontSize();
//    }
//
//    @Override
//    public boolean audibleBell() {
//        return App.getGlobalSettings().isTerminalBell();
//    }
//

//
//    @Override
//    public KeyStroke[] getCopyKeyStrokes() {
//        return new KeyStroke[]{getKeyStroke(Settings.COPY_KEY)};
//    }
//
//    @Override
//    public KeyStroke[] getPasteKeyStrokes() {
//        return new KeyStroke[]{getKeyStroke(Settings.PASTE_KEY)};
//    }
//
//    @Override
//    public KeyStroke[] getClearBufferKeyStrokes() {
//        return new KeyStroke[]{getKeyStroke(Settings.CLEAR_BUFFER)};
//    }
//
//    @Override
//    public KeyStroke[] getFindKeyStrokes() {
//        return new KeyStroke[]{getKeyStroke(Settings.FIND_KEY)};
//    }
//
//    private KeyStroke getKeyStroke(String key) {
//        return KeyStroke.getKeyStroke(App.getGlobalSettings().getKeyCodeMap().get(key),
//                App.getGlobalSettings().getKeyModifierMap().get(key));
//    }

}

