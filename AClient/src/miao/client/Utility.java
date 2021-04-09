package miao.client;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import miao.Message;
import miao.Node;

public class Utility {
	
	private static final String PROPERTIES_FILE_NAME = "config.properties";
	
	private Node myNode;
	
	private String role;
	
//	public HashMap<Integer, String> files = new HashMap<Integer, String>();
	public HashMap<Integer, Node> servers = new HashMap<Integer, Node>();
	public HashMap<Integer, Node> clients = new HashMap<Integer, Node>();
//	
//	public HashMap<Integer, Socket> socketTONeighbors = new HashMap<Integer, Socket>();
//	public HashMap<Integer, ObjectOutputStream> oStream = new HashMap<Integer, ObjectOutputStream>();
//	
//	private boolean socketListening = false;
	
	public Utility() {
		try {
			getConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isCommand(String str) {
		if(str.equals("break")||str.equals("get")||str.equals("update")||str.equals("create")||str.equals("delete")) {
			return true;
		}else {
			return false;
		}
	}
	
	public  Message createMsg(String[] lineList) {
		Message message = new Message(lineList);
		message.setClientID(myNode.getID());
		message.setTimestamp(Protocol.atomTS.getTimestamp());
		return message;
	}
	
	public void getConfig() throws IOException {
		
		String proFilePath = System.getProperty("user.dir") + "/config/"+ PROPERTIES_FILE_NAME;
		System.out.println(proFilePath);
		BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(proFilePath));
		ResourceBundle rb = new PropertyResourceBundle(inputStream);
		
		role = rb.getString("role");
		
		// get hostnames and port numbers of Servers
		String[] serverNodes = rb.getString("server_nodes").split(" ");
		int server_nodes_length = serverNodes.length;
			
		if(server_nodes_length > 0 && server_nodes_length%2 == 0) {
			for(int i = 0; i < server_nodes_length; i+=2) {
				Node server = new Node(serverNodes[i]);
				server.setID(Integer.parseInt(serverNodes[i+1]));
				servers.put(server.getID(), server);
				System.out.println(server);
			}
		}else {
			System.out.println("Items in \"server nodes\" should be even.");
		}
		
		// get hostnames and port numbers of Clients
		String[] clientNodes = rb.getString("client_nodes").split(" ");
		int client_nodes_length = clientNodes.length;
			
		if(client_nodes_length > 0 && client_nodes_length%2 == 0) {
			for(int i = 0; i < client_nodes_length; i+=2) {
				Node client = new Node(clientNodes[i]);
				client.setID(Integer.parseInt(serverNodes[i+1]));
				clients.put(client.getID(), client);
				System.out.println(client);
			}
		}else {
			System.out.println("Items in \"client nodes\" should be even.");
		}
		
		myNode = new Node(InetAddress.getLocalHost().toString().split("\\.")[0]);
		if(role.equals("server")) {
			for (int i=0; i<servers.size(); i++) {
				String host = servers.get(i).getHostname();
				if( host.equals(this.myNode.getHostname()) ) {
					this.myNode.setID(servers.get(i).getID());
					System.out.println(servers.get(i).getID());
					break;
				}
			}
		}else if(role.equals("client")){
			for (int i=0; i<clients.size(); i++) {
				String host = clients.get(i).getHostname();
				if( host.equals(this.myNode.getHostname()) ) {
					this.myNode.setID(clients.get(i).getID());
					System.out.println(clients.get(i).getID());
					break;
				}
			}
		}
	}
	
	public Node getMyNode() {
		return myNode;
	}
	
	public int getDstID(String  dstHostName, HashMap<Integer, Node> hm) {
		int dstID = -1;
		for (int i=0; i<hm.size(); i++) {
			String host = hm.get(i).getHostname();
			if( host.equals(dstHostName) ) {
				dstID = hm.get(i).getID();
				break;
			}
		}
		return dstID;
	}
}

