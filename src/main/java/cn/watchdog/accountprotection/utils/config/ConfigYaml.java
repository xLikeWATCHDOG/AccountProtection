package cn.watchdog.accountprotection.utils.config;

import cn.watchdog.accountprotection.*;
import cn.watchdog.accountprotection.utils.config.generic.*;
import cn.watchdog.accountprotection.utils.config.generic.adapter.*;

public class ConfigYaml extends KeyedConfiguration {
    private final AccountProtection plugin;

    public ConfigYaml(AccountProtection plugin, ConfigurationAdapter adapter) {
        super(adapter, ConfigKeys.getKeys());
        this.plugin = plugin;
        init();
    }

    @Override
    protected void load(boolean initial) {
        super.load(initial);
    }

    @Override
    public void reload() {
        super.reload();
    }

    public AccountProtection getPlugin() {
        return this.plugin;
    }
}
