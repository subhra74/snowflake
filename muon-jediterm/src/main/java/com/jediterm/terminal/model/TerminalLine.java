package com.jediterm.terminal.model;

//import com.google.common.base.Joiner;
//import com.google.common.collect.Lists;
import com.jediterm.terminal.StyledTextConsumer;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.util.CharUtils;
import com.jediterm.terminal.util.Pair;
////import org.jetbrains.annotations.NotNull;
////import org.jetbrains.annotations.Nullable;
//import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author traff
 */
public class TerminalLine {

	private TextEntries myTextEntries = new TextEntries();
	private boolean myWrapped = false;

	public TerminalLine() {
	}

	public TerminalLine(TextEntry entry) {
		myTextEntries.add(entry);
	}

	public static TerminalLine createEmpty() {
		return new TerminalLine();
	}

	private List<TextEntry> newList(Iterable<TextEntry> items) {
		List<TextEntry> list = new ArrayList<>();
		for (TextEntry e : items) {
			list.add(e);
		}
		return list;
	}

	public synchronized String getText() {
		final StringBuilder sb = new StringBuilder();

		for (TextEntry textEntry : newList(myTextEntries)) {
			// NUL can only be at the end
			if (textEntry.getText().isNul()) {
				break;
			} else {
				sb.append(textEntry.getText());
			}
		}

		return sb.toString();
	}

	public char charAt(int x) {
		String text = getText();
		return x < text.length() ? text.charAt(x) : CharUtils.EMPTY_CHAR;
	}

	public boolean isWrapped() {
		return myWrapped;
	}

	public void setWrapped(boolean wrapped) {
		myWrapped = wrapped;
	}

	public synchronized void clear(TextEntry filler) {
		myTextEntries.clear();
		myTextEntries.add(filler);
		setWrapped(false);
	}

	public void writeString(int x, CharBuffer str, TextStyle style) {
		writeCharacters(x, style, str);
	}

	private synchronized void writeCharacters(int x, TextStyle style,
			CharBuffer characters) {
		int len = myTextEntries.length();

		if (x >= len) {
			// fill the gap
			if (x - len > 0) {
				myTextEntries.add(new TextEntry(TextStyle.EMPTY,
						new CharBuffer(CharUtils.NUL_CHAR, x - len)));
			}
			myTextEntries.add(new TextEntry(style, characters));
		} else {
			len = Math.max(len, x + characters.length());
			myTextEntries = merge(x, characters, style, myTextEntries, len);
		}
	}

	private static TextEntries merge(int x, CharBuffer str, TextStyle style,
			TextEntries entries, int lineLength) {
		Pair<char[], TextStyle[]> pair = toBuf(entries, lineLength);

		for (int i = 0; i < str.length(); i++) {
			pair.first[i + x] = str.charAt(i);
			pair.second[i + x] = style;
		}

		return collectFromBuffer(pair.first, pair.second);
	}

	private static Pair<char[], TextStyle[]> toBuf(TextEntries entries,
			int lineLength) {
		Pair<char[], TextStyle[]> pair = Pair.create(new char[lineLength],
				new TextStyle[lineLength]);

		int p = 0;
		for (TextEntry entry : entries) {
			for (int i = 0; i < entry.getLength(); i++) {
				pair.first[p + i] = entry.getText().charAt(i);
				pair.second[p + i] = entry.getStyle();
			}
			p += entry.getLength();
		}
		return pair;
	}

	private static TextEntries collectFromBuffer(char[] buf,
			TextStyle[] styles) {
		TextEntries result = new TextEntries();

		TextStyle curStyle = styles[0];
		int start = 0;

		for (int i = 1; i < buf.length; i++) {
			if (styles[i] != curStyle) {
				result.add(new TextEntry(curStyle,
						new CharBuffer(buf, start, i - start)));
				curStyle = styles[i];
				start = i;
			}
		}

		result.add(new TextEntry(curStyle,
				new CharBuffer(buf, start, buf.length - start)));

		return result;
	}

	public synchronized void deleteCharacters(int x) {
		deleteCharacters(x, TextStyle.EMPTY);
	}

	public synchronized void deleteCharacters(int x, TextStyle style) {
		deleteCharacters(x, myTextEntries.length() - x, style);
		// delete to the end of line : line is no more wrapped
		setWrapped(false);
	}

	public synchronized void deleteCharacters(int x, int count,
			TextStyle style) {
		int p = 0;
		TextEntries newEntries = new TextEntries();

		int remaining = count;

		for (TextEntry entry : myTextEntries) {
			if (remaining == 0) {
				newEntries.add(entry);
				continue;
			}
			int len = entry.getLength();
			if (p + len <= x) {
				p += len;
				newEntries.add(entry);
				continue;
			}
			int dx = x - p; // >=0
			if (dx > 0) {
				// part of entry before x
				newEntries.add(new TextEntry(entry.getStyle(),
						entry.getText().subBuffer(0, dx)));
				p = x;
			}
			if (dx + remaining < len) {
				// part that left after deleting count
				newEntries.add(new TextEntry(entry.getStyle(), entry.getText()
						.subBuffer(dx + remaining, len - (dx + remaining))));
				remaining = 0;
			} else {
				remaining -= (len - dx);
				p = x;
			}
		}
		if (count > 0 && style != TextStyle.EMPTY) { // apply style to the end
														// of the line
			newEntries.add(new TextEntry(style,
					new CharBuffer(CharUtils.NUL_CHAR, count)));
		}

		myTextEntries = newEntries;
	}

	public synchronized void insertBlankCharacters(int x, int count, int maxLen,
			TextStyle style) {
		int len = myTextEntries.length();
		len = Math.min(len + count, maxLen);

		char[] buf = new char[len];
		TextStyle[] styles = new TextStyle[len];

		int p = 0;
		for (TextEntry entry : myTextEntries) {
			for (int i = 0; i < entry.getLength() && p < len; i++) {
				if (p == x) {
					for (int j = 0; j < count && p < len; j++) {
						buf[p] = CharUtils.EMPTY_CHAR;
						styles[p] = style;
						p++;
					}
				}
				if (p < len) {
					buf[p] = entry.getText().charAt(i);
					styles[p] = entry.getStyle();
					p++;
				}
			}
			if (p >= len) {
				break;
			}
		}

		// if not inserted yet (ie. x > len)
		for (; p < x && p < len; p++) {
			buf[p] = CharUtils.EMPTY_CHAR;
			styles[p] = TextStyle.EMPTY;
			p++;
		}
		for (; p < x + count && p < len; p++) {
			buf[p] = CharUtils.EMPTY_CHAR;
			styles[p] = style;
			p++;
		}

		myTextEntries = collectFromBuffer(buf, styles);
	}

	public synchronized void clearArea(int leftX, int rightX, TextStyle style) {
		if (rightX == -1) {
			rightX = myTextEntries.length();
		}
		writeCharacters(leftX, style,
				new CharBuffer(
						rightX >= myTextEntries.length() ? CharUtils.NUL_CHAR
								: CharUtils.EMPTY_CHAR,
						rightX - leftX));
	}

	public synchronized TextStyle getStyleAt(int x) {
		int i = 0;

		for (TextEntry te : myTextEntries) {
			if (x >= i && x < i + te.getLength()) {
				return te.getStyle();
			}
			i += te.getLength();
		}

		return null;
	}

	public synchronized void process(int y, StyledTextConsumer consumer,
			int startRow) {
		int x = 0;
		int nulIndex = -1;
		for (TextEntry te : newList(myTextEntries)) {
			if (te.getText().isNul()) {
				if (nulIndex < 0) {
					nulIndex = x;
				}
				consumer.consumeNul(x, y, nulIndex, te.getStyle(), te.getText(),
						startRow);
			} else {
				consumer.consume(x, y, te.getStyle(), te.getText(), startRow);
			}
			x += te.getLength();
		}
		consumer.consumeQueue(x, y, nulIndex < 0 ? x : nulIndex, startRow);
	}

	public synchronized boolean isNul() {
		for (TextEntry e : myTextEntries.entries()) {
			if (!e.isNul()) {
				return false;
			}
		}

		return true;
	}

	public void runWithLock(Runnable r) {
		synchronized (this) {
			r.run();
		}
	}

	void forEachEntry(Consumer<TextEntry> action) {
		myTextEntries.forEach(action);
	}

	public List<TextEntry> getEntries() {
		return myTextEntries.entries();
	}

	void appendEntry(TextEntry entry) {
		myTextEntries.add(entry);
	}

	@Override
	public String toString() {
		return myTextEntries.length() + " chars, "
				+ (myWrapped ? "wrapped, " : "")
				+ myTextEntries.myTextEntries.size() + " entries: "
				+ String.join("|",
						myTextEntries.myTextEntries.stream()
								.map(entry -> entry.getText().toString())
								.collect(Collectors.toList()));
	}

	public static class TextEntry {
		private final TextStyle myStyle;
		private final CharBuffer myText;

		public TextEntry(TextStyle style, CharBuffer text) {
			myStyle = style;
			myText = text.clone();
		}

		public TextStyle getStyle() {
			return myStyle;
		}

		public CharBuffer getText() {
			return myText;
		}

		public int getLength() {
			return myText.length();
		}

		public boolean isNul() {
			return myText.isNul();
		}

		@Override
		public String toString() {
			return myText.length() + " chars, style: " + myStyle + ", text: "
					+ myText;
		}
	}

	private static class TextEntries implements Iterable<TextEntry> {
		private List<TextEntry> myTextEntries = new ArrayList<>();

		private int myLength = 0;

		public void add(TextEntry entry) {
			// NUL can only be at the end of the line
			if (!entry.getText().isNul()) {
				for (TextEntry t : myTextEntries) {
					if (t.getText().isNul()) {
						t.getText().unNullify();
					}
				}
			}
			myTextEntries.add(entry);
			myLength += entry.getLength();
		}

		private List<TextEntry> entries() {
			return Collections.unmodifiableList(myTextEntries);
		}

		public Iterator<TextEntry> iterator() {
			return entries().iterator();
		}

		public int length() {
			return myLength;
		}

		public void clear() {
			myTextEntries.clear();
			myLength = 0;
		}
	}
}
