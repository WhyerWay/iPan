package indi.ipan.model;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

@Alias("file")
public class File implements Serializable {
    private static final long serialVersionUID = 1L;
	private Integer id;
	private String fileName;
	private String username;
	
	public File() {}
	public File(String username, String fileName) {
	    this.username = username;
	    this.fileName = fileName;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public String toString() {
		return "File[username=" + username + ",file_name=" + fileName + "]";
	}
}
