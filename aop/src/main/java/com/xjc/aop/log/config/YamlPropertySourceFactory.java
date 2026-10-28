package com.xjc.aop.log.config;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        String filename = resource.getResource().getFilename();
        List<PropertySource<?>> propertySources = loader.load(filename, resource.getResource());
        return propertySources.isEmpty() ? null : propertySources.get(0);
    }
}
