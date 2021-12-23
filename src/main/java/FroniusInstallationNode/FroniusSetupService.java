package FroniusInstallationNode;

import java.io.InputStream;
import java.util.Locale;

import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.InstallationNodeService;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.ContributionConfiguration;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
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
