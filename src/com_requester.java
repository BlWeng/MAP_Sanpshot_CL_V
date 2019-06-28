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
        while( this.requested && !this.sent) {
            try {
                com_port = new Socket(InetAddress.getLocalHost(), this.rv.getReceiver_port());
               // com_port = new Socket(InetAddress.getByName(this.rv.getReceiver_ip()+".utdallas.edu"),
               //         this.rv.getReceiver_port());

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
