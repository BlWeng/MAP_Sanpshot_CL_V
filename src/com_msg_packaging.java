import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class com_msg_packaging implements Serializable {

    private String sender;
    //private int sender_logical_time;
    private int[] sender_logical_time;
    private String receiver;
    private String receiver_ip;
    private int receiver_port;

    public enum action_options{
        MAP_request,
        Maker_request,
        Maker_reply,
        Convergecast_visited,
        Okay_for_next,
        Touchdown,
        Inform_finish,
        Inforn_activation
    }
    private action_options act_selected;
/*
    public enum calling_option{
        broadcast_clique,
        single,
        shut_down
    }
    private calling_option calling_act;
*/
    private HashMap<String, int[]> sender_snapshot_set;


    public com_msg_packaging(String sender, int[] sender_lg_time,
                             String receiver,String receiver_ip, String dest_port, action_options action){
        this.sender = sender;
        this.sender_logical_time = sender_lg_time;
        this.receiver = receiver;
        this.receiver_ip = receiver_ip;
        this.receiver_port = Integer.parseInt(dest_port);
        this.act_selected = action;

        this.sender_snapshot_set = new HashMap<>();

    }

    public com_msg_packaging(String sender, int[] sender_lg_time, HashMap<String, int[]> sender_snapshot_set,
                             String receiver,String receiver_ip, String dest_port, action_options action){
        this.sender = sender;
        this.sender_snapshot_set = new HashMap<String, int[]>(sender_snapshot_set);
        this.receiver = receiver;
        this.receiver_ip = receiver_ip;
        this.receiver_port = Integer.parseInt(dest_port);
        this.act_selected = action;

        this.sender_logical_time = sender_lg_time;
    }

    public com_msg_packaging(String sender,
                             String receiver,String receiver_ip, String dest_port, action_options action){
        this.sender = sender;
        this.receiver = receiver;
        this.receiver_ip = receiver_ip;
        this.receiver_port = Integer.parseInt(dest_port);
        this.act_selected = action;

        this.sender_logical_time = null;
        this.sender_snapshot_set = new HashMap<>();
    }


    public com_msg_packaging(com_msg_packaging in_req_msg){
        this.sender = in_req_msg.getSender();
        if(in_req_msg.getSender_logical_time() != null)
            this.sender_logical_time = in_req_msg.getSender_logical_time();
        else
            this.sender_logical_time = null;
        if(!in_req_msg.getSender_snapshot_set().isEmpty()) {
            System.out.println("In Snapshot initialization");
            this.sender_snapshot_set = new HashMap<>(in_req_msg.getSender_snapshot_set());
        }
        else
            this.sender_snapshot_set = new HashMap<>();
        this.receiver_ip = in_req_msg.getReceiver_ip();
        this.receiver = in_req_msg.getReceiver();
        this.receiver_port = in_req_msg.getReceiver_port();
        //this.calling_act = in_req_msg.getCalling_action();
        this.act_selected = in_req_msg.getAct_selected();

    }

    // Get functions
    public String getSender() { return this.sender; }
    public int[] getSender_logical_time() {return this.sender_logical_time;}
    public String getReceiver_ip() {return this.receiver_ip;}
    public String getReceiver() { return  this.receiver;}
    public int getReceiver_port() {return this.receiver_port;}
    public action_options getAct_selected() {return this.act_selected;}
    //public calling_option getCalling_action() {return this.calling_act;}
    public HashMap<String, int[]> getSender_snapshot_set() {return this.sender_snapshot_set;}

    // Set functions
    public void setSender(String dv) {this.sender = dv;}
    public void setSender_logical_time(int[] dv) {this.sender_logical_time = dv.clone();}
    public void setReceiver_ip(String dv) {this.receiver_ip = dv;}
    public void setReceiver(String dv) {this.receiver=dv;}
    public void setReceiver_port(int dv) {this.receiver_port = dv;}
    public void setAct_selected(action_options dv) {this.act_selected = dv;}
    //public void setCalling_action(calling_option dv) {this.calling_act = dv;}
    public void setSender_snapshot_set(HashMap<String, int[]> dv){
        this.sender_snapshot_set.clear();
        this.sender_snapshot_set.putAll(dv);
    }

}