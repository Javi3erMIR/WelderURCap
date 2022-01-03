package FroniusInstallationNode;

public class CheckKey {     
    private String key;
    private String serial;
    
    public CheckKey(String key){
        this.key = key;
    }

    private int checkSerialSum(){
        int sum = 0;
        char[] ary = serial.toCharArray();
        for(int i = 0; i < serial.length(); i++){
            sum += (int) ary[i];
        }
        return sum;
    }

    public boolean checKeyBySerial(String serial){
        this.serial = serial;
        checkSerialSum();
        int sum = 0;
        int sum_serial = checkSerialSum();
        char[] ary = key.toCharArray();
        for(int i = 0; i < key.length(); i++){
            sum += (int) ary[i];
        }
        if(sum == sum_serial){
            return true;
        }
        else{
            return false;
        }
    }
}