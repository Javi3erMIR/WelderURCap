package styleClasses;

import javax.swing.*;
import java.awt.*;

public abstract class Style {

    private static final int SMALL_HEADER_FONT_SIZE = 16;

    protected abstract int getHorizontalSpacing();

    protected abstract int getVerticalSpacing();

    protected abstract int getHorizontalIndent();

    public Box createInfo(String text) {
        Box infoBox = Box.createHorizontalBox();
        infoBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoBox.add(new JLabel(text));
        return infoBox;
    }

    public Component createHorizontalSpacing() {
        return Box.createRigidArea(new Dimension(getHorizontalSpacing(), 0));
    }

    public Component createHorizontalIndent() {
        return Box.createRigidArea(new Dimension(getHorizontalIndent(), 0));
    }

    public Component createVerticalSpacing() {
        return Box.createRigidArea(new Dimension(0, getVerticalSpacing()));
    }

    public JButton createButton(String text) {
        return new JButton(text);
    }

    public JTextField createInput(){
        JTextField jTextField = new JTextField();
        jTextField.setPreferredSize(new Dimension(150, 25));
        jTextField.setMaximumSize(jTextField.getPreferredSize());
        jTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        return jTextField;
    }

    public Box createSection(int axis) {
        Box panel = new Box(axis);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        return panel;
    }

    public int getSmallHeaderFontSize() {
        return SMALL_HEADER_FONT_SIZE;
    }

}
