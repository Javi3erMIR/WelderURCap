package WeldProgramNodes;

import java.util.Locale;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.ContributionConfiguration;
import com.ur.urcap.api.contribution.program.CreationContext;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;
import com.ur.urcap.api.domain.SystemAPI;
import com.ur.urcap.api.domain.data.DataModel;
import styleClasses.Style;
import styleClasses.V3Style;
import styleClasses.V5Style;

public class WeldONService implements SwingProgramNodeService<WeldONContribution, WeldONView>{

	@Override
	public String getId() {
		return "ArcON";
	}

	@Override
	public void configureContribution(ContributionConfiguration configuration) {
		configuration.setChildrenAllowed(false);
	}

	@Override
	public String getTitle(Locale locale) {
		return "Arc ON";
	}

	@Override
	public WeldONView createView(ViewAPIProvider apiProvider) {
		SystemAPI systemAPI = apiProvider.getSystemAPI();
		Style style = systemAPI.getSoftwareVersion().getMajorVersion() >= 5 ? new V5Style() : new V3Style();
		return new WeldONView(style);
	}

	@Override
	public WeldONContribution createNode(ProgramAPIProvider apiProvider, WeldONView view, DataModel model,
			CreationContext context) {
		return new WeldONContribution(apiProvider, view, model, context);
	}

}
