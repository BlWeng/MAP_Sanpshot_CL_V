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


    // constructor
    public com_handler(int id, Socket in_obj, Node node, duty in_role, ObjectInputStream ois, ObjectOutputStream oos) {
        this.id = id;
        this.in_obj = in_obj;
        this.node = node;
        this.worker = Thread.currentThread();
        this.ply_role = in_role;
        this.ois = ois;
        this.oos = oos;
    }


    @Override
    public void run() {

        System.out.println("[*] COM_HANDLER thread is created, from " + in_obj.getInetAddress() + ".");

        try {

            running.set(true);

            while (running.get()) {

                com_msg_packaging in = (com_msg_packaging) ois.readObject();
                System.out.println(">> In ID: " + in.getSender() + " Host node: " + this.node.getNid());
                //System.out.println(">> Action: " + in.getAct_selected() + " Calling option: " + in.getCalling_action());

                this.node.setLogical_time_assign_value_pls_one(in.getSender_logical_time());

                com_msg_packaging processing_msg = new com_msg_packaging(in);
                processing_msg.setSender_logical_time(this.node.getLogical_time());

                node.setMessage_receive_unit_increase();

                System.out.println("Message received: " + node.getMessage_receive());

                if(this.node.getNode_status().equals(Node.status.passive)
                        && this.node.getMessage_sent() < this.node.getMaxNumber()
                            && in.getAct_selected().equals(com_msg_packaging.action_options.snapshot_request))
                {
                    this.node.setNode_status(Node.status.active);
                }


            }
        }catch(Exception e){}
    }
}