package snowflake.components.files.logviewer;

public interface SearchListener {
    public void search(String text, boolean regex, boolean matchCase, boolean fullWord);
    public void select(int index);
}
