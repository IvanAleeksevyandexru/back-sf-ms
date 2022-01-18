package ru.gosuslugi.pgu.sp.adapter.config;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.ComparisonDateTool;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.MathTool;
import org.apache.velocity.tools.generic.SortTool;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import ru.gosuslugi.pgu.sp.adapter.config.props.VelocityProperties;
import ru.gosuslugi.pgu.sp.adapter.config.props.VelocityProperties.ResourceLoader;
import ru.gosuslugi.pgu.sp.adapter.util.*;

/**
 * Velocity beans configuration
 */
@Configuration
@EnableConfigurationProperties(VelocityProperties.class)
public class VelocityConfig {

    public static final String DATE_TOOL_VAR_NAME = "dateTool";
    public static final String DATE_SERVICE_VAR_NAME = "dateService";
    public static final String STRING_SERVICE_VAR_NAME = "stringService";
    public static final String MONEY_SERVICE_VAR_NAME = "moneyService";
    public static final String XML_SERVICE_VAR_NAME = "xmlService";
    public static final String CONSOLE_NAME = "console";
    public static final String ADDRESS_SERVICE_VAR_NAME = "addressService";
    public static final String MATH_TOOL_VAR_NAME = "mathTool";
    public static final String DATE_COMPARE_TOOL_VAR_NAME = "compareDateTool";
    public static final String STRING_TOOL_VAR_NAME = "strTool";
    public static final String APACHE_STRING_TOOL_VAR_NAME = "apacheStrTool";
    public static final String INTEGER_VAR_NAME = "Integer";
    public static final String SORT_TOOL_VAR_NAME = "sorter";

    @Bean
    public VelocityEngine velocityEngine(VelocityProperties properties) {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty("runtime.log.logsystem.log4j.logger","root");
        if (ResourceLoader.FILE.equals(properties.getResourceLoader())) {
            engine.setProperty("resource.loaders", ResourceLoader.FILE.name().toLowerCase());
            engine.setProperty("file.resource.loader.class", properties.getFileResourceLoaderClass());
            engine.setProperty("file.resource.loader.path", properties.getFileResourceLoaderPath());
            engine.setProperty("resource.loader.file.cache", properties.getFileResourceLoaderClass());
            engine.setProperty("resource.loader.file.modification_check_interval", properties.getResourceLoaderFileModificationCheckInterval());
        }
        if (ResourceLoader.CLASS.equals(properties.getResourceLoader())) {
            engine.setProperty("resource.loader", ResourceLoader.CLASS.name().toLowerCase());
            engine.setProperty("class.resource.loader.class", properties.getClassResourceLoaderClass());
        }
        engine.init();
        return engine;
    }

    @Bean
    public VelocityContext prototypeTemplateContext() {
        VelocityContext context = new VelocityContext();
        context.put(DATE_TOOL_VAR_NAME, new DateTool());
        context.put(DATE_SERVICE_VAR_NAME, new DateService());
        context.put(STRING_SERVICE_VAR_NAME, new StringService());
        context.put(CONSOLE_NAME, new LogDebugConsole());
        context.put(ADDRESS_SERVICE_VAR_NAME, new AddressService());
        context.put(MATH_TOOL_VAR_NAME, new MathTool());
        context.put(MONEY_SERVICE_VAR_NAME, new MoneyService());
        context.put(DATE_COMPARE_TOOL_VAR_NAME, new ComparisonDateTool());
        context.put(STRING_TOOL_VAR_NAME, StringUtils.class);
        context.put(APACHE_STRING_TOOL_VAR_NAME, org.apache.commons.lang3.StringUtils.class);
        context.put(INTEGER_VAR_NAME, Integer.class);
        context.put(XML_SERVICE_VAR_NAME, new XMLService());
        context.put(SORT_TOOL_VAR_NAME, new SortTool());
        return context;
    }


}
