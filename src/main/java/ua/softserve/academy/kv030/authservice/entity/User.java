package ua.softserve.academy.kv030.authservice.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "USERS")
public class User implements Serializable {

	@Id
	@Column(name = "USER_ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userId;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "ROLE_ID")
	private Role role;

	@Column(name = "EMAIL", nullable = false, length = 128, unique = true)
	private String email;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private List<Password> passwords;

	@Column(name = "FIRST_NAME", nullable = false, length = 45)
	private String firstName;

	@Column(name = "LAST_NAME", nullable = false, length = 45)
	private String lastName;

	@Column(name = "STATUS", nullable = false)
	private Boolean status;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "USER_FILE_ACCESS", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = {
			@JoinColumn(name = "RESOURCE_ID")})
	private List<Resource> resources = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
	private List<Resource> availableResources;

	public User(){
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Password> getPasswords() { return passwords; }

	public void setPasswords(List<Password> passwords) { this.passwords = passwords; }

	public String getFirstName() { return firstName; }

	public void setFirstName(String firstName) { this.firstName = firstName; }

	public String getLastName() { return lastName; }

	public void setLastName(String lastName) { this.lastName = lastName; }

	public Boolean isStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void addResource(Resource resource) {
		this.resources.add(resource);
	}

	public List<Resource> getAvailableResources() {
		return availableResources;
	}

	public void setAvailableResources(List<Resource> availableResources) {
		this.availableResources = availableResources;
	}

}
