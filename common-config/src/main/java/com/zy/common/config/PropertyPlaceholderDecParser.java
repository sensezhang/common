package com.zy.common.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Created by limeng on 2017/7/14.
 */
public class PropertyPlaceholderDecParser extends AbstractPropertyBeanParser {

    protected Class<?> getBeanClass(Element element) {
        return EncPropertyPlaceholderConfigurer.class;
    }

    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        super.doParse(element, builder);
        builder.addPropertyValue("ignoreUnresolvablePlaceholders", Boolean.valueOf(element.getAttribute("ignore-unresolvable")));
        String systemPropertiesModeName = element.getAttribute("system-properties-mode");
        if(StringUtils.hasLength(systemPropertiesModeName) && !systemPropertiesModeName.equals("ENVIRONMENT")) {
            builder.addPropertyValue("systemPropertiesModeName", "SYSTEM_PROPERTIES_MODE_" + systemPropertiesModeName);
        }

    }


}
