package com.uniweibo.wxnotice.kit;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WxNoticeGsonKit {
    private static final Logger log = LoggerFactory.getLogger(WxNoticeGsonKit.class);
    public static final Gson GSON = new Gson();

    public static <T> T fromJSON(final String json, Class<T> clazz) {
        try {
            return GSON.fromJson(json, clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static String toJSON(Object object) {
        try {
            return GSON.toJson(object);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }
}
