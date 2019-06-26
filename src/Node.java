import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.*;


public class Node implements Serializable{


    // Private variables

    private String nid; // ID of Node
    //private int logical_time;
    private int logical_time[];
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

    private int expected_child_message;
    private int received_child_message;
    private boolean visited;
    private String parent;
    private HashMap<String, int[]> snapshot;
    private int[] global_snapshot;

    private HashMap<String, String[]> neighbors_information;

    //private Vector<com_msg_packaging> buffer;
    private ArrayList<com_msg_packaging> buffer;

    private com_msg_packaging processing_message;

    // Constructor of Node
    //public Node(String in_nodeID, String[] node_config, String[] sys_setup) {
    public Node(String in_nodeID, String[] node_config, String[] sys_setup, HashMap<String, String[]> in_neighbors_information) {

        this.neighbors_information = new HashMap<>(in_neighbors_information);

        this.nid = in_nodeID; // assign node ID

        this.node_ip = node_config[0];

        try {
            //this.port = new ServerSocket(this.nid + 5000);
            //System.out.println("[*] Server Port: " + (this.nid + 5000) + " created.");
            this.port = new ServerSocket(Integer.parseInt(node_config[1]));
            //this.port = new ServerSocket(Integer.parseInt(node_config[1]), 10);
            System.out.println(
                    "\nNode " + this.nid + " :\n" +
                    "Server Port: " + this.port
                    + " with IP: " + InetAddress.getLocalHost()+ " created.");
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.logical_time = new int[Integer.parseInt(sys_setup[0])];
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

        //this.expected_child_message = -1;

        //this.received_child_message = 0;

        //this.visited = false;

        this.parent = "";

        this.snapshot = new HashMap<>();

        //this.buffer = new Vector<>();
        this.buffer = new ArrayList<>();

        this.global_snapshot = new int[this.node_numbers];

    }


    // Get Functions
    public String getNid() { return nid;}
    //public int getLogical_time() {return logical_time;}
    public int[] getLogical_time() {return logical_time;}
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

    public int getExpected_child_message() {return this.expected_child_message;}

    public int getReceived_child_message() {return this.received_child_message;}

    public boolean getVisited() {return this.visited;}

    public String getParent() {return this.parent;}

    public HashMap<String, int[]> getSnapshot() {return this.snapshot;}

    public int[] getGlobal_snapshot() {return this.global_snapshot;}

    public HashMap<String, String[]> getNeighbors_information() {return this.neighbors_information;}

/*
    public com_msg_packaging getBuffer_popout() {
        com_msg_packaging temp = new com_msg_packaging(this.buffer.firstElement());
        this.buffer.remove(0);
        return temp;
    }

    public Vector<com_msg_packaging> getBuffer() {return this.buffer;}
*/

    public com_msg_packaging getBuffer_popout() {
        com_msg_packaging temp = new com_msg_packaging(this.buffer.get(0));
        this.buffer.remove(0);
        return temp;
    }

    public ArrayList<com_msg_packaging> getBuffer() {return this.buffer;}

    public com_msg_packaging getProcessing_message() {return this.processing_message;}

    // Set Functions
    public void setNode_ip(String dv) {this.node_ip = dv;}
    //public void setLogical_time_unit_increase() {this.logical_time++;}
    //public void setLogical_time_assign_value_pls_one( int a_t) { this.logical_time = a_t + 1; }
    public void setLogical_time_unit_increase() {this.logical_time[Integer.parseInt(this.nid)]++;}

    public void setLogical_time_Fidge_Mattern(int[] incoming_timestamp_v) {
      int[] temp = new int[this.node_numbers];
      setLogical_time_unit_increase();
      for(int i = 0; i < this.node_numbers; i++ )
      {
          if(incoming_timestamp_v[i] > this.logical_time[i])
              temp[i]= incoming_timestamp_v[i];
          else
              temp[i] = this.logical_time[i];
      }

      for(int j = 0; j < this.node_numbers; j++ )
          this.logical_time[j] = temp[j];
    }

    public void setLock(boolean dv) {this.lock = dv;}

    public void setNode_status(status dv) {this.node_status = dv;}

    public void setMessage_sent_unit_increase() {this.message_sent++;}

    public void setMessage_receive_unit_increase() {this.message_receive++;}

    public void setExpected_child_message_unit_decrease() {this.expected_child_message--;}
    public void resetExpected_child_message() {
        this.expected_child_message = this.neighbors_information.size()-1;}

    public void setReceived_child_message_unit_increase() {this.received_child_message++;}
    public void resetReceived_child_message() {this.received_child_message = 0;}

    public void setVisited(boolean dv) {this.visited = dv;}

    public void setParent(String dv) {this.parent = dv;}

    public void setSnapshot(String nid, int[] time_vector) {this.snapshot.put(nid, time_vector);}
    public void MergeSnapshot(HashMap<String, int[]> dv) {this.snapshot.putAll(dv);}
    public void resetSnapshot() {this.snapshot.clear();}

    public void setGlobal_snapshot(int node_number, int n_logical_time) {
        this.global_snapshot[node_number] = n_logical_time;
    }

    public void resetNeighbos_info(HashMap<String, String[]> reset_set)
    {
        this.neighbors_information.clear();
        this.neighbors_information.putAll(reset_set);
    }

    public void setBuffer_pushin(com_msg_packaging dv) {this.buffer.add(dv);}
    public void resetBuffer() {this.buffer.clear();}

    public void setProcessing_message(com_msg_packaging dv) {this.processing_message = new com_msg_packaging(dv);}
}