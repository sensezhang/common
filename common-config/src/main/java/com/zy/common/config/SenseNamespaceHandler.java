package com.zy.common.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by limeng on 2017/7/14.
 */
public class zyNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
//        registerBeanDefinitionParser("propertyPlaceholderDec",new PropertyPlaceholderDecParser());
        registerBeanDefinitionParser("property-placeholder-dec",new PropertyPlaceholderDecParser());
    }
}
