package pt.alu.lab;

public class VirtualMachine {
	public String getDescription() {
		return description;
	}

	public String getName() {
		return this.name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	private final String name;
	private String description;
	private String os;
	private String owner;
	
	VirtualMachine(String name, String description, String os, String owner) {
		this.name = name;
		this.description = description;
		this.owner = owner;
		this.os = os;
	}
}
