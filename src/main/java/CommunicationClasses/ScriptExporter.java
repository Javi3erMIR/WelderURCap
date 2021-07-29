package CommunicationClasses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ScriptExporter {
		
	private final String SEND_IP;	
	private String RETURN_IP;
	private int RETURN_PORT = 5500;	
	private final String RETURN_SOCKETNAME = "\"EXPORT_SOCKET\"";
	
	public ScriptExporter() {
		this.SEND_IP = "127.0.0.1";
		this.RETURN_IP = this.SEND_IP;
	}
	
	public ScriptExporter(String Robot_IP) {
		this.SEND_IP = Robot_IP;
		this.RETURN_IP = this.SEND_IP;
	}
	
	public void setReturnPort(int port) {
		this.RETURN_PORT = port;
	}
	
	public void setReturnIP(String Return_IP) {
		this.RETURN_IP = Return_IP;
	}
	
	public int exportIntegerFromURScript(ScriptCommand command, String variable_name) {
		ScriptCommand newCommand = buildScriptCommandToExport(command, variable_name);
		String reply = readValueFromRobot(newCommand);
		
		int integerValue = Integer.parseInt(reply);
		
		return integerValue;
	}
	
	public String exportStringFromURScript(ScriptCommand command, String variable_name) {
		ScriptCommand newCommand = buildScriptCommandToExport(command, variable_name);
		String reply = readValueFromRobot(newCommand);
		
		return reply;
	}
	
	private ScriptCommand buildScriptCommandToExport(ScriptCommand command, String variable_name) {
		// Change to secondary program
		command.setAsSecondaryProgram();
		
		command.appendLine("socket_open(\""+RETURN_IP+"\","+RETURN_PORT+","+RETURN_SOCKETNAME+")");
		
		command.appendLine("socket_send_string("+variable_name+","+RETURN_SOCKETNAME+")");
		command.appendLine("socket_send_byte(13,"+RETURN_SOCKETNAME+")");	// CR
		command.appendLine("socket_send_byte(10,"+RETURN_SOCKETNAME+")");	// LF
		
		command.appendLine("socket_close("+RETURN_SOCKETNAME+")");
		
		return command;
	}
	
	private String readValueFromRobot(ScriptCommand commandWithReturn) {
		String input = "";
		try{
			// Create return socket
			ServerSocket server = new ServerSocket(RETURN_PORT);
			
			ScriptSender sender = new ScriptSender(SEND_IP);
			sender.sendScriptCommand(commandWithReturn);
			
			Socket returnSocket = server.accept();
			
			BufferedReader readerFromURScript = new BufferedReader(new InputStreamReader(returnSocket.getInputStream()));
			input = readerFromURScript.readLine();
			
			// Housekeeping
			readerFromURScript.close();
			returnSocket.close();
			server.close();
		} 
		catch (IOException e){
			System.out.println(e);
		}
		return input;
	}
}
