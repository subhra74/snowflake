package muon.ui.widgets;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;

import java.awt.*;

import static com.sun.jna.platform.win32.WinDef.*;

public interface Dwmapi extends Library {
    public final static Dwmapi INSTANCE = Native.loadLibrary("dwmapi", Dwmapi.class);

    public static void applyRoundCorner(Window window){
        final HWND hwnd = new HWND();
        hwnd.setPointer(Native.getComponentPointer(window));
        INSTANCE.DwmSetWindowAttribute(hwnd, 33, new IntByReference(2), 4);
    }

    int DwmSetWindowAttribute(HWND hwnd, int dwAttribute, PointerType pvAttribute, int cbAttribute);
}
