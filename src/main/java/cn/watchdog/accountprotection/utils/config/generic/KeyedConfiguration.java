package cn.watchdog.accountprotection.utils.config.generic;

import cn.watchdog.accountprotection.utils.*;
import cn.watchdog.accountprotection.utils.config.generic.adapter.*;
import cn.watchdog.accountprotection.utils.config.generic.key.*;

import java.lang.reflect.*;
import java.util.*;

public class KeyedConfiguration {
    private final ConfigurationAdapter adapter;
    private final List<? extends ConfigKey<?>> keys;
    private final ValuesMap values;

    public KeyedConfiguration(ConfigurationAdapter adapter, List<? extends ConfigKey<?>> keys) {
        this.adapter = adapter;
        this.keys = keys;
        this.values = new ValuesMap(keys.size());
    }

    /**
     * 初始化给定的伪枚举键类。
     *
     * @param keysClass 键类
     * @return 由设置序号值的类定义的键的列表
     */
    public static List<SimpleConfigKey<?>> initialise(Class<?> keysClass) {
        //获取所有键的列表
        List<SimpleConfigKey<?>> keys = Arrays.stream(keysClass.getFields()).filter(f -> Modifier.isStatic(f.getModifiers())).filter(f -> ConfigKey.class.equals(f.getType())).map(f -> {
            try {
                return f.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).collect(ImmutableCollectors.toList());

        // 设置顺序值
        for (int i = 0; i < keys.size(); i++) {
            keys.get(i).setOrdinal(i);
        }

        return keys;
    }

    protected void init() {
        load(true);
    }

    /**
     * 获取给定上下文键的值。
     *
     * @param key the key
     * @param <T> 键返回类型
     * @return 映射到给定键的值。可能是null。
     */
    public <T> T get(ConfigKey<T> key) {
        return this.values.get(key);
    }

    protected void load(boolean initial) {
        for (ConfigKey<?> key : this.keys) {
            if (initial || key.reloadable()) {
                this.values.put(key, key.get(this.adapter));
            }
        }
    }

    /**
     * 重新加载配置。
     */
    public void reload() {
        this.adapter.reload();
        load(false);
    }

    public static class ValuesMap {
        private final Object[] values;

        public ValuesMap(int size) {
            this.values = new Object[size];
        }

        @SuppressWarnings("unchecked")
        public <T> T get(ConfigKey<T> key) {
            return (T) this.values[key.ordinal()];
        }

        public void put(ConfigKey<?> key, Object value) {
            this.values[key.ordinal()] = value;
        }
    }
}
