import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
	private String username;
	private Date lastActive;
	
	private User(){}
	
	public User(String username, Date lastActive) {
		this.username = username;
		this.lastActive = lastActive;
	}

	@Override
	public String toString() {
		return username;
	}

	@JsonProperty("username")
	public String getUsername() {
		return username;
	}

	@JsonProperty("username")	
	public void setUsername(String username) {
		this.username = username;
	}


	public Date getLastActive() {
		return this.lastActive;
	}

	@JsonProperty("last_active")
	public void setLastActive(Date lastActive) {
		this.lastActive = lastActive;
	}
	
	
}