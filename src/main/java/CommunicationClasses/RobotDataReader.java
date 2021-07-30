package CommunicationClasses;

import jdk.vm.ci.meta.SpeculationLog;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class RobotDataReader {

    private final String Robot_ip;
    private final int port;

    public RobotDataReader(String robot_ip) {
        this.Robot_ip = robot_ip;
        this.port = 30003;
    }

    public RobotDataReader() {
        this.Robot_ip = "127.0.0.1";
        this.port = 30003;
    }

    private double[] RealtimeMessage;

    public void readNow(){
        readSocket();
    }

    private void readSocket() {
        try{

            Socket rt = new Socket(Robot_ip, port);
            if (rt.isConnected()){
                System.out.println("Connected to UR Realtime Client");
            }
            DataInputStream in;
            in = new DataInputStream(rt.getInputStream());
            int length = in.readInt();
            RealtimeMessage = new double[length];
            RealtimeMessage[0] = length;
            int data_available = (length-4)/8;
            int i = 1;
            while (i <= data_available){
                RealtimeMessage[i] = in.readDouble();
                i++;
            }
            in.close();
            rt.close();
            System.out.println("Disconnected from UR Realtime Client");
        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    private enum RTinfo {
        // name			(index in plot, number of doubles)
        digital_in      (86, 1),
        q_target		(2, 6),
        qd_target		(8, 6),
        qdd_target		(14, 6),
        q_actual		(32, 6),
        qd_actual		(38, 6),
        TCP_actual		(56, 6),
        TCPd_actual		(62, 6),
        TCP_force		(68, 6),
        TCP_target		(74, 6),
        TCPd_target		(80, 6),
        temp_joint		(87, 6),
        robotmode		(95, 1),
        jointmode		(96, 6),
        safetymode		(97, 1),
        tcp_accel		(109, 3),
        speedscaling	(118, 1),
        prgstate		(132, 1);
        private final int index;
        private final int count;
        RTinfo(int index, int count){
            this.index = index;
            this.count = count;
        }
        private int index() {return index;}
        private int count() {return count;}
    }

    public double[] getActualTcpPose(){
        double[] val = new double[RTinfo.TCP_actual.count()];
        int i = 0;
        while (i < RTinfo.TCP_actual.count()){
            val[i] = RealtimeMessage[RTinfo.TCP_actual.index()+i];
            ++i;
        }
        return val;
    }

    private Integer[] getintbin(int dec) {
        int i = 0;
        Integer[] binum = new Integer[8];
        for(int j= 0; j<8; j++)
            binum[j] = 0;
        int bina = dec;
        while(bina > 0) {
            binum[i] = bina % 2;
            bina = bina / 2;
            i++;
        }
        return binum;
    }

    public Integer[] getDigitalIn(){
       double val = RealtimeMessage[RTinfo.digital_in.index];
       return getintbin(Integer.valueOf((int) val));
    }

    public double[] getActualJointPose(){
        double[] val = new double[RTinfo.q_actual.count()];
        int i = 0;
        while (i < RTinfo.q_actual.count()){
            val[i] = RealtimeMessage[RTinfo.q_actual.index()+i];
            ++i;
        }
        return val;
    }

}
