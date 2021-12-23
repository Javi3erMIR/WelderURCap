package FroniusInstallationNode;

import java.io.InputStream;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.InstallationNodeService;
import com.ur.urcap.api.domain.URCapAPI;
import com.ur.urcap.api.domain.data.DataModel;

public class FroniusSetupService implements InstallationNodeService {

	public FroniusSetupService(){}

	@Override
	public String getTitle() {
		return "Fronius Setup";
	}

	@Override
	public InstallationNodeContribution createInstallationNode(URCapAPI api, DataModel model) {
		return new FroniusSetupContribution(api, model);
	}

	@Override
	public InputStream getHTML() {
		return this.getClass().getResourceAsStream("/impl/View.html");
	}

}
