package muon.screens.appwin.tabs.filebrowser.transfer.foreground;

import muon.widgets.InteractivePromptPanel;

import javax.swing.*;
import java.awt.*;

public class ForegroundTransferProgressPanel extends JPanel {
    private InteractivePromptPanel interactivePromptPanel;
    private TransferConfirmPanel transferConfirmPanel;
    private TransferProgressPanel transferProgressPanel;
    private TransferRetryPanel transferRetryPanel;

    public ForegroundTransferProgressPanel() {
        super(new CardLayout());
        interactivePromptPanel = new InteractivePromptPanel(e -> {
        });
        transferConfirmPanel = new TransferConfirmPanel(null, null);
        transferRetryPanel = new TransferRetryPanel(null, null);
        transferProgressPanel = new TransferProgressPanel(null);
        add(interactivePromptPanel, "InteractivePromptPanel");
        add(transferConfirmPanel, "TransferConfirmPanel");
        add(transferProgressPanel, "TransferProgressPanel");
        add(transferRetryPanel, "TransferRetryPanel");
    }

    public void showPrompt() {
        ((CardLayout) getLayout()).show(this, "InteractivePromptPanel");
    }

    public void showConfirm() {
        ((CardLayout) getLayout()).show(this, "TransferConfirmPanel");
        transferConfirmPanel.setFocus();
    }

    public void showProgress() {
        ((CardLayout) getLayout()).show(this, "TransferProgressPanel");
    }

    public void showRetry() {
        ((CardLayout) getLayout()).show(this, "TransferRetryPanel");
    }
}
