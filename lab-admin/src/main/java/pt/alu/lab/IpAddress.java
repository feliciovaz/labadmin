package pt.alu.lab;

public class IpAddress implements Comparable<IpAddress> {
	
	private static final String DEFAULT_CREATION_IP = "159.23.66.0";
	
	public String getIp() {
		return ip;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getIsBeingUsed() {
		return isBeingUsed;
	}

	private final String ip;
	private final String description;
	private final Boolean isBeingUsed;
	
	IpAddress() {
		this.ip = DEFAULT_CREATION_IP;
		this.description = "";
		this.isBeingUsed = Boolean.FALSE;
	}
	
	IpAddress(String ip, Boolean isBeingUsed, String description) {
		this.ip = ip;
		this.description = description;
		this.isBeingUsed = isBeingUsed;
	}
	
	public int[] getOctets() {
		String[] octetsStr = ip.split("\\.");
		int[] octets = new int[octetsStr.length];
		for (int i=0; i<octets.length; i++) {
			octets[i] = Integer.parseInt(octetsStr[i]);
		}
		return octets;
	}
	
	@Override
	public int compareTo(IpAddress another) {
		int[] anotherOctets = another.getOctets();
		int[] meOctets = getOctets();
		int lesser = (meOctets.length > anotherOctets.length) ? anotherOctets.length : meOctets.length;
		for (int i=0; i<lesser; i++) {
			if (meOctets[i] != anotherOctets[i]) {
				return (meOctets[i] > anotherOctets[i]) ? 1 : -1;
			}
		}
		return 1;
	}
	
	@Override
	public String toString() {
		return this.ip;
	}
	
	/**
	 * Returns the CSS class for an IP. This class is used in a table to make the difference between a used and a free IP address.
	 * 
	 * @return
	 * 		The CSS class 'used-ip-class' if the ip is being used and 'free-ip-class' otherwise.
	 */
	public String getPresentationClassForList() {
		if (this.isBeingUsed) 
			return "used-ip-class";
		return "free-ip-class";
		
	}
}
