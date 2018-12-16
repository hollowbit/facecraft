/*
 * Copyright (c) 2018 Ian Clement.  All rights reserved.
 */

package uk.co.olbois.facecraft.server;

import uk.co.olbois.facecraft.ui.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * A builder for an HTTP request.
 *
 * @author Ian Clement (ian.clement@johnabbott.qc.ca)
 * @version 1
 */
public class HttpRequestBuilder {

    // Use the default for web forms.
    public static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";

    //
    private static final int BUFFER_SIZE = 1024;

    /**
     * Supported HTTP methods
     */
    public enum Method {
        GET, POST, PUT, DELETE, PATCH
    }

    private String url;
    private Method method;
    private String contentType;
    private byte[] requestBody;
    private boolean cookies;
    private int expectedStatusCode;
    private OnResponseListener onResponseListener;

    /**
     * Create a request builder with specific path on the server.
     * - Default request method is GET.
     * @param path the path on the server
     */
    public HttpRequestBuilder(String path) {
        this.url = ServerConfig.getInstance().getUrlPrefix() + "/" + path;
        method = Method.GET;
        expectedStatusCode = -1;
        cookies = false;
    }

    /**
     * Set the request method
     * @param method
     * @return
     */
    public HttpRequestBuilder method(Method method) {
        this.method = method;
        return this;
    }

    /**
     * Set the request body and it's content type
     * @param contentType
     * @param requestBody
     * @return
     */
    public HttpRequestBuilder withRequestBody(String contentType, byte[] requestBody) {
        this.contentType = contentType;
        this.requestBody = requestBody;
        return this;
    }

    /**
     * Set the request body with the default content-type (www-form)
     * @param requestBody
     * @return
     */
    public HttpRequestBuilder withRequestBody(byte[] requestBody) {
        this.contentType = DEFAULT_CONTENT_TYPE;
        this.requestBody = requestBody;
        return this;
    }

    /**
     * Set the expected status code. If the expectedStatusCode is not the expected value a ServerException is thrown when the request is performed.
     * @param code
     * @return
     */
    public HttpRequestBuilder expectingStatus(int code) {
        this.expectedStatusCode = code;
        return this;
    }

    /**
     * Use the ServerConfig instance's cookies.
     * @return
     */
    public HttpRequestBuilder cookies() {
        cookies = true;
        return this;
    }

    /**
     * Set on Progress listener
     * @param onResponseListener
     */
    public HttpRequestBuilder setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
        return this;
    }

    /**
     * Perform the request
     * @return
     * @throws IOException
     * @throws ServerException
     */
    public HttpResponse perform() throws IOException, ServerException {

        // create the http connection
        HttpURLConnection httpConnection = (HttpURLConnection) new URL(url).openConnection();
        httpConnection.setRequestMethod(method.toString());

        // send content type if required
        if(contentType != null)
            httpConnection.setRequestProperty("Content-Type", contentType);

        // send the cookies if requested
        if(cookies) {
            CookieManager cookieManager = ServerConfig.getInstance().getCookieManager();
            httpConnection.setRequestProperty("Cookie", StringUtils.join(cookieManager.getCookieStore().getCookies(), ";"));
        }

        // send the request body
        if(requestBody != null) {
            httpConnection.setDoOutput(true);
            copyStreamBuffered(new ByteArrayInputStream(requestBody), httpConnection.getOutputStream(), null);
        }

        // check the status code if there was an expected code
        if(expectedStatusCode >= 0) {
            if(httpConnection.getResponseCode() != expectedStatusCode)
                throw new ServerException(String.format("Unexpected status code: expected=%d, got=%d", expectedStatusCode, httpConnection.getResponseCode()));
        }

        // save and return all response info
        HttpResponse response = new HttpResponse();
        response.setCode(httpConnection.getResponseCode());
        response.setHeaderFields(httpConnection.getHeaderFields());

        // if we are using cookies, update the cookies in the manager
        if(cookies) {
            String cookiesHeader = httpConnection.getHeaderField("Set-Cookie");
            List<HttpCookie> cookies = HttpCookie.parse(cookiesHeader);
            for(HttpCookie cookie : cookies)
                ServerConfig.getInstance().getCookieManager().getCookieStore().add(null, cookie);
        }


        // get the length of the response body from the response headers
        int contentLength = -1;  // default if not specified
        if (httpConnection.getHeaderField("Content-Length") != null)
            contentLength = Integer.parseInt(httpConnection.getHeaderFields().get("Content-Length").get(0));


        try {
            HttpProgress progress = new HttpProgress(contentLength);
            ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
            copyStreamBuffered(httpConnection.getInputStream(), responseBytes, progress);
            response.setContent(responseBytes.toByteArray());
        }
        catch (FileNotFoundException error) {
            // no content
        }

        return response;
    }

    private void copyStreamBuffered(InputStream in, OutputStream out, HttpProgress currentProgress) throws IOException {
        BufferedInputStream inBuf = new BufferedInputStream(in, BUFFER_SIZE);
        BufferedOutputStream outBuf = new BufferedOutputStream(out, BUFFER_SIZE);

        int transferredSoFar = 0;

        publishProgress(currentProgress, onResponseListener, transferredSoFar);

        int i;
        while((i = inBuf.read()) >= 0) {
            outBuf.write(i);
            transferredSoFar++;
            if(transferredSoFar % BUFFER_SIZE == 0) {
                // slow down the transfer
                /*try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                }*/
                publishProgress(currentProgress, onResponseListener, transferredSoFar);
            }
        }

        publishProgress(currentProgress, onResponseListener, transferredSoFar);
        outBuf.flush();
    }

    private void publishProgress(HttpProgress progress, OnResponseListener listener, int transferredSoFar) {
        if(progress != null && listener != null) {
            progress.setReadBytes(transferredSoFar);
            listener.onProgress(progress);
        }
    }

}
