package miao.client;

import java.util.Scanner;


public class Main {

	public static void main(String[] args) {
		Utility u = new Utility();
		Connection con = new Connection();
		con.createConnections(u);
		
        Scanner in = new Scanner(System.in);
        while(true) {
            System.out.print("input command> ");
            String line = in.nextLine();
//            System.out.println((line.hashCode() & Integer.MAX_VALUE)%7);
            Protocol.handleInput(u, con, line);
        }
		
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		con.disconnectServer(0, u);
		
	}

}
