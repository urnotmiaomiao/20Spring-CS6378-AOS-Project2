package miao;

public class Node {
	private int id = -1;
	private String hostname = "";
	private int serverPort = 6000;
	private int clientPort = 5000;
	
	public Node() {}
	public Node(String hostname) {
		this.hostname = hostname;
	}
	public Node(String hostname, int tPort) {
		this.hostname = hostname;
		this.serverPort = tPort;
	}
	
	public String toString() {
		return ""+id+" "+hostname;
	}
	
//	id
	public int getID() {
		return id;
	}
	public void setID(int nodeID) {
		this.id = nodeID;
	}
	
//	hostname
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

//	TCP port
	public int getServerPort() {
		return serverPort;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
//	client port
	public int getClientPort() {
		return clientPort;
	}
	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}
}
