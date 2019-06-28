import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class com_msg_packaging implements Serializable {

    private String sender;
    //private int sender_logical_time;
    private int[] sender_logical_time;
    private String receiver;
    private String receiver_ip;
    private int receiver_port;

    public enum action_options{
        MAP_request,
        Maker_reply,
        Establish_graph
    }
    private action_options act_selected;

    private HashMap<String, int[]> sender_snapshot_set;

    private Vector<String> parent_graph;

    private Vector<String> global_snapshot_sent_list;

    private String initiator;

    private int snapshot_it;


    public com_msg_packaging(String sender, int[] sender_lg_time, HashMap<String, int[]> sender_snapshot_set,
                             Vector<String> sender_parent, Vector<String> in_global_snapshot_sent_list,
                             String receiver,String receiver_ip, String dest_port, action_options action,
                             String in_initiator, int in_lsp_it){
        this.sender = sender;
        this.sender_snapshot_set = new HashMap<String, int[]>(sender_snapshot_set);
        this.receiver = receiver;
        this.receiver_ip = receiver_ip;
        this.receiver_port = Integer.parseInt(dest_port);
        this.act_selected = action;
        this.sender_logical_time = sender_lg_time;
        this.parent_graph = new Vector<String>(sender_parent);
        this.global_snapshot_sent_list = new Vector<String>(in_global_snapshot_sent_list);
        this.snapshot_it = in_lsp_it;
        this.initiator = in_initiator;
    }



    public com_msg_packaging(com_msg_packaging in_req_msg){
        this.sender = in_req_msg.getSender();
        this.sender_logical_time = in_req_msg.getSender_logical_time();
        this.sender_snapshot_set = new HashMap<>(in_req_msg.getSender_snapshot_set());
        this.receiver_ip = in_req_msg.getReceiver_ip();
        this.receiver = in_req_msg.getReceiver();
        this.receiver_port = in_req_msg.getReceiver_port();
        this.act_selected = in_req_msg.getAct_selected();
        this.parent_graph = new Vector<String>(in_req_msg.getParent_graph());
        this.snapshot_it = in_req_msg.getSnapshot_it();
        this.initiator = in_req_msg.getInitiator();
    }

    // Get functions

    public String getSender() { return this.sender; }
    public int[] getSender_logical_time() {return this.sender_logical_time;}
    public String getReceiver_ip() {return this.receiver_ip;}
    public String getReceiver() { return  this.receiver;}
    public int getReceiver_port() {return this.receiver_port;}
    public action_options getAct_selected() {return this.act_selected;}
    public HashMap<String, int[]> getSender_snapshot_set() {return this.sender_snapshot_set;}
    public Vector<String> getParent_graph() {return this.parent_graph;}
    public Vector<String> getGlobal_snashot_sent_list() {return this.global_snapshot_sent_list;}
    public int getSnapshot_it() {return this.snapshot_it;}
    public String getInitiator() {return this.initiator;}

    // Set functions

    public void setSender(String dv) {this.sender = dv;}
    public void setSender_logical_time(int[] dv) {this.sender_logical_time = dv.clone();}
    public void setReceiver_ip(String dv) {this.receiver_ip = dv;}
    public void setReceiver(String dv) {this.receiver=dv;}
    public void setReceiver_port(int dv) {this.receiver_port = dv;}
    public void setAct_selected(action_options dv) {this.act_selected = dv;}
    public void setSender_snapshot_set(HashMap<String, int[]> dv){
        this.sender_snapshot_set.clear();
        this.sender_snapshot_set.putAll(dv);
    }
    public void setParent_graph(Vector<String> dv) {this.parent_graph = new Vector<String>(dv);}
    public void setGlobal_snashot_sent_list(Vector<String> dv) {this.global_snapshot_sent_list = new Vector<String>(dv);}
    public void setSnapshot_it(int dv) {this.snapshot_it = dv;}
    public void setInitiator(String dv) {this.initiator = dv;}

}