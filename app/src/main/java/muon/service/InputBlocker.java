package muon.service;

public interface InputBlocker {
    void blockInput();

    void unblockInput();

    String[] getUserInput(String text1, String text2, String[] prompt, boolean[] echo);

    void showBanner(String message);

    void showConnectionInProgress();

    void showRetryOption();

    void showError();
}
