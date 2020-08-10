package indi.ipan.model;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

@Component
public class UserAndFile implements Serializable{
    private User user;
    private List<File> file;
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public List<File> getFile() {
        return file;
    }
    public void setFile(List<File> file) {
        this.file = file;
    }
}
