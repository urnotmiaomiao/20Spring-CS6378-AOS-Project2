package miao.server;

import java.io.IOException;

public class ThreadSocketListen implements Runnable {
	Connection con;
	Utility u;
	String role;
	public ThreadSocketListen(Utility u, Connection con, String role) {
		this.u = u;
		this.con = con;
		this.role = role;
	}
	@Override
	public void run() {
		try {
			con.socketListen(u, role);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
