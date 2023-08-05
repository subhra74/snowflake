package muon.app.ui.components.session.files.transfer;

public class ByteChunk {
    private byte[] buf;
    private long len;

    public ByteChunk(byte[] buf, long len) {
        this.buf = buf;
        this.len = len;
    }

    public byte[] getBuf() {
        return buf;
    }

    public void setBuf(byte[] buf) {
        this.buf = buf;
    }

    public long getLen() {
        return len;
    }

    public void setLen(long len) {
        this.len = len;
    }
}