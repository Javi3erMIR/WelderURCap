package FroniusInstallationNode;

import CommunicationClasses.IOModbusState;
import CommunicationClasses.ScriptCommand;
import CommunicationClasses.ScriptSender;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.domain.URCapAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.ui.annotation.Div;
import com.ur.urcap.api.ui.annotation.Input;
import com.ur.urcap.api.ui.annotation.Label;
import com.ur.urcap.api.ui.annotation.Select;
import com.ur.urcap.api.ui.component.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class FroniusSetupContribution implements InstallationNodeContribution{

	private URCapAPI apiProvider;
	private final DataModel model;
	private static final String INPUT_KEY = "input_key";	
	public String DEFAULT_INPUT = "not_set";
	private ScriptSender sender;
	public int ioweld = 0;
	public int ioready = 0;
	private Timer uiTimer;
	public IOModbusState iomod;
	private int flag = 0;

	public FroniusSetupContribution(URCapAPI apiProvider, DataModel model) {
		this.apiProvider = apiProvider;
		this.model = model;
		this.sender = new ScriptSender();
	}

	@Div(id = "iodiv")
	private DivComponent lav;

	@Div(id = "divlbl")
	private DivComponent statuslbl;

	@Label(id = "imgPower")
	private LabelComponent imgPower;

	@Label(id = "lblstatus")
	private LabelComponent lblsta;

	@Label(id = "lblmode")
	private LabelComponent lblmo;

	@Label(id = "lbljob")
	private LabelComponent lbljo;

	@Label(id = "labela")
	private LabelComponent ad;

	@Label(id = "imageLive")
	private LabelComponent image;

	@Select(id = "selObject")
	private SelectDropDownList selObject;

	@Select(id = "weldMode")
	private SelectDropDownList mode;

	@Input(id = "btnDis")
	private InputButton btnDis;

	@Label(id = "machineWe")
	private LabelComponent machineW;

	@Select(id = "Ioweld")
	private SelectDropDownList ioWeld;

	@Select(id = "IoReady")
	private SelectDropDownList ioReady;

	@Div(id = "tpsiV")
	private DivComponent view_model;

	@Input(id = "TpsIP")
	private InputTextField inputTextField;

	@Input(id = "errorreset")
	private InputButton btnerror;

	@Override
	public void openView() {
		//Call this model setter method
		setModel();
		addHtmlItems();
		//Updating view for status connection label
		uiTimer = new Timer(true);
		uiTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				EventQueue.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						//Update method
						updateUI();
					}
				});
			}
		}, 0, 1000);
	}

	@Override
	public void closeView() {
		//Call this model setter method
		setModel();
		if (uiTimer != null) {
			uiTimer.cancel();
		}
	}

	@Override
	public void generateScript(ScriptWriter writer) {
		// TODO Auto-generated method stub
		
	}

	@Select(id = "selObject")
	public void onSelectChange(SelectEvent event){
		if (event.getEvent() == SelectEvent.EventType.ON_SELECT){
			if(selObject.getSelectedItem().equals("TPS 320i")){
				setNotReady();
				view_model.setEnabled(true);
				inputTextField.setEnabled(true);
				mode.setEnabled(false);
				lav.setEnabled(false);
				btnCon.setEnabled(true);
				btnerror.setEnabled(false);
				addTPSiItems();
				try {
					machineW.setVisible(true);
					machineW.setImage(ImageIO.read(getClass().getResource("/impl/TPS320i.png")));
				} catch (IOException e) {
					e.printStackTrace();
				}
				statuslbl.setEnabled(true);
			}else if(selObject.getSelectedItem().equals("TPS MagicWave 2200")){
				setNotReady();
				view_model.setEnabled(true);
				inputTextField.setEnabled(false);
				mode.setEnabled(true);
				lav.setEnabled(true);
				btnCon.setEnabled(false);
				btnDis.setEnabled(false);
				btnerror.setEnabled(true);
				addTPSItems();
				try {
					machineW.setVisible(true);
					machineW.setImage(ImageIO.read(getClass().getResource("/impl/M-71128.png")));
				} catch (IOException e) {
					e.printStackTrace();
				}
				statuslbl.setEnabled(false);

			}else if(selObject.getSelectedItem().equals("Select model...              ")){
				mode.selectItemAtIndex(0);
				view_model.setEnabled(false);
				machineW.setVisible(false);
				setNotReady();
			}
		}
	}

	@Select(id = "IoReady")
	private void setIndexReady(SelectEvent event){
		if(event.getEvent() == SelectEvent.EventType.ON_SELECT){
			setIOmenuR(ioReady.getSelectedIndex());
		}
	}

	@Input(id = "TpsIP")
	public void onClicklistener(InputEvent event){
		if(event.getEventType() == InputEvent.EventType.ON_CHANGE){
			model.set(INPUT_KEY, inputTextField.getText());
		}
	}

	@Select(id = "weldMode")
	private void setMod(SelectEvent event){
		if(event.getEvent() == SelectEvent.EventType.ON_SELECT){
			if(selObject.getSelectedItem().equals("TPS 320i")){
				setMode(mode.getSelectedItem().toString());
			}
			else if(selObject.getSelectedItem().equals("TPS MagicWave 2200")){
				setModeTig(mode.getSelectedItem().toString());
			}
		}
	}

	@Input(id = "errorreset")
	private void error(InputEvent event){
		if(event.getEventType() == InputEvent.EventType.ON_PRESSED){
			errorreset();
		}
	}

	@Select(id ="Ioweld")
	private void setIndex(SelectEvent event){
		if(event.getEvent() == SelectEvent.EventType.ON_SELECT){
			setIOmenu(ioWeld.getSelectedIndex());
			setReady();
		}
	}

	private void changedTpsIV(){
		selObject.setEnabled(false);
		inputTextField.setEnabled(false);
		mode.setEnabled(true);
		btnCon.setEnabled(false);
		btnDis.setEnabled(true);
	}

	private void changedTpsIVDis(){
		selObject.setEnabled(true);
		inputTextField.setEnabled(true);
		mode.setEnabled(false);
		btnCon.setEnabled(true);
		btnDis.setEnabled(false);
		mode.selectItemAtIndex(0);
	}

	private void addHtmlItems(){
		try{
			image.setImage(ImageIO.read(getClass().getResource("/impl/iconFro1.png")));
			imgPower.setImage(ImageIO.read(getClass().getResource("/impl/circulo.png")));
			imgPower.setVisible(false);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException w){
			w.printStackTrace();
		}
		if (selObject.getItemCount() == 0) {
			selObject.addItem("Select model...              ");
			selObject.addItem("TPS MagicWave 2200");
			selObject.addItem("TPS 320i");
			view_model.setVisible(true);
			view_model.setEnabled(false);
		}
		if (mode.getItemCount() == 0){
			mode.addItem("Select mode...              ");
		}
		if(ioWeld.getItemCount() == 0){
			for(int i = 0; i < 8; i++){
				ioWeld.addItem("digital_out[" + i + "]");
			}
		}
		if(ioReady.getItemCount() == 0){
			for(int i = 0; i < 8; i++){
				ioReady.addItem("digital_out[" + i + "]");
			}
		}
	}

	private void addTPSiItems(){
		mode.removeAllItems();
		mode.addItem("Select mode...              ");
		mode.addItem("Internal parameter selection");
		mode.addItem("Job mode");
		mode.addItem("Teach Mode");
	}

	private void addTPSItems(){
		mode.removeAllItems();
		mode.addItem("Select mode...              ");
		mode.addItem("Job mode");
		mode.addItem("Parameter selection internal");
	}

	@Input(id = "btnCon")
	private InputButton btnCon;
	
	//Method of connect directive
	@Input(id ="btnCon")
	private void onClickCon(InputEvent event){
		if(event.getEventType() == InputEvent.EventType.ON_PRESSED){
			directiveCon();
		}
	}

	@Input(id = "btnDis")
	private void Dis(InputEvent event){
		if(event.getEventType() == InputEvent.EventType.ON_PRESSED){
			directiveDis();
		}
	}

	public void directiveCon() {
		lblsta.setText("Connecting...");
		iomod = new IOModbusState(getinput());
		try {
			addSignals();
			Thread.sleep(100);
			iomod.start();
			dialogThread(true);
		} catch (Exception e) {}
		changedTpsIV();
	}
	
	//Method of disconnect directive
	public void directiveDis() {
		turnoffSignals();
		try {
			iomod.killThread();
			Thread.sleep(250);
			iomod.setValue(0);
			deleteSignals();
		} catch (Exception e) {}
		changedTpsIVDis();
	}
	
	//Custom message dialog in case of disconnection from welder
	private void showDialog() {
		String[] btns = {"OK"};
		int op = JOptionPane.showOptionDialog(null, "Fronius TPS 320i disconnected", "Warning!", 0, JOptionPane.ERROR_MESSAGE, null, btns, "");
		if(op == 0) {
			directiveDis();
			iomod.setFlag(0);
			dialogThread(false);
			flag = 0;
		}
	}
	
	//Thread flag to call custom dialog in case of disconnection
	private void dialogThread(final boolean cycle) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(cycle) {
					try {
						Thread.sleep(100);					
						if(iomod.getFlag() == 1) {
							flag++;
							if(flag == 1) {
								showDialog();
							}								
						}
					} catch (Exception e) {}
				}
			}
		}).start();
	}
	
	//Adding signals
	private void addSignals() {
		final ScriptCommand writer = new ScriptCommand("WeldConnect");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					writer.appendLine("modbus_add_signal(\"" + getinput() + "\", 255, 0, 1, \"robotON\")"); 
					writer.appendLine("modbus_set_runstate_dependent_choice(\"robotON\", 1)");		
					writer.appendLine("modbus_set_signal_update_frequency(\"robotON\", 1)");
					for(int i = 0; i < 10; i++){
						writer.appendLine("modbus_add_signal(\"" + getinput() + "\", 255, "+ (64+i) +", 1, \"bit"+ (64+i) +"\")");
						writer.appendLine("modbus_set_signal_update_frequency(\"bit"+ (64+i) +"\", 1)");
					}
					for(int i = 0; i < 64; i++){
						writer.appendLine("modbus_add_signal(\"" + getinput() + "\", 255, "+ (80+i) +", 1, \"bit"+ (80+i) +"\")");
						writer.appendLine("modbus_set_signal_update_frequency(\"bit"+ (80+i) +"\", 1)");
					}
					sender.sendScriptCommand(writer);
					Thread.sleep(250);
				} catch (Exception e) {}
			}
		}).start();
	}
	
	//Method for set all script signals to False
	private void turnoffSignals() {
		final ScriptCommand writer = new ScriptCommand("OFFSignals");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					writer.appendLine("modbus_set_output_signal(\"robotON\", False, False)");
					for(int i = 0; i < 10; i++){
						writer.appendLine("modbus_set_output_signal(\"bit"+ (64+i) +"\", False, False)");
					}
					for(int i = 0; i < 64; i++){
						writer.appendLine("modbus_set_output_signal(\"bit"+ (80+i) +"\", False, False)");
					}
					sender.sendScriptCommand(writer);
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	
	//Deleting signals to disconnect robot from welder
	private void deleteSignals() {
		final ScriptCommand writer = new ScriptCommand("RobotConnetionDel");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				writer.appendLine("modbus_delete_signal(\"robotON\")");
				for(int i = 0; i < 10; i++){
					writer.appendLine("modbus_delete_signal(\"bit"+ (64+i) +"\")");
				}
				for(int i = 0; i < 64; i++){
					writer.appendLine("modbus_delete_signal(\"bit"+ (80+i) +"\")");
				}
				sender.sendScriptCommand(writer);		
			}
		}).start();
	}
	
	//Connection status label
	private void updateUI() {
		if(iomod.getPower()[0]) {
			imgPower.setVisible(true);
		}else if(!iomod.getPower()[0]){
			imgPower.setVisible(false);
		}
		if(iomod.getValue() == 1) {
			lblsta.setText("Connected");
		}
		else if(iomod.getValue() == 0) {
			lblsta.setText("Disconnected");
		}
		String val = iomod.getWeldingProcess();
		if(val.equals("00000")){
			lblmo.setText("Internal Parameter Selection");
		}
		if(val.equals("10000")){
			lblmo.setText("MIG/MAG pulse");
		}
		if(val.equals("01000")){
			lblmo.setText("MIG/MAG Standard");
		}
		if(val.equals("11000")){
			lblmo.setText("MIG/MAG PMC");
		}
		if(val.equals("00100")){
			lblmo.setText("MIG/MAG LSC");
		}
		if(val.equals("10100")){
			lblmo.setText("MIG/MAG standard manual");
		}
		if(val.equals("01100")){
			lblmo.setText("Electrode");
		}
		if(val.equals("11100")){
			lblmo.setText("TIG");
		}
	}
	
	//Methods to set Robot Ready signal for MagicWave Welder 
	public void setReady() {		
		if(selObject.getSelectedItem().equals("TPS MagicWave 2200")) {
			ScriptCommand writer = new ScriptCommand("RobotReadyOuts");
			writer.appendLine("set_digital_out(" + ioready + ", True)");
			sender.sendScriptCommand(writer);
		}
	}
	
	public void setNotReady() {
		if(selObject.getSelectedItem().equals("TPS MagicWave 2200")) {
			ScriptCommand writer = new ScriptCommand("RobotReadyOuts");
			writer.appendLine("set_digital_out(" + ioready + ", False)");
			sender.sendScriptCommand(writer);
		}		
	}
	
	public String getinput() {
		return model.get(INPUT_KEY, DEFAULT_INPUT);
	}

	//Set welding mode to TPS 320i welder
	public void setMode(String item) {
		if(item.equals("Internal parameter selection") || item.equals("Select mode...              ")) {
			iomod.setInternalmode();
			if(iomod.getTeachModeValue()[0])
				iomod.setTeachMode(false);
		}
		if(item.equals("Job mode")) {
			iomod.setJobMode();
			if(iomod.getTeachModeValue()[0])
				iomod.setTeachMode(false);
		}
		if(item.equals("Teach Mode")){
			iomod.setTeachMode(true);
		}
	}
	
	//Set mode to TIG MagicWave welder
	public void setModeTig(String item) {
		ScriptCommand writer = new ScriptCommand("WeldingTigMode");
		if(item.equals("Job mode")) {
			writer.appendLine("set_digital_out(2, False)");
			writer.appendLine("set_digital_out(3, True)");
			writer.appendLine("set_digital_out(4, False)");
			sender.sendScriptCommand(writer);
		}
		if(item.equals("Parameter selection internal")) {
			writer.appendLine("set_digital_out(2, True)");
			writer.appendLine("set_digital_out(3, True)");
			writer.appendLine("set_digital_out(4, False)");
			sender.sendScriptCommand(writer);
		}
	}
	
	public void setIOmenu(int index) {
		ioweld = index;
	}
	
	public void setIOmenuR(int index) {
		ioready = index;
	}
	
	public void errorreset() {
		ScriptCommand writer = new ScriptCommand("Mode");
		writer.appendLine("set_digital_out(5, True)");
		writer.appendLine("sleep(2)");
		writer.appendLine("set_digital_out(5, False)");
		sender.sendScriptCommand(writer);
	}
	
	public String setModel() {
		if(selObject.getSelectedItem() == null){
			return "Select";
		}else{
			return (String) selObject.getSelectedItem();
		}
	}

	public String getMode(){
		String ew = "Select";
		try {
			if(mode.getSelectedItem() == null){
				ew = "Select";
			}else{
				ew = mode.getSelectedItem().toString();
			}
		}catch (NullPointerException e){

		}
		return ew;
	}
	
}
