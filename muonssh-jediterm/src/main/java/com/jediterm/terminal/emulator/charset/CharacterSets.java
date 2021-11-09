package com.jediterm.terminal.emulator.charset;

import com.jediterm.terminal.util.CharUtils;

/**
 * Provides the (graphical) character sets.
 */
public final class CharacterSets {
  private static final int C0_START = 0;
  private static final int C0_END = 31;
  private static final int C1_START = 128;
  private static final int C1_END = 159;
  private static final int GL_START = 32;
  private static final int GL_END = 127;
  private static final int GR_START = 160;
  private static final int GR_END = 255;

  public static final String[] ASCII_NAMES = {"<nul>", "<soh>", "<stx>", "<etx>", "<eot>", "<enq>", "<ack>", "<bell>",
    "\b", "\t", "\n", "<vt>", "<ff>", "\r", "<so>", "<si>", "<dle>", "<dc1>", "<dc2>", "<dc3>", "<dc4>", "<nak>",
    "<syn>", "<etb>", "<can>", "<em>", "<sub>", "<esc>", "<fs>", "<gs>", "<rs>", "<us>", " ", "!", "\"", "#", "$",
    "%", "&", "'", "(", ")", "*", "+", ",", "-", ".", "/", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ":",
    ";", "<", "=", ">", "?", "@", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
    "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "[", "\\", "]", "^", "_", "`", "a", "b", "c", "d", "e", "f",
    "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "{", "|",
    "}", "~", "<del>"};

  /**
   * Denotes the mapping for C0 characters.
   */
  public final static Object[][] C0_CHARS = {{0, "nul"}, //
    {0, "soh"}, //
    {0, "stx"}, //
    {0, "etx"}, //
    {0, "eot"}, //
    {0, "enq"}, //
    {0, "ack"}, //
    {0, "bel"}, //
    {(int) '\b', "bs"}, //
    {(int) '\t', "ht"}, //
    {(int) '\n', "lf"}, //
    {0, "vt"}, //
    {0, "ff"}, //
    {(int) '\r', "cr"}, //
    {0, "so"}, //
    {0, "si"}, //
    {0, "dle"}, //
    {0, "dc1"}, //
    {0, "dc2"}, //
    {0, "dc3"}, //
    {0, "dc4"}, //
    {0, "nak"}, //
    {0, "syn"}, //
    {0, "etb"}, //
    {0, "can"}, //
    {0, "em"}, //
    {0, "sub"}, //
    {0, "esq"}, //
    {0, "fs"}, //
    {0, "gs"}, //
    {0, "rs"}, //
    {0, "us"}};

  /**
   * Denotes the mapping for C1 characters.
   */
  public static final Object[][] C1_CHARS = {{0, null}, //
    {0, null}, //
    {0, null}, //
    {0, null}, //
    {0, "ind"}, //
    {0, "nel"}, //
    {0, "ssa"}, //
    {0, "esa"}, //
    {0, "hts"}, //
    {0, "htj"}, //
    {0, "vts"}, //
    {0, "pld"}, //
    {0, "plu"}, //
    {0, "ri"}, //
    {0, "ss2"}, //
    {0, "ss3"}, //
    {0, "dcs"}, //
    {0, "pu1"}, //
    {0, "pu2"}, //
    {0, "sts"}, //
    {0, "cch"}, //
    {0, "mw"}, //
    {0, "spa"}, //
    {0, "epa"}, //
    {0, null}, //
    {0, null}, //
    {0, null}, //
    {0, "csi"}, //
    {0, "st"}, //
    {0, "osc"}, //
    {0, "pm"}, //
    {0, "apc"}};

  /**
   * The DEC special characters (only the last 32 characters).
   * Contains [light][heavy] flavors for box drawing
   */
  public static final Object[][] DEC_SPECIAL_CHARS = {{'\u25c6', null}, // black_diamond
    {'\u2592', null}, // Medium Shade
    {'\u2409', null}, // Horizontal tab (HT)
    {'\u240c', null}, // Form Feed (FF)
    {'\u240d', null}, // Carriage Return (CR)
    {'\u240a', null}, // Line Feed (LF)
    {'\u00b0', null}, // Degree sign
    {'\u00b1', null}, // Plus/minus sign
    {'\u2424', null}, // New Line (NL)
    {'\u240b', null}, // Vertical Tab (VT)
    {'\u2518', '\u251b'}, // Forms up and left
    {'\u2510', '\u2513'}, // Forms down and left
    {'\u250c', '\u250f'}, // Forms down and right
    {'\u2514', '\u2517'}, // Forms up and right
    {'\u253c', '\u254b'}, // Forms vertical and horizontal
    {'\u23ba', null}, // Scan 1
    {'\u23bb', null}, // Scan 3
    {'\u2500', '\u2501'}, // Scan 5 / Horizontal bar
    {'\u23bc', null}, // Scan 7
    {'\u23bd', null}, // Scan 9
    {'\u251c', '\u2523'}, // Forms vertical and right
    {'\u2524', '\u252b'}, // Forms vertical and left
    {'\u2534', '\u253b'}, // Forms up and horizontal
    {'\u252c', '\u2533'}, // Forms down and horizontal
    {'\u2502', '\u2503'}, // vertical bar
    {'\u2264', null}, // less than or equal sign
    {'\u2265', null}, // greater than or equal sign
    {'\u03c0', null}, // pi
    {'\u2260', null}, // not equal sign
    {'\u00a3', null}, // pound sign
    {'\u00b7', null}, // middle dot
    {' ', null}, //
  };

  public static boolean isDecBoxChar(char c) {
    if (c < '\u2500' || c >= '\u2580') { // fast path
      return false;
    }
    for (Object[] o : DEC_SPECIAL_CHARS) {
      if (c == (Character) o[0]) {
        return true;
      }
    }
    return false;
  }
  
  public static char getHeavyDecBoxChar(char c) {
    if (c < '\u2500' || c >= '\u2580') { // fast path
      return c;
    }
    for (Object[] o : DEC_SPECIAL_CHARS) {
      if (c == (Character) o[0]) {
        return o[1] != null ? (Character) o[1] : c;
      }
    }
    return c;
  }

  /**
   * Creates a new {@link CharacterSets} instance, never used.
   */
  private CharacterSets() {
    // Nop
  }

  // METHODS

  /**
   * Returns the character mapping for a given original value using the given
   * graphic sets GL and GR.
   *
   * @param original the original character to map;
   * @param gl       the GL graphic set, cannot be <code>null</code>;
   * @param gr       the GR graphic set, cannot be <code>null</code>.
   * @return the mapped character.
   */
  public static char getChar(char original, GraphicSet gl, GraphicSet gr) {
    Object[] mapping = getMapping(original, gl, gr);

    int ch = (Integer) mapping[0];
    if (ch > 0) {
      return (char)ch;
    }

    return CharUtils.NUL_CHAR;
  }

  /**
   * Returns the name for the given character using the given graphic sets GL
   * and GR.
   *
   * @param original the original character to return the name for;
   * @param gl       the GL graphic set, cannot be <code>null</code>;
   * @param gr       the GR graphic set, cannot be <code>null</code>.
   * @return the character name.
   */
  public static String getCharName(char original, GraphicSet gl, GraphicSet gr) {
    Object[] mapping = getMapping(original, gl, gr);

    String name = (String)mapping[1];
    if (name == null) {
      name = String.format("<%d>", (int)original);
    }

    return name;
  }

  /**
   * Returns the mapping for a given character using the given graphic sets GL
   * and GR.
   *
   * @param original the original character to map;
   * @param gl       the GL graphic set, cannot be <code>null</code>;
   * @param gr       the GR graphic set, cannot be <code>null</code>.
   * @return the mapped character.
   */
  private static Object[] getMapping(char original, GraphicSet gl, GraphicSet gr) {
    int mappedChar = original;
    if (original >= C0_START && original <= C0_END) {
      int idx = original - C0_START;
      return C0_CHARS[idx];
    }
    else if (original >= C1_START && original <= C1_END) {
      int idx = original - C1_START;
      return C1_CHARS[idx];
    }
    else if (original >= GL_START && original <= GL_END) {
      int idx = original - GL_START;
      mappedChar = gl.map(original, idx);
    } 
    //To support UTF-8 we don't use GR table
    //TODO: verify that approach
    
    //else if (original >= GR_START && original <= GR_END) {
    //  int idx = original - GR_START;
    //  mappedChar = gr.map(original, idx);
    //}

    return new Object[]{mappedChar, null};
  }
}

