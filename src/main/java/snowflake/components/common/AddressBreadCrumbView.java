package snowflake.components.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.*;

public class AddressBreadCrumbView extends JComponent {
    private String text;
    private List<TextRect> rlist;
    private char separator;
    private List<ActionListener> listeners = new ArrayList<>();
    private String selectedText;
    private Icon icon;
    private int iconSize;
    private JPopupMenu popup;// , popup2;
    private Rectangle iconRect;
    private int hotIndex = -1;
    // private ActionListener openNewTabListener, openTerminal, addToFav;

    public AddressBreadCrumbView(char separator, ActionListener popupTriggerListener) {
        text = "";
        this.separator = separator;
        this.setFont(UIManager.getFont("Label.font"));
        this.icon = UIManager.getIcon("FileChooser.hardDriveIcon");
        iconSize = this.icon.getIconHeight();

        MouseAdapter ma = new MouseAdapter() {

            @Override
            public void mouseExited(MouseEvent e) {
                hotIndex = -1;
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                hotIndex = -1;
                Point p = e.getPoint();
                if (rlist == null) {
                    return;
                }
                for (int i = 0; i < rlist.size(); i++) {
                    TextRect r = rlist.get(i);

                    if (r.x != -1 && p.getX() > r.x && p.getX() < r.x + r.width) {
                        hotIndex = i;
                        repaint();
                        break;
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                if (iconRect != null) {
                    if (iconRect.contains(p)) {
                        createAndShowPopup();
                    }
                }
                for (int i = 0; i < rlist.size(); i++) {
                    TextRect r = rlist.get(i);

                    // System.out.println("r=" + r + " p=" + p);

                    if (r.x != -1 && p.getX() > r.x && p.getX() < r.x + r.width) {
                        StringBuilder sb = new StringBuilder();
                        for (int j = 0; j <= i; j++) {
                            sb.append(AddressBreadCrumbView.this.separator + rlist.get(j).text);
                        }
                        // System.out.println("matched: " + sb);
                        selectedText = sb.toString();
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            System.out.println("matched: " + sb);
                            if (popupTriggerListener != null) {
                                popupTriggerListener.actionPerformed(new ActionEvent(e, hashCode(), selectedText));
                            }
                        } else {
                            for (ActionListener l : listeners) {
                                System.out.println("Performing action");
                                l.actionPerformed(new ActionEvent(this, hashCode(), selectedText));
                            }
                        }
                        break;
                    }
                }
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);

    }

    @Override
    public Dimension getPreferredSize() {
        if (text != null) {
            Font font = UIManager.getFont("Label.font");
            int width = getFontMetrics(font).stringWidth(text) + 20;
            int height = getFontMetrics(font).getHeight() + 10;
            return new Dimension(width, height);
        } else {
            return super.getPreferredSize();
        }
    }

    public void setText(String text) {
        if (text == null) {
            System.out.println("Text is null");
            return;
        }
        this.text = text;
        rlist = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(UIManager.getFont("Label.font"));

        int h = g2.getFontMetrics().getHeight();
        int asc = g2.getFontMetrics().getAscent();

        Color borderColor = Color.GRAY;//UIManager.getColor("AddressBar.borderColor");
        // System.out.println(borderColor);
        Color bgColor = UIManager.getColor("control");
        Color color = UIManager.getColor("Label.foreground");
        Color activeColor = new Color(240,240,240);//UIManager.getColor("Table.selectionBackground");
        Color activeForeground = UIManager.getColor("Label.foreground");
        Color hotColor = Color.LIGHT_GRAY;//UIManager.getColor("AddressBar.hot");

        g2.setColor(bgColor);
        g2.fill(g2.getClipBounds());

        int paddingX = 10;//UIManager.getInt("AddressBar.textPaddingX");
        int paddingY = 10;//UIManager.getInt("AddressBar.textPaddingY");

        Rectangle r = getBounds();
        int width = r.width - 2 * (getInsets().left + getInsets().right) - 1 - iconSize;

        int realHeight = r.height - getInsets().top - getInsets().bottom - 1;

        try {
            this.iconRect = new Rectangle(getInsets().left, getInsets().top, iconSize, realHeight);
            if (borderColor != null) {
                // g2.setColor(borderColor);
                // g2.draw(this.iconRect);
            }

            if (rlist == null) {
                rlist = new ArrayList<>();
                String[] arr = text.split("[\\/\\\\]");

                for (int i = 0; i < arr.length; i++) {
                    String str = arr[i];
                    if (str.length() > 0) {
                        int w = g2.getFontMetrics().stringWidth(str);
                        int w1 = w + 2 * paddingX;
                        int h1 = r.height - getInsets().top - getInsets().bottom + 2 * paddingY;
                        TextRect tr = new TextRect(str, w, w1, h1);
                        rlist.add(tr);
                    }
                }
            }

            if (rlist.size() > 0) {
                int startIndex = -1;
                int total = 0;
                for (int i = rlist.size() - 1; i >= 0; i--) {
                    if (total + rlist.get(i).width > width) {
                        break;
                    }
                    total += rlist.get(i).width;
                    startIndex = i;
                }

                int x = getInsets().left + iconSize;
                int y = getInsets().top;

                // special case where even the last string does not fit
                // completely
                if (startIndex == -1) {
                    if (activeColor != null) {
                        Graphics2D g2bak = (Graphics2D) g2.create();
                        g2bak.setColor(activeColor);
                        g2bak.translate(x, y);
//                        Painter painter = (Painter) UIManager.get("Button[Default].backgroundPainter");
//                        painter.paint(g2bak, null, width, realHeight);
                        g2bak.fillRect(0, 0, width, realHeight);
                        g2bak.dispose();

//                        g2.setColor(activeColor);
//                        g2.fillRect(x, y, width, realHeight);
                    }
                    if (borderColor != null) {
                        // g2.setColor(borderColor);
                        // g2.drawRect(x, y, width, realHeight);
                    }

                    if (activeForeground != null) {
                        g2.setColor(activeForeground);
                    }

                    char[] carr = rlist.get(rlist.size() - 1).text.toCharArray();
                    int maxIndex = 0;
                    int dotWidth = g2.getFontMetrics().stringWidth("...");
                    int avail = width - 2 * paddingX - dotWidth;
                    if (avail < 1) {
                        g2.drawString("...", x + width / 2 - dotWidth / 2, asc + realHeight / 2 - h / 2);
                        return;
                    }
                    int cw = 0;
                    for (int i = 0; i < carr.length; i++) {
                        int cc = g2.getFontMetrics().charWidth(carr[i]);
                        if (cw + cc > avail) {
                            break;
                        }
                        maxIndex = i;
                        cw += cc;
                    }
                    g2.drawChars(carr, 0, maxIndex + 1, x + paddingX, asc + realHeight / 2 - h / 2);
                    g2.drawString("...", x + paddingX + cw, asc + realHeight / 2 - h / 2);
                    return;
                }

                for (int i = startIndex; i < rlist.size(); i++) {
                    TextRect tr = rlist.get(i);
                    int w = tr.width;
                    int sw = tr.stringWidth;
                    tr.x = x;
                    //Painter painter = (Painter) UIManager.get("Button[Default].backgroundPainter");
                    if (i == rlist.size() - 1) {
                        if (activeColor != null) {
                            g2.setColor(activeColor);
                            //painter = (Painter) UIManager.get("Button[Pressed].backgroundPainter");
                        }
                    } else {
                        if (hotIndex == i) {
                            if (hotColor != null) {
                                g2.setColor(hotColor);
                                //painter = (Painter) UIManager.get("Button[Default+MouseOver].backgroundPainter");
                            }
                        } else {
                            if (bgColor != null) {
                                g2.setColor(bgColor);
                                //painter = (Painter) UIManager.get("Button[Default].backgroundPainter");
                            }
                        }
                    }

                    Graphics2D g2bak = (Graphics2D) g2.create();
                    g2bak.translate(x, y);
                    //Painter painter=(Painter)UIManager.get("Button[Default].backgroundPainter");
                    //painter.paint(g2bak, null, w, realHeight);
                    g2bak.fillRect(0, 0, w, realHeight);
                    g2bak.dispose();
//-------------------------------------------------------------------------
                    //g2.fillRect(x, y, w, realHeight);

                    if (borderColor != null) {
                        g2.setColor(borderColor);
                    }

                    // g2.drawRect(x, y, w, realHeight);

                    int xPos = (int) (x + tr.width / 2 - sw / 2);
                    int yPos = (int) (asc + y + realHeight / 2 - h / 2);

                    if (i == rlist.size() - 1) {
                        if (activeForeground != null) {
                            g2.setColor(activeForeground);
                        }
                    } else {
                        if (color != null) {
                            g2.setColor(color);
                        }
                    }
                    g2.drawString(tr.text, xPos, yPos);
                    x += w;
                }
            }
        } finally {
            icon.paintIcon(this, g2, getInsets().left, getInsets().top + (realHeight / 2 - iconSize / 2));
            // g2.setColor(borderColor);
            // g2.drawRect(0, 0, getWidth(), getHeight()-1);
        }


    }

    class TextRect {
        public TextRect(String text, int stringWidth, int width, int height) {
            super();
            this.text = text;
            this.stringWidth = stringWidth;
            this.width = width;
            this.height = height;
        }

        String text;
        int stringWidth;
        int width, height;
        int x = -1;

        @Override
        public String toString() {
            return "TextRect [text=" + text + ", stringWidth=" + stringWidth + ", width=" + width + ", height=" + height
                    + ", x=" + x + "]";
        }
    }

    public String getSelectedText() {
        return selectedText;
    }

    public void setSelectedText(String selectedText) {
        this.selectedText = selectedText;
    }

    public void addActionListener(ActionListener e) {
        this.listeners.add(e);
    }

    private void createAndShowPopup() {
        if (popup == null) {
            popup = new JPopupMenu();
        } else {
            popup.removeAll();
        }

        if (separator == '/') {
            JMenuItem item = new JMenuItem("<ROOT>");
            item.putClientProperty("item.path", "/");
            item.addActionListener(e -> {
                selectedText = (String) item.getClientProperty("item.path");
                for (ActionListener l : listeners) {
                    l.actionPerformed(new ActionEvent(this, hashCode(), selectedText));
                }
            });
            popup.add(item);
        }

        if (this.rlist.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.rlist.size(); i++) {
                sb.append(separator + this.rlist.get(i).text);
                JMenuItem item = new JMenuItem(this.rlist.get(i).text);
                item.putClientProperty("item.path", sb.toString());
                item.addActionListener(e -> {
                    selectedText = (String) item.getClientProperty("item.path");
                    for (ActionListener l : listeners) {
                        l.actionPerformed(new ActionEvent(this, hashCode(), selectedText));
                    }
                });
                popup.add(item);
            }
        }

        if (separator == '\\') {
            File[] roots = File.listRoots();
            for (File f : roots) {
                JMenuItem item = new JMenuItem(f.getAbsolutePath());
                item.putClientProperty("item.path", f.getAbsolutePath());
                item.addActionListener(e -> {
                    selectedText = (String) item.getClientProperty("item.path");
                    for (ActionListener l : listeners) {
                        l.actionPerformed(new ActionEvent(this, hashCode(), selectedText));
                    }
                });
                popup.add(item);
            }
        }

        popup.show(this, 0, getHeight());
    }

}
