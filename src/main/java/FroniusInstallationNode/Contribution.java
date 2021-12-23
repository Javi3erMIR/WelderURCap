package FroniusInstallationNode;

import java.awt.EventQueue;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextField;

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
	private Timer uiTimer;
	public IOModbusState iomod;
	private int flag = 0;
    
    public Contribution(InstallationAPIProvider apiProvider, FroniusSetupView view, DataModel model, CreationContext context){
        this.apiProvider = apiProvider.getInstallationAPI();
        this.view = view;
        this.model = model;
        keyboardInputFactory = apiProvider.getUserInterfaceAPI()
                                          .getUserInteraction()
                                          .getKeyboardInputFactory();
        this.sender = new ScriptSender();
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

    private String getDataModel(){
        return model.get(INPUT_KEY, DEFAULT_INPUT);
    }

    private void addSignals() {
        final ScriptCommand writer = new ScriptCommand("WeldConnect");
		new Thread(new Runnable() {			
			@Override
			public void run() {
				try {
					writer.appendLine("modbus_add_signal(\"" + getDataModel() + "\", 255, 0, 1, \"robotON\")"); 
					writer.appendLine("modbus_set_runstate_dependent_choice(\"robotON\", 1)");		
					writer.appendLine("modbus_set_signal_update_frequency(\"robotON\", 1)");
					for(int i = 0; i < 10; i++){
						writer.appendLine("modbus_add_signal(\"" + getDataModel() + "\", 255, "+ (64+i) +", 1, \"bit"+ (64+i) +"\")");
						writer.appendLine("modbus_set_signal_update_frequency(\"bit"+ (64+i) +"\", 1)");
					}
					for(int i = 0; i < 64; i++){
						writer.appendLine("modbus_add_signal(\"" + getDataModel() + "\", 255, "+ (80+i) +", 1, \"bit"+ (80+i) +"\")");
						writer.appendLine("modbus_set_signal_update_frequency(\"bit"+ (80+i) +"\", 1)");
					}
					sender.sendScriptCommand(writer);
				} catch (Exception e) {}
			}
		}).start();
    }

    private void turnoffSignals() {
		final ScriptCommand writer = new ScriptCommand("OFFSignals");
		new Thread(new Runnable() {			
			@Override
			public void run() {				
				writer.appendLine("modbus_set_output_signal(\"robotON\", False, False)");
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

    public void connectFunction() {
		view.connection_indicator.setText("Connecting...");
		iomod = new IOModbusState();
        iomod.setIP(getDataModel());
		try {
			addSignals();
			Thread.sleep(1000);
			iomod.start();
			//dialogThread(true);
		} catch (Exception e) {}
	}
    
    public void disconnectFunction(){
        try {
            turnoffSignals();
            Thread.sleep(250);
            deleteSignals();
            Thread.sleep(250);
            iomod.deadThread();
            iomod.setValue(0);
        } catch (Exception e) {}
    }

    private void updateUI() {
		if(iomod.getPower()[0]) {
			view.indicator_led_label.setVisible(true);
		}else if(!iomod.getPower()[0]){
			view.indicator_led_label.setVisible(false);
		}
		if(iomod.getValue() == 1) {
			view.connection_indicator.setText("Connected");
		}
		else if(iomod.getValue() == 0) {
			view.connection_indicator.setText("Disconnected");
		}
		String val = iomod.getWeldingProcess();
		if(val.equals("00000")){
			view.mode_indicator.setText("Internal Parameter Selection");
		}
		if(val.equals("10000")){
			view.mode_indicator.setText("MIG/MAG pulse");
		}
		if(val.equals("01000")){
			view.mode_indicator.setText("MIG/MAG Standard");
		}
		if(val.equals("11000")){
			view.mode_indicator.setText("MIG/MAG PMC");
		}
		if(val.equals("00100")){
			view.mode_indicator.setText("MIG/MAG LSC");
		}
		if(val.equals("10100")){
			view.mode_indicator.setText("MIG/MAG standard manual");
		}
		if(val.equals("01100")){
			view.mode_indicator.setText("Electrode");
		}
		if(val.equals("11100")){
			view.mode_indicator.setText("TIG");
		}
	}

    @Override
    public void openView() {
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
