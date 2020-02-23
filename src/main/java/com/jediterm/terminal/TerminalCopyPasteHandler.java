package com.jediterm.terminal;

////import org.jetbrains.annotations.NotNull;
////import org.jetbrains.annotations.Nullable;

public interface TerminalCopyPasteHandler {
	void setContents(String text,
			boolean useSystemSelectionClipboardIfAvailable);

	String getContents(boolean useSystemSelectionClipboardIfAvailable);
}
