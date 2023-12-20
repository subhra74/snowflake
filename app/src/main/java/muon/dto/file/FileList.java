package muon.dto.file;

import java.util.List;

public class FileList {
    private List<FileInfo> files;
    private List<FileInfo> folders;
    private String currentPath;
    private String folderName;

    public FileList(
            List<FileInfo> files,
            List<FileInfo> folders,
            String currentPath,
            String folderName) {
        this.files = files;
        this.folders = folders;
        this.currentPath = currentPath;
        this.folderName = folderName;
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<FileInfo> files) {
        this.files = files;
    }

    public List<FileInfo> getFolders() {
        return folders;
    }

    public void setFolders(List<FileInfo> folders) {
        this.folders = folders;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
