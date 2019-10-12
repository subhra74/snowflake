package snowflake.components.files.logviewer;

public interface SearchListener {
    public void search(String text);
    public void select(long index);
}
