package miao.server;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ThreadHandleClientIS implements Runnable{
//	Utility u;
	Connection con;
	int dstID;
	public ThreadHandleClientIS(Connection con, int dstID) {
//		this.u = u;
		this.con = con;
		this.dstID = dstID;
	}
	

	@Override
	public void run() {
		try {
			ObjectInputStream ois = new ObjectInputStream(con.socketTOClients.get(dstID).getInputStream());
			Protocol.receiveClientMsg(con, ois, dstID);
		} catch (IOException e) { e.printStackTrace();}
	}
}
