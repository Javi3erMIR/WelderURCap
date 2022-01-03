package CommunicationClasses;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

//Support library for connection status
import EasyModbus.ModbusClient;
/*Class for check connection status*/
import styleClasses.ShowDialog;

public class IOModbusState extends Thread {

	private int value, flag;
	private boolean bool;
	private boolean[] inputs;
	private String IP;
	public ModbusClient client;
	private ScriptCommand command;
	private ScriptSender sender;
	private ShowDialog dialog;

	public IOModbusState(String IP){
		value = 0;
		flag = 0;
		bool = true;
		this.IP = IP;
		client = new ModbusClient(this.IP, 502);
		command = new ScriptCommand("ClassSpecial");
		sender = new ScriptSender();		
	}

	@Override
	public void run() {
		try {
			//client.Disconnect();
			Thread.sleep(100);
			client.Connect();
			if (client.isConnected()) {				
				while (bool) {
					inputs = client.ReadDiscreteInputs(1, 1);
					Thread.sleep(250);
					if (inputs[0]) 
						value = 1;					
					else if (!inputs[0]) {
						killThread();
						flag++;
						value = 0;
						client.WriteSingleCoil(1, false);
						client.Disconnect();					
					}
				}
				client.Disconnect();
			}
		} catch (UnknownHostException e) {
			command.appendLine("popup(\""+e.getMessage()+"\")");
			sender.sendScriptCommand(command);
		} catch (IOException e) {
			command.appendLine("popup(\""+e.getMessage()+"\")");
			sender.sendScriptCommand(command);
		} catch(Exception e){
			command.appendLine("popup(\""+e.getMessage()+"\")");
			sender.sendScriptCommand(command);
		}		
	}

	//Welding mode methods-----------------------------------------------------------------------------------------------------------------------------------------------
	public void setInternalmode() {
		boolean[] values = {false, false, false, false, false};
		try {
			if (client.isConnected())
				client.WriteMultipleCoils(2, values);
		} catch (Exception e) {
			flag++;
			value = 0;
		}
	}

	public void setSpecialMode() {
		boolean[] values = {true, false, false, false, false};
		try {
			if (client.isConnected())
				client.WriteMultipleCoils(2, values);
		} catch (Exception e) {
			flag++;
			value = 0;
		}
	}

	public void setJobMode() {
		boolean[] values = {false, true, false, false, false};
		try {
			if (client.isConnected())
				client.WriteMultipleCoils(2, values);
		} catch (Exception e) {
			flag++;
			value = 0;
		}
	}

	public void set2stepMode() {
		boolean[] values = {false, false, false, true, false};
		try {
			if (client.isConnected())
				client.WriteMultipleCoils(2, values);
		} catch (Exception e) {
			flag++;
			value = 0;
		}
	}

	public void setTeachMode(boolean w) {
		try {
			if (client.isConnected())
				client.WriteSingleCoil(25, w);
		} catch (Exception e) {
			flag++;
			value = 0;
		}
	}

	public boolean[] getTeachModeValue() {
		boolean[] values = new boolean[1];
		try {
			if (client.isConnected())
				values = client.ReadCoils(25, 1);
		} catch (Exception e) {
			flag++;
			value = 0;
		}
		return values;
	}

	public String getWeldingProcess() {
		boolean[] values = {false, false, false, false, false};
		try {
			if (client.isConnected())
				values = client.ReadDiscreteInputs(48, 5);
		} catch (Exception e) {
			flag++;
			value = 0;
		}
		String bin = "";
		for (int i = 0; i < values.length; i++) {
			if (values[i]) {
				bin += "1";
			} else if (!values[i]) {
				bin += "0";
			}
		}
		return bin;
	}

	public boolean[] getPower() {
		boolean[] values = {false};
		try {
			if (client.isConnected())
				values = client.ReadDiscreteInputs(0, 1);
		} catch (Exception e) {
			flag++;
			value = 0;
		}
		return values;
	}

	public int getVoltage() {
		boolean[] values = new boolean[16];
		try {
			if (client.isConnected())
				values = client.ReadDiscreteInputs(64, 16);
		} catch (Exception e) {
		}
		String bin = "";
		for (int i = values.length - 1; i >= 0; i--) {
			if (values[i]) {
				bin += "1";
			} else if (!values[i]) {
				bin += "0";
			}
		}
		int decimal = 0;
		int posicion = 0;
		for (int x = bin.length() - 1; x >= 0; x--) {
			short digito = 1;
			if (bin.charAt(x) == '0') {
				digito = 0;
			}
			double multiplicador = Math.pow(2, posicion);
			decimal += digito * multiplicador;
			posicion++;
		}
		return decimal;
	}

	public int getCurrent() {
		boolean[] values = new boolean[16];
		try {
			if (client.isConnected())
				values = client.ReadDiscreteInputs(80, 16);
		} catch (Exception e) {
		}
		String bin = "";
		for (int i = values.length - 1; i >= 0; i--) {
			if (values[i]) {
				bin += "1";
			} else if (!values[i]) {
				bin += "0";
			}
		}
		int decimal = 0;
		int posicion = 0;
		for (int x = bin.length() - 1; x >= 0; x--) {
			short digito = 1;
			if (bin.charAt(x) == '0') {
				digito = 0;
			}
			double multiplicador = Math.pow(2, posicion);
			decimal += digito * multiplicador;
			posicion++;
		}
		return decimal;
	}

	public int getSpeed() {
		boolean[] values = new boolean[16];
		try {
			if (client.isConnected())
				values = client.ReadDiscreteInputs(96, 16);
		} catch (Exception e) {
		}
		String bin = "";
		for (int i = values.length - 1; i >= 0; i--) {
			if (values[i]) {
				bin += "1";
			} else if (!values[i]) {
				bin += "0";
			}
		}
		int decimal = 0;
		int posicion = 0;
		for (int x = bin.length() - 1; x >= 0; x--) {
			short digito = 1;
			if (bin.charAt(x) == '0') {
				digito = 0;
			}
			double multiplicador = Math.pow(2, posicion);
			decimal += digito * multiplicador;
			posicion++;
		}
		return decimal;
	}

//Class methods------------------------------------------------------------------------------------------------------------------------------------------------------		
	public int getValue() {
		return this.value;
	}
		
	public void killThread() {
		this.bool = false;
	}
	
	public void setValue(int val) {
		value = val;
	}

	public int getFlag() {
		return this.flag;
	}
	
	public void setFlag(int fla) {
		this.flag = fla;
	}

	public void startLoop(){
		this.bool = true;
	}	
}
