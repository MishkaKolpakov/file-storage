package ua.softserve.academy.kv030.authservice.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "PASSWORDS")
public class Password {

	@Id
	@Column(name = "PASSWORD_ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long passwordId;
	
	@Column(name = "PASS", nullable = false, length = 256)
	private String password;
	
	@Column(name = "EXPIRATION_TIME", nullable = false)
	private Timestamp expirationTime;
	
	@Column(name = "STATUS", nullable = false)
	private Boolean status;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "USER_ID")
	private User user;

	public Password() {
	}

	public Password(long passwordId, Timestamp expirationTime, boolean status) {
		this.passwordId = passwordId;
		this.expirationTime = expirationTime;
		this.status = status;
	}

	public long getPasswordId() {
		return passwordId;
	}

	public void setPasswordId(long passwordId) {
		this.passwordId = passwordId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Timestamp getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Timestamp expirationTime) {
		this.expirationTime = expirationTime;
	}

	public Boolean isStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
