package snowflake.components.keymanager;

import snowflake.common.ssh.SshModalUserInteraction;
import snowflake.common.ssh.files.SshFileSystem;
import snowflake.components.common.StartPage;
import snowflake.components.common.TabbedPanel;
import snowflake.components.newsession.SessionInfo;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class KeyManagerPanel extends JPanel implements AutoCloseable {
    private CardLayout cardLayout = new CardLayout();
    private RemoteKeyPanel remoteKeyPanel;
    private LocalKeyPanel localKeyPanel;
    private TabbedPanel tabs;
    private SessionInfo info;
    private JPanel waitPanel;
    private JPanel mainPanel;
    private StartPage startPage;
    private SshKeyHolder holder;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private AtomicBoolean stopFlag = new AtomicBoolean(false);
    private SshFileSystem fileSystem;

    public KeyManagerPanel(SessionInfo info) {
        setLayout(cardLayout);
        this.info = info;
        holder = new SshKeyHolder();

        tabs = new TabbedPanel();
        remoteKeyPanel = new RemoteKeyPanel(info,
                a -> {
                    cardLayout.show(this, "Wait");
                    executorService.submit(() -> {
                        try (SshFileSystem fileSystem = new SshFileSystem(new SshModalUserInteraction(info))) {
                            this.fileSystem = fileSystem;
                            fileSystem.connect();
                            SshKeyManager.generateKeys(holder, fileSystem, false);
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
                        try (SshFileSystem fileSystem = new SshFileSystem(new SshModalUserInteraction(info))) {
                            this.fileSystem = fileSystem;
                            fileSystem.connect();
                            holder = SshKeyManager.getKeyDetails(fileSystem);
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
                        try (SshFileSystem fileSystem = new SshFileSystem(new SshModalUserInteraction(info))) {
                            this.fileSystem = fileSystem;
                            fileSystem.connect();
                            SshKeyManager.saveAuthorizedKeysFile(a, fileSystem);
                            holder = SshKeyManager.getKeyDetails(fileSystem);
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
                        try (SshFileSystem fileSystem = new SshFileSystem(new SshModalUserInteraction(info))) {
                            this.fileSystem = fileSystem;
                            fileSystem.connect();
                            SshKeyManager.generateKeys(holder, fileSystem, true);
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
                        try (SshFileSystem fileSystem = new SshFileSystem(new SshModalUserInteraction(info))) {
                            this.fileSystem = fileSystem;
                            fileSystem.connect();
                            holder = SshKeyManager.getKeyDetails(fileSystem);
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

        startPage = new StartPage("Manage SSH keys",
                "Create, edit or manage SSH keys for remote and local machine", "Open", e ->
        {
            cardLayout.show(this, "Wait");
            executorService.submit(() -> {
                try (SshFileSystem fileSystem = new SshFileSystem(new SshModalUserInteraction(info))) {
                    this.fileSystem = fileSystem;
                    fileSystem.connect();
                    holder = SshKeyManager.getKeyDetails(fileSystem);
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


        this.add(startPage, "Start");

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
        if (this.fileSystem != null) {
            this.executorService.submit(() -> {
                try {
                    this.fileSystem.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
