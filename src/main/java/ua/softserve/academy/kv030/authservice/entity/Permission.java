package ua.softserve.academy.kv030.authservice.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "PERMISSIONS")
public class Permission {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PERMISSION_ID")
	private long permissionId;
	
	@Column(name = "PERMISSION_NAME")
	private String permission;

	@OneToMany(mappedBy = "permission", cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	private List<Resource> resources;

	public Permission() {
	}

	public Permission(long permissionId, String permission) {
		this.permissionId = permissionId;
		this.permission = permission;
	}

	public long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(long permissionId) {
		this.permissionId = permissionId;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

}
