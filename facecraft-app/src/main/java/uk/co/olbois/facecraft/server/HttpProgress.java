package uk.co.olbois.facecraft.server;

public class HttpProgress {

    private int readBytes;
    private int totalBytes;

    public HttpProgress(int totalBytes) {
        this.totalBytes = totalBytes;
    }

    public int getReadBytes() {
        return readBytes;
    }

    public void setReadBytes(int readBytes) {
        this.readBytes = readBytes;
    }

    public int getTotalBytes() {
        return totalBytes;
    }
}
