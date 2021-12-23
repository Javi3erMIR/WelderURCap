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

    public Dimension componentSize(int width, int heigth){
        return new Dimension(width, heigth);
    }

    public Component spaceComponent(int width,int heigth){
        return Box.createRigidArea(componentSize(width, heigth));
    }

    public JButton createButton(String text, int width, int heigth){
        JButton btn = new JButton(text);
        btn.setPreferredSize(componentSize(width, heigth));
        btn.setMaximumSize(btn.getPreferredSize());
        return btn;
    }
    public JButton createButton(String text){
        return new JButton(text);
    }

    public JComboBox createComboBoxComponent(int width, int heigth, Object[] items){
        JComboBox combo = new JComboBox();
        combo.setPreferredSize(componentSize(width, heigth));
        combo.setMaximumSize(combo.getPreferredSize());
        for(int i = 0; i < items.length; i++){
            combo.addItem(items[i]);
        }
        return combo;
    }

    public JTextField createInput(){
        JTextField jTextField = new JTextField();
        jTextField.setPreferredSize(new Dimension(150, 25));
        jTextField.setMaximumSize(jTextField.getPreferredSize());
        jTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        return jTextField;
    }

    public JTextField createInput(String text){
        JTextField jTextField = new JTextField();
        jTextField.setPreferredSize(new Dimension(150, 25));
        jTextField.setMaximumSize(jTextField.getPreferredSize());
        jTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        jTextField.setText(text);
        return jTextField;
    }

    public Box createSection(int axis) {
        Box panel = new Box(axis);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        return panel;
    }

    public Box createSection2(int axis) {
        Box panel = new Box(axis);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    public int getSmallHeaderFontSize() {
        return SMALL_HEADER_FONT_SIZE;
    }

}
