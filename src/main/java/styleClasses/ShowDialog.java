package styleClasses;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import java.awt.Color;

public class ShowDialog extends JOptionPane{

    private String dialog_type, message;
    public static int btn_option;
   
    public ShowDialog(String dialog_type, String message){
        this.dialog_type = dialog_type;
        this.message = message;
        //super.setBorder(BorderFactory.createMatteBorder(2,2,2,2, Color.BLACK));
       
    }

    public void showCustomDialog(){
        String[] op_btns = {"OK", "Cancel"};
        String[] op_btns_1 = {"OK"};
        if(dialog_type == "Option"){
            UIManager.put("RootPane.frameBorder", new LineBorder(Color.black));
            UIManager.put("RootPane.dialogBorder", new LineBorder(Color.black));    
            UIManager.put("RootPane.errorDialogBorder", new LineBorder(Color.black));
            btn_option = super.showOptionDialog(null, 
                                                  message, 
                                                  "Warning!", 
                                                  0, 
                                                  JOptionPane.INFORMATION_MESSAGE, 
                                                  null, 
                                                  op_btns, 
                                                  "");
        }
        if(dialog_type == "OptionWithoutCancel"){
            UIManager.put("RootPane.frameBorder", new LineBorder(Color.black));
            UIManager.put("RootPane.dialogBorder", new LineBorder(Color.black));    
            UIManager.put("RootPane.errorDialogBorder", new LineBorder(Color.black));
            btn_option = super.showOptionDialog(null, 
                                                  message, 
                                                  "Warning!", 
                                                  0, 
                                                  JOptionPane.INFORMATION_MESSAGE, 
                                                  null, 
                                                  op_btns_1, 
                                                  "");
        }
        if(dialog_type == "Message"){
            UIManager.put("RootPane.frameBorder", new LineBorder(Color.black));
            UIManager.put("RootPane.dialogBorder", new LineBorder(Color.black));    
            UIManager.put("RootPane.errorDialogBorder", new LineBorder(Color.black));
            super.showMessageDialog(null, message, "Warning!", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
}
