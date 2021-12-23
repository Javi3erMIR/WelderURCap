<<<<<<< HEAD
package WeldProgramNodes;

import FroniusInstallationNode.Contribution;
import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.ProgramAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;

public class WeldOFFContribution implements ProgramNodeContribution{
	
	private ProgramAPI apiProvider;
	private WeldOFFView view;
	private DataModel model;
	
	public WeldOFFContribution(ProgramAPIProvider apiProvider, WeldOFFView view, DataModel model) {
		this.apiProvider = apiProvider.getProgramAPI();
		this.view = view;
		this.model = model;
	}

	@Override
	public void openView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTitle() {
		return "Arc OFF";
	}

	@Override
	public boolean isDefined() {
		// TODO Auto-generated method stub
		return true;
	}
	
	private Contribution getInstallation() {
		return apiProvider.getInstallationNode(Contribution.class);
	}

	@Override
	public void generateScript(ScriptWriter writer) {
		if(getInstallation().setModel().equals("TPS 320i")) {		
			writer.appendLine("modbus_set_output_signal(\"robotON\", False, False)");
			writer.appendLine("sleep(2.5)");
		}
		if(getInstallation().setModel().equals("TPS MagicWave 2200")) {
			writer.appendLine("set_digital_out(" + getInstallation().ioweld +", False)");
			writer.appendLine("sleep(1)");
		}
	}

}
=======
package WeldProgramNodes;

import FroniusInstallationNode.FroniusSetupContribution;
import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.ProgramAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;

public class WeldOFFContribution implements ProgramNodeContribution{
	
	private ProgramAPI apiProvider;
	private WeldOFFView view;
	private DataModel model;
	
	public WeldOFFContribution(ProgramAPIProvider apiProvider, WeldOFFView view, DataModel model) {
		this.apiProvider = apiProvider.getProgramAPI();
		this.view = view;
		this.model = model;
	}

	@Override
	public void openView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTitle() {
		return "Arc OFF";
	}

	@Override
	public boolean isDefined() {
		// TODO Auto-generated method stub
		return true;
	}
	
	private FroniusSetupContribution getInstallation() {
		return apiProvider.getInstallationNode(FroniusSetupContribution.class);
	}

	@Override
	public void generateScript(ScriptWriter writer) {
		if(getInstallation().setModel().equals("TPS 320i")) {		
			writer.appendLine("modbus_set_output_signal(\"robotON\", False, False)");
			writer.appendLine("sleep(2.5)");
		}
		if(getInstallation().setModel().equals("TPS MagicWave 2200")) {
			writer.appendLine("set_digital_out(" + getInstallation().ioweld +", False)");
			writer.appendLine("sleep(1)");
		}
	}

}
>>>>>>> 52f11ef4de6b4620465be119ee7b0186426ab9cf
