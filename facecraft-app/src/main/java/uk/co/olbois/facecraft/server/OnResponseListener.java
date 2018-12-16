package uk.co.olbois.facecraft.server;

import uk.co.olbois.facecraft.sqlite.DatabaseException;

public interface OnResponseListener<T> {
    void onResponse(T data);
    void onProgress(HttpProgress value);
    void onError(Exception error);
}
