package ua.softserve.academy.kv030.authservice.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "ROLES")
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ROLE_ID", nullable = false)
	private long roleId;

	@Column(name = "ROLE_NAME", nullable = false, length = 45, unique = true)
	private String roleName;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
	private Set<User> users;

	public Role(){
	}
	
	public Role(long roleId, String roleName) {
		this.roleId = roleId;
		this.roleName = roleName;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}
}
