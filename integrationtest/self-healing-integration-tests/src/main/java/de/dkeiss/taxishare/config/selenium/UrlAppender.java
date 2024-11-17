package de.dkeiss.taxishare.config.selenium;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Fault tolerant URL appender.
 */
public final class UrlAppender {

    private UrlAppender() {
    }

    public static String appendUrl(String url, String... appenders) {
        StringBuilder urlBuilder = new StringBuilder(url);

        for (String appender : appenders) {
            if (urlBuilder.charAt(urlBuilder.length() - 1) == '/' && appender.startsWith("/")) {
                urlBuilder.append(appender.substring(1));
            } else if (urlBuilder.charAt(urlBuilder.length() - 1) != '/' && !appender.startsWith("/")) {
                urlBuilder.append("/").append(appender);
            } else {
                urlBuilder.append(appender);
            }
        }

        return urlBuilder.toString();
    }

    public static String appendQueryParams(String url, Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return url;
        }

        StringBuilder query = new StringBuilder(url.contains("?") ? "&" : "?");
        queryParams.forEach((key, value) -> {
            if (query.length() > 1) {
                query.append("&");
            }
            query.append(key);
            if (StringUtils.isNotEmpty(value)) {
                query.append("=").append(value);
            }
        });

        return url + query;
    }
}
