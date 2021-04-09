package miao.client;

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
	public HashMap<Integer, Socket> socketTOServers = new HashMap<Integer, Socket>();
	public HashMap<Integer, ObjectOutputStream> oStreamServers = new HashMap<Integer, ObjectOutputStream>();
	
	public Connection() {
		for(int i = 0; i<isActive.length; i++) {
			isActive[i] = true;
		}
	}
	
	// boolean[] isActive
	public boolean getIsActive(int i) {
		return isActive[i];
	}
	public void setIsActive(int i, boolean b) {
		isActive[i] = b;
	}
	
	public void createConnections(Utility u) {
		for (int i=0; i<u.servers.size(); i++) {
			socketConnect(i, u);
		}
	}
	
	public void socketConnect(int serverNodeID, Utility u) {
		boolean tryConnect = true;
		Node serverNode = u.servers.get(serverNodeID);
		String hostName = serverNode.getHostname();
		int port = serverNode.getClientPort();
		while(tryConnect){
			try{
				InetAddress address = InetAddress.getByName(hostName);
				System.out.println (u.getMyNode().getID() + " send connection request to server" + serverNode.getID() + " at " + port);
				Socket clientSocket = new Socket(address, port);
				ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

				socketTOServers.put(serverNode.getID(), clientSocket);
				oStreamServers.put(serverNode.getID(), oos);
				
				// Handle input stream in new thread
				new Thread(new ThreadHandleServerIS(this, serverNode.getID())).start();  
				
				tryConnect = false;
			} catch (IOException e) {}
		}
	}
	
	public void sendMsg(Utility u, Message msg, int dstID) {
		if(isActive[dstID]) {
			try {
				ObjectOutputStream oos = oStreamServers.get(dstID);
				Protocol.atomTS.increaseTimestamp();
				oos.writeObject(msg);
				oos.flush();
				System.out.println("c"+u.getMyNode().getID()+" sends to s"+dstID+ " ["+msg+"]");
		
			}catch (IOException e) {System.out.println(e);}
		}else {
			System.out.println("c"+u.getMyNode().getID()+" to s"+dstID+" shutdown");
		}
		
	}

}
