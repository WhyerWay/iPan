package indi.ipan.model;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

@Alias("userOperationLog")
public class UserOperationLog implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String username;
    private String operation;
    private Integer count;
    private Boolean status;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
    public Integer getCount() {
        return count;
    }
    public void setCount(Integer count) {
        this.count = count;
    }
    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return "UserOperationLog[username=" + username 
                + ",operation=" + operation 
                + ",count=" + count 
                + ",status=" + status 
                + "]";
    }
}
