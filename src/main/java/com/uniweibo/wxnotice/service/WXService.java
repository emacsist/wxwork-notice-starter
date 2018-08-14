package com.uniweibo.wxnotice.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.uniweibo.wxnotice.autoconfig.WxProperties;
import com.uniweibo.wxnotice.kit.WxNoticeGsonKit;
import com.uniweibo.wxnotice.wxwork.AccessToken;
import com.uniweibo.wxnotice.wxwork.TextMsg;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author emacsist
 */
public class WXService {

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private static final Logger log = LoggerFactory.getLogger(WXService.class);

    private static final String TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
    private static final String SEND_MSG_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s";

    @Autowired
    private WxProperties wxProperties;

    @Autowired
    private RestTemplate restTemplate;

    private static final Cache<Integer, AccessToken> TOKEN_CACE = CacheBuilder.newBuilder().expireAfterWrite(7200, TimeUnit.SECONDS).maximumSize(100).build();

    public boolean sendText(final int agentId, final String text) {
        final AccessToken accessToken = getAccessToken(agentId);
        if (accessToken == null) {
            return false;
        }
        final TextMsg textMsg = new TextMsg();
        textMsg.setAgentid(agentId);
        TextMsg.TextBean textBean = new TextMsg.TextBean();
        textBean.setContent(text);
        textMsg.setText(textBean);

        final String sendMsgUrl = String.format(SEND_MSG_URL, accessToken.getAccessToken());

        // set headers
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<String> entity = new HttpEntity<>(WxNoticeGsonKit.toJSON(textMsg), headers);

        // send request and parse result
        ResponseEntity<String> response = restTemplate.exchange(sendMsgUrl, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("send <= {} => err, {}", text, response);
            return false;
        }
        return true;
    }

    public void sendText(final String text) {
        sendText(wxProperties.getDefaultAgent(), text);
    }


    public AccessToken getAccessToken(final int agentId) {
        final AccessToken accessToken = TOKEN_CACE.getIfPresent(agentId);
        if (accessToken != null) {
            return accessToken;
        }

        final String tokenUrl = String.format(TOKEN_URL, wxProperties.getCorpid(), wxProperties.getCorpsecret(agentId));
        final ResponseEntity<String> result = restTemplate.getForEntity(tokenUrl, String.class);
        if (result == null || result.getStatusCode() != HttpStatus.OK) {
            log.error("invalid token {}", result);
            return null;
        }
        final AccessToken newestToken = WxNoticeGsonKit.fromJSON(result.getBody(), AccessToken.class);
        if (newestToken == null || StringUtils.isBlank(newestToken.getAccessToken())) {
            log.error("invalid agentid: {} => token {}", agentId, result.getBody());
        } else {
            TOKEN_CACE.put(agentId, newestToken);
            // 在指定的时间后, 清除 token 缓存, 以获取最新的
            log.info("{} , token expire in {}", agentId, newestToken.getExpiresIn());
            threadPoolTaskScheduler.schedule(() -> cleanToken(agentId), new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(newestToken.getExpiresIn() - 1)));
        }
        return newestToken;
    }

    private void cleanToken(final int agentId) {
        TOKEN_CACE.invalidate(agentId);
        log.error("clean agent {} token", agentId);
    }
}
