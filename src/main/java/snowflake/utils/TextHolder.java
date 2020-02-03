package snowflake.utils;

import java.util.*;

public class TextHolder {
	private static Properties texts = new Properties();

	static {
		loadStrings();
	}

	public static String getString(String key) {
		return texts.getProperty(key);
	}

	public static void addString(String key, String value) {
		texts.setProperty(key, value);
	}

	private static void loadStrings() {
		TextHolder.addString("common.ok", "OK");
		TextHolder.addString("common.yes", "Yes");
		TextHolder.addString("common.no", "No");
		TextHolder.addString("common.save", "Save");
		TextHolder.addString("common.open", "Open");
		TextHolder.addString("common.cancel", "Cancel");
		TextHolder.addString("common.faied",
				"Connection failed, would you like to retry?");
		TextHolder.addString("common.confirm", "Confirm");

		TextHolder.addString("messagebox.ok", "OK");

		TextHolder.addString("host.name", "Host");
		TextHolder.addString("host.port", "Port");
		TextHolder.addString("host.user", "User");
		TextHolder.addString("host.pass", "Password");
		TextHolder.addString("host.localdir", "Local folder");
		TextHolder.addString("host.remotedir", "Remote folder");
		TextHolder.addString("host.keyfile", "Private key file");
		TextHolder.addString("host.browse", "Browse..");
		TextHolder.addString("sessionTab.viewDesktop", "Desktop view");
		TextHolder.addString("sessionTab.viewTabbed", "Tabbed view");
		TextHolder.addString("sessionTab.viewTiled", "Tiled view");
		TextHolder.addString("sessionTab.splitVertically", "Split vertically");
		TextHolder.addString("sessionTab.splitHorizontally",
				"Split Horizontally");
		TextHolder.addString("toolbar.localFileBrowser", "Local file browser");
		TextHolder.addString("toolbar.remoteFileBrowser",
				"Remote file browser");
		TextHolder.addString("toolbar.terminal", "Terminal");
		TextHolder.addString("toolbar.sysmon", "System monitor");
		TextHolder.addString("toolbar.transfers", "Idle");
		TextHolder.addString("toolbar.workspace", "Workspace");
		TextHolder.addString("message.NoHost",
				"Host name can not be left blank");
		TextHolder.addString("message.NoUser",
				"User name can not be left blank");
		TextHolder.addString("sessionTree.defaultText", "My sites");
		TextHolder.addString("sessionTree.defaultFolderText", "New folder");
		TextHolder.addString("session.newHost", "New site");
		TextHolder.addString("session.newFolder", "New folder");
		TextHolder.addString("session.remove", "Remove");
		TextHolder.addString("session.duplicate", "Duplicate");
		TextHolder.addString("session.connect", "Connect");
		TextHolder.addString("session.save", "Save");
		TextHolder.addString("session.cancel", "Close");
		TextHolder.addString("session.name", "Name");

		TextHolder.addString("transfers.pause", "Pause");
		TextHolder.addString("transfers.resume", "Resume");
		TextHolder.addString("transfers.remove", "Remove");
		TextHolder.addString("transfers.transferring", "Transferring");
		TextHolder.addString("transfers.stopped", "Stopped");
		TextHolder.addString("transfers.failed", "Failed");
		TextHolder.addString("transfers.finished", "Finished");

		TextHolder.addString("folderview.localtitle", "Local file browser");
		TextHolder.addString("folderview.sftptitle", "SFTP file browser");
		TextHolder.addString("folderview.opennewtab", "Open in new tab");
		TextHolder.addString("folderview.copyPath", "Copy path");
		TextHolder.addString("folderview.openterm", "Open in terminal");
		TextHolder.addString("folderview.createLink", "Create Link");
		TextHolder.addString("folderview.hardLink", "Hard link");
		TextHolder.addString("folderview.linkName", "Link name");
		TextHolder.addString("folderview.fileName", "File name");
		TextHolder.addString("folderview.run1", "Run in terminal");
		TextHolder.addString("folderview.run2", "Run script...");
		TextHolder.addString("folderview.normal", "Execute normally");
		TextHolder.addString("folderview.nohup", "Execute with nohup");
		TextHolder.addString("folderview.background", "Execute in backgrond");
		TextHolder.addString("folderview.command", "Arguments");
		TextHolder.addString("folderview.runoption", "Run options");
		TextHolder.addString("folderview.background", "Execute in backgrond");
		TextHolder.addString("folderview.logview", "Open with LogViewer");
		TextHolder.addString("folderview.upload", "Upload files here");
		TextHolder.addString("folderview.download", "Download files");
		TextHolder.addString("folderview.openDefault", "Default application");
		TextHolder.addString("folderview.openIntern", "Internal editor");
		TextHolder.addString("folderview.openCust", "External editor");
		TextHolder.addString("folderview.openLogView", "Log viewer");
		TextHolder.addString("folderview.openWith", "Open with");
		TextHolder.addString("folderview.open", "Open");
		TextHolder.addString("folderview.showHidden", "Show hidden files");
		TextHolder.addString("folderview.selectAll", "Select All");
		TextHolder.addString("folderview.clearSelection", "Clear selection");
		TextHolder.addString("folderview.noeditor",
				"No external editor has been configured. Would you like to select one?");
		TextHolder.addString("folderview.noeditortitle", "External editor");
		TextHolder.addString("folderview.inverseSelection",
				"Inverse selection");
		TextHolder.addString("folderview.selectByPattern", "Select by pattern");
		TextHolder.addString("folderview.unselectByPattern",
				"Unselect by pattern");
		TextHolder.addString("folderview.selectSimilar",
				"Select similar file types");
		TextHolder.addString("folderview.unSelectSimilar",
				"Unselect similar file types");
		TextHolder.addString("folderview.filter", "Filter files");
		TextHolder.addString("folderview.localtitle", "Local file browser");
		TextHolder.addString("folderview.confirmDelete",
				"Delete selected files?");

		TextHolder.addString("sysmon.error", "Operation failed");
		TextHolder.addString("sysmon.superuser", "Run as superuser");
		TextHolder.addString("sysmon.unsupported",
				"This os is not supported yet: %s");
		TextHolder.addString("sysmon.processTitle", "Processes");
		TextHolder.addString("sysmon.pollInterval", "Refresh interval");
		TextHolder.addString("sysmon.refresh", "Refresh");
		TextHolder.addString("sysmon.sysinfo", "System information");
		TextHolder.addString("sysmon.socketTitle", "Process and ports");
		TextHolder.addString("sysmon.serviceTitle", "Services (systemd)");
		TextHolder.addString("sysmon.loadTitle", "System load");
		TextHolder.addString("sysmon.diskTitle", "Diskspace usage");
		TextHolder.addString("sysmon.title", "System Monitor");
		TextHolder.addString("sysmon.clear", "Clear");
		TextHolder.addString("sysmon.filterTxt", "Filter");
		TextHolder.addString("sysmon.searchTxt",
				"Show entries containing search text");
		TextHolder.addString("sysmon.processFilter", "Filter process");
		TextHolder.addString("sysmon.processFilterApply", "Apply");
		TextHolder.addString("sysmon.processFilterClear", "Clear");
		TextHolder.addString("sysmon.killText", "Kill process");
		TextHolder.addString("sysmon.sigText", "Send signal");
		TextHolder.addString("sysmon.niceText", "Change priority");
		TextHolder.addString("sysmon.showAll", "Show processes from all users");
		TextHolder.addString("sysmon.service.start", "Start");
		TextHolder.addString("sysmon.service.stop", "Stop");
		TextHolder.addString("sysmon.service.enable", "Enable");
		TextHolder.addString("sysmon.service.disable", "Disable");
		TextHolder.addString("sysmon.service.reload", "Reload");
		TextHolder.addString("sysmon.service.restart", "Restart");
		TextHolder.addString("sysmon.service.search", "Search");

		TextHolder.addString("folderview.selectFolder", "Select Folder");
		TextHolder.addString("folderview.selectFile", "Select File");
		TextHolder.addString("folderview.genericError", "Operation failed");
		TextHolder.addString("folderview.select", "Select");
		TextHolder.addString("folderview.save", "Save");
		TextHolder.addString("folderview.cancel", "Cancel");
		TextHolder.addString("folderview.selected", "Selected");
		TextHolder.addString("folderview.reload", "Reload");
		TextHolder.addString("folderview.rename", "Rename");
		TextHolder.addString("folderview.delete", "Delete");
		TextHolder.addString("folderview.move", "Move to");
		TextHolder.addString("folderview.input", "Input");
		TextHolder.addString("folderview.newFile", "New file");
		TextHolder.addString("folderview.newFolder", "New folder");
		TextHolder.addString("folderview.renameTitle", "Rename file to");
		TextHolder.addString("folderview.sortByName", "Name");
		TextHolder.addString("folderview.sortBySize", "Size");
		TextHolder.addString("folderview.sortByType", "Type");
		TextHolder.addString("folderview.sortByModified", "Modified");
		TextHolder.addString("folderview.sortByPerm", "Permission");
		TextHolder.addString("folderview.owner", "Owner");
		TextHolder.addString("folderview.sortAsc", "Ascending");
		TextHolder.addString("folderview.sortDesc", "Descending");
		TextHolder.addString("folderview.sortBy", "Sort by");
		TextHolder.addString("folderview.renameFailed",
				"Failed to rename file/folder");
		TextHolder.addString("folderview.copy", "Copy");
		TextHolder.addString("folderview.paste", "Paste");
		TextHolder.addString("folderview.cut", "Cut");
		TextHolder.addString("folderview.bookmark", "Add to favourites");
		TextHolder.addString("folderview.editExternal",
				"Open with external editor");
		TextHolder.addString("folderview.openExternal",
				"Open with default app");
		TextHolder.addString("folderview.props", "Properties");
		TextHolder.addString("folderview.upload", "Upload files here");
		TextHolder.addString("folderview.download", "Download selected files");

		TextHolder.addString("archiver.unknownformat",
				"Format is not supported");
		TextHolder.addString("archiver.title", "Archiver");
		TextHolder.addString("archiver.stop", "Stop");
		TextHolder.addString("archiver.close", "Close");
		TextHolder.addString("archiver.exitcode", "Exit code: ");
		TextHolder.addString("archiver.error", "Error extracting file");
		TextHolder.addString("archiver.filename", "Archive name");
		TextHolder.addString("archiver.savein", "Save in");
		TextHolder.addString("archiver.browse", "Browse..");
		TextHolder.addString("archiver.format", "Format");
		TextHolder.addString("archiver.ok", "OK");
		TextHolder.addString("archiver.compressing", "Compressing...");
		TextHolder.addString("archiver.compress", "Compress");
		TextHolder.addString("archiver.extract", "Extract");
		TextHolder.addString("archiver.extractto", "Extract to");
		TextHolder.addString("archiver.extracthere", "Extract here");
		TextHolder.addString("archiver.preview", "View archive");
		TextHolder.addString("archiver.addext",
				"Automatically add extension: %s");
		TextHolder.addString("archiver.search", "Search");
		TextHolder.addString("archiver.open", "Open");
		TextHolder.addString("archiver.cancel", "Cancel");
		TextHolder.addString("archiver.extractto", "Extract to folder");

		TextHolder.addString("duplicate.overwrite", "Replace existing");
		TextHolder.addString("duplicate.skip", "Skip");
		TextHolder.addString("duplicate.rename", "Auto rename");
		TextHolder.addString("duplicate.cancel", "Cancel");
		TextHolder.addString("duplicate.apply",
				"Apply same action for other conflicts");
		TextHolder.addString("duplicate.prompt",
				"Below file already exists on target location\n%s\nPlease select an action");
		TextHolder.addString("duplicate.confirm", "Confirm");

		TextHolder.addString("editor.save", "Save");
		TextHolder.addString("editor.font", "Font");
		TextHolder.addString("editor.open", "Open");
		TextHolder.addString("editor.find", "Find");
		TextHolder.addString("editor.replace", "Replace");
		TextHolder.addString("editor.gotoline", "Go to Line");
		TextHolder.addString("editor.reload", "Reload");
		TextHolder.addString("editor.cutText", "Cut");
		TextHolder.addString("editor.pasteText", "Paste");
		TextHolder.addString("editor.copyText", "Copy");
		TextHolder.addString("editor.file", "File");
		TextHolder.addString("editor.edit", "Edit");
		TextHolder.addString("editor.options", "Options");
		TextHolder.addString("editor.help", "Help");
		TextHolder.addString("editor.new", "New");
		TextHolder.addString("editor.open", "Open");
		TextHolder.addString("editor.save", "Save");
		TextHolder.addString("editor.saveAs", "Save As..");
		TextHolder.addString("editor.exit", "Exit");
		TextHolder.addString("editor.undo", "Undo");
		TextHolder.addString("editor.redo", "Redo");
		TextHolder.addString("editor.cut", "Cut");
		TextHolder.addString("editor.copy", "Copy");
		TextHolder.addString("editor.paste", "Paste");
		TextHolder.addString("editor.findReplace", "Find/Replace");
		TextHolder.addString("editor.settings", "Settings");
		TextHolder.addString("editor.support", "Contents");
		TextHolder.addString("editor.about", "About");
		TextHolder.addString("editor.fontSize", "Font size");
		TextHolder.addString("editor.wrapText", "Wrap text");
		TextHolder.addString("editor.syntax", "Syntax");
		TextHolder.addString("editor.theme", "Theme");
		TextHolder.addString("editor.title", "Text Editor");

		TextHolder.addString("searchbox.search", "Find");
		TextHolder.addString("searchbox.replace", "Replace");
		TextHolder.addString("searchbox.replaceAll", "Replace all");
		TextHolder.addString("searchbox.ignoreCase", "Ignore case");
		TextHolder.addString("searchbox.regex", "Regular expression");
		TextHolder.addString("searchbox.reverse", "Reverse");
		TextHolder.addString("searchbox.wholeWord", "Whole word");
		TextHolder.addString("searchbox.close", "Close");

		TextHolder.addString("logviewer.all", "Full content");
		TextHolder.addString("logviewer.autoupdate", "Auto update");
		TextHolder.addString("logviewer.partial", "Show last");
		TextHolder.addString("logviewer.kb", "KB only");
		TextHolder.addString("logviewer.reload", "Reload");
		TextHolder.addString("logviewer.search", "Search");
		TextHolder.addString("logviewer.searchnext", "Find next");
		TextHolder.addString("logviewer.searchprev", "Find prev");
		TextHolder.addString("logviewer.onlymatched", "Show only matched");
		TextHolder.addString("logviewer.clearsearch", "Clear");
		TextHolder.addString("logviewer.openwithtitle", "Open with Log Viewer");
		TextHolder.addString("logviewer.title", "Log Viewer");
		TextHolder.addString("logviewer.pageCount", " / %d");
		TextHolder.addString("logviewer.liveMode", "Live mode");
		TextHolder.addString("logviewer.matchCase", "Match case");
		TextHolder.addString("logviewer.wholeWord", "Whole word");
		TextHolder.addString("logviewer.copy", "Copy");

		TextHolder.addString("logview.highlight.add", "Add");
		TextHolder.addString("logview.highlight.edit", "Edit");
		TextHolder.addString("logview.highlight.del", "Delete");
		TextHolder.addString("logview.highlight.description", "Description");
		TextHolder.addString("logview.highlight.pattern", "Pattern");
		TextHolder.addString("logview.highlight.color", "Highlight color");
		TextHolder.addString("logview.highlight.newHighlight",
				"New pattern highlight");
		TextHolder.addString("logview.highlight.blankField",
				"Description or pattern can not be left blank");
		TextHolder.addString("logview.highlight.pattern", "Pattern");
		TextHolder.addString("logview.highlight.title",
				"Logviewer pattern highlight");

		TextHolder.addString("filesearch.search", "Search");
		TextHolder.addString("filesearch.searchItemCount", "%d items");
		TextHolder.addString("filesearch.searchfor", "Search for");
		TextHolder.addString("filesearch.name", "In filename");
		TextHolder.addString("filesearch.filename", "Name");
		TextHolder.addString("filesearch.content", "In file content");
		TextHolder.addString("filesearch.compress",
				"Look inside compressed files");
		TextHolder.addString("filesearch.contains", "Name");
		TextHolder.addString("filesearch.folder", "Search in");
		TextHolder.addString("filesearch.size", "Size");
		TextHolder.addString("filesearch.eq", "Equal to");
		TextHolder.addString("filesearch.lt", "Less than");
		TextHolder.addString("filesearch.gt", "More than");
		TextHolder.addString("filesearch.mtime", "Modified");
		TextHolder.addString("filesearch.mtime1", "Any time");
		TextHolder.addString("filesearch.mtime2", "Today");
		TextHolder.addString("filesearch.mtime3", "This week");
		TextHolder.addString("filesearch.mtime4", "Between");
		TextHolder.addString("filesearch.from", "From");
		TextHolder.addString("filesearch.to", "To");
		TextHolder.addString("filesearch.find", "Find");
		TextHolder.addString("filesearch.idle", "Idle");
		TextHolder.addString("filesearch.searching", "Searching...");
		TextHolder.addString("filesearch.type", "Type");
		TextHolder.addString("filesearch.size", "Size");
		TextHolder.addString("filesearch.modified", "Modified");
		TextHolder.addString("filesearch.permission", "Permissions");
		TextHolder.addString("filesearch.links", "Link count");
		TextHolder.addString("filesearch.user", "User");
		TextHolder.addString("filesearch.group", "Group");
		TextHolder.addString("filesearch.filepath", "Path");
		TextHolder.addString("filesearch.file", "File");
		TextHolder.addString("filesearch.folder", "Folder");
		TextHolder.addString("filesearch.lookfor", "Look for");
		TextHolder.addString("filesearch.both", "Both file and folder");
		TextHolder.addString("filesearch.fileonly", "File only");
		TextHolder.addString("filesearch.folderonly", "Folder only");
		TextHolder.addString("filesearch.title", "File search");
		TextHolder.addString("filesearch.showInBrowser", "Show location");
		TextHolder.addString("filesearch.deletingLabel",
				"Deleting files, please wait...");
		TextHolder.addString("filesearch.deletingTitle", "Please wait...");
		TextHolder.addString("filesearch.delete", "Delete");
		TextHolder.addString("filesearch.download", "Download");

		TextHolder.addString("waiting.title", "Operation in progress");
		TextHolder.addString("waiting.message",
				"Operation in progress, please wait...");

		TextHolder.addString("downloader.urls", "Urls to download");
		TextHolder.addString("downloader.folder", "Download folder");
		TextHolder.addString("downloader.proxylabel", "Proxy configuration");
		TextHolder.addString("downloader.go", "Download");
		TextHolder.addString("downloader.title", "Download");
		TextHolder.addString("downloader.app", "Application");
		TextHolder.addString("downloader.httpdownload", "HTTP Download");
		TextHolder.addString("downloader.httpupload", "HTTP Upload");
		TextHolder.addString("downloader.sftpdownload", "SFTP Download");
		TextHolder.addString("downloader.sftpupload", "SFTP Upload");
		TextHolder.addString("downloader.ftpdownload", "FTP Download");
		TextHolder.addString("downloader.ftpupload", "FTP Upload");

		TextHolder.addString("uploader.url", "Upload url");
		TextHolder.addString("uploader.files", "Files to upload");
		TextHolder.addString("uploader.proxylabel", "Proxy configuration");
		TextHolder.addString("uploader.go", "Upload");
		TextHolder.addString("uploader.title", "Upload");
		TextHolder.addString("uploader.app", "Application");

		TextHolder.addString("filetransfer.title", "File transfer");
		TextHolder.addString("filetransfer.back", "Back");
		TextHolder.addString("filetransfer.sendto", "Send files");

		TextHolder.addString("appmenu.connect", "Connections");
		TextHolder.addString("appmenu.settings", "Settings");
		TextHolder.addString("appmenu.save", "Save session");
		TextHolder.addString("appmenu.load", "Load session");
		TextHolder.addString("appmenu.delete", "Delete session");
		TextHolder.addString("appmenu.help", "Help");
		TextHolder.addString("desktop.start", "Start");

		TextHolder.addString("runas.title", "Run as");
		TextHolder.addString("runas.run", "Run");
		TextHolder.addString("runas.cancel", "Cancel");
		TextHolder.addString("runas.cmd", "Command");
		TextHolder.addString("runas.args", "Arguments");

		TextHolder.addString("duplicate.prompt",
				"Some files already exists, please select an action");
		TextHolder.addString("duplicate.autorename", "Autorename");
		TextHolder.addString("duplicate.overwrite", "Overwite");
		TextHolder.addString("duplicate.skip", "Skip");
		TextHolder.addString("duplicate.ok", "OK");
		TextHolder.addString("duplicate.cancel", "Cancel");
		TextHolder.addString("duplicate.failed",
				"An error occured while copying files, do you want to retry?");

		TextHolder.addString("edit.default", "Open with text editor");
		TextHolder.addString("edit.extern", "Open with system default app");

		TextHolder.addString("filebrowser.selected", "Path");

		TextHolder.addString("workspace.home", "Home");

		TextHolder.addString("ftp.title", "Ftp browser");

		TextHolder.addString("curl.paramName", "Name");
		TextHolder.addString("curl.paramValue", "Value");
		TextHolder.addString("curl.url", "Url");
		TextHolder.addString("curl.param", "Parameters");
		TextHolder.addString("curl.add", "Add");
		TextHolder.addString("curl.del", "Delete");
		TextHolder.addString("curl.exec", "Execute");
		TextHolder.addString("curl.stop", "Stop");
		TextHolder.addString("curl.result", "Results");
		TextHolder.addString("curl.back", "Back");
		TextHolder.addString("curl.title", "cURL");

		TextHolder.addString("http.title", "Http client");
		TextHolder.addString("appmenu.multiTerm",
				"Run command on multiple servers");

		TextHolder.addString("elevated.title",
				"Perform operation as super user");
		TextHolder.addString("elevated.details",
				"Operation failed. Would you like to perform the operation as super user?");
		TextHolder.addString("elevated.prompt", "Elevation command to use");
		TextHolder.addString("elevated.ok", "Permform action");
		TextHolder.addString("elevated.cancel", "Cancel");

		TextHolder.addString("keygen.pubKeyTitle", "Public Key");
		TextHolder.addString("keygen.loading",
				"L o a d i n g,  p l e a s e  w a i t ...");
		TextHolder.addString("keygen.colHost", "Host");
		TextHolder.addString("keygen.colUser", "User");
		TextHolder.addString("keygen.colStatus", "Status");
		TextHolder.addString("keygen.chkCopyToRemoteServer",
				"Copy public key to remote server(s)");
		TextHolder.addString("keygen.add", "Add");
		TextHolder.addString("keygen.import", "Import");
		TextHolder.addString("keygen.delete", "Delete");
		TextHolder.addString("keygen.pubKey", "Public key");
		TextHolder.addString("keygen.remote",
				"Configure keys for connecting to other servers from this server (Remote keys)");
		TextHolder.addString("keygen.local",
				"Configure keys for connecting to remote servers from this local machine (Local keys)");
		TextHolder.addString("keygen.prompt", "Please select an option");
		TextHolder.addString("keygen.noKey",
				"No public key found in default location");
		TextHolder.addString("keygen.fileLabel", "Key loaded from: ");
		TextHolder.addString("keygen.loadFromFile", "Load key from file");
		TextHolder.addString("keygen.selectAnother",
				"Load key from another file");
		TextHolder.addString("keygen.genKey", "Generate new key");
		TextHolder.addString("keygen.copyDesc",
				"Servers to be accessed with this public key");
		TextHolder.addString("keygen.start", "Start configuration process");
		TextHolder.addString("keygen.remote2remote", "Remote to remote SSH");
		TextHolder.addString("keygen.local2remote", "Local to remote SSH");
		TextHolder.addString("keygen.promptPassphrase",
				"Use passphrase to protect private key (Optional)");
		TextHolder.addString("keygen.passphrase", "Passphrase");
		TextHolder.addString("keygen.warnoverwrite",
				"WARNING: This will overwrite the existing SSH key"
						+ "\n\nIf the key was being used to connect to other servers,"
						+ "\nconnection will fail."
						+ "\nYou have to reconfigure all the servers"
						+ "\nto use the new key\nDo you still want to continue?");
		TextHolder.addString("keygen.warn", "Warning");
		TextHolder.addString("keygen.hostName", "Hostname");
		TextHolder.addString("keygen.userName", "Username");
		TextHolder.addString("keygen.password", "Password");
		TextHolder.addString("keygen.addServer", "Add server details");
		TextHolder.addString("keygen.blankHost", "Host can not be blank");
		TextHolder.addString("keygen.blankUser", "User can not be blank");
		TextHolder.addString("keygen.emptyList", "Server list is blank");

		TextHolder.addString("app.title", "Nix Explorer");
		TextHolder.addString("app.connections", "Connected servers");
		TextHolder.addString("app.control.files", "File browser");
		TextHolder.addString("app.control.terminal", "Terminal");
		TextHolder.addString("app.control.editor", "Text Editor");
		TextHolder.addString("app.control.logviewer", "Log Viewer");
		TextHolder.addString("app.control.taskmgr", "Task Manager");
		TextHolder.addString("app.control.fileshare", "FXP / SCP");
		TextHolder.addString("app.control.curl", "cURL GUI");
		TextHolder.addString("app.control.search", "Find Files");
		TextHolder.addString("app.control.utility", "Utilities");
		TextHolder.addString("app.local.title", "Local Files");
		TextHolder.addString("app.remote.title", "Remote Files");
		TextHolder.addString("app.files.title", "Files");
		TextHolder.addString("app.control.disconnect", "Disconnect");
		TextHolder.addString("app.control.notification", "Notification");

		TextHolder.addString("app.auth.title", "Authorization");
		TextHolder.addString("app.auth.user", "User Name");
		TextHolder.addString("app.auth.pass", "Password");

		TextHolder.addString("app.control.settings", "Settings");

		TextHolder.addString("config.title.general", "General");
		TextHolder.addString("config.title.folderView", "File browser");
		TextHolder.addString("config.title.terminal", "Terminal");

		TextHolder.addString("config.general.useDarkTheme",
				"Use Dark Theme (Needs restart)");
		TextHolder.addString("config.general.confirmBeforeExit",
				"Confirm before exit");
		TextHolder.addString("config.general.timeout",
				"Connection timeout (in seconds) ");

		TextHolder.addString("config.folderview.caching", "Cache folder");
		TextHolder.addString("config.folderview.browse", "Browse");
		TextHolder.addString("config.folderview.externalEditor",
				"External editor");
		TextHolder.addString("config.folderview.autoReload",
				"Reload folder after operation");
		TextHolder.addString("config.folderview.sidePane", "Show side panel");
		TextHolder.addString("config.folderview.preferShell",
				"Prefer shell over sftp operation");
		TextHolder.addString("config.folderview.delete",
				"Confirm before delete");

		TextHolder.addString("config.folderview.dblClickText",
				"Double click action");

		TextHolder.addString("config.folderview.viewMode", "Sidebar view mode");
		TextHolder.addString("config.folderview.view", "View mode");

		TextHolder.addString("config.folderview.ListView", "List");
		TextHolder.addString("config.folderview.DetailsView", "Details");

		TextHolder.addString("config.folderview.openInTerminal",
				"Open in Terminal");
		TextHolder.addString("config.folderview.openWithExternalEditor",
				"Open with External Editor");
		TextHolder.addString("config.folderview.openWithSystemDefaultApp",
				"Open with System Default App");
		TextHolder.addString("config.folderview.openWithTextEditor",
				"Open with Text Editor");

		TextHolder.addString("config.folderview.treeView", "Tree view");
		TextHolder.addString("config.folderview.listView", "List view");

		TextHolder.addString("config.terminal.select", "Select color");
		TextHolder.addString("config.terminal.foregroundColor", "Text color");
		TextHolder.addString("config.terminal.backgroundColor",
				"Background color");
		TextHolder.addString("config.terminal.fontSize", "Font size");
		TextHolder.addString("config.terminal.x11CopyPaste", "X11 copy paste");
		TextHolder.addString("config.button.save", "Save");
		TextHolder.addString("config.button.cancel", "Cancel");

		TextHolder.addString("pfapp.local", "Local port forwarding");
		TextHolder.addString("pfapp.remote", "Remote port forwarding");
		TextHolder.addString("pfapp.dynamic", "Dynamic port forwarding");

		TextHolder.addString("snippet.title", "Command snippets");
		TextHolder.addString("snippet.add", "New");
		TextHolder.addString("snippet.delete", "Delete");
		TextHolder.addString("snippet.edit", "Edit");
		TextHolder.addString("snippet.alt", "ALT");
		TextHolder.addString("snippet.shift", "SHIFT");
		TextHolder.addString("snippet.ctrl", "CTRL");
		TextHolder.addString("snippet.new", "New snippet");
		TextHolder.addString("snippet.chars", "Key combination");
		TextHolder.addString("snippet.missingName",
				"Name is missing,\nplease enter an valid name for snippet");
		TextHolder.addString("snippet.missingCommand",
				"Command is missing,\nplease enter an valid command for snippet");
		TextHolder.addString("snippet.noCharSelected",
				"No key combination is selected");
		TextHolder.addString("snippet.command", "Command to execute");
		TextHolder.addString("snippet.name", "Snippet name");
		TextHolder.addString("snippet.key", "Key combination");

		TextHolder.addString("terminal.snippet", "Command shortcuts");
		TextHolder.addString("terminal.manageSnippets", "Manage");
		TextHolder.addString("terminal.reconnect", "Reconnect");
		TextHolder.addString("terminal.reconnectText",
				"Connection interrupted");

		TextHolder.addString("diskUsageViewer.title", "Disk Usage");
		TextHolder.addString("diskUsageViewer.fileName", "Directory name");
		TextHolder.addString("diskUsageViewer.fileSize", "Size");
		TextHolder.addString("diskUsageViewer.filePath", "Path");
		TextHolder.addString("diskUsageViewer.usage", "Usage");

		TextHolder.addString("diskUsageViewer.targetLabel", "Directory");
		TextHolder.addString("diskUsageViewer.go", "Analyze");

		TextHolder.addString("viewMode.listViewText", "List view");
		TextHolder.addString("viewMode.detailsViewText", "Details view");

		TextHolder.addString("fileselection.empty", "No item selected");

		TextHolder.addString("proxy.type", "Proxy type");
		TextHolder.addString("proxy.host", "Proxy host");
		TextHolder.addString("proxy.port", "Proxy port");
		TextHolder.addString("proxy.user", "Proxy user");
		TextHolder.addString("proxy.pass", "Proxy password");
		TextHolder.addString("pass.warn", " ( Warning: it will be saved in plain text! )");
	}
}
