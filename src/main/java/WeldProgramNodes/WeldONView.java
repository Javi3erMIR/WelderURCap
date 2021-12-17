package WeldProgramNodes;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeView;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;
import styleClasses.Style;

public class WeldONView implements SwingProgramNodeView<WeldONContribution>{

	private Style style;
	private ViewAPIProvider apiProvier;
	public JTextField wireFeedtf;
	public JTextField jobNumtf;
	public JTextField arcLentf;
	public JTextField pulseDynamictf;
	public JTextField wireRetracttf;
	private JLabel voltagelbl;
	private JLabel currentlbl;
	private JLabel wireFeedlbl;

	public WeldONView(Style style) {
		this.style = style;
	}

	@Override
	public void buildUI(JPanel panel, ContributionProvider<WeldONContribution> provider) {
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(logo(provider));
	}
	
	private Box logo(final ContributionProvider<WeldONContribution> provider) {
		Box box = style.createSection(BoxLayout.PAGE_AXIS);
		JLabel imge = new JLabel();
		imge.setIcon(new ImageIcon(getClass().getResource("/impl/logosgrafico3.png")));
		Box infoSectcion = style.createSection(BoxLayout.LINE_AXIS);
		infoSectcion.add(style.createInfo("Set and edit welding parameters"));
		infoSectcion.add(createSpacer(60, 0));
		infoSectcion.add(imge);
		box.add(infoSectcion);
		box.add(style.createVerticalSpacing());
		Box parametersSection = style.createSection(BoxLayout.LINE_AXIS);
		parametersSection.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height));
		parametersSection.setMaximumSize(parametersSection.getPreferredSize());
		parametersSection.setBorder(BorderFactory.createMatteBorder(2,0,0,0, Color.BLACK));
		parametersSection.add(inputsSection(provider));
		parametersSection.add(style.createHorizontalSpacing());
		parametersSection.add(viewIOSection(provider));
		box.add(parametersSection);
		return box;
	}

	private Box iconsSection(){
		JLabel imge2 = new JLabel();
		JLabel imge3 = new JLabel();
		Box icons_div = style.createSection(BoxLayout.LINE_AXIS);
		imge2.setIcon(new ImageIcon(getClass().getResource("/impl/welding.png")));
		imge3.setIcon(new ImageIcon(getClass().getResource("/impl/comprobar.png")));
		icons_div.add(imge2);
		icons_div.add(imge3);
		return icons_div;
	}

	private Box inputsSection(final ContributionProvider<WeldONContribution> provider){
		Box section = style.createSection(BoxLayout.PAGE_AXIS);
		//section.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width/2, Toolkit.getDefaultToolkit().getScreenSize().height));
		section.setBorder(BorderFactory.createMatteBorder(0,0,0,2, Color.BLACK));
		section.add(style.createVerticalSpacing());
		section.add(inputJob(provider));
		section.add(style.createVerticalSpacing());
		section.add(inputWireFeed(provider));
		section.add(style.createVerticalSpacing());
		section.add(inputArcLen(provider));
		section.add(style.createVerticalSpacing());
		section.add(inputPulse(provider));
		section.add(style.createVerticalSpacing());
		section.add(inputWireRetrac(provider));
		return section;
	}

	private Box viewIOSection(final ContributionProvider<WeldONContribution> provider){
		Box viewIOStection = style.createSection(BoxLayout.PAGE_AXIS);
		voltagelbl = new JLabel("Voltage");
		voltagelbl.setFont(voltagelbl.getFont().deriveFont(Font.BOLD, style.getSmallHeaderFontSize()));
		viewIOStection.add(style.createVerticalSpacing());
		viewIOStection.add(style.createInfo("Welding voltage"));
		viewIOStection.add(voltagelbl);
		viewIOStection.add(style.createVerticalSpacing());
		viewIOStection.add(style.createInfo("Welding current"));
		currentlbl = new JLabel("Current");
		currentlbl.setFont(currentlbl.getFont().deriveFont(Font.BOLD, style.getSmallHeaderFontSize()));
		viewIOStection.add(currentlbl);
		viewIOStection.add(style.createVerticalSpacing());
		viewIOStection.add(style.createInfo("Wire feed speed"));
		wireFeedlbl = new JLabel("Speed");
		wireFeedlbl.setFont(wireFeedlbl.getFont().deriveFont(Font.BOLD, style.getSmallHeaderFontSize()));
		viewIOStection.add(wireFeedlbl);
		viewIOStection.add(style.createVerticalSpacing());
		viewIOStection.add(iconsSection());
		return viewIOStection;
	}

	private Box inputWireFeed(final ContributionProvider<WeldONContribution> provider){
		Box box = style.createSection(BoxLayout.PAGE_AXIS);
		box.add(style.createInfo("Wire feed speed"));
		Box secion = style.createSection(BoxLayout.LINE_AXIS);
		wireFeedtf = style.createInput();
		wireFeedtf.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(wireFeedtf.isEnabled()) {
					KeyboardNumberInput keyboardNumberInput = provider.get().getKeyboardForInput();
					keyboardNumberInput.show(wireFeedtf, provider.get().getKeyBoardCallBack(wireFeedtf, "wire_input"));
				}
			}
		});
		JLabel label = new JLabel("m/min");
		secion.add(wireFeedtf);
		secion.add(createSpacer(0, 15));
		secion.add(label);
		box.add(secion);
		return box;
	}

	private Box inputJob(final ContributionProvider<WeldONContribution> provider){
		Box box = style.createSection(BoxLayout.PAGE_AXIS);
		jobNumtf = style.createInput();
		jobNumtf.setEnabled(false);
		jobNumtf.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(jobNumtf.isEnabled()){
					KeyboardNumberInput keyboardNumberInput = provider.get().getKeyboardInt();
					keyboardNumberInput.show(jobNumtf, provider.get().getKeyboardInputCallbackInt(jobNumtf, "job_input"));
				}
			}
		});
		box.add(style.createInfo("Job number"));
		box.add(jobNumtf);
		return box;
	}

	private Box inputArcLen(final ContributionProvider<WeldONContribution> provider){
		Box box = style.createSection(BoxLayout.PAGE_AXIS);
		arcLentf = style.createInput();
		arcLentf.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(arcLentf.isEnabled()){
					KeyboardNumberInput keyboardNumberInput = provider.get().getKeyboardForInput();
					keyboardNumberInput.show(arcLentf, provider.get().getKeyBoardCallBack(arcLentf, "arc_input"));
				}
			}
		});
		box.add(style.createInfo("Arclength correction"));
		box.add(arcLentf);
		return box;
	}

	private Box inputPulse(final ContributionProvider<WeldONContribution> provider){
		Box box = style.createSection(BoxLayout.PAGE_AXIS);
		box.add(style.createInfo("Pulse-/dynamic correction"));
		pulseDynamictf = style.createInput();
		pulseDynamictf.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(pulseDynamictf.isEnabled()){
					KeyboardNumberInput keyboardNumberInput = provider.get().getKeyboardForInput();
					keyboardNumberInput.show(pulseDynamictf, provider.get().getKeyBoardCallBack(pulseDynamictf, "pulse_input"));
				}
			}
		});
		box.add(pulseDynamictf);
		return box;
	}

	private Box inputWireRetrac(final ContributionProvider<WeldONContribution> provider){
		Box box = style.createSection(BoxLayout.PAGE_AXIS);
		box.add(style.createInfo("Wire retract correction"));
		wireRetracttf = style.createInput();
		wireRetracttf.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(wireRetracttf.isEnabled()){
					KeyboardNumberInput keyboardNumberInput = provider.get().getKeyboardInt();
					keyboardNumberInput.show(wireRetracttf, provider.get().getKeyboardInputCallbackInt(wireRetracttf, "retract_input"));
				}
			}
		});
		box.add(wireRetracttf);
		return box;
	}

	private Component createSpacer(int ancho, int alto) {
		return Box.createRigidArea(new Dimension(ancho, alto));
	}

	public void setTextField(JTextField textField, String text) {
		textField.setText(text);
	}

	public void setIOvalues(String voltage, String current, String speed){
		this.voltagelbl.setText(voltage + " V");
		this.currentlbl.setText(current + " A");
		this.wireFeedlbl.setText(speed + " m/min");
	}

}
