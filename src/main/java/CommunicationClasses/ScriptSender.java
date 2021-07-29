package CommunicationClasses;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ScriptSender {
	
	private final String TCP_IP;
	private final int TCP_port = 30003;
	
	public ScriptSender() { 
		this.TCP_IP = "127.0.0.1";
	}
	
	public ScriptSender(String IP) {
		this.TCP_IP = IP;
	}
	
	public void sendScriptCommand(ScriptCommand command) {
		sendToSecondary(command.toString());
		command.clear();
	}
	
	private void sendToSecondary(String command){
		try{
			// Create a new Socket Client
			Socket sc = new Socket(TCP_IP, TCP_port);
			if (sc.isConnected()){
				// Create stream for data
				DataOutputStream out;
				out = new DataOutputStream(sc.getOutputStream());
				
				// Send command
				out.write(command.getBytes("US-ASCII"));
				out.flush();

				// Perform housekeeping 
				out.close();
			}
			sc.close();
		} 
		catch (IOException e){
			System.out.println(e);
		}
	}
	
}
