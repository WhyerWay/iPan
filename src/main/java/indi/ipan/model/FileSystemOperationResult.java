package indi.ipan.model;

import org.springframework.stereotype.Component;

@Component
public class FileSystemOperationResult {
    private String origin;
    private String cache;
    private String destination;
    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    public String getCache() {
        return cache;
    }
    public void setCache(String cache) {
        this.cache = cache;
    }
    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
}
