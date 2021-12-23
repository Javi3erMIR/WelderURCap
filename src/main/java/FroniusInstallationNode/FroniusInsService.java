package FroniusInstallationNode;

import java.util.Locale;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.ContributionConfiguration;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.domain.SystemAPI;
import com.ur.urcap.api.domain.data.DataModel;

import styleClasses.Style;
import styleClasses.V3Style;
import styleClasses.V5Style;

public class FroniusInsService implements SwingInstallationNodeService<Contribution, FroniusSetupView>{

    @Override
    public void configureContribution(ContributionConfiguration configuration) {
    }

    @Override
    public String getTitle(Locale locale) {
        return "Fronius Setup";
    }

    @Override
    public FroniusSetupView createView(ViewAPIProvider apiProvider) {
        SystemAPI systemAPI = apiProvider.getSystemAPI();
        Style style = systemAPI.getSoftwareVersion().getMajorVersion() >= 5 ? new V5Style() : new V3Style();
        return new FroniusSetupView(style);
    }

    @Override
    public Contribution createInstallationNode(InstallationAPIProvider apiProvider, FroniusSetupView view,
            DataModel model, CreationContext context) {
        return new Contribution(apiProvider, view, model, context);
    }

   
  
}
