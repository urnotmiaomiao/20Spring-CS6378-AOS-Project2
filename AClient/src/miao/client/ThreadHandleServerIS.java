package miao.client;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ThreadHandleServerIS implements Runnable{
//	Utility u;
	Connection con;
	int dstID;
	public ThreadHandleServerIS(Connection con, int dstID) {
//		this.u = u;
		this.con = con;
		this.dstID = dstID;
	}
	

	@Override
	public void run() {
		try {
			ObjectInputStream ois = new ObjectInputStream(con.socketTOServers.get(dstID).getInputStream());
			Protocol.receiveServerMsg(con, ois, dstID);
		} catch (IOException e) { e.printStackTrace();}
	}
}
