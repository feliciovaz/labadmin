package pt.alu.lab;

public class VirtualMachineRole {
	VirtualMachineRole(int id, String role, String vmName) {
		this.id = id;
		this.role = role;
		this.vmName = vmName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getVmName() {
		return vmName;
	}
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	
	private int id;
	private String role;
	private String vmName;
	
}
