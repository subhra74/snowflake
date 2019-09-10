package snowflake.components.main;

public interface ConnectedResource {
    boolean isInitiated();

    boolean isConnected();

    void close();
}
