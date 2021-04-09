package miao.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import miao.Message;
import miao.Node;

public class Connection {
	private boolean[] isActive = new boolean[7];
	private boolean[] isActiveClient = new boolean[5];
	
	public HashMap<Integer, Socket> socketTOServers = new HashMap<Integer, Socket>();
	public HashMap<Integer, ObjectOutputStream> oStreamServers = new HashMap<Integer, ObjectOutputStream>();
	
	public HashMap<Integer, Socket> socketTOClients = new HashMap<Integer, Socket>();
	public HashMap<Integer, ObjectOutputStream> oStreamClients = new HashMap<Integer, ObjectOutputStream>();
		
	private boolean socketListening = false;
	
	public Connection() {
		for(int i = 0; i<isActive.length; i++) {
			isActive[i] = true;
		}
		for(int i = 0; i<isActiveClient.length; i++) {
			isActiveClient[i] = true;
		}
	}

//	Send message 
	public void sendMsg(Utility u, Message msg, int dstID) {
		if(isActive[dstID]&&dstID!=u.getMyNode().getID()) {
			try {
				ObjectOutputStream oos = oStreamServers.get(dstID);
				Protocol.atomTS.increaseTimestamp();
				oos.writeObject(msg);
				oos.flush();
				System.out.println("s"+u.getMyNode().getID()+" sends to s"+dstID+"\n  ["+msg+"]" );			
			}catch (IOException e) {System.out.println(e);}
		}else {
			System.out.println("s"+u.getMyNode().getID()+" to s"+dstID+" shutdown");
		}
	}
	
	public void sendMsgClient(Utility u, Message msg, int dstID) {
		if(isActiveClient[dstID]) {
			try {
				ObjectOutputStream oos = oStreamClients.get(dstID);
				Protocol.atomTS.increaseTimestamp();
				oos.writeObject(msg);
				oos.flush();
				System.out.println("s"+u.getMyNode().getID()+" sends to c"+dstID+"\n  ["+msg+"]" );	
			}catch (IOException e) {System.out.println(e);}
		}else {
			System.out.println("s"+u.getMyNode().getID()+" to c"+dstID+" shutdown");
		}
	}
	
	public void createConnections(Utility u) {
		for (int i=0; i<u.servers.size(); i++) {
			int sID = u.servers.get(i).getID();
			if ((u.getMyNode().getID() < sID) && !(socketListening)) {
//				start listening, wait for connection
				socketListening = true;
				new Thread(new ThreadSocketListen(u, this, "server")).start();
			}else if (u.getMyNode().getID() > sID) {
//				start sending connection
				socketConnect(i, u);
			}
		}
	}
	
//	Called in Class ThreadSocketListen @Thread
	public void socketListen(Utility u, String role) throws IOException {
		int tport = -1;
		HashMap<Integer, Node> hm = null;
		if(role.equals("server")) {
			tport = u.getMyNode().getServerPort();
			hm = u.servers;
		}else if(role.equals("client")){
			tport = u.getMyNode().getClientPort();
			hm = u.clients;
		}
		ServerSocket serverSock = new ServerSocket(tport);  // Create a server socket service at port
		System.out.println( u.getMyNode().getHostname() +" (" + u.getMyNode().getID() + ")" + " server socket listening ("+ tport +")"+" to clients...");
	
		while(true){
			Socket sock = serverSock.accept();	// Wait for socket connection
			String dstHostName = sock.getInetAddress().getHostName().split("\\.")[0];
			int dstID = u.getDstID(dstHostName, hm);
			
			System.out.println(u.getMyNode().getID() + " - "+role+" " + dstID + " channel created");
			
			try{
				// Save output Stream
				ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
				
				// Start new thread to handle input stream
				if(role.equals("server")) {
					socketTOServers.put(dstID, sock);
					oStreamServers.put(dstID, oos);
					new Thread(new ThreadHandleServerIS(this, dstID)).start();   
				}else {
					socketTOClients.put(dstID, sock);
					oStreamClients.put(dstID, oos);
					new Thread(new ThreadHandleClientIS(this, dstID)).start();   
				}
			}catch(IOException e){e.printStackTrace();} 
		}
	}
	
	public void socketConnect(int serverNodeID, Utility u) {
		boolean tryConnect = true;
		Node serverNode = u.servers.get(serverNodeID);
		String hostName = serverNode.getHostname();
		int port = serverNode.getServerPort();
		while(tryConnect){
			try{
				InetAddress address = InetAddress.getByName(hostName);
				System.out.println (u.getMyNode().getID() + " send connection request to " + serverNodeID + " at " + serverNode.getServerPort());
				Socket clientSocket = new Socket(address, port);
				ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

				socketTOServers.put(serverNodeID, clientSocket);
				oStreamServers.put(serverNodeID, oos);
				System.out.println(u.getMyNode().getID() + " - server " + serverNodeID + " channel created");
				
				// Handle input stream in new thread
				new Thread(new ThreadHandleServerIS(this, serverNodeID)).start();  
				
				tryConnect = false;
			} catch (IOException e) {}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) { e.printStackTrace();}
		}
	}
	
	// boolean[] isActive
	public boolean getIsActive(int i) {
		return isActive[i];
	}
	public void setIsActive(int i, boolean b) {
		isActive[i] = b;
	}
	
	// boolean[] isActiveClient
	public boolean getIsActiveClient(int i) {
		return isActiveClient[i];
	}
	public void setIsActiveClient(int i, boolean b) {
		isActive[i] = b;
	}
	
}
