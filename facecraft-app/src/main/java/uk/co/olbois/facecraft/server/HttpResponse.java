/*
 * Copyright (c) 2018 Ian Clement.  All rights reserved.
 */

package uk.co.olbois.facecraft.server;

import java.util.List;
import java.util.Map;

/**
 * A data class for HTTP responses. Used by HttpRequestBuilder.
 *
 * @author Ian Clement (ian.clement@johnabbott.qc.ca)
 * @version 1
 */
public class HttpResponse {

    private int code;
    private Map<String, List<String>> headerFields;
    private byte[] content;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Map<String, List<String>> getHeaderFields() {
        return headerFields;
    }

    public void setHeaderFields(Map<String, List<String>> headerFields) {
        this.headerFields = headerFields;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
