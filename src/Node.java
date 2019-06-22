import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.*;


public class Node implements Serializable{


    // Private variables
    private String nid; // ID of Node
    private int logical_time;
    private String node_ip;
    private ServerSocket port;
    private boolean lock;
    //private Vector<request_message> buffer = new Vector<>();

    private InetSocketAddress Ip_Port;

    public enum status{
        active,
        passive
    }

    private status node_status;

    private int node_numbers;
    private int minPerActive;
    private int maxPerActive;
    private int minSendDelay;
    private int snapshotDelay;
    private int maxNumber;

    private int message_sent;

    private int message_receive;

    // Constructor of Node
    public Node(String in_nodeID, String[] node_config, String[] sys_setup) {

        this.nid = in_nodeID; // assign node ID

        this.node_ip = node_config[0];

        try {
            //this.port = new ServerSocket(this.nid + 5000);
            //System.out.println("[*] Server Port: " + (this.nid + 5000) + " created.");
            this.port = new ServerSocket(Integer.parseInt(node_config[1]));
            System.out.println(
                    "\nNode " + this.nid + " :\n" +
                    "Server Port: " + this.port
                    + " with IP: " + InetAddress.getLocalHost()+ " created.");
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.logical_time = 0;
        this.lock = false;

        this.node_numbers = Integer.parseInt(sys_setup[0]);
        this.minPerActive = Integer.parseInt(sys_setup[1]);
        this.maxPerActive = Integer.parseInt(sys_setup[2]);
        this.minSendDelay = Integer.parseInt(sys_setup[3]);
        this.snapshotDelay =Integer.parseInt(sys_setup[4]);
        this.maxNumber = Integer.parseInt(sys_setup[5]);

        this.node_status = status.passive;

        this.message_sent = 0;

        this.message_receive = 0;

    }


    // Get Functions
    public String getNid() { return nid;}
    public int getLogical_time() {return logical_time;}
    public ServerSocket getPort() { return port; }
    public boolean getLock() {return this.lock;}

    public int getNode_numbers() {return this.node_numbers;}
    public int getMinPerActive() {return this.minPerActive;}
    public int getMaxPerActive() {return this.maxPerActive;}
    public int getMinSendDelay() {return this.minSendDelay;}
    public int getSnapshotDelay() {return this.snapshotDelay;}
    public int getMaxNumber() {return this.maxNumber;}

    public status getNode_status() {return this.node_status;}

    public int getMessage_sent() {return this.message_sent;}

    public int getMessage_receive() {return this.message_receive;}

    public String getNode_ip() {return this.node_ip;}

    // Set Functions
    public void setNode_ip(String dv) {this.node_ip = dv;}
    public void setLogical_time_unit_increase() {this.logical_time++;}
    public void setLogical_time_assign_value_pls_one( int a_t) { this.logical_time = a_t + 1; }
    public void setLock(boolean dv) {this.lock = dv;}

    public void setNode_status(status dv) {this.node_status = dv;}

    public void setMessage_sent_unit_increase() {this.message_sent++;}

    public void setMessage_receive_unit_increase() {this.message_receive++;}
}