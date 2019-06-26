import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

//public class com_handler  implements Runnable {
public class com_handler implements Runnable, Serializable {


    private int id;
    private Socket in_obj = null;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
    private Node node;
    private Thread worker;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private int interval = 1000;
    public enum duty{
        server,
        client
    }
    private duty ply_role;

    private boolean task_processing;

    // constructor
    public com_handler(int id, Socket in_obj, Node node, duty in_role, ObjectInputStream ois, ObjectOutputStream oos) {
        this.id = id;
        this.in_obj = in_obj;
        this.node = node;
        this.worker = Thread.currentThread();
        this.ply_role = in_role;
        this.ois = ois;
        this.oos = oos;

        this.task_processing = false;
    }


    @Override
    public void run() {

        System.out.println("[*] COM_HANDLER thread is created, from " + in_obj.getInetAddress() + ".");

        try {

            running.set(true);

            while (running.get()) {

                com_msg_packaging in = (com_msg_packaging) ois.readObject();
                System.out.println(">> In ID: " + in.getSender() + " Host node: " + this.node.getNid());
                System.out.println(">> Action: " + in.getAct_selected());
                //System.out.println(">> Action: " + in.getAct_selected() + " Calling option: " + in.getCalling_action());

                //this.node.setLogical_time_assign_value_pls_one(in.getSender_logical_time());
                this.node.setLogical_time_Fidge_Mattern(in.getSender_logical_time());

                //com_msg_packaging processing_msg = new com_msg_packaging(in);
                //processing_msg.setSender_logical_time(this.node.getLogical_time());

                this.node.setMessage_receive_unit_increase();

                System.out.println("Message received: " + node.getMessage_receive());


                if(in.getAct_selected().equals(com_msg_packaging.action_options.MAP_request)) {
                    if (this.node.getNode_status().equals(Node.status.passive)
                            && this.node.getMessage_sent() < this.node.getMaxNumber()
                            && in.getAct_selected().equals(com_msg_packaging.action_options.MAP_request))
                    {
                        this.node.setNode_status(Node.status.active);
                    }
                }
                else if(in.getAct_selected().equals(com_msg_packaging.action_options.Okay_for_next)) {
                /*
                    if(node.getGlobal_snapshot_start_point()){
                        node.setGlobal_snapshot_start_point(false);
                        node.setGlobal_snapshot_touchdown(true);
                        CL_snapshot.converge_cast_dyn(node, com_msg_packaging.action_options.Touchdown);
                    }
                    else node.setOk_for_next_global_snapshot(true);
                */
                System.out.println("==================IN OKAY FOR NEXT======================");

                    CL_snapshot.Global_snapshot_activator(node, CL_snapshot.roles.initiator);
                }

                else if(in.getAct_selected().equals(com_msg_packaging.action_options.Inform_finish)){
                    node.setGlobal_snapshot_touchdown(true);
                }
                else
                {
                    //Thread.sleep(100);
                    System.out.println("Preparing to push in Buffer: " + in.getSender());
                    try {
                        this.node.setBuffer_pushin(in);
                    }catch (Exception e) {e.printStackTrace();}
                    System.out.println("Message Buffer in handler size: " + this.node.getBuffer().size());
                }

            }
        }catch(Exception e){}
    }
}