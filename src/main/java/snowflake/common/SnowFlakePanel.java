package snowflake.common;

import java.util.Arrays;

public enum SnowFlakePanel {
    FILES("Files", "\uf07c"), TERMINAL("Terminal", "\uf109"), SYSTEM_MONITOR("System monitor", "\uf080"),
    DISK_SPACE_ANALYZER("Disk space analyzer", "\uf1fe"), ACTIVE_TRANSFERS("Active transfers", "\uf252"),
    LINUX_TOOLS("Linux tools", "\uf085"), SSH_KEYS("SSH keys", "\uf084"), NETWORK_TOOLS("Network tools", "\uf0b1");

    private String name;
    private String icon;

    SnowFlakePanel(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public static SnowFlakePanel fromName(String name) {
        return Arrays.stream(values()).filter(it -> it.getName().equals(name)).findFirst().get();
    }
}
