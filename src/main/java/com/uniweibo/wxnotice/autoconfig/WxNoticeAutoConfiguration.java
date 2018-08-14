package com.uniweibo.wxnotice.autoconfig;

import com.uniweibo.wxnotice.kit.WxNoticeGsonKit;
import com.uniweibo.wxnotice.service.WXService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * @author emacsist
 */
@Configuration
@EnableConfigurationProperties(WxProperties.class)
@ConditionalOnClass(WXService.class)
@ConditionalOnProperty(name = AppConstant.PROPERTY_PREFIX + ".enable", havingValue = "true", matchIfMissing = true)
public class WxNoticeAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(WxNoticeAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter(WxNoticeGsonKit.GSON));
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        log.info("message converters {}", restTemplate.getMessageConverters());
        return restTemplate;
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        int timeout = 5000;
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        return clientHttpRequestFactory;
    }

    @Bean
    @ConditionalOnMissingBean(WXService.class)
    public WXService wxService() {
        WXService wxService = new WXService();
        log.info("init {} module ok", WXService.class.getName());
        return wxService;
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(2);
        taskScheduler.setDaemon(true);
        taskScheduler.setThreadNamePrefix("--clean-wx-notice-token-");
        taskScheduler.initialize();
        return taskScheduler;
    }
}
