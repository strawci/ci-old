package com.strawci.ci.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import com.strawci.ci.utils.FileUtils;
import com.strawci.ci.utils.SystemUtils;

import org.yaml.snakeyaml.Yaml;

public abstract class ConfigurationFile {

    private final File file;

    public ConfigurationFile (final String fileName) {
        this.file = new File(SystemUtils.getConfigurationDirectory(), fileName);
    }

    protected abstract void OnLoad (final YamlConfiguration yamlConfiguration);

    public void load () throws Exception {
        final Yaml yaml = new Yaml();

        if (!file.exists()) {
            FileUtils.extractResource(SystemUtils.getConfigurationDirectory(), file.getName());
        }

        final InputStream stream = new FileInputStream(this.file);
        final Map<String, Object> values = yaml.load(stream);
        final YamlConfiguration config = new YamlConfiguration(values);
        this.OnLoad(config);
    }
}
