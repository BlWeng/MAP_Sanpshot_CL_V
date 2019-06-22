import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class com_requester {

    private com_msg_packaging rv;

    Socket com_port = null;

    static Vector<com_handler> server_list_client_end = new Vector<>();

    private boolean requested;

    private boolean sent;


    public com_requester(com_msg_packaging in_rv) {
        this.rv = new com_msg_packaging(in_rv);
        this.requested = true;
        this.sent = false;
        }

    public void send(){
            //this.node.setNm_msg_sent();
        while( this.requested && !this.sent) {
            try {
                //com_port = new Socket(InetAddress.getLocalHost(), this.rv.getReceiver_port());

                String go_ip = this.rv.getReceiver_ip()+".utdallas.edu";

                System.out.println("IP: " + go_ip);

                com_port = new Socket(InetAddress.getByName(go_ip), this.rv.getReceiver_port());

/*
                String s_id;

                    if (this.rv. < 10)
                        server_id = "dc0" + this.rv.getReceiver() + ".utdallas.edu";
                    else
                        server_id = "dc" + this.rv.getReceiver() + ".utdallas.edu";


               com_port = new Socket(server_id, 5000+this.rv.getReceiver());
*/


                ObjectOutputStream oos = new ObjectOutputStream(com_port.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(com_port.getInputStream());

                oos.writeObject(this.rv);

                this.sent = true;

            } catch (Exception e) {
                //i--;
                System.out.print("Port of Node " + this.rv.getReceiver() + " is not ready.\r");
            }
        }
    }


}
