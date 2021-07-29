package TrackingNodes;

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

import java.util.Locale;

public class TrackingService implements SwingProgramNodeService<TrackingContribution, TrackingView> {

    @Override
    public String getId() {
        return "TrackingMode";
    }

    @Override
    public void configureContribution(ContributionConfiguration configuration) {
        configuration.setDeprecated(false);
        configuration.setChildrenAllowed(true);
        configuration.setUserInsertable(true);
    }

    @Override
    public String getTitle(Locale locale) {
        return "Tracking Weld Mode";
    }

    @Override
    public TrackingView createView(ViewAPIProvider apiProvider) {
        SystemAPI systemAPI = apiProvider.getSystemAPI();
        Style style = systemAPI.getSoftwareVersion().getMajorVersion() >= 5 ? new V5Style() : new V3Style();
        return new TrackingView(style);
    }

    @Override
    public TrackingContribution createNode(ProgramAPIProvider apiProvider, TrackingView view, DataModel model, CreationContext context) {
        return new TrackingContribution(apiProvider, view, model, context);
    }
}
