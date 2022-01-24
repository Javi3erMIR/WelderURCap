package FroniusInstallationNode;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeView;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;

import styleClasses.ShowDialog;
import styleClasses.Style;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FroniusSetupView implements SwingInstallationNodeView<Contribution>{

    private Style style;
    private JComboBox model_dropmenu, 
                      mode_drop_menu, 
                      weld_in_drop_menu, 
                      rob_ready_drop_menu, 
                      source_error_drop_menu,
                      mode_drop_menutps;
    private JButton connect_btn, disconnect_btn;
    public JPanel panel_main = new JPanel();
    private JTextField ip_input;
    public JLabel indicator_led_label,
                  connection_indicator,
                  mode_indicator;
    private JCheckBox robot_ready, error_reset;

    public FroniusSetupView(Style style){
        this.style = style;
        robot_ready = new JCheckBox();
        error_reset = new JCheckBox();       
    }  

    public Box licenseCheck(final Contribution contribution){
        Box box = style.createSection(BoxLayout.LINE_AXIS);
        final JTextField key = style.createInput();
        JLabel label = new JLabel("Product key for " + Contribution.serial_num);
        key.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                key.setText("");
                KeyboardTextInput keyboardTextInput = contribution.getKeyboardTextInputKey();
                keyboardTextInput.show(key, contribution.gKeyboardInputCallbackKey(key, Contribution.PRODUCT_KEY_KEY));
            }
        });
        box.add(label);
        box.add(key);
        return box;
    }
    
    public void isConnected(){
        model_dropmenu.setEnabled(false);
        ip_input.setEnabled(false);
        ip_input.setEditable(false);
        connect_btn.setEnabled(false);
        disconnect_btn.setEnabled(true);
        mode_drop_menu.setEnabled(true);
    }

    public void isDisconnected(){
        model_dropmenu.setEnabled(true);
        ip_input.setEnabled(true);
        ip_input.setEditable(true);
        connect_btn.setEnabled(true);
        disconnect_btn.setEnabled(false);
        mode_drop_menu.setEnabled(false);
    }

    private Box controlSectionModel1(final Contribution contribution){
        Box control_section = style.createSection(BoxLayout.LINE_AXIS);
        ip_input = style.createInput("Ip address");
        ip_input.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                ip_input.setText("");
                KeyboardTextInput keyboardTextInput = contribution.getKeyboardTextInput();
                keyboardTextInput.show(ip_input, contribution.gKeyboardInputCallback(ip_input, contribution.INPUT_KEY));
            }
        });
        connect_btn = style.createButton("Connect", 110, 25);
        connect_btn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {                
                isConnected();                
                contribution.connectFunction();
            }
        });
        disconnect_btn = style.createButton("Disconnect", 125, 25);
        disconnect_btn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                contribution.disconnectFunction();
                isDisconnected();
            }
        });        
        String[] items = {"Internal parameters", "Job mode", "Teach mode"};
        mode_drop_menu = style.createComboBoxComponent(150, 25, items);
        mode_drop_menu.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if(arg0.getStateChange() == ItemEvent.SELECTED){
                    if(mode_drop_menu.getSelectedItem().equals("Internal parameters")){
                        contribution.setMode(mode_drop_menu.getSelectedItem().toString());
                    }    
                    if(mode_drop_menu.getSelectedItem().equals("Job mode")){
                        contribution.setMode(mode_drop_menu.getSelectedItem().toString());
                    }                    
                    else if(mode_drop_menu.getSelectedItem().equals("Teach mode")){
                        contribution.setMode(mode_drop_menu.getSelectedItem().toString());
                    }
                }
            }
        });
        isDisconnected();
        control_section.add(ip_input);
        control_section.add(style.spaceComponent(10, 0));
        control_section.add(connect_btn);
        control_section.add(style.spaceComponent(10, 0));
        control_section.add(disconnect_btn);
        control_section.add(style.spaceComponent(10, 0));
        control_section.add(mode_drop_menu);
        return control_section;
    }   

    private Box statusLabels(final Contribution contribution){
        Box page_section = style.createSection(BoxLayout.PAGE_AXIS);
        JLabel[] main_labels = {new JLabel("Powersource:"), new JLabel("Status:"), new JLabel("Welding mode:")}; 
        JLabel[] dynamic_labels = {indicator_led_label = new JLabel(), 
                                   connection_indicator = new JLabel("Disconnected"), 
                                   mode_indicator = new JLabel("Internal Parameters")};              
        indicator_led_label.setIcon(new ImageIcon(getClass().getResource("/impl/circulo.png")));
        indicator_led_label.setVisible(false);
        for(int i = 0; i < 3; i++){
            page_section.add(style.spaceComponent(0, 20));
            page_section.add(boxLineLabelSection(contribution, main_labels[i], dynamic_labels[i]));    
        }       
        return page_section;
    }

    private Box imageSection(String path){
        Box section = style.createSection(BoxLayout.PAGE_AXIS);
        JLabel img = new JLabel();
        img.setIcon(new ImageIcon(getClass().getResource(path)));
        section.add(img);
        return section;
    }

    private Box statusSection(final Contribution contribution){
        Box line_section = style.createSection(BoxLayout.LINE_AXIS);
        line_section.add(statusLabels(contribution));
        line_section.add(style.spaceComponent(115, 0));
        line_section.add(imageSection("/impl/TPS320i.png"));
        return line_section;
    } 

    private Box boxLineComboSection(final Contribution contribution, JLabel label, JComboBox comboBox){
        Box line_section = style.createSection(BoxLayout.LINE_AXIS);
        line_section.add(label);
        line_section.add(comboBox);
        return line_section;
    }

    private Box boxLineLabelSection(final Contribution contribution, JLabel main_label, JLabel dynamic_label){
        Box line_section = style.createSection(BoxLayout.LINE_AXIS);
        line_section.add(main_label);
        line_section.add(style.spaceComponent(10, 0));
        line_section.add(dynamic_label);
        return line_section;
    }

    private Box boxLineControlSection(final Contribution contribution, final JCheckBox ready_check, final JCheckBox error){
        Box line_section = style.createSection(BoxLayout.LINE_AXIS);
        JLabel[] labels = {new JLabel("Robot ready"), new JLabel("Reset")};
        ready_check.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(ready_check.isSelected()){
                    contribution.setReady();
                } 
                if(!ready_check.isSelected()){
                    contribution.setNotReady();
                }               
            }
        });
        error.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(error.isSelected()){
                    contribution.errorReset("True");
                }
                if(!error.isSelected()){
                    contribution.errorReset("False");
                }
            }
        });
        line_section.add(labels[0]);
        line_section.add(ready_check);
        line_section.add(style.spaceComponent(10, 0));
        line_section.add(labels[1]);
        line_section.add(error);
        return line_section;
    }

    private Box controlSectionModel2(final Contribution contribution){
        Box control_section = style.createSection(BoxLayout.PAGE_AXIS);
        JLabel[] label = {new JLabel("Arc signal:"), new JLabel("Robot ready:"), new JLabel("Source error reset:"), new JLabel("")};
        String[] items = new String[8];
        for(int i = 0; i < 8; i++){
            items[i] = "digital_in["+i+"]";
        }
        weld_in_drop_menu = style.createComboBoxComponent(160, 25, items);
        weld_in_drop_menu.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                int index = weld_in_drop_menu.getSelectedIndex();
                if(arg0.getStateChange() == ItemEvent.SELECTED){
                    contribution.ioweld = index;
                }
            }
        });
        rob_ready_drop_menu = style.createComboBoxComponent(160, 25, items);
        rob_ready_drop_menu.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if(arg0.getStateChange() == ItemEvent.SELECTED){
                    contribution.setInWeld(weld_in_drop_menu.getSelectedIndex());
                }
            }
        });
        source_error_drop_menu = style.createComboBoxComponent(160, 25, items);
        source_error_drop_menu.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if(arg0.getStateChange() == ItemEvent.SELECTED){
                    contribution.setResetIn(source_error_drop_menu.getSelectedIndex());
                }
            }
        });
        String[] mode_items = {"Select welding mode...", "Internal parameters", "Job mode"};
        mode_drop_menutps = style.createComboBoxComponent(200, 25, mode_items);
        mode_drop_menutps.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if(arg0.getStateChange() == ItemEvent.SELECTED){
                    if(mode_drop_menutps.getSelectedItem().equals("Select welding mode...")){
                        contribution.setModeTig(mode_drop_menutps.getSelectedItem().toString());
                    }
                    if(mode_drop_menutps.getSelectedItem().equals("Internal parameters")){
                        contribution.setModeTig(mode_drop_menutps.getSelectedItem().toString());
                    }
                    if(mode_drop_menutps.getSelectedItem().equals("Job mode")){
                        contribution.setModeTig(mode_drop_menutps.getSelectedItem().toString());
                    }
                }            
            }
        });
        control_section.add(boxLineComboSection(contribution, label[0], weld_in_drop_menu));
        control_section.add(style.spaceComponent(0, 10));
        control_section.add(boxLineComboSection(contribution, label[1], rob_ready_drop_menu));
        control_section.add(style.spaceComponent(0, 10));
        control_section.add(boxLineComboSection(contribution, label[2], source_error_drop_menu));
        control_section.add(style.spaceComponent(0, 10));        
        control_section.add(boxLineControlSection(contribution, robot_ready, error_reset));
        control_section.add(style.spaceComponent(0, 10));
        control_section.add(boxLineComboSection(contribution, label[3], mode_drop_menutps));        
        return control_section;
    }

    private Box layoutSection1(final Contribution contribution){
        Box page_section = style.createSection(BoxLayout.PAGE_AXIS);
        page_section.add(controlSectionModel1(contribution));
        page_section.add(style.spaceComponent(0, 10));
        page_section.add(statusSection(contribution));
        return page_section; 
    }

    private Box layoutSection2(final Contribution contribution){
        Box line_section = style.createSection(BoxLayout.LINE_AXIS);        
        line_section.add(controlSectionModel2(contribution));
        line_section.add(style.spaceComponent(80, 0));
        line_section.add(imageSection("/impl/M-71128.png"));
        return line_section;
    }

    private Box headerSection(final Contribution contribution){
        panel_main.setLayout(new BoxLayout(panel_main, BoxLayout.PAGE_AXIS));
        panel_main.removeAll();
        Box header_section = style.createSection(BoxLayout.LINE_AXIS);
        JLabel logo = new JLabel();
		logo.setIcon(new ImageIcon(getClass().getResource("/impl/logosgrafico3.png")));
        String[] items = {"Select machine model...", "TPS 320i", "TPS MagicWave"};
        model_dropmenu = style.createComboBoxComponent(175, 25, items);
        model_dropmenu.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String[] btns = {"Yes", "Cancel"};
		        int op = JOptionPane.showOptionDialog(null, 
                                                      "Do you want to change of machine?\n This will be disconnect or disable the other machine.", 
                                                      "Warning!", 
                                                      0, 
                                                      JOptionPane.INFORMATION_MESSAGE, 
                                                      null, 
                                                      btns, 
                                                      "");
                if(op == 0){
                    if(model_dropmenu.getSelectedItem().equals("Select machine model...")){
                        panel_main.removeAll();
                    }
                    if(model_dropmenu.getSelectedItem().equals("TPS 320i")){
                        panel_main.removeAll();
                        panel_main.add(layoutSection1(contribution));
                    }
                    if(model_dropmenu.getSelectedItem().equals("TPS MagicWave")){
                        panel_main.removeAll();
                        panel_main.add(layoutSection2(contribution));
                    }
                }
                if(op == 1){
                    model_dropmenu.setSelectedItem(contribution.setModel());
                }
            }
        });          
        header_section.add(model_dropmenu);
        header_section.add(Box.createRigidArea(style.componentSize(110, 0)));
        header_section.add(logo);
        header_section.setBorder(BorderFactory.createMatteBorder(0,0,2,0, Color.BLACK));
        return header_section;
    }

    public void setIpText(String text){
        ip_input.setText(text);
    }

    @Override
    public void buildUI(JPanel panel, Contribution contribution) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(headerSection(contribution));
        panel.add(style.spaceComponent(0, 10));
        panel.add(panel_main);
    }
    
    public String getTextFromInput(){
        return ip_input.getText();
    }

    public JComboBox getModelMenu(){
        return model_dropmenu;
    }

    public JComboBox getModeTPSi(){
        return mode_drop_menu;
    }

    public void setComboModelEnabled(boolean state){
        model_dropmenu.setEnabled(state);
    }
}