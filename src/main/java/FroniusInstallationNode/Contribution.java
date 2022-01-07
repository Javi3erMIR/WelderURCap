package FroniusInstallationNode;

import java.awt.EventQueue;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.plaf.PanelUI;
import javax.swing.text.html.HTMLDocument.BlockElement;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.InstallationAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;
import CommunicationClasses.IOModbusState;
import CommunicationClasses.ScriptCommand;
import CommunicationClasses.ScriptSender;
import styleClasses.ShowDialog;

public class Contribution implements InstallationNodeContribution{
    
    private InstallationAPI apiProvider;
    private FroniusSetupView view;
    private DataModel model;
    private KeyboardInputFactory keyboardInputFactory;
    public static final String INPUT_KEY = "input_key";	
	public String DEFAULT_INPUT = "not_set";
	private ScriptSender sender;
	public int ioweld = 0;
	public int ioready = 0;
    private int reset_in = 0;
	private Timer uiTimer;
	public IOModbusState iomod;
	private int flag = 0;
    public CheckKey license;
    public static String PRODUCT_KEY = "not_set";
    public static String PRODUCT_KEY_KEY = "product_key";
    public static String serial_num;
    private ShowDialog option_dialog, connection_dialog;
    private boolean fit = false;
    private ScriptCommand command;
    private int value = 0;
    private int flag_1 = 0;
    
    public Contribution(InstallationAPIProvider apiProvider, FroniusSetupView view, DataModel model, CreationContext context){
        this.apiProvider = apiProvider.getInstallationAPI();
        this.view = view;
        this.model = model;
        keyboardInputFactory = apiProvider.getUserInterfaceAPI()
                                          .getUserInteraction()
                                          .getKeyboardInputFactory();
        sender = new ScriptSender();
        command = new ScriptCommand("exceptions");
        serial_num = apiProvider.getSystemAPI().getRobotModel().getSerialNumber();
        option_dialog = new ShowDialog("Option", "Are you sure to connect?");
        connection_dialog = new ShowDialog("Option", "Fronius Machine is disconnected");
        license = new CheckKey(serial_num);
    }

    private void showExceptionPopup(String message){
        command.appendLine("popup(\"" + message + "\")");
        sender.sendScriptCommand(command);
    }

    public KeyboardTextInput getKeyboardTextInput(){
        KeyboardTextInput keyboardTextInput = keyboardInputFactory.createStringKeyboardInput();
        return keyboardTextInput;
    }

    public KeyboardInputCallback<String> gKeyboardInputCallback(final JTextField textField, final String key){
        return new KeyboardInputCallback<String>() {
            @Override
            public void onOk(String value) {
               model.set(key, value);
               view.setIpText(value);
            }            
        };
    }

    public KeyboardTextInput getKeyboardTextInputKey(){
        KeyboardTextInput keyboardTextInput = keyboardInputFactory.createStringKeyboardInput();
        return keyboardTextInput;
    }

    public KeyboardInputCallback<String> gKeyboardInputCallbackKey(final JTextField textField, final String key){
        return new KeyboardInputCallback<String>() {
            @Override
            public void onOk(String value) {
                model.set(key, value);
                if(license.checKeyBySerial(value)){
                    fit = true;
                    view.panel_main.removeAll();
                    openView();
                }
            }            
        };
    }

    public String setModel(){
        if(view.getModelMenu().getSelectedItem() == null){
			return "Select";
		}else{
			return (String) view.getModelMenu().getSelectedItem();
		}
    }

    public String getMode(){
        String str = "Select";
        try {
            if(view.getModeTPSi().getSelectedItem() != null){
                str = view.getModeTPSi().getSelectedItem().toString();   
            }
        } catch (NullPointerException e) {}
        return str;
    }

    public void setIOmenu(int index) {
		ioweld = index;
	}
	
	public void setIOmenuR(int index) {
		ioready = index;
	}

    public void setResetIn(int index) {
		reset_in = index;
	}

    private String getDataModel(){
        return model.get(INPUT_KEY, DEFAULT_INPUT);
    }

    private void addSignals() {
        final ScriptCommand writer = new ScriptCommand("WeldConnect");
		new Thread(new Runnable() {			
			@Override
			public void run() {
				writer.appendLine("modbus_add_signal(\"" + getDataModel() + "\", 255, 0, 1, \"robotON\")"); 
                writer.appendLine("modbus_set_runstate_dependent_choice(\"robotON\", 1)");		
                writer.appendLine("modbus_set_signal_update_frequency(\"robotON\", 1)");
                writer.appendLine("modbus_add_signal(\"" + getDataModel() + "\", 255, 1, 1, \"robotReady\")"); 		
                writer.appendLine("modbus_set_signal_update_frequency(\"robotReady\", 1)");
                for(int i = 0; i < 10; i++){
                    writer.appendLine("modbus_add_signal(\"" + getDataModel() + "\", 255, "+ (64+i) +", 1, \"bit"+ (64+i) +"\")");
                    writer.appendLine("modbus_set_signal_update_frequency(\"bit"+ (64+i) +"\", 1)");
                }
                for(int i = 0; i < 64; i++){
                    writer.appendLine("modbus_add_signal(\"" + getDataModel() + "\", 255, "+ (80+i) +", 1, \"bit"+ (80+i) +"\")");
                    writer.appendLine("modbus_set_signal_update_frequency(\"bit"+ (80+i) +"\", 1)");
                }
                sender.sendScriptCommand(writer);
			}
		}).start();
    }

    private void turnoffSignals() {
		final ScriptCommand writer = new ScriptCommand("OFFSignals");
		new Thread(new Runnable() {			
			@Override
			public void run() {				
				writer.appendLine("modbus_set_output_signal(\"robotON\", False, False)");
                writer.appendLine("modbus_set_output_signal(\"robotReady\", False, False)");
                for(int i = 0; i < 10; i++){
                	writer.appendLine("modbus_set_output_signal(\"bit"+ (64+i) +"\", False, False)");
                }
                for(int i = 0; i < 64; i++){
                	writer.appendLine("modbus_set_output_signal(\"bit"+ (80+i) +"\", False, False)");
                }
                sender.sendScriptCommand(writer);
			}
		}).start();		
	}

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

    private void robotReady(){
        ScriptCommand writer = new ScriptCommand("robotReady");
        writer.appendLine("modbus_set_output_signal(\"robotReady\", True, False)");
        sender.sendScriptCommand(writer);
    }

    private void robotNotReady(){
        ScriptCommand writer = new ScriptCommand("robotReady");
        writer.appendLine("modbus_set_output_signal(\"robotReady\", False, False)");
        sender.sendScriptCommand(writer);
    }

    private void delay(int millis){
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            showExceptionPopup(e.getMessage());
        }
    }

    private void connectionDialog(final boolean bool){        
        new Thread(new Runnable() {
            @Override
            public void run() {                
                while(bool){
                    try {
                        value = iomod.getValue();
                        if(value==0){                        
                            flag++;
                            if(flag == 1)
                                disconnectFunction();                     
                        }
                        else if(value==1)
                            flag = 0;             
                    } catch (Exception e) {}
                    delay(250);
                }
            }            
        }).start();
    }

    public void connectFunction() {
        iomod = new IOModbusState(getDataModel()); 
        view.connection_indicator.setText("Connecting...");	
        addSignals();
        delay(250);
        robotReady();
        delay(1000);           
        option_dialog.showCustomDialog();
        if(ShowDialog.btn_option == 0){
            iomod.start();
            delay(1000);
            connectionDialog(true);    
        }       
        else{
            connectionDialog(false);
            delay(300);
            disconnectFunction();
        }
	}
    
    public void disconnectFunction(){
        robotNotReady();
        turnoffSignals();
        delay(250);
        deleteSignals();
        delay(250);
        iomod.killThread();
        delay(250);
        connectionDialog(false);
        delay(300);
        iomod.setValue(0);        
        delay(1000);
        iomod = null;
        view.isDisconnected();
        view.indicator_led_label.setVisible(false);
        view.connection_indicator.setText("Disconnected");
    }

    private void updateUI() {
        if(iomod != null){
            if(iomod.getPower()[0])
                view.indicator_led_label.setVisible(true);
            else if(!iomod.getPower()[0])
                view.indicator_led_label.setVisible(false);
            if(iomod.getValue() == 1)
                view.connection_indicator.setText("Connected");
            else if(iomod.getValue() == 0)
                view.connection_indicator.setText("Disconnected");
            String val = iomod.getWeldingProcess();
            if(val.equals("00000"))
                view.mode_indicator.setText("Internal Parameter Selection");
            if(val.equals("10000"))
                view.mode_indicator.setText("MIG/MAG pulse");
            if(val.equals("01000"))
                view.mode_indicator.setText("MIG/MAG Standard");
            if(val.equals("11000"))
                view.mode_indicator.setText("MIG/MAG PMC");
            if(val.equals("00100"))
                view.mode_indicator.setText("MIG/MAG LSC");
            if(val.equals("10100"))
                view.mode_indicator.setText("MIG/MAG standard manual");
            if(val.equals("01100"))
                view.mode_indicator.setText("Electrode");
            if(val.equals("11100"))
                view.mode_indicator.setText("TIG");
        }		
	}

    public void setMode(String item) {
		if(item.equals("Internal parameters") || item.equals("Select welding mode...")) {
			iomod.setInternalmode();
			if(iomod.getTeachModeValue()[0])
				iomod.setTeachMode(false);
		}
		if(item.equals("Job mode")) {
			iomod.setJobMode();
			if(iomod.getTeachModeValue()[0])
				iomod.setTeachMode(false);
		}
		if(item.equals("Teach mode"))
			iomod.setTeachMode(true);
	}

    public void setReady() {		
		if(view.getModelMenu().getSelectedItem().equals("TPS MagicWave")) {
			ScriptCommand writer = new ScriptCommand("RobotReadyOuts");
			writer.appendLine("set_digital_out(" + ioready + ", True)");
			sender.sendScriptCommand(writer);
		}
	}
	
	public void setNotReady() {
		if(view.getModelMenu().getSelectedItem().equals("TPS MagicWave")) {
			ScriptCommand writer = new ScriptCommand("RobotReadyOuts");
			writer.appendLine("set_digital_out(" + ioready + ", False)");
			sender.sendScriptCommand(writer);
		}		
	}

    public void setModeTig(String item) {
		ScriptCommand writer = new ScriptCommand("WeldingTigMode");
		if(item.equals("Job mode")) {
			writer.appendLine("set_digital_out(2, False)");
			writer.appendLine("set_digital_out(3, True)");
			writer.appendLine("set_digital_out(4, False)");
			sender.sendScriptCommand(writer);
		}
		if(item.equals("Internal parameters")) {
			writer.appendLine("set_digital_out(2, True)");
			writer.appendLine("set_digital_out(3, True)");
			writer.appendLine("set_digital_out(4, False)");
			sender.sendScriptCommand(writer);
		}
	}

    public void errorReset() {
		ScriptCommand writer = new ScriptCommand("Mode");
		writer.appendLine("set_digital_out(5, True)");
		writer.appendLine("sleep(2)");
		writer.appendLine("set_digital_out(5, False)");
		sender.sendScriptCommand(writer);
	}

    public boolean licenseKeyDialog(){       
        if(!fit){
            flag_1++;
            if(flag_1==1){
                view.panel_main.add(view.licenseCheck(this));             
                view.setComboModelEnabled(false);
            }                
        }
        else{
            flag_1 = 0;
            view.setComboModelEnabled(true);
        }            
        return fit;
    }

    @Override
    public void openView() {
        if(licenseKeyDialog()){
            setModel();
            uiTimer = new Timer(true);
            uiTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();                      
                        }                    
                    });
                }            
            }, 0, 1000);
        }        
    }

    @Override
    public void closeView() {
        setModel();
        if(uiTimer != null){
            uiTimer.cancel();
        }
    }

    @Override
    public void generateScript(ScriptWriter writer) {
    }
}
