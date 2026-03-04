package com.co.edu.escuelaing.server;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private String method;
    private String path;
    private String protocolVersion;
    private String query;
    private Map<String, String> queryParams;

    public HttpRequest() {
        this.method = "";
        this.path = "";
        this.protocolVersion = "";
        this.query = "";
        this.queryParams = new HashMap<>();
    }

    public HttpRequest(String method, String path, String protocolVersion, String query) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
        this.query = query == null ? "" : query;
        this.queryParams = parseQueryParams(this.query);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getQuery() {
        return query;
    }

    public String getValue(String varName) {
        return queryParams.getOrDefault(varName, "");
    }

    public String getValues(String varName) {
        return getValue(varName);
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();

        if (query == null || query.isEmpty()) {
            return params;
        }

        String[] pairs = query.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key = decode(keyValue[0]);
            String value = keyValue.length > 1 ? decode(keyValue[1]) : "";
            params.put(key, value);
        }

        return params;
    }

    private String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return value;
        }
    }
}