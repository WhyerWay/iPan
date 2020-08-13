package indi.ipan.model;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

@Alias("user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
	private Integer id;
	private String username;
    private String password;
    private String email;

    public User() {}
    
    public User(String username) {
        this.username = username;
    }
    
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
        return email;
    }
	

    public void setEmail(String email) {
        this.email = email;
    }
    

    @Override
    public String toString() {
        if (email != null) {
            return "User[username=" + username 
                    + ",password=" + password 
                    + ",email=" + email 
                    + "]";
        }else {
            return "User[username=" + username 
                    + ",password=" + password 
                    + "]";
        }
        
    }
}