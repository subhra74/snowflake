package com.jediterm.terminal.util;

//import com.google.common.base.*;
import com.jediterm.terminal.emulator.charset.*;
import com.jediterm.terminal.model.*;

import muon.terminal.Ascii;

import java.util.*;

/**
 * @author traff
 */
public class CharUtils {
	public static final int ESC = Ascii.ESC;
	public static final int DEL = Ascii.DEL;

	// NUL can only be at the end of the line
	public static final char NUL_CHAR = 0x0;
	public static final char EMPTY_CHAR = ' ';

	// JediTerm Unicode private use area U+100000–U+10FFFD
	public static final char DWC = '\uE000'; // Second part of double-width
												// character

	private CharUtils() {
	}

	private static final String[] NONPRINTING_NAMES = { "NUL", "SOH", "STX",
			"ETX", "EOT", "ENQ", "ACK", "BEL", "BS", "TAB", "LF", "VT", "FF",
			"CR", "S0", "S1", "DLE", "DC1", "DC2", "DC3", "DC4", "NAK", "SYN",
			"ETB", "CAN", "EM", "SUB", "ESC", "FS", "GS", "RS", "US" };

	public static byte[] VT102_RESPONSE = makeCode(ESC, '[', '?', '6', 'c');

	public static String getNonControlCharacters(int maxChars, char[] buf,
			int offset, int charsLength) {
		int len = Math.min(maxChars, charsLength);

		final int origLen = len;
		char tmp;
		while (len > 0) {
			tmp = buf[offset++];
			if (0x20 <= tmp) { // stop when we reach control chars
				len--;
				continue;
			}
			offset--;
			break;
		}

		int length = origLen - len;

		return new String(buf, offset - length, length);
	}

	public static int countDoubleWidthCharacters(char[] buf, int start,
			int length, boolean ambiguousIsDWC) {
		int cnt = 0;
		for (int i = 0; i < length; i++) {
			int ucs = Character.codePointAt(buf, i + start);
			if (isDoubleWidthCharacter(ucs, ambiguousIsDWC)) {
				cnt++;
			}
		}

		return cnt;
	}

	public enum CharacterType {
		NONPRINTING, PRINTING, NONASCII, NONE
	}

	public static CharacterType appendChar(final StringBuilder sb,
			final CharacterType last, final char c) {
		if (c <= 0x1F) {
			sb.append(EMPTY_CHAR);
			sb.append(NONPRINTING_NAMES[c]);
			return CharacterType.NONPRINTING;
		} else if (c == DEL) {
			sb.append(" DEL");
			return CharacterType.NONPRINTING;
		} else if (c > 0x1F && c <= 0x7E) {
			if (last != CharacterType.PRINTING)
				sb.append(EMPTY_CHAR);
			sb.append(c);
			return CharacterType.PRINTING;
		} else {
			sb.append(" 0x").append(Integer.toHexString(c));
			return CharacterType.NONASCII;
		}
	}

	public static void appendBuf(final StringBuilder sb, final char[] bs,
			final int begin, final int length) {
		CharacterType last = CharacterType.NONPRINTING;
		final int end = begin + length;
		for (int i = begin; i < end; i++) {
			final char c = (char) bs[i];
			last = appendChar(sb, last, c);
		}
	}

	public static byte[] makeCode(final int... bytesAsInt) {
		final byte[] bytes = new byte[bytesAsInt.length];
		int i = 0;
		for (final int byteAsInt : bytesAsInt) {
			bytes[i] = (byte) byteAsInt;
			i++;
		}
		return bytes;
	}

	/**
	 * Computes text length as sum of characters length, treating
	 * double-width(full-width) characters as 2, normal-width(half-width) as 1
	 * (Read http://en.wikipedia.org/wiki/Halfwidth_and_fullwidth_forms)
	 */
	public static int getTextLengthDoubleWidthAware(char[] buffer, int start,
			int length, boolean ambiguousIsDWC) {
		int result = 0;
		for (int i = start; i < start + length; i++) {
			result += (buffer[i] != CharUtils.DWC)
					&& isDoubleWidthCharacter(buffer[i], ambiguousIsDWC)
					&& !((i + 1 < start + length)
							&& (buffer[i + 1] == CharUtils.DWC)) ? 2 : 1;
		}
		return result;
	}

	public static boolean isDoubleWidthCharacter(int c,
			boolean ambiguousIsDWC) {
		if (c == DWC || c <= 0xa0 || (c > 0x452 && c < 0x1100)) {
			return false;
		}

		return mk_wcwidth(c, ambiguousIsDWC) == 2;
	}

	public static CharBuffer heavyDecCompatibleBuffer(CharBuffer buf) {
		char[] c = Arrays.copyOfRange(buf.getBuf(), 0, buf.getBuf().length);
		for (int i = 0; i < c.length; i++) {
			c[i] = CharacterSets.getHeavyDecBoxChar(c[i]);
		}
		return new CharBuffer(c, buf.getStart(), buf.length());
	}

	// The following code and data in converted from the
	// https://www.cl.cam.ac.uk/~mgk25/ucs/wcwidth.c
	// which can be treated a standard way to determine the width of a character

	private static final char[][] COMBINING = new char[][] {
			new char[] { 0x0300, 0x036F }, new char[] { 0x0483, 0x0486 },
			new char[] { 0x0488, 0x0489 }, new char[] { 0x0591, 0x05BD },
			new char[] { 0x05BF, 0x05BF }, new char[] { 0x05C1, 0x05C2 },
			new char[] { 0x05C4, 0x05C5 }, new char[] { 0x05C7, 0x05C7 },
			new char[] { 0x0600, 0x0603 }, new char[] { 0x0610, 0x0615 },
			new char[] { 0x064B, 0x065E }, new char[] { 0x0670, 0x0670 },
			new char[] { 0x06D6, 0x06E4 }, new char[] { 0x06E7, 0x06E8 },
			new char[] { 0x06EA, 0x06ED }, new char[] { 0x070F, 0x070F },
			new char[] { 0x0711, 0x0711 }, new char[] { 0x0730, 0x074A },
			new char[] { 0x07A6, 0x07B0 }, new char[] { 0x07EB, 0x07F3 },
			new char[] { 0x0901, 0x0902 }, new char[] { 0x093C, 0x093C },
			new char[] { 0x0941, 0x0948 }, new char[] { 0x094D, 0x094D },
			new char[] { 0x0951, 0x0954 }, new char[] { 0x0962, 0x0963 },
			new char[] { 0x0981, 0x0981 }, new char[] { 0x09BC, 0x09BC },
			new char[] { 0x09C1, 0x09C4 }, new char[] { 0x09CD, 0x09CD },
			new char[] { 0x09E2, 0x09E3 }, new char[] { 0x0A01, 0x0A02 },
			new char[] { 0x0A3C, 0x0A3C }, new char[] { 0x0A41, 0x0A42 },
			new char[] { 0x0A47, 0x0A48 }, new char[] { 0x0A4B, 0x0A4D },
			new char[] { 0x0A70, 0x0A71 }, new char[] { 0x0A81, 0x0A82 },
			new char[] { 0x0ABC, 0x0ABC }, new char[] { 0x0AC1, 0x0AC5 },
			new char[] { 0x0AC7, 0x0AC8 }, new char[] { 0x0ACD, 0x0ACD },
			new char[] { 0x0AE2, 0x0AE3 }, new char[] { 0x0B01, 0x0B01 },
			new char[] { 0x0B3C, 0x0B3C }, new char[] { 0x0B3F, 0x0B3F },
			new char[] { 0x0B41, 0x0B43 }, new char[] { 0x0B4D, 0x0B4D },
			new char[] { 0x0B56, 0x0B56 }, new char[] { 0x0B82, 0x0B82 },
			new char[] { 0x0BC0, 0x0BC0 }, new char[] { 0x0BCD, 0x0BCD },
			new char[] { 0x0C3E, 0x0C40 }, new char[] { 0x0C46, 0x0C48 },
			new char[] { 0x0C4A, 0x0C4D }, new char[] { 0x0C55, 0x0C56 },
			new char[] { 0x0CBC, 0x0CBC }, new char[] { 0x0CBF, 0x0CBF },
			new char[] { 0x0CC6, 0x0CC6 }, new char[] { 0x0CCC, 0x0CCD },
			new char[] { 0x0CE2, 0x0CE3 }, new char[] { 0x0D41, 0x0D43 },
			new char[] { 0x0D4D, 0x0D4D }, new char[] { 0x0DCA, 0x0DCA },
			new char[] { 0x0DD2, 0x0DD4 }, new char[] { 0x0DD6, 0x0DD6 },
			new char[] { 0x0E31, 0x0E31 }, new char[] { 0x0E34, 0x0E3A },
			new char[] { 0x0E47, 0x0E4E }, new char[] { 0x0EB1, 0x0EB1 },
			new char[] { 0x0EB4, 0x0EB9 }, new char[] { 0x0EBB, 0x0EBC },
			new char[] { 0x0EC8, 0x0ECD }, new char[] { 0x0F18, 0x0F19 },
			new char[] { 0x0F35, 0x0F35 }, new char[] { 0x0F37, 0x0F37 },
			new char[] { 0x0F39, 0x0F39 }, new char[] { 0x0F71, 0x0F7E },
			new char[] { 0x0F80, 0x0F84 }, new char[] { 0x0F86, 0x0F87 },
			new char[] { 0x0F90, 0x0F97 }, new char[] { 0x0F99, 0x0FBC },
			new char[] { 0x0FC6, 0x0FC6 }, new char[] { 0x102D, 0x1030 },
			new char[] { 0x1032, 0x1032 }, new char[] { 0x1036, 0x1037 },
			new char[] { 0x1039, 0x1039 }, new char[] { 0x1058, 0x1059 },
			new char[] { 0x1160, 0x11FF }, new char[] { 0x135F, 0x135F },
			new char[] { 0x1712, 0x1714 }, new char[] { 0x1732, 0x1734 },
			new char[] { 0x1752, 0x1753 }, new char[] { 0x1772, 0x1773 },
			new char[] { 0x17B4, 0x17B5 }, new char[] { 0x17B7, 0x17BD },
			new char[] { 0x17C6, 0x17C6 }, new char[] { 0x17C9, 0x17D3 },
			new char[] { 0x17DD, 0x17DD }, new char[] { 0x180B, 0x180D },
			new char[] { 0x18A9, 0x18A9 }, new char[] { 0x1920, 0x1922 },
			new char[] { 0x1927, 0x1928 }, new char[] { 0x1932, 0x1932 },
			new char[] { 0x1939, 0x193B }, new char[] { 0x1A17, 0x1A18 },
			new char[] { 0x1B00, 0x1B03 }, new char[] { 0x1B34, 0x1B34 },
			new char[] { 0x1B36, 0x1B3A }, new char[] { 0x1B3C, 0x1B3C },
			new char[] { 0x1B42, 0x1B42 }, new char[] { 0x1B6B, 0x1B73 },
			new char[] { 0x1DC0, 0x1DCA }, new char[] { 0x1DFE, 0x1DFF },
			new char[] { 0x200B, 0x200F }, new char[] { 0x202A, 0x202E },
			new char[] { 0x2060, 0x2063 }, new char[] { 0x206A, 0x206F },
			new char[] { 0x20D0, 0x20EF }, new char[] { 0x302A, 0x302F },
			new char[] { 0x3099, 0x309A }, new char[] { 0xA806, 0xA806 },
			new char[] { 0xA80B, 0xA80B }, new char[] { 0xA825, 0xA826 },
			new char[] { 0xFB1E, 0xFB1E }, new char[] { 0xFE00, 0xFE0F },
			new char[] { 0xFE20, 0xFE23 }, new char[] { 0xFEFF, 0xFEFF },
			new char[] { 0xFFF9, 0xFFFB } };

	private static final char[][] AMBIGUOUS = new char[][] {
			new char[] { 0x00A1, 0x00A1 }, { 0x00A4, 0x00A4 },
			{ 0x00A7, 0x00A8 }, new char[] { 0x00AA, 0x00AA },
			new char[] { 0x00AE, 0x00AE }, new char[] { 0x00B0, 0x00B4 },
			new char[] { 0x00B6, 0x00BA }, new char[] { 0x00BC, 0x00BF },
			new char[] { 0x00C6, 0x00C6 }, new char[] { 0x00D0, 0x00D0 },
			new char[] { 0x00D7, 0x00D8 }, new char[] { 0x00DE, 0x00E1 },
			new char[] { 0x00E6, 0x00E6 }, new char[] { 0x00E8, 0x00EA },
			new char[] { 0x00EC, 0x00ED }, new char[] { 0x00F0, 0x00F0 },
			new char[] { 0x00F2, 0x00F3 }, new char[] { 0x00F7, 0x00FA },
			new char[] { 0x00FC, 0x00FC }, new char[] { 0x00FE, 0x00FE },
			new char[] { 0x0101, 0x0101 }, new char[] { 0x0111, 0x0111 },
			new char[] { 0x0113, 0x0113 }, new char[] { 0x011B, 0x011B },
			new char[] { 0x0126, 0x0127 }, new char[] { 0x012B, 0x012B },
			new char[] { 0x0131, 0x0133 }, new char[] { 0x0138, 0x0138 },
			new char[] { 0x013F, 0x0142 }, new char[] { 0x0144, 0x0144 },
			new char[] { 0x0148, 0x014B }, new char[] { 0x014D, 0x014D },
			new char[] { 0x0152, 0x0153 }, new char[] { 0x0166, 0x0167 },
			new char[] { 0x016B, 0x016B }, new char[] { 0x01CE, 0x01CE },
			new char[] { 0x01D0, 0x01D0 }, new char[] { 0x01D2, 0x01D2 },
			new char[] { 0x01D4, 0x01D4 }, new char[] { 0x01D6, 0x01D6 },
			new char[] { 0x01D8, 0x01D8 }, new char[] { 0x01DA, 0x01DA },
			new char[] { 0x01DC, 0x01DC }, new char[] { 0x0251, 0x0251 },
			new char[] { 0x0261, 0x0261 }, new char[] { 0x02C4, 0x02C4 },
			new char[] { 0x02C7, 0x02C7 }, new char[] { 0x02C9, 0x02CB },
			new char[] { 0x02CD, 0x02CD }, new char[] { 0x02D0, 0x02D0 },
			new char[] { 0x02D8, 0x02DB }, new char[] { 0x02DD, 0x02DD },
			new char[] { 0x02DF, 0x02DF }, new char[] { 0x0391, 0x03A1 },
			new char[] { 0x03A3, 0x03A9 }, new char[] { 0x03B1, 0x03C1 },
			new char[] { 0x03C3, 0x03C9 }, new char[] { 0x0401, 0x0401 },
			new char[] { 0x0410, 0x044F }, new char[] { 0x0451, 0x0451 },
			new char[] { 0x2010, 0x2010 }, new char[] { 0x2013, 0x2016 },
			new char[] { 0x2018, 0x2019 }, new char[] { 0x201C, 0x201D },
			new char[] { 0x2020, 0x2022 }, new char[] { 0x2024, 0x2027 },
			new char[] { 0x2030, 0x2030 }, new char[] { 0x2032, 0x2033 },
			new char[] { 0x2035, 0x2035 }, new char[] { 0x203B, 0x203B },
			new char[] { 0x203E, 0x203E }, new char[] { 0x2074, 0x2074 },
			new char[] { 0x207F, 0x207F }, new char[] { 0x2081, 0x2084 },
			new char[] { 0x20AC, 0x20AC }, new char[] { 0x2103, 0x2103 },
			new char[] { 0x2105, 0x2105 }, new char[] { 0x2109, 0x2109 },
			new char[] { 0x2113, 0x2113 }, new char[] { 0x2116, 0x2116 },
			new char[] { 0x2121, 0x2122 }, new char[] { 0x2126, 0x2126 },
			new char[] { 0x212B, 0x212B }, new char[] { 0x2153, 0x2154 },
			new char[] { 0x215B, 0x215E }, new char[] { 0x2160, 0x216B },
			new char[] { 0x2170, 0x2179 }, new char[] { 0x2190, 0x2199 },
			new char[] { 0x21B8, 0x21B9 }, new char[] { 0x21D2, 0x21D2 },
			new char[] { 0x21D4, 0x21D4 }, new char[] { 0x21E7, 0x21E7 },
			new char[] { 0x2200, 0x2200 }, new char[] { 0x2202, 0x2203 },
			new char[] { 0x2207, 0x2208 }, new char[] { 0x220B, 0x220B },
			new char[] { 0x220F, 0x220F }, new char[] { 0x2211, 0x2211 },
			new char[] { 0x2215, 0x2215 }, new char[] { 0x221A, 0x221A },
			new char[] { 0x221D, 0x2220 }, new char[] { 0x2223, 0x2223 },
			new char[] { 0x2225, 0x2225 }, new char[] { 0x2227, 0x222C },
			new char[] { 0x222E, 0x222E }, new char[] { 0x2234, 0x2237 },
			new char[] { 0x223C, 0x223D }, new char[] { 0x2248, 0x2248 },
			new char[] { 0x224C, 0x224C }, new char[] { 0x2252, 0x2252 },
			new char[] { 0x2260, 0x2261 }, new char[] { 0x2264, 0x2267 },
			new char[] { 0x226A, 0x226B }, new char[] { 0x226E, 0x226F },
			new char[] { 0x2282, 0x2283 }, new char[] { 0x2286, 0x2287 },
			new char[] { 0x2295, 0x2295 }, new char[] { 0x2299, 0x2299 },
			new char[] { 0x22A5, 0x22A5 }, new char[] { 0x22BF, 0x22BF },
			new char[] { 0x2312, 0x2312 }, new char[] { 0x2460, 0x24E9 },
			new char[] { 0x24EB, 0x254B }, new char[] { 0x2550, 0x2573 },
			new char[] { 0x2580, 0x258F }, new char[] { 0x2592, 0x2595 },
			new char[] { 0x25A0, 0x25A1 }, new char[] { 0x25A3, 0x25A9 },
			new char[] { 0x25B2, 0x25B3 }, new char[] { 0x25B6, 0x25B7 },
			new char[] { 0x25BC, 0x25BD }, new char[] { 0x25C0, 0x25C1 },
			new char[] { 0x25C6, 0x25C8 }, new char[] { 0x25CB, 0x25CB },
			new char[] { 0x25CE, 0x25D1 }, new char[] { 0x25E2, 0x25E5 },
			new char[] { 0x25EF, 0x25EF }, new char[] { 0x2605, 0x2606 },
			new char[] { 0x2609, 0x2609 }, new char[] { 0x260E, 0x260F },
			new char[] { 0x2614, 0x2615 }, new char[] { 0x261C, 0x261C },
			new char[] { 0x261E, 0x261E }, new char[] { 0x2640, 0x2640 },
			new char[] { 0x2642, 0x2642 }, new char[] { 0x2660, 0x2661 },
			new char[] { 0x2663, 0x2665 }, new char[] { 0x2667, 0x266A },
			new char[] { 0x266C, 0x266D }, new char[] { 0x266F, 0x266F },
			new char[] { 0x273D, 0x273D }, new char[] { 0x2776, 0x277F },
			new char[] { 0xE000, 0xF8FF }, new char[] { 0xFFFD, 0xFFFD } };

	/* auxiliary function for binary search in interval table */
	static int bisearch(char ucs, char[][] table, int max) {
		int min = 0;
		int mid;

		if (ucs < table[0][0] || ucs > table[max][1])
			return 0;
		while (max >= min) {
			mid = (min + max) / 2;
			if (ucs > table[mid][1])
				min = mid + 1;
			else if (ucs < table[mid][0])
				max = mid - 1;
			else
				return 1;
		}

		return 0;
	}

	private static int mk_wcwidth(int ucs, boolean ambiguousIsDoubleWidth) {
		/* sorted list of non-overlapping intervals of non-spacing characters */
		/*
		 * generated by
		 * "uniset +cat=Me +cat=Mn +cat=Cf -00AD +1160-11FF +200B c"
		 */

		/* test for8-bnew char[]it control characters */
		if (ucs == 0)
			return 0;
		if (ucs < 32 || (ucs >= 0x7f && ucs < 0xa0))
			return -1;

		if (ambiguousIsDoubleWidth) {
			if (bisearch((char) ucs, AMBIGUOUS, AMBIGUOUS.length - 1) > 0) {
				return 2;
			}
		}

		/* binary search in table of non-spacing characters */
		if (bisearch((char) ucs, COMBINING, COMBINING.length - 1) > 0) {
			return 0;
		}

		/*
		 * if we arrive here, ucs is not a combining or C0/C1 control character
		 */

		return 1 + ((ucs >= 0x1100
				&& (ucs <= 0x115f || /* Hangul Jamo init. consonants */
						ucs == 0x2329 || ucs == 0x232a
						|| (ucs >= 0x2e80 && ucs <= 0xa4cf && ucs != 0x303f)
						|| /* CJK ... Yi */
						(ucs >= 0xac00 && ucs <= 0xd7a3)
						|| /* Hangul Syllables */
						(ucs >= 0xf900 && ucs <= 0xfaff)
						|| /* CJK Compatibility Ideographs */
						(ucs >= 0xfe10 && ucs <= 0xfe19) || /* Vertical forms */
						(ucs >= 0xfe30 && ucs <= 0xfe6f)
						|| /* CJK Compatibility Forms */
						(ucs >= 0xff00 && ucs <= 0xff60)
						|| /* Fullwidth Forms */
						(ucs >= 0xffe0 && ucs <= 0xffe6)
						|| (ucs >= 0x20000 && ucs <= 0x2fffd)
						|| (ucs >= 0x30000 && ucs <= 0x3fffd))) ? 1 : 0);
	}
}
