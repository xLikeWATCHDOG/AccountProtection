package cn.watchdog.accountprotection.utils.config.generic.key;


import cn.watchdog.accountprotection.utils.config.generic.adapter.*;

/**
 * 表示配置中的一个键。
 *
 * @param <T> 值类型
 */
public interface ConfigKey<T> {

    /**
     * 获取此键在键枚举中的位置。
     *
     * @return 位置
     */
    int ordinal();

    /**
     * 获取配置键是否可以重新加载。
     *
     * @return 是否可以重新加载密钥
     */
    boolean reloadable();

    /**
     * 使用给定的配置实例解析并返回映射到此键的值。
     *
     * @param adapter 配置适配器实例
     * @return 映射到此键的值
     */
    T get(ConfigurationAdapter adapter);

}
