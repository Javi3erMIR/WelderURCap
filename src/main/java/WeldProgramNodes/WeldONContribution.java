<<<<<<< HEAD
package WeldProgramNodes;

import EasyModbus.ModbusClient;
import FroniusInstallationNode.Contribution;
import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.CreationContext;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.ProgramAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.undoredo.UndoRedoManager;
import com.ur.urcap.api.domain.undoredo.UndoableChanges;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;
import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class WeldONContribution implements ProgramNodeContribution{
	
	private ProgramAPI apiProvider;
	private WeldONView view;
	private DataModel model;
	private UndoRedoManager undoRedoManager;
	private KeyboardInputFactory keyboardInputFactory;
	private static final String JOB_INPUT_KEY = "job_input";
	private static final String WIRE_INPUT_KEY = "wire_input";
	private static final String ARC_INPUT_KEY = "arc_input";
	private static final String PULSE_INPUT_KEY = "pulse_input";
	private static final String RETRACT_INPUT_KEY = "retract_input";
	private static final String DEFAULT_VALUE = "0";
	private Double inputText = 0.0;
	private Timer uiTimer;
	private ModbusClient client;
	
	public WeldONContribution(ProgramAPIProvider apiProvider, WeldONView view, DataModel model, CreationContext context){
		this.apiProvider = apiProvider.getProgramAPI();
		this.view = view;
		this.model = model;
		undoRedoManager = apiProvider.getProgramAPI().getUndoRedoManager();
		keyboardInputFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();
		client = getInstallation().iomod.client;
	}

	@Override
	public void openView() {
		update();
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
		if(uiTimer != null){
			uiTimer.cancel();
		}
	}

	@Override
	public String getTitle() {
		String title = "mode";
		if(getInstallation().getMode().equals("Job mode")){
			title = "Arc ON: job " + model.get(JOB_INPUT_KEY, DEFAULT_VALUE);
		}else{
			title = "Arc ON";
		}
		return title;
	}

	@Override
	public boolean isDefined() {
		return true;
	}
	
	private Contribution getInstallation() {
		try {
			return apiProvider.getInstallationNode(Contribution.class);
		}catch (NullPointerException e){
			return null;
		}
	}

	@Override
	public void generateScript(ScriptWriter writer) {
		if(getInstallation().setModel().equals("TPS 320i")) {
			if(getInstallation().getMode().equals("Job mode")){
				Integer[] binumJob = getintbin(JOB_INPUT_KEY);
				for(int i = 0; i < 10; i++){
					if(binumJob[i] == 1) {
						writer.appendLine("modbus_set_output_signal(\"bit"+(64+i)+"\", True, False)");
					}
					else {
						writer.appendLine("modbus_set_output_signal(\"bit"+(64+i)+"\", False, False)");
					}
				}
			}else{
				Integer[] binnumWire = getBinDouble(WIRE_INPUT_KEY);
				for(int i = 0; i< binnumWire.length; i++){
					if(binnumWire[i] == 1) {
						writer.appendLine("modbus_set_output_signal(\"bit"+(80+i)+"\", True, False)");
					}
					else {
						writer.appendLine("modbus_set_output_signal(\"bit"+(80+i)+"\", False, False)");
					}
				}
				Integer[] binnumArc = getBinDouble(ARC_INPUT_KEY);
				for(int i = 0; i< binnumArc.length; i++){
					if(binnumArc[i] == 1) {
						writer.appendLine("modbus_set_output_signal(\"bit"+(96+i)+"\", True, False)");
					}
					else {
						writer.appendLine("modbus_set_output_signal(\"bit"+(96+i)+"\", False, False)");
					}
				}
				Integer[] binnumPulse = getBinDouble(PULSE_INPUT_KEY);
				for(int i = 0; i< binnumPulse.length; i++){
					if(binnumPulse[i] == 1) {
						writer.appendLine("modbus_set_output_signal(\"bit"+(112+i)+"\", True, False)");
					}
					else {
						writer.appendLine("modbus_set_output_signal(\"bit"+(112+i)+"\", False, False)");
					}
				}
				Integer[] binnumRetract = getintbin(RETRACT_INPUT_KEY);
				for(int i = 0; i< binnumRetract.length; i++){
					if(binnumRetract[i] == 1) {
						writer.appendLine("modbus_set_output_signal(\"bit"+(128+i)+"\", True, False)");
					}
					else {
						writer.appendLine("modbus_set_output_signal(\"bit"+(128+i)+"\", False, False)");
					}
				}
			}
			writer.appendLine("sleep(0.5)");
			writer.appendLine("modbus_set_output_signal(\"robotON\", True, False)");
			writer.appendLine("sleep(1)");
		}
		if(getInstallation().setModel().equals("TPS MagicWave")) {
			if(getInstallation().getMode().equals("Job mode")){
				Integer[] binum = getintbin(JOB_INPUT_KEY);
				for(int i = 0; i < 8; i++){
					if(binum[i] == 1) {
						writer.appendLine("set_configurable_digital_out("+i+", True)");
					}
					else {
						writer.appendLine("set_configurable_digital_out("+i+", False)");
					}
				}
			}
			writer.appendLine("sleep(0.5)");
			writer.appendLine("set_digital_out(" + getInstallation().ioweld +", True)");
			writer.appendLine("sleep(1)");
		}
	}

	public KeyboardNumberInput getKeyboardForInput() {
		KeyboardNumberInput keyboard = keyboardInputFactory.createDoubleKeypadInput();
		return keyboard;
	}

	public KeyboardInputCallback<Double> getKeyBoardCallBack(final JTextField textField, final String key) {
		return new KeyboardInputCallback<Double>() {
			@Override
			public void onOk(Double value) {
				inputText = value;
				undoRedoManager.recordChanges(new UndoableChanges() {
					@Override
					public void executeChanges() {
						model.set(key, inputText);
					}
				});
				view.setTextField(textField, value.toString());
			}
		};
	}

	public KeyboardNumberInput getKeyboardInt(){
		KeyboardNumberInput keyboardNumberInput = keyboardInputFactory.createIntegerKeypadInput();
		return keyboardNumberInput;
	}

	public KeyboardInputCallback<Integer> getKeyboardInputCallbackInt(final JTextField textField, final String key){
		return new KeyboardInputCallback<Integer>() {
			@Override
			public void onOk(final Integer value) {
				undoRedoManager.recordChanges(new UndoableChanges() {
					@Override
					public void executeChanges() {
						model.set(key, value);
					}
				});
				view.setTextField(textField, value.toString());
			}
		};
	}

	private void update(){
		view.setTextField(view.jobNumtf, model.get(JOB_INPUT_KEY, DEFAULT_VALUE));
		view.setTextField(view.wireFeedtf, model.get(WIRE_INPUT_KEY, DEFAULT_VALUE));
		view.setTextField(view.arcLentf, model.get(ARC_INPUT_KEY, DEFAULT_VALUE));
		view.setTextField(view.pulseDynamictf, model.get(PULSE_INPUT_KEY, DEFAULT_VALUE));
		view.setTextField(view.wireRetracttf, model.get(RETRACT_INPUT_KEY, DEFAULT_VALUE));
		if(getInstallation().setModel().equals("TPS 320i")){
			if(getInstallation().getMode().equals("Job mode")){
				view.jobNumtf.setEnabled(true);
				view.wireFeedtf.setEnabled(false);
				view.arcLentf.setEnabled(false);
				view.pulseDynamictf.setEnabled(false);
				view.wireRetracttf.setEnabled(false);
			}else{
				view.jobNumtf.setEnabled(false);
				view.wireFeedtf.setEnabled(true);
				view.arcLentf.setEnabled(true);
				view.pulseDynamictf.setEnabled(true);
				view.wireRetracttf.setEnabled(true);
			}
		}else if(getInstallation().setModel().equals("TPS MagicWave")){
			if(getInstallation().getMode().equals("Job mode")){
				view.jobNumtf.setEnabled(true);
				view.wireFeedtf.setEnabled(false);
				view.arcLentf.setEnabled(false);
				view.pulseDynamictf.setEnabled(false);
				view.wireRetracttf.setEnabled(false);
			}else{
				view.jobNumtf.setEnabled(false);
				view.wireFeedtf.setEnabled(false);
				view.arcLentf.setEnabled(false);
				view.pulseDynamictf.setEnabled(false);
				view.wireRetracttf.setEnabled(false);
			}
		}else{
			view.jobNumtf.setEnabled(false);
			view.wireFeedtf.setEnabled(false);
			view.arcLentf.setEnabled(false);
			view.pulseDynamictf.setEnabled(false);
			view.wireRetracttf.setEnabled(false);
		}
	}

	public Integer[] getintbin(String key) {
		int dec = Integer.parseInt(model.get(key, DEFAULT_VALUE));
		int i = 0;
		Integer[] binum = new Integer[16];
		for(int j= 0; j<16; j++)
			binum[j] = 0;
		int bina = Integer.valueOf(dec);
		while(bina > 0) {
			binum[i] = bina % 2;
			bina = bina / 2;
			i++;
		}
		return binum;
	}

	public Integer[] getBinDouble(String key){
		Double decD = Double.parseDouble(model.get(key, DEFAULT_VALUE)) * 100;
		int dec = Integer.parseInt(String.valueOf(decD));
		int i = 0;
		Integer[] binum = new Integer[16];
		for(int j= 0; j<16; j++)
			binum[j] = 0;
		int bina = Integer.valueOf(dec);
		while(bina > 0) {
			binum[i] = bina % 2;
			bina = bina / 2;
			i++;
		}
		return binum;
	}

	public void updateUI(){
		int voltageInt = getInstallation().iomod.getVoltage();
		double voltage = voltageInt;
		int currentInt = getInstallation().iomod.getCurrent();
		double current = currentInt;
		int speedInt = getInstallation().iomod.getSpeed();
		double speed = speedInt;
 		view.setIOvalues(String.valueOf(voltage/100), String.valueOf(current/10), String.valueOf(speed/100));
	}

}
=======
package WeldProgramNodes;

import EasyModbus.ModbusClient;
import FroniusInstallationNode.FroniusSetupContribution;
import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.CreationContext;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.ProgramAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.undoredo.UndoRedoManager;
import com.ur.urcap.api.domain.undoredo.UndoableChanges;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.io.IOException;
import java.util.TimerTask;

public class WeldONContribution implements ProgramNodeContribution{
	
	private ProgramAPI apiProvider;
	private WeldONView view;
	private DataModel model;
	private UndoRedoManager undoRedoManager;
	private KeyboardInputFactory keyboardInputFactory;
	private static final String JOB_INPUT_KEY = "job_input";
	private static final String WIRE_INPUT_KEY = "wire_input";
	private static final String ARC_INPUT_KEY = "arc_input";
	private static final String PULSE_INPUT_KEY = "pulse_input";
	private static final String RETRACT_INPUT_KEY = "retract_input";
	private static final String DEFAULT_VALUE = "0";
	private Double inputText = 0.0;
	private Timer uiTimer;
	private ModbusClient client;
	
	public WeldONContribution(ProgramAPIProvider apiProvider, WeldONView view, DataModel model, CreationContext context){
		this.apiProvider = apiProvider.getProgramAPI();
		this.view = view;
		this.model = model;
		undoRedoManager = apiProvider.getProgramAPI().getUndoRedoManager();
		keyboardInputFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();
		client = getInstallation().iomod.client;
	}

	@Override
	public void openView() {
		update();
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
		if(uiTimer != null){
			uiTimer.cancel();
		}
	}

	@Override
	public String getTitle() {
		String title = "mode";
		if(getInstallation().getMode().equals("Job mode")){
			title = "Arc ON: job " + model.get(JOB_INPUT_KEY, DEFAULT_VALUE);
		}else{
			title = "Arc ON";
		}
		return title;
	}

	@Override
	public boolean isDefined() {
		return true;
	}
	
	private FroniusSetupContribution getInstallation() {
		try {
			return apiProvider.getInstallationNode(FroniusSetupContribution.class);
		}catch (NullPointerException e){
			return null;
		}
	}

	@Override
	public void generateScript(ScriptWriter writer) {
		if(getInstallation().setModel().equals("TPS 320i")) {
			if(getInstallation().getMode().equals("Job mode")){
				Integer[] binumJob = getintbin(JOB_INPUT_KEY);
				for(int i = 0; i < 10; i++){
					if(binumJob[i] == 1) {
						writer.appendLine("modbus_set_output_signal(\"bit"+(64+i)+"\", True, False)");
					}
					else {
						writer.appendLine("modbus_set_output_signal(\"bit"+(64+i)+"\", False, False)");
					}
				}
			}else{
				Integer[] binnumWire = getBinDouble(WIRE_INPUT_KEY);
				for(int i = 0; i< binnumWire.length; i++){
					if(binnumWire[i] == 1) {
						writer.appendLine("modbus_set_output_signal(\"bit"+(80+i)+"\", True, False)");
					}
					else {
						writer.appendLine("modbus_set_output_signal(\"bit"+(80+i)+"\", False, False)");
					}
				}
				Integer[] binnumArc = getBinDouble(ARC_INPUT_KEY);
				for(int i = 0; i< binnumArc.length; i++){
					if(binnumArc[i] == 1) {
						writer.appendLine("modbus_set_output_signal(\"bit"+(96+i)+"\", True, False)");
					}
					else {
						writer.appendLine("modbus_set_output_signal(\"bit"+(96+i)+"\", False, False)");
					}
				}
				Integer[] binnumPulse = getBinDouble(PULSE_INPUT_KEY);
				for(int i = 0; i< binnumPulse.length; i++){
					if(binnumPulse[i] == 1) {
						writer.appendLine("modbus_set_output_signal(\"bit"+(112+i)+"\", True, False)");
					}
					else {
						writer.appendLine("modbus_set_output_signal(\"bit"+(112+i)+"\", False, False)");
					}
				}
				Integer[] binnumRetract = getintbin(RETRACT_INPUT_KEY);
				for(int i = 0; i< binnumRetract.length; i++){
					if(binnumRetract[i] == 1) {
						writer.appendLine("modbus_set_output_signal(\"bit"+(128+i)+"\", True, False)");
					}
					else {
						writer.appendLine("modbus_set_output_signal(\"bit"+(128+i)+"\", False, False)");
					}
				}
			}
			writer.appendLine("sleep(0.5)");
			writer.appendLine("modbus_set_output_signal(\"robotON\", True, False)");
			writer.appendLine("sleep(1)");
		}
		if(getInstallation().setModel().equals("TPS MagicWave 2200")) {
			if(getInstallation().setModel().equals("Job mode")){
				Integer[] binum = getintbin(JOB_INPUT_KEY);
				for(int i = 0; i < 8; i++){
					if(binum[i] == 1) {
						writer.appendLine("set_configurable_digital_out("+i+", True)");
					}
					else {
						writer.appendLine("set_configurable_digital_out("+i+", False)");
					}
				}
			}
			writer.appendLine("sleep(0.5)");
			writer.appendLine("set_digital_out(" + getInstallation().ioweld +", True)");
			writer.appendLine("sleep(1)");
		}
	}

	public KeyboardNumberInput getKeyboardForInput() {
		KeyboardNumberInput keyboard = keyboardInputFactory.createDoubleKeypadInput();
		return keyboard;
	}

	public KeyboardInputCallback<Double> getKeyBoardCallBack(final JTextField textField, final String key) {
		return new KeyboardInputCallback<Double>() {
			@Override
			public void onOk(Double value) {
				inputText = value;
				undoRedoManager.recordChanges(new UndoableChanges() {
					@Override
					public void executeChanges() {
						model.set(key, inputText);
					}
				});
				view.setTextField(textField, value.toString());
			}
		};
	}

	public KeyboardNumberInput getKeyboardInt(){
		KeyboardNumberInput keyboardNumberInput = keyboardInputFactory.createIntegerKeypadInput();
		return keyboardNumberInput;
	}

	public KeyboardInputCallback<Integer> getKeyboardInputCallbackInt(final JTextField textField, final String key){
		return new KeyboardInputCallback<Integer>() {
			@Override
			public void onOk(final Integer value) {
				undoRedoManager.recordChanges(new UndoableChanges() {
					@Override
					public void executeChanges() {
						model.set(key, value);
					}
				});
				view.setTextField(textField, value.toString());
			}
		};
	}

	private void update(){
		view.setTextField(view.jobNumtf, model.get(JOB_INPUT_KEY, DEFAULT_VALUE));
		view.setTextField(view.wireFeedtf, model.get(WIRE_INPUT_KEY, DEFAULT_VALUE));
		view.setTextField(view.arcLentf, model.get(ARC_INPUT_KEY, DEFAULT_VALUE));
		view.setTextField(view.pulseDynamictf, model.get(PULSE_INPUT_KEY, DEFAULT_VALUE));
		view.setTextField(view.wireRetracttf, model.get(RETRACT_INPUT_KEY, DEFAULT_VALUE));
		if(getInstallation().setModel().equals("TPS 320i")){
			if(getInstallation().getMode().equals("Job mode")){
				view.jobNumtf.setEnabled(true);
				view.wireFeedtf.setEnabled(false);
				view.arcLentf.setEnabled(false);
				view.pulseDynamictf.setEnabled(false);
				view.wireRetracttf.setEnabled(false);
			}else{
				view.jobNumtf.setEnabled(false);
				view.wireFeedtf.setEnabled(true);
				view.arcLentf.setEnabled(true);
				view.pulseDynamictf.setEnabled(true);
				view.wireRetracttf.setEnabled(true);
			}
		}else if(getInstallation().setModel().equals("TPS MagicWave 2200")){
			if(getInstallation().getMode().equals("Job mode")){
				view.jobNumtf.setEnabled(true);
				view.wireFeedtf.setEnabled(false);
				view.arcLentf.setEnabled(false);
				view.pulseDynamictf.setEnabled(false);
				view.wireRetracttf.setEnabled(false);
			}else{
				view.jobNumtf.setEnabled(false);
				view.wireFeedtf.setEnabled(false);
				view.arcLentf.setEnabled(false);
				view.pulseDynamictf.setEnabled(false);
				view.wireRetracttf.setEnabled(false);
			}
		}else{
			view.jobNumtf.setEnabled(false);
			view.wireFeedtf.setEnabled(false);
			view.arcLentf.setEnabled(false);
			view.pulseDynamictf.setEnabled(false);
			view.wireRetracttf.setEnabled(false);
		}
	}

	public Integer[] getintbin(String key) {
		int dec = Integer.parseInt(model.get(key, DEFAULT_VALUE));
		int i = 0;
		Integer[] binum = new Integer[16];
		for(int j= 0; j<16; j++)
			binum[j] = 0;
		int bina = Integer.valueOf(dec);
		while(bina > 0) {
			binum[i] = bina % 2;
			bina = bina / 2;
			i++;
		}
		return binum;
	}

	public Integer[] getBinDouble(String key){
		Double decD = Double.parseDouble(model.get(key, DEFAULT_VALUE)) * 100;
		int dec = Integer.parseInt(String.valueOf(decD));
		int i = 0;
		Integer[] binum = new Integer[16];
		for(int j= 0; j<16; j++)
			binum[j] = 0;
		int bina = Integer.valueOf(dec);
		while(bina > 0) {
			binum[i] = bina % 2;
			bina = bina / 2;
			i++;
		}
		return binum;
	}

	public void updateUI(){
		int voltageInt = getInstallation().iomod.getVoltage();
		double voltage = voltageInt;
		int currentInt = getInstallation().iomod.getCurrent();
		double current = currentInt;
		int speedInt = getInstallation().iomod.getSpeed();
		double speed = speedInt;
 		view.setIOvalues(String.valueOf(voltage/100), String.valueOf(current/10), String.valueOf(speed/100));
	}

}
>>>>>>> 52f11ef4de6b4620465be119ee7b0186426ab9cf
