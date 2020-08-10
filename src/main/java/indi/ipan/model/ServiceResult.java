package indi.ipan.model;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

@Component
public class ServiceResult {
    private Integer index;
    private List<String> errorInfo;
    public Integer getIndex() {
        return index;
    }
    public void setIndex(Integer index) {
        this.index = index;
    }
    public List<String> getErrorInfo() {
        return errorInfo;
    }
    public void setErrorInfo(List<String> errorInfo) {
        this.errorInfo = errorInfo;
    }
    
}