package miao;
import java.io.Serializable;

public class Message implements Comparable<Message>, Serializable {

	private boolean isSent = false;
//	private int seqNo = -1;
	
	String command = "";
	String objectName = "";
	int objectHashValue = -1;
	private int timestamp = -1;
	private int clientID = -1;
	private int[] serverID = {-1,-1,-1};
	private String content = "";
	
	public Message(String[] cmds) {
		this.command = cmds[0];
		this.objectName = cmds[1];
		this.objectHashValue = objectName.hashCode() & Integer.MAX_VALUE;
		for(int i = 0; i<3; i++) {
			serverID[i] = (this.objectHashValue+i)%7;
		}
	}
	
	
	public String toString() {
		return "ts:"+timestamp+" client:"+clientID+" obj:"+objectName+" hash:"+objectHashValue+" c:"+content;
	}
	
	public String toKey() {
		return "t"+timestamp+"c"+clientID+"o"+objectHashValue;
	}

	// boolean isSent
	public void setIsSent(boolean is) {
		this.isSent = is;
	}
	public boolean getIsSent() {
		return this.isSent;
	}
		
	// String command
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	
	// String objectName
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	
	// int objectHashValue
	public int getObjectHashValue() {
		return objectHashValue;
	}
	public void setObjectHashValue(int objectHashValue) {
		this.objectHashValue = objectHashValue;
	}
	
	// int timestamp
	public int getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
	
	// String client
	public int getClientID() {
		return clientID;
	}
	public void setClientID(int clientID) {
		this.clientID = clientID;
	}
	
	// int serverID
	public int[] getServerID() {
		return serverID;
	}
	public void setServerID(int[] serverID) {
		this.serverID = serverID;
	}
	
	// String content
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public int compareTo(Message msg) {
		if(this.getTimestamp() == msg.getTimestamp()) {
			return this.getClientID() - msg.getClientID();
		}
		return this.getTimestamp() - msg.getTimestamp();
	}

}
