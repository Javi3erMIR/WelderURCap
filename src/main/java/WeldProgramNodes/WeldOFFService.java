package WeldProgramNodes;

import java.util.Locale;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.ContributionConfiguration;
import com.ur.urcap.api.contribution.program.CreationContext;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;
import com.ur.urcap.api.domain.data.DataModel;

public class WeldOFFService implements SwingProgramNodeService<WeldOFFContribution, WeldOFFView>{

	@Override
	public String getId() {
		return "ArcOFF";
	}

	@Override
	public void configureContribution(ContributionConfiguration configuration) {
		configuration.setChildrenAllowed(false);
	}

	@Override
	public String getTitle(Locale locale) {
		return "Arc OFF";
	}

	@Override
	public WeldOFFView createView(ViewAPIProvider apiProvider) {
		return new WeldOFFView(apiProvider);
	}

	@Override
	public WeldOFFContribution createNode(ProgramAPIProvider apiProvider, WeldOFFView view, DataModel model,
			CreationContext context) {
		return new WeldOFFContribution(apiProvider, view, model);
	}

}
