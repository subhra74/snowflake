package snowflake.components.keymanager;

import snowflake.components.common.TabbedPanel;
import snowflake.components.newsession.SessionInfo;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class KeyManagerPanel extends JPanel  implements AutoCloseable{
    private CardLayout cardLayout = new CardLayout();
    private RemoteKeyPanel remoteKeyPanel;
    private LocalKeyPanel localKeyPanel;
    private TabbedPanel tabs;
    private SessionInfo info;
    private JPanel waitPanel;
    private JPanel mainPanel;
    private JPanel startPanel;
    private SshKeyHolder holder;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private AtomicBoolean stopFlag = new AtomicBoolean(false);

    public KeyManagerPanel(SessionInfo info) {
        setLayout(cardLayout);
        this.info = info;
        holder = new SshKeyHolder();

        tabs = new TabbedPanel();
        remoteKeyPanel = new RemoteKeyPanel(info,
                a -> {
                    cardLayout.show(this, "Wait");
                    executorService.submit(() -> {
                        try {
                            SshKeyManager.generateKeys(holder, info, stopFlag, false);
                            SwingUtilities.invokeLater(() -> {
                                setKeyData(holder);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            SwingUtilities.invokeLater(() -> {
                                cardLayout.show(this, "Main");
                            });
                        }
                    });
                },
                a -> {
                    cardLayout.show(this, "Wait");
                    executorService.submit(() -> {
                        try {
                            holder = SshKeyManager.getKeyDetails(info, stopFlag);
                            SwingUtilities.invokeLater(() -> {
                                setKeyData(holder);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            SwingUtilities.invokeLater(() -> {
                                cardLayout.show(this, "Main");
                            });
                        }
                    });
                },
                a -> {
                    cardLayout.show(this, "Wait");
                    executorService.submit(() -> {
                        try {
                            SshKeyManager.saveAuthorizedKeysFile(a, info, stopFlag);
                            holder = SshKeyManager.getKeyDetails(info, stopFlag);
                            SwingUtilities.invokeLater(() -> {
                                setKeyData(holder);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            SwingUtilities.invokeLater(() -> {
                                cardLayout.show(this, "Main");
                            });
                        }
                    });
                });
        localKeyPanel = new LocalKeyPanel(info,
                a -> {
                    cardLayout.show(this, "Wait");
                    executorService.submit(() -> {
                        try {
                            SshKeyManager.generateKeys(holder, info, stopFlag, true);
                            SwingUtilities.invokeLater(() -> {
                                setKeyData(holder);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            SwingUtilities.invokeLater(() -> {
                                cardLayout.show(this, "Main");
                            });
                        }
                    });
                },
                a -> {
                    cardLayout.show(this, "Wait");
                    executorService.submit(() -> {
                        try {
                            holder = SshKeyManager.getKeyDetails(info, stopFlag);
                            SwingUtilities.invokeLater(() -> {
                                setKeyData(holder);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            SwingUtilities.invokeLater(() -> {
                                cardLayout.show(this, "Main");
                            });
                        }
                    });
                }
        );
        tabs.addTab("Server", remoteKeyPanel);
        tabs.addTab("Local computer", localKeyPanel);

        startPanel = new JPanel();

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e ->
        {
            cardLayout.show(this, "Wait");
            executorService.submit(() -> {
                try {
                    holder = SshKeyManager.getKeyDetails(info, stopFlag);
                    SwingUtilities.invokeLater(() -> {
                        setKeyData(holder);
                    });
                } catch (Exception err) {
                    err.printStackTrace();
                } finally {
                    SwingUtilities.invokeLater(() -> {
                        cardLayout.show(this, "Main");
                    });
                }
            });
        });
        startPanel.add(startButton);
        this.add(startPanel, "Start");

        waitPanel = new JPanel();

        JLabel waitLabel = new JLabel("Please wait");
        waitPanel.add(waitLabel);
        this.add(waitPanel, "Wait");

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabs);
        this.add(mainPanel, "Main");

        cardLayout.show(this, "Start");

        tabs.setSelectedIndex(0);
    }

    private void setKeyData(SshKeyHolder holder) {
        System.out.println("Holder: " + holder);
        this.localKeyPanel.setKeyData(holder);
        this.remoteKeyPanel.setKeyData(holder);
    }


    @Override
    public void close() throws Exception {

    }
}
