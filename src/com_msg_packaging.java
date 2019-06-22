import java.io.Serializable;
import java.util.ArrayList;

public class com_msg_packaging implements Serializable {

    private String sender;
    private int sender_logical_time;

    private String receiver;
    private String receiver_ip;
    private int receiver_port;

    public enum action_options{
        VOTE_REQUEST,
        VOTE_REQUEST_REPLY,
        CATCH_UP,
        CATCH_UP_REPLY,
        COMMIT,
        ABORT,
        REQUEST_TIME_UPDATE,

        grant,
        release,
        complete,
        relinquish,
        close,
        initial,
        enter_cs,

        checking_connection,
        connection_established,
        snapshot_request
    }
    private action_options act_selected;

    public enum calling_option{
        broadcast_clique,
        single,
        shut_down
    }
    private calling_option calling_act;




    public com_msg_packaging(String sender, int sender_lg_time,
                             String receiver,String receiver_ip, String dest_port, action_options action){
        this.sender = sender;
        this.sender_logical_time = sender_lg_time;
        this.receiver = receiver;
        this.receiver_ip = receiver_ip;
        this.receiver_port = Integer.parseInt(dest_port);
        this.act_selected = action;
    }


    public com_msg_packaging(com_msg_packaging in_req_msg){
        this.sender = in_req_msg.getSender();
        this.sender_logical_time = in_req_msg.getSender_logical_time();
        this.receiver_ip = in_req_msg.getReceiver_ip();
        this.receiver = in_req_msg.getReceiver();
        this.receiver_port = in_req_msg.getReceiver_port();
        this.calling_act = in_req_msg.getCalling_action();
        this.act_selected = in_req_msg.getAct_selected();

    }

    // Get functions
    public String getSender() { return this.sender; }
    public int getSender_logical_time() {return this.sender_logical_time;}
    public String getReceiver_ip() {return this.receiver_ip;}
    public String getReceiver() { return  this.receiver;}
    public int getReceiver_port() {return this.receiver_port;}
    public action_options getAct_selected() {return this.act_selected;}
    public calling_option getCalling_action() {return this.calling_act;}

    // Set functions
    public void setSender(String dv) {this.sender = dv;}
    public void setSender_logical_time(int dv) {this.sender_logical_time = dv;}
    public void setReceiver_ip(String dv) {this.receiver_ip = dv;}
    public void setReceiver(String dv) {this.receiver=dv;}
    public void setReceiver_port(int dv) {this.receiver_port = dv;}
    public void setAct_selected(action_options dv) {this.act_selected = dv;}
    public void setCalling_action(calling_option dv) {this.calling_act = dv;}

}