package com.uniweibo.wxnotice.autoconfig;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author emacsist
 */
@ConfigurationProperties(prefix = AppConstant.PROPERTY_PREFIX)
public class WxProperties {

    private static final Logger log = LoggerFactory.getLogger(WxProperties.class);

    private String[] accounts;

    private int defaultAgent;

    private String corpid;

    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(final boolean enable) {
        this.enable = enable;
    }

    public int getDefaultAgent() {
        return defaultAgent;
    }

    public void setDefaultAgent(final int defaultAgent) {
        this.defaultAgent = defaultAgent;
    }

    public String[] getAccounts() {
        return accounts;
    }

    public void setAccounts(final String[] accounts) {
        this.accounts = accounts;
    }

    public String getCorpid() {
        return corpid;
    }

    public void setCorpid(final String corpid) {
        this.corpid = corpid;
    }

    private Map<Integer, String> agentAccountMap = new HashMap<>();

    @PostConstruct
    public void init() {
        if (accounts == null || accounts.length == 0) {
            log.error("no wx accounts!");
            System.exit(-1);
        }
        Arrays.asList(accounts).stream().forEach(account -> {
            final List<String> accountFields = Splitter.on(CharMatcher.anyOf(",;")).trimResults().omitEmptyStrings().splitToList(account);
            if (CollectionUtils.isEmpty(accountFields) || accountFields.size() != 2) {
                log.error("app.wx proprties error, size !=3 or is emtpy!=>  {}", Arrays.toString(accounts));
                System.exit(-1);
            }
            final String secret = accountFields.get(0);
            final int agentId = Integer.parseInt(accountFields.get(1));
            agentAccountMap.put(agentId, secret);
            log.info("init agent {}", agentId);
        });
    }

    public String getCorpsecret(final int agentId) {
        return agentAccountMap.get(agentId);
    }
}