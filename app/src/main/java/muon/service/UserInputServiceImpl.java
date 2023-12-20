package muon.service;

import muon.screens.dialogs.BannerDialog;
import muon.screens.dialogs.UserInputDialog;

public class UserInputServiceImpl implements UserInputService {
    private UserInputDialog userInputDialog;
    private BannerDialog bannerDialog;

    public UserInputServiceImpl(UserInputDialog userInputDialog, BannerDialog bannerDialog) {
        this.userInputDialog = userInputDialog;
        this.bannerDialog = bannerDialog;
    }

    @Override
    public synchronized String[] getUserInput(String text1, String text2, String[] prompt, boolean[] echo) {
        var userInputs = userInputDialog.getInputs(text2 + "@" + text1, prompt, echo);
        if (userInputs.size() > 0) {
            return userInputs.toArray(new String[0]);
        }
        throw new RuntimeException("Operation cancelled");
    }

    @Override
    public String getPassword(String host, String user) {
        return getUserInput(host, user, new String[]{"Password"}, new boolean[]{true})[0];
    }

    @Override
    public synchronized void showBanner(String message) {
        bannerDialog.showBanner(message);
    }
}
