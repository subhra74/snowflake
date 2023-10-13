package muon.service;

import muon.dto.file.FileList;
import muon.exceptions.FSException;

public interface FileSystem extends AutoCloseable {
    FileList list(String folder) throws FSException;

    String getHome() throws FSException;
}
