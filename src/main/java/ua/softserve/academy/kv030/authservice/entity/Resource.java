package ua.softserve.academy.kv030.authservice.entity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "RESOURCE")
public class Resource {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RESOURCE_ID")
	private long resourceId;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "KEY_ID")
	private SecretKey secretKey;

	@Column(name = "LINK_TO_FILE", nullable = false, unique = true)
	private String linkToFile;

	@Column(name = "FILE_NAME", nullable = false)
	private String fileName;

	@Column(name = "MIME_TYPE", nullable = false)
	private String mimeType;

	@Column(name = "SIZE", nullable = false)
	private long size;

	@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JoinColumn(name = "PERMISSION_ID")
	private Permission permission;

	@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JoinColumn(name = "OWNER_ID", referencedColumnName = "USER_ID")
	private User owner;

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "resources", cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	private Set<User> users = new HashSet<>();

	public Resource() {
	}

	public Resource(SecretKey secretKey, String linkToFile, Permission permission, User owner) {
		this.secretKey = secretKey;
		this.linkToFile = linkToFile;
		this.permission = permission;
		this.owner = owner;
	}

	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	public SecretKey getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}

	public String getLinkToFile() {
		return linkToFile;
	}

	public void setLinkToFile(String linkToFile) {
		this.linkToFile = linkToFile;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission persmission) {
		this.permission = persmission;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public String getFileName() { return fileName; }

	public void setFileName(String fileName) { this.fileName = fileName; }

	public String getMimeType() { return mimeType; }

	public void setMimeType(String mimeType) { this.mimeType = mimeType; }

	public long getSize() { return size; }

	public void setSize(long size) { this.size = size; }
}
