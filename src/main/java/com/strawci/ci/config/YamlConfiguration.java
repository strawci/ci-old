package com.strawci.ci.config;

import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class YamlConfiguration {
    
    private final Map<String, Object> values;

    public YamlConfiguration (final Map<String, Object> values) {
        this.values = values;
    }

    public YamlConfiguration (final InputStream stream) {
        final Yaml yaml = new Yaml();
        this.values = yaml.load(stream);
    }

    public Object get (final String key) {
        return this.values.get(key);
    }

    public int getInt (final String key) {
        if (this.values.containsKey(key)) {
            return (int) this.get(key);
        }

        return -1;
    }

    public String getString (final String key) {
        if (this.values.containsKey(key)) {
            return (String) this.get(key);
        }

        return null;
    }

    public Boolean getBoolean (final String key) {
        if (this.values.containsKey(key)) {
            return (Boolean) this.get(key);
        }

        return null;
    }
}
