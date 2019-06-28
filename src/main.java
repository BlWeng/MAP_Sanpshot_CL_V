import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class main {

    public static void main(String[] args) {

        String node_ID = args[0];
        String status_cmd = args[1];

        boolean instruction_read = false;
        boolean do_again = true;
        String response = "";

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//        System.out.println("====================== Start of node setup =========================");

        Node node = new Node(node_ID);
        if(status_cmd.equals("a")) node.setNode_status(Node.status.active);

//        System.out.println("====================== End of node setup =========================");

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//        System.out.println("====================== Start of Configuration File Processing =========================");

        DMA.Database_Inquiry_Administrator(node, DMA.enquiry.Create_Database);

        DMA.Database_Inquiry_Administrator(node, DMA.enquiry.Show_Database_Result);

//        System.out.println("====================== End of Configuration File Processing =========================");

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//        System.out.println("====================== Start of Database Request to DMA =========================");

        DMA.Database_Inquiry_Administrator(node, DMA.enquiry.Configurate_System_Environment);

//        System.out.println("====================== End of Database Request to DMA =========================");

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//        System.out.println("====================== Start of Server Establishment =========================");

        MAP_Protocol.server_establisher(node);

//        System.out.println("====================== End of Server Establishment =========================");

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //while(do_again) {
//        System.out.println("====================== Start of MAP Protocol Execution =========================");

            MAP_Protocol.MAP_Thread_executor(node);

//        System.out.println("====================== End of MAP Protocol Execution =========================");

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//        System.out.println("====================== Start of Global Snapshot Execution =========================");

            CL_snapshot.CL_snapshot_Thread_executor(node);

//        System.out.println("====================== Start of Global Snapshot Execution =========================");

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
            while (!instruction_read) {
                Scanner read_in = new Scanner(System.in);

                System.out.println(">>>>>>>>>>>>>Waiting Further Action in Node " + node.getNid() + " <<<<<<<<<<<<<<<<<");
                System.out.println("Please Enter Y for executing MAP protocol and global snapshot again " +
                        "while N for Exit Program.");

                response = read_in.nextLine();

                if (response.equals("Y")) {
                    instruction_read = true;
                    do_again = true;
                }
                else{
                    instruction_read = true;
                    do_again = false;
                }
            }
        }
*/
        System.out.println("!!!!!!!!!!!!!!!! Operating System Program of Application Terminated !!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!!!!!!!!!!!!!!!                  GOODBYE ^_^                       !!!!!!!!!!!!!!!!!!!!!");

        //System.out.println("Running Thread: " + Thread.activeCount());

        //System.exit(0);
        return;

    }


}