package miao.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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
	
	public boolean isObjectExists(String objectName) {
		File file = new File(System.getProperty("user.dir")+"//s"+this.getMyNode().getID()+"//"+objectName);
//		System.out.print("file: "+file);
//		System.out.print(", filename: "+filename);
//		System.out.println(", exists: "+file.exists());
		return file.exists();
	}
	
	public void createFile(String objectName) {
		File file = new File(System.getProperty("user.dir")+"//s"+this.getMyNode().getID()+"//"+objectName);
		try {
			if(file.createNewFile()){
	            System.out.println(file.getName() + " is created!");
	        }else{
	            System.out.println(file.getName() + " creating fails!");
	        }
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void appendFile(String objectName, String content) {
		File file = new File(System.getProperty("user.dir")+"//s"+this.getMyNode().getID()+"//"+objectName);
		try {
			FileOutputStream fos = new FileOutputStream(file,true);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			osw.append(content);
			osw.close();
		} catch (FileNotFoundException e) {e.printStackTrace();
		} catch (UnsupportedEncodingException e) {e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();
		}
		 System.out.println(file.getName() + " appended!");
	}
	
	public void deleteFile(String objectName) {
		File file = new File(System.getProperty("user.dir")+"//s"+this.getMyNode().getID()+"//"+objectName);
		if(file.delete()){
            System.out.println(file.getName() + " is deleted!");
        }else{
            System.out.println(file.getName() + " Deleting fails!");
        }
	}
	
	public String readFile(String objectName) {
		String content = "";
		String filePath = System.getProperty("user.dir")+"//s"+this.getMyNode().getID()+"//"+objectName;
		System.out.println(filePath);
//		filePath = "C:\\Users\\miaomiao\\eclipse-workspace\\project2\\config.properties";
		try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            String str;
            while ((str = in.readLine()) != null) {
            	content+=str;
//                System.out.println(str);
            }
        } catch (IOException e) {
        }
		System.out.println(objectName+" Content: \n  "+content);
		return content;
	}
}
