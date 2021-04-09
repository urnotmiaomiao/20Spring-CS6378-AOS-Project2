package miao.client;

import java.io.IOException;
import java.io.ObjectInputStream;

import miao.AtomTimeStamp;
import miao.Message;

public class Protocol {

	public static AtomTimeStamp atomTS = new AtomTimeStamp();
	
	public static void handleTimeStamp(int ts) {
		if(ts>atomTS.getTimestamp()) {
			atomTS.setTimestamp(ts);
		}
	}
	
	public static boolean handleInput(Utility u, Connection con, String line) {
		String[] lineList = line.split(" ");
		if(lineList.length!=2||!u.isCommand(lineList[0])) {
			System.out.println("Input not correct!");
			return false;
		}
		Message message = u.createMsg(lineList);
		int dstID = -1;
		switch(lineList[0]) {
			case "get":
				dstID = message.getServerID()[(int)(Math.random()*3)];
				con.sendMsg(u, message, dstID);
				break;
			case "break":
				dstID = Integer.valueOf(lineList[1]);
				con.sendMsg(u, message, dstID);
				con.setIsActive(dstID, false);
				break;
			default:
				for(int i = 0; i<3; i++) {
					dstID = message.getServerID()[i];
					con.sendMsg(u, message, dstID);
				}
		}
		return true;
	}
	
	public static void receiveServerMsg(Connection con, ObjectInputStream ois, int dstID) {
		while(true) {
			try {
				Message message = (Message) ois.readObject();
				if(message.getContent().equals("break")) {
					con.setIsActive(dstID, false);
				}else {
					System.out.println(message);
					handleTimeStamp(message.getTimestamp());
				}
			} catch (ClassNotFoundException | IOException e) { e.printStackTrace();}
		}
	}
	
}
