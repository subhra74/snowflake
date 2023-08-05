package muon.app.ui.components.session.utilpage.keys;

public class SshKeyHolder {
    private String remotePublicKey;
    private String localPublicKey;
    private String remoteAuthorizedKeys;
    private String remotePubKeyFile;
    private String localPubKeyFile;

    public SshKeyHolder(){}

    public SshKeyHolder(String remotePublicKey, String localPublicKey,
                        String remoteAuthorizedKeys, String remotePubKeyFile,
                        String localPubKeyFile) {
        this.remotePublicKey = remotePublicKey;
        this.localPublicKey = localPublicKey;
        this.remoteAuthorizedKeys = remoteAuthorizedKeys;
        this.remotePubKeyFile = remotePubKeyFile;
        this.localPubKeyFile = localPubKeyFile;
    }

    public String getLocalPubKeyFile() {
        return localPubKeyFile;
    }

    public void setLocalPubKeyFile(String localPubKeyFile) {
        this.localPubKeyFile = localPubKeyFile;
    }

    public String getRemotePublicKey() {
        return remotePublicKey;
    }

    public void setRemotePublicKey(String remotePublicKey) {
        this.remotePublicKey = remotePublicKey;
    }

    public String getLocalPublicKey() {
        return localPublicKey;
    }

    public void setLocalPublicKey(String localPublicKey) {
        this.localPublicKey = localPublicKey;
    }

    public String getRemoteAuthorizedKeys() {
        return remoteAuthorizedKeys;
    }

    public void setRemoteAuthorizedKeys(String remoteAuthorizedKeys) {
        this.remoteAuthorizedKeys = remoteAuthorizedKeys;
    }

    public String getRemotePubKeyFile() {
        return remotePubKeyFile;
    }

    public void setRemotePubKeyFile(String remotePubKeyFile) {
        this.remotePubKeyFile = remotePubKeyFile;
    }

    @Override
    public String toString() {
        return "SshKeyHolder{" +
                "remotePublicKey='" + remotePublicKey + '\'' +
                ", localPublicKey='" + localPublicKey + '\'' +
                ", remoteAuthorizedKeys='" + remoteAuthorizedKeys + '\'' +
                ", remotePubKeyFile='" + remotePubKeyFile + '\'' +
                ", localPubKeyFile='" + localPubKeyFile + '\'' +
                '}';
    }
}
