package WeldProgramNodes;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeView;

public class WeldOFFView implements SwingProgramNodeView<WeldOFFContribution>{
	
	private ViewAPIProvider apiProvider;
	
	public WeldOFFView(ViewAPIProvider apiProvider) {
		this.apiProvider = apiProvider;
	}

	@Override
	public void buildUI(JPanel panel, ContributionProvider<WeldOFFContribution> provider) {
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(logo());
		panel.add(createSpacer(0, 75));
		panel.add(Weldnot());
	}
	
	private Box logo() {
		JLabel imag = new JLabel();
		Box box = Box.createVerticalBox();
		box.setAlignmentX(Component.CENTER_ALIGNMENT);
		imag.setIcon(new ImageIcon(getClass().getResource("/impl/logosgrafico3.png")));
		//box.add(createspacer(logoLocate(280), 0));
		box.add(imag);
		return box;
	}
	
	private Box Weldnot() {
		JLabel image = new JLabel();
		JLabel image2 = new JLabel();
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.CENTER_ALIGNMENT);		
		image.setIcon(new ImageIcon(getClass().getResource("/impl/welding.png")));	
		image2.setIcon(new ImageIcon(getClass().getResource("/impl/cancelar.png")));
		box.add(image);
		box.add(image2);
		return box;
	}
	
	public Component createSpacer(int ancho, int alto) {
		return Box.createRigidArea(new Dimension(ancho, alto));
	}


}
