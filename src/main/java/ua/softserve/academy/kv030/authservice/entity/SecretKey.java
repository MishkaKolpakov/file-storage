package ua.softserve.academy.kv030.authservice.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "SECRET_KEY")
public class SecretKey {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "KEY_ID", nullable = false)
	private long keyId;

	@Column(name = "EXPIRATION_DATE", nullable = false)
	private Timestamp expirationDate;

	@Column(name = "STATUS", nullable = false)
	private boolean status;

	@Column(name = "KEY_VALUE", nullable = false, unique = true)
	private String key;

	@OneToOne(mappedBy = "secretKey", cascade = CascadeType.ALL)
	private Resource resource;

	public SecretKey() {
	}

	public SecretKey(Timestamp expirationDate, boolean status, String key) {
		this.expirationDate = expirationDate;
		this.status = status;
		this.key = key;
	}

	public long getKeyId() {
		return keyId;
	}

	public void setKeyId(long keyId) {
		this.keyId = keyId;
	}

	public Timestamp getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Timestamp expirationDate) {
		this.expirationDate = expirationDate;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
}
