package miao.server;

public class ThreadQueueProcess  implements Runnable{
	Connection con;
	Utility u;
	
	public ThreadQueueProcess(Utility u, Connection con) {
		this.con = con;
		this.u = u;
	}
	
	@Override
	public void run() {
		while(true) {
			if(Protocol.MsgQueueClient.peek()!=null) {
//				System.out.println("Processing msg..");
				Protocol.processMsgQueueClient(con, u, Protocol.MsgQueueClient.peek());
			}
		}
	}
}
