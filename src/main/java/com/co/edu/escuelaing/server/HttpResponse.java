package com.co.edu.escuelaing.server;

public class HttpResponse {

    private int statusCode;
    private String statusMessage;
    private String contentType;

    public HttpResponse() {
        this.statusCode = 200;
        this.statusMessage = "OK";
        this.contentType = "text/plain; charset=UTF-8";
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getContentType() {
        return contentType;
    }

    public void setStatus(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}