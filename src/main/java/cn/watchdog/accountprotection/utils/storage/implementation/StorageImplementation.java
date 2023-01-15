package cn.watchdog.accountprotection.utils.storage.implementation;

public interface StorageImplementation {
    String getImplementationName();

    void init() throws Exception;

    void shutdown();
}
