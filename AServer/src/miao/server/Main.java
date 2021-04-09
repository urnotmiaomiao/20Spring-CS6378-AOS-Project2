package miao.server;

import java.util.Scanner;


public class Main {

	public static void main(String[] args) {
		
		Utility u = new Utility();
//		u.readFile("obj");
		Connection con = new Connection();
		con.createConnections(u);
		new Thread(new ThreadSocketListen(u, con, "client")).start();
		new Thread(new ThreadQueueProcess(u, con)).start();

        Scanner in = new Scanner(System.in);
        while(true) {
            System.out.print("input command> ");
            String line = in.nextLine();
//            System.out.println((line.hashCode() & Integer.MAX_VALUE)%7);
            Protocol.handleInput(u, con, line);
        }
		
	}
	
}
