package TrackingNodes;

import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeView;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;
import styleClasses.Style;
import styleClasses.TemplateType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TrackingView implements SwingProgramNodeView<TrackingContribution> {

    private Style style;
    private ViewAPIProvider apiProvider;
    private JLabel jLabel = new JLabel();
    private JButton ArcOn;
    private JButton ArcOff;
    private JButton reset;
    private JTextField textField = new JTextField();

    public TrackingView(Style style) {
        this.style = style;
    }

    @Override
    public void buildUI(JPanel panel, ContributionProvider<TrackingContribution> provider) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createBtnArc(provider));
        panel.add(createResetSection(provider));
    }

    public void setTextField(String text){
        textField.setText(text);
    }

    private Box createBtnArc(final ContributionProvider<TrackingContribution> provider){
        Box box = style.createSection(BoxLayout.PAGE_AXIS);
        Box infoSection = style.createSection(BoxLayout.PAGE_AXIS);
        infoSection.add(style.createInfo("Write the parameters"));
        infoSection.add(style.createVerticalSpacing());
        box.add(infoSection);
        Box btnsection = style.createSection(BoxLayout.LINE_AXIS);
        JLabel jLabel = new JLabel();
        jLabel.setIcon(new ImageIcon(getClass().getResource("/impl/speedometer.png")));
        textField.setPreferredSize(new Dimension(90, 25));
        textField.setMaximumSize(textField.getPreferredSize());
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                KeyboardNumberInput keyboardTextInput = provider.get().getKeyboardForInput();
                keyboardTextInput.show(textField, provider.get().getKeyBoardCallBack());
            }
        });
        JLabel label = new JLabel(" mm/s");
        btnsection.add(jLabel);
        btnsection.add(style.createHorizontalIndent());
        btnsection.add(textField);
        btnsection.add(label);
        box.add(btnsection);
        return box;
    }

    private Component createResetSection(final ContributionProvider<TrackingContribution> provider) {
        Box section = style.createSection(BoxLayout.PAGE_AXIS);

        section.add(style.createVerticalSpacing());

        Box infoSection = style.createSection(BoxLayout.PAGE_AXIS);
        infoSection.add(style.createVerticalSpacing());
        infoSection.add(style.createInfo("Tap the button to reset your selection."));
        infoSection.add(style.createVerticalSpacing());
        infoSection.add(style.createInfo("keep this view open for add nodes on tree."));
        infoSection.add(style.createVerticalSpacing());
        section.add(infoSection);

        Box buttonSection = style.createSection(BoxLayout.LINE_AXIS);
        buttonSection.add(style.createHorizontalIndent());
        this.reset = style.createButton("Reset");
        this.reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                provider.get().reset();
            }
        });
        buttonSection.add(this.reset, FlowLayout.LEFT);
        section.add(buttonSection);

        return section;
    }

    void update(TrackingContribution contribution) {
        TemplateType templateType = contribution.getTemplateType();

        if (templateType == TemplateType.EMPTY) {
            ArcOn.setEnabled(true);
            ArcOff.setEnabled(true);
            reset.setEnabled(false);
        } else {
            reset.setEnabled(true);
        }
    }

}
