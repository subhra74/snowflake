package muon.app.ui.components.session.files.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import muon.app.App;
import muon.app.common.FileInfo;
import muon.app.common.FileType;
import util.FileIconUtil;
import util.FontAwesomeContants;
import util.FormatUtils;

import java.awt.*;

public class FolderViewListCellRenderer extends JPanel implements ListCellRenderer<FileInfo> {
	private JLabel lblIcon;
	private WrappedLabel lblText;

	public FolderViewListCellRenderer() {
		super(new BorderLayout(10, 5));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		lblIcon = new JLabel();
		lblIcon.setOpaque(true);
		lblIcon.setBorder(new EmptyBorder(0, 20, 0, 20));
		lblIcon.setHorizontalAlignment(JLabel.CENTER);
		lblIcon.setVerticalAlignment(JLabel.CENTER);
		lblIcon.setFont(App.SKIN.getIconFont().deriveFont(Font.PLAIN, 48.f));
		lblIcon.setText(FontAwesomeContants.FA_FOLDER);

		this.lblText = new WrappedLabel();

//		lblText = new JTextArea(2, 1);
//		lblText.setEditable(false);
//		lblText.set
//		lblText.setLineWrap(true);
		// lblText.setHorizontalAlignment(JLabel.CENTER);

		this.add(this.lblIcon);
		this.add(lblText, BorderLayout.SOUTH);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends FileInfo> list, FileInfo value, int index,
			boolean isSelected, boolean cellHasFocus) {
		this.lblIcon.setText(getIconForType(value));
		this.lblIcon.setBackground(list.getBackground());
		this.lblIcon.setForeground(isSelected ? list.getSelectionBackground() : list.getForeground());
		this.lblText.setBackground(list.getBackground());
		this.lblText.setForeground(isSelected ? list.getSelectionBackground() : list.getForeground());
		this.lblText.setText(value.getName());
		this.setBackground(list.getBackground());
		return this;
	}

	public String getIconForType(FileInfo ent) {
		return FileIconUtil.getIconForType(ent);
	}

//	JPanel panel = new JPanel(new BorderLayout(10, 5));
//	JLabel lblTitle, lx1, lx2;
//	Color lightText = Color.GRAY, lightTextSelected = new Color(230, 230, 230);
//	JLabel lblIcon;
//
//	public FolderViewListCellRenderer() {
//		lblIcon = new JLabel();
//		lblIcon.setBorder(new EmptyBorder(5, 5, 5, 5));
//		lblIcon.setText("\uf1c6");
//		lblIcon.setFont(App.SKIN.getIconFont().deriveFont(Font.PLAIN, 25.f));
//		lblIcon.setHorizontalAlignment(JLabel.CENTER);
//		lblIcon.setVerticalAlignment(JLabel.CENTER);
//		lblIcon.setForeground(Color.WHITE);
//		lblIcon.setBackground(new Color(92, 167, 25));
//		lblIcon.setOpaque(true);
////        lblIcon.setMinimumSize(new Dimension(40, 40));
////        lblIcon.setPreferredSize(new Dimension(40, 40));
//
//		lblTitle = new JLabel();
//		lblTitle.setForeground(new Color(80, 80, 80));
//		lblTitle.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
//		lblTitle.setAlignmentX(Box.LEFT_ALIGNMENT);
//
//		Box b32 = Box.createHorizontalBox();
//		lx1 = new JLabel();
//		lx1.setForeground(Color.GRAY);
//		lx2 = new JLabel();
//		lx2.setForeground(Color.GRAY);
//
//		b32.add(lx1);
//		b32.add(Box.createHorizontalGlue());
//		b32.add(lx2);
//		b32.setAlignmentX(Box.LEFT_ALIGNMENT);
//
//		panel.add(lblIcon, BorderLayout.WEST);
//		panel.setBackground(new Color(3, 155, 229));
//		Box b43 = Box.createVerticalBox();
//		b43.add(Box.createVerticalGlue());
//		b43.add(lblTitle);
//		b43.add(b32);
//		b43.add(Box.createVerticalGlue());
//
//		panel.add(b43);
//
//		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
//	}
//
//	@Override
//	public Component getListCellRendererComponent(
//			JList<? extends FileInfo> list, FileInfo value, int index,
//			boolean isSelected, boolean cellHasFocus) {
//		panel.setOpaque(isSelected);
//		lblTitle.setText(value.getName());
//		if (value.getType() == FileType.Directory
//				|| value.getType() == FileType.DirLink) {
//			lblIcon.setText("\uf07b");
//			lx1.setText(FormatUtils.formatDate(value.getLastModified()));
//		} else {
//			lblIcon.setText("\uf016");
//			lx1.setText(
//					FormatUtils.humanReadableByteCount(value.getSize(), true)
//							+ " - "
//							+ FormatUtils.formatDate(value.getLastModified()));
//		}
//
//		if (value.getUser() == null || value.getPermissionString() == null
//				|| value.getPermissionString().length() < 1) {
//			lx2.setText("");
//		} else {
//			lx2.setText(value.getUser() + " - " + value.getPermissionString());
//		}
//
//		if (isSelected) {
//			lblTitle.setForeground(Color.WHITE);
//			lx1.setForeground(lightTextSelected);
//			lx2.setForeground(lightTextSelected);
//		} else {
//			lblTitle.setForeground(Color.DARK_GRAY);
//			lx1.setForeground(lightText);
//			lx2.setForeground(lightText);
//		}
//
//		return panel;
//	}
}
