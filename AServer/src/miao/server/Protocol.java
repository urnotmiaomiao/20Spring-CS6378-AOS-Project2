package miao.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import miao.AtomTimeStamp;
import miao.Message;

public class Protocol {
	public static AtomTimeStamp atomTS = new AtomTimeStamp();
	
	public static BlockingQueue<Message> MsgQueueClient = new PriorityBlockingQueue<Message>();
	public static HashMap <String, Message> MsgHMServer = new HashMap<String, Message>();
	
	// command like: "break 4 c", "break 6 s"
	public static boolean handleInput(Utility u, Connection con, String line) {
		String[] lineList = line.split(" ");
		if(lineList.length!=3||!u.isCommand(lineList[0])) {
			System.out.println("Input not correct!");
			return false;
		}
		Message message = u.createMsg(lineList);
		int dstID = -1;
		switch(lineList[0]) {
			case "break":
				dstID = Integer.valueOf(lineList[1]);
				if(lineList[2].equals("s")) {
					con.sendMsg(u, message, dstID);
					con.setIsActive(dstID, false);
				}else if(lineList[2].equals("c")){
					con.sendMsgClient(u, message, dstID);
					con.setIsActiveClient(dstID, false);
				}
				break;
		}
		return true;
	}
	
	public static void handleTimeStamp(int ts) {
		if(ts>atomTS.getTimestamp()) {
			atomTS.setTimestamp(ts);
		}
	}
	
	public static void receiveServerMsg(Connection con, ObjectInputStream ois, int dstID) {
		
		while(true) {
			try {
				Message message = (Message) ois.readObject();
				System.out.println(message);
				handleTimeStamp(message.getTimestamp());
				if(message.getCommand().equals("break")) {
					con.setIsActive(dstID, false);
				}else {
					// put msg in hashmap
					MsgHMServer.put(message.toKey(), message);
				}
			} catch (ClassNotFoundException | IOException e) { e.printStackTrace();}
		}
	}
	
	public static void receiveClientMsg(Connection con, ObjectInputStream ois, int dstID) {
			
		while(true) {
			try {
				Message message = (Message) ois.readObject();
				System.out.println(message);
				handleTimeStamp(message.getTimestamp());
				if(message.getCommand().equals("break")) {
					con.setIsActiveClient(dstID, false);
				}else {
					// put msg in queue
					MsgQueueClient.add(message);
				}
				
			} catch (ClassNotFoundException | IOException e) { e.printStackTrace();}
		}
	}
		
	public static void processMsgQueueClient(Connection con, Utility u, Message message) {
		if(message.getCommand().equals("get")) {
			if(u.isObjectExists(message.getObjectName())) {
				message.setContent(u.readFile(message.getObjectName()));
			}else {
				message.setContent("Object does not exist");
			}
			con.sendMsgClient(u, message, message.getClientID());
			try {
				MsgQueueClient.take();
			} catch (InterruptedException e) {e.printStackTrace();}
			
		}else {
			if(!message.getIsSent()) {
				for(int i = 0; i<3; i++) {
					int dstID = message.getServerID()[i];
					if(dstID != u.getMyNode().getID()) {
						con.sendMsg(u, message, dstID);
					}
				}
				message.setIsSent(true);
			}else {
				if(MsgHMServer.containsKey(message.toKey())) {
					MsgHMServer.remove(message.toKey());
					try {
						MsgQueueClient.take();
					} catch (InterruptedException e) { e.printStackTrace(); }
					processMessage(u, message);
					con.sendMsgClient(u, message, message.getClientID());
				}else if(atomTS.getTimestamp() - message.getTimestamp()>=50) {
					try {
						MsgQueueClient.take();
					} catch (InterruptedException e) { e.printStackTrace(); }
				}
			}
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) { e.printStackTrace();}
		
	}
	
	public static void processMessage(Utility u, Message message) {
		switch(message.getCommand()) {
		case "delete":
			if(u.isObjectExists(message.getObjectName())){
				u.deleteFile(message.getObjectName());
			}
			break;
		case "update":
			if(u.isObjectExists(message.getObjectName())){
				u.appendFile(message.getObjectName(), message.toString()+"\n");
			}
			break;
		case "create":
			if(!u.isObjectExists(message.getObjectName())){
				u.createFile(message.getObjectName());
			}
			break;
		}
	}
	
	
}
