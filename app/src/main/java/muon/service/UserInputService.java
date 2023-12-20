package muon.service;

public interface UserInputService {
    String[] getUserInput(String text1, String text2, String[] prompt, boolean[] echo);
    String getPassword(String host, String user);
    void showBanner(String message);
}
