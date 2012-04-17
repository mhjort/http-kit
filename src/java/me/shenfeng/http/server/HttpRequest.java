package me.shenfeng.http.server;

import static me.shenfeng.http.HttpUtils.CHARSET;
import static me.shenfeng.http.HttpUtils.CONNECTION;
import static me.shenfeng.http.HttpUtils.CONTENT_TYPE;
import static me.shenfeng.http.codec.HttpVersion.HTTP_1_1;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import me.shenfeng.http.HttpUtils;
import me.shenfeng.http.codec.HttpMethod;
import me.shenfeng.http.codec.HttpVersion;

public class HttpRequest {
    private int serverPort;
    private String serverName;
    private String remoteAddr;
    private String queryString;
    private String uri;
    private HttpMethod method;
    private Map<String, String> headers;
    private HttpVersion version;
    private int contentLength = 0;
    private byte[] body;
    private String contentType;
    private String charset = "utf8";
    private boolean isKeepAlive = false;

    public HttpRequest(HttpMethod method, String url, HttpVersion version) {
        this.method = method;
        this.version = version;
        int idx = url.indexOf('?');
        if (idx > 0) {
            uri = url.substring(0, idx);
            queryString = url.substring(idx + 1);
        } else {
            uri = url;
            queryString = null;
        }
    }

    public InputStream getBody() {
        if (body != null) {
            return new ByteArrayInputStream(body, 0, contentLength);
        }
        return null;
    }

    public String getCharactorEncoding() {
        return charset;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getServerName() {
        return serverName;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public String getScheme() {
        return "http";
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getUri() {
        return uri;
    }

    public boolean isKeepAlive() {
        // header keys are all lowercased
        return isKeepAlive;
    }

    public void setBody(byte[] body, int count) {
        this.body = body;
        this.contentLength = count;
    }

    public void setHeaders(Map<String, String> headers) {
        String h = headers.get(HttpUtils.HOST);
        if (h != null) {
            int idx = h.indexOf(':');
            if (idx != -1) {
                this.serverName = h.substring(0, idx);
                serverPort = Integer.valueOf(h.substring(idx + 1));
            } else {
                this.serverName = h;
            }
        }
        String con = headers.get(CONNECTION);
        if (con != null) {
            con = con.toLowerCase();
        }
        String ct = headers.get(CONTENT_TYPE);
        if (ct != null) {
            int idx = ct.indexOf(";");
            if (idx != -1) {
                contentType = ct.substring(0, idx);
                idx = ct.indexOf(CHARSET, idx);
                if (idx != -1) {
                    charset = ct.substring(idx + CHARSET.length());
                }
            } else {
                contentType = ct;
            }
        }

        isKeepAlive = (version == HTTP_1_1 && !"close".equals(con))
                || "keep-alive".equals(con);

        this.headers = headers;
    }
}
