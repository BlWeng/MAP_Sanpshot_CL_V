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
    //private String parent;
    private Vector<String> parent;

    private boolean leaf;

    private HashMap<String, int[]> snapshot;
    private int[] global_snapshot;
    private boolean consistency;

    private Vector<com_msg_packaging> snapshot_buffer;
    //private ArrayList<com_msg_packaging> buffer;

    private Vector<com_msg_packaging> MAP_buffer;

    private com_msg_packaging processing_message;

    private boolean global_snapshot_complete;

    private boolean ok_for_next_global_snapshot;

    private String next_candidate;

    private boolean global_snapshot_start_point;

    private boolean global_snapshot_touchdown;

    private boolean has_replied;

    private Vector<String> asked_list;

    private boolean graph_complete;

    private Vector<String> global_snapshot_finished_list;

    private boolean snapshot_activate;

    private int global_snapshot_times;

    private boolean MAP_protocol_termination;

    public enum data_category{
        server_specification,
        node_specification,
        node_neighbors
    }
    private HashMap<data_category, HashMap<String, String[]>> database;

    private String[] node_config;
    private String[] sys_setup;
    private HashMap<String, String[]> neighbors_information;

    private int local_snapshot_time;

    private String initiator;

    // Constructor of Node
    public Node(String in_nodeID) {

        this.nid = in_nodeID; // assign node ID

        this.lock = false;

        this.node_status = status.passive;

        this.message_sent = 0;

        this.message_receive = 0;

        this.parent = new Vector<>();

        this.snapshot = new HashMap<>();

        //this.buffer = new Vector<>();
        this.snapshot_buffer = new Vector<>();

        //this.global_snapshot = new int[this.node_numbers];

        this.ok_for_next_global_snapshot = false;

        this.global_snapshot_start_point = false;

        this.global_snapshot_touchdown = false;

        this.has_replied = false;

        this.leaf = false;

        this.asked_list = new Vector<>();

        this.graph_complete = false;

        this.global_snapshot_finished_list = new Vector<>();

        this.snapshot_activate = false;

        this.global_snapshot_times = 0;

        this.MAP_buffer = new Vector<>();

        this.MAP_protocol_termination = false;

        this.local_snapshot_time = -1;

        this.initiator = "NONE";

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

    //public String getParent() {return this.parent;}
    public Vector<String> getParent() {return this.parent;}

    public HashMap<String, int[]> getSnapshot() {return this.snapshot;}

    public int[] getGlobal_snapshot() {return this.global_snapshot;}

    public boolean getConsitency() {return this.consistency;}

    public com_msg_packaging getSnapshot_Buffer_popout() {
        com_msg_packaging temp = new com_msg_packaging(this.snapshot_buffer.get(0));
        this.snapshot_buffer.remove(0);
        return temp;
    }

    public Vector<com_msg_packaging> getSnapshot_buffer() {return this.snapshot_buffer;}

    public com_msg_packaging getProcessing_message() {return this.processing_message;}

    public boolean getGlobal_snapshot_complete() {return this.global_snapshot_complete;}

    public boolean getOk_for_next_global_snapshot() {return this.ok_for_next_global_snapshot;}

    public String getNext_candidate() {return this.next_candidate;}

    public boolean getGlobal_snapshot_start_point() {return this.global_snapshot_start_point;}

    public boolean getGlobal_snapshot_touchdown() {return this.global_snapshot_touchdown;}

    public boolean getHas_replied() {return this.has_replied;}

    public boolean getLeaf() {return this.leaf;}

    public Vector<String> getAsked_list() {return this.asked_list;}

    public boolean getGraph_complete() {return this.graph_complete;}

    public Vector<String> getGlobal_snapshot_finished_list() {return this.global_snapshot_finished_list;}

    public boolean getSnapshot_activate() {return this.snapshot_activate;}

    public int getGlobal_snapshot_times() {return this.global_snapshot_times;}

    public Vector<com_msg_packaging> getMAP_buffer() {return this.MAP_buffer;}

    public boolean getMAP_protocol_termination() {return this.MAP_protocol_termination;}

    public HashMap<data_category, HashMap<String, String[]>> getDatabase() {return this.database;}

    public String[] getNode_config() {return this.node_config;}
    public String[] getSys_setup() {return this.sys_setup;}
    public HashMap<String, String[]> getNeighbors_information() {return this.neighbors_information;}

    public int getLocal_snapshot_time() {return this.local_snapshot_time;}

    public String getInitiator() {return this.initiator;}

    // Set Functions

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

    public void initialize_Expected_child_message_unit_decrease(int dv) {this.expected_child_message = dv;}
    public void setExpected_child_message_unit_decrease() {this.expected_child_message--;}
    public void resetExpected_child_message() {
        this.expected_child_message = this.neighbors_information.size();}

    public void setReceived_child_message_unit_increase() {this.received_child_message++;}
    public void resetReceived_child_message() {this.received_child_message = 0;}

    public void setVisited(boolean dv) {this.visited = dv;}

    //public void setParent(String dv) {this.parent = dv;}
    public void setParent_addset(Vector<String> dv) {this.parent.addAll(dv);}

    public void setParent_addelement(String dv) {this.parent.add(dv);}

    public void resetParent() {this.parent.clear();}

    public void setSnapshot(String nid, int[] time_vector) {this.snapshot.put(nid, time_vector);}
    public void MergeSnapshot(HashMap<String, int[]> dv) {this.snapshot.putAll(dv);}
    public void resetSnapshot() {this.snapshot.clear();}

    public void initialize_Global_snapshot(int dv) {this.global_snapshot = new int[dv];}

    public void setGlobal_snapshot(int node_number, int n_logical_time) {
        this.global_snapshot[node_number] = n_logical_time;
    }

    public void setConsistency(boolean dv) {this.consistency = dv;}

    public void resetNeighbos_info(HashMap<String, String[]> reset_set)
    {
        this.neighbors_information.clear();
        this.neighbors_information.putAll(reset_set);
    }

    public void setSnapshot_buffer_pushin(com_msg_packaging dv) {this.snapshot_buffer.add(dv);}
    public void resetSnapshot_Buffer() {this.snapshot_buffer.clear();}

    public void setProcessing_message(com_msg_packaging dv) {this.processing_message = new com_msg_packaging(dv);}

    public void setGlobal_snapshot_complete(boolean dv) {this.global_snapshot_complete = dv;}

    public void setOk_for_next_global_snapshot(boolean dv) {this.ok_for_next_global_snapshot = dv;}

    public void setNext_candidate(String dv) {this.next_candidate=dv;}

    public void setGlobal_snapshot_start_point(boolean dv) {this.global_snapshot_start_point=dv;}

    public void setGlobal_snapshot_touchdown(boolean dv) {this.global_snapshot_touchdown = dv;}

    public void setHas_replied(boolean dv) {this.has_replied = dv;}

    public void setLeaf(boolean dv) {this.leaf = dv;}

    public void setAsked_list(String dv) {this.asked_list.add(dv);}

    public void resetAsked_list() {this.asked_list.clear();}

    public void setGraph_complete(boolean dv) {this.graph_complete=dv;}

    public void setGlobal_snapshot_finished_list_addself() {this.global_snapshot_finished_list.add(this.nid);}
    public void resetGlobal_snapshot_finished_list_addone() {this.global_snapshot_finished_list.clear();}

    public void setSnapshot_activate(boolean dv) {this.snapshot_activate = dv;}

    public void setGlobal_snapshot_times_addone() {this.global_snapshot_times++;}
    public void resetGlobal_snapshot_times() {this.global_snapshot_times=0;}

    public void setMAP_buffer_addone(com_msg_packaging dv) {this.MAP_buffer.add(dv);}
    public void resetMAP_buffer() {this.MAP_buffer.clear();}

    public void setMAP_protocol_termination(boolean dv) {this.MAP_protocol_termination=dv;}

    public void setDatabase(HashMap<data_category, HashMap<String, String[]>> in_database){
        this.database = new HashMap<data_category, HashMap<String, String[]>> (in_database);
    }

     public void setNode_config(String[] dv) { this.node_config = dv.clone(); }
     public void setSys_setup(String[] dv) { this.sys_setup = dv.clone();}
     public void setNeighbors_information(HashMap<String, String[]> dv)
                { this.neighbors_information = new HashMap<String, String[]>(dv);}

    public void setlogical_time(int dv) {this.logical_time = new int[dv];}
    public void setNode_numbers(int dv) {this.node_numbers = dv;}
    public void setMinPerActive(int dv) {this.minPerActive = dv;}
    public void setMaxPerActive(int dv) {this.maxPerActive = dv;}
    public void setMinSendDelay(int dv) {this.minSendDelay = dv;}
    public void setSnapshotDelay(int dv) {this.snapshotDelay = dv;}
    public void setMaxNumber(int dv) {this.maxNumber = dv;}
    public void setNode_ip(String dv){this.node_ip = dv;}
    public void setPort(String dv){
        try {
            //this.port = new ServerSocket(this.nid + 5000);
            //System.out.println("[*] Server Port: " + (this.nid + 5000) + " created.");
            this.port = new ServerSocket(Integer.parseInt(dv));
            //this.port = new ServerSocket(Integer.parseInt(node_config[1]), 10);
            System.out.println(
                    "\nNode " + this.nid + " :\n" +
                            "Server Port: " + this.port
                            + " with IP: " + InetAddress.getLocalHost()+ " created.");
        } catch (IOException e) { e.printStackTrace();}
    }

    public void setLocal_snapshot_time_addone() {this.local_snapshot_time++;}
    public void resetLocal_snapshot_time() {this.local_snapshot_time=0;}

    public void setInitiator(String dv) {this.initiator = dv;}
}