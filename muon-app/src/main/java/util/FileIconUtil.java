package util;

import java.util.Locale;

import muon.app.common.FileInfo;
import muon.app.common.FileType;

public class FileIconUtil {
	public static String getIconForType(FileInfo ent) {
		if (ent.getType() == FileType.Directory || ent.getType() == FileType.DirLink) {
			return FontAwesomeContants.FA_FOLDER;
		}
		String name = ent.getName().toLowerCase(Locale.ENGLISH);
		if (name.endsWith(".zip") || name.endsWith(".tar") || name.endsWith(".tgz") || name.endsWith(".gz")
				|| name.endsWith(".bz2") || name.endsWith(".tbz2") || name.endsWith(".tbz") || name.endsWith(".txz")
				|| name.endsWith(".xz")) {
			return FontAwesomeContants.FA_FILE_ARCHIVE_O;
		} else if (name.endsWith(".mp3") || name.endsWith(".aac") || name.endsWith(".mp2") || name.endsWith(".wav")
				|| name.endsWith(".flac") || name.endsWith(".mpa") || name.endsWith(".m4a")) {
			return FontAwesomeContants.FA_FILE_AUDIO_O;
		} else if (name.endsWith(".c") || name.endsWith(".js") || name.endsWith(".cpp") || name.endsWith(".java")
				|| name.endsWith(".cs") || name.endsWith(".py") || name.endsWith(".pl") || name.endsWith(".rb")
				|| name.endsWith(".sql") || name.endsWith(".go") || name.endsWith(".ksh") || name.endsWith(".css")
				|| name.endsWith(".scss") || name.endsWith(".html") || name.endsWith(".htm") || name.endsWith(".ts")) {
			return FontAwesomeContants.FA_FILE_CODE_O;
		} else if (name.endsWith(".xls") || name.endsWith(".xlsx")) {
			return FontAwesomeContants.FA_FILE_EXCEL_O;
		} else if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".ico")
				|| name.endsWith(".gif") || name.endsWith(".svg")) {
			return FontAwesomeContants.FA_FILE_IMAGE_O;
		} else if (name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".m4v") || name.endsWith(".avi")) {
			return FontAwesomeContants.FA_FILE_VIDEO_O;
		} else if (name.endsWith(".pdf")) {
			return FontAwesomeContants.FA_FILE_PDF_O;
		} else if (name.endsWith(".ppt") || name.endsWith(".pptx")) {
			return FontAwesomeContants.FA_FILE_POWERPOINT_O;
		} else if (name.endsWith(".doc") || name.endsWith(".docx")) {
			return FontAwesomeContants.FA_FILE_WORD_O;
		}
		return FontAwesomeContants.FA_FILE;
	}
}
