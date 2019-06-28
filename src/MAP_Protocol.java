import java.util.*;

public class MAP_Protocol implements Runnable
{

    private Node node;

    private HashMap<Node.data_category, HashMap<String, String[]>> database;

    MAP_Protocol(Node in_node){
        this.node = in_node;
        this.database = new HashMap<Node.data_category, HashMap<String, String[]>>(node.getDatabase());
    }

    public Node getNode(){return this.node;}
    public HashMap<Node.data_category, HashMap<String, String[]>> getDatabase() {return this.database;}

    public static void MAP_Thread_executor(Node node)
    {
        MAP_Protocol MAP_to_Thread = new MAP_Protocol(node);
        Thread MAP_Thread = new Thread(MAP_to_Thread);
        MAP_Thread.run();
    }

    @Override
    public void run(){
        while(node.getMessage_sent() +1 < node.getMaxNumber())
            MAP_Protocol.MAP_protocol_executor(node,
                    database.get(Node.data_category.node_neighbors).get(node.getNid()),
                    database.get(Node.data_category.node_specification));
        System.out.println("===============Finish Thread of MAP Protocol!================");
    }

    public static void server_establisher(Node node){
        Thread node_server = new Thread(new com_server(node));
        node_server.start();
    }
/*
    public static void system_connection_checker(Node node, HashMap<String, String[]> network_map){

        for(Map.Entry<String, String[]> entry : network_map.entrySet()) {
            if (!entry.getKey().equals(node.getNid())) {
                com_msg_packaging message =
                        new com_msg_packaging(node.getNid(), node.getLogical_time(), entry.getKey(), entry.getValue()[1],
                                com_msg_packaging.action_options.checking_connection);

            }
        }
    }
*/
    public static void sending_message(Node node, String receiver, String receiver_ip,String receiver_port,
                                       com_msg_packaging.action_options action)
    {
        node.setLogical_time_unit_increase();
        com_msg_packaging message =
                new com_msg_packaging(node.getNid(), node.getLogical_time(), node.getSnapshot(),
                                        node.getParent(),node.getGlobal_snapshot_finished_list(),
                                        receiver, receiver_ip, receiver_port, action);
        com_requester sending_msg = new com_requester(message);
        sending_msg.send();
        node.setMessage_sent_unit_increase();

    }

    public static void MAP_protocol_executor(Node node, String[] neighbors, HashMap<String, String[]> neighbor_info){

        while(node.getNode_status().equals(Node.status.active)){
            int expected_messages;
            int exp_msg_counter = 0;
            String selected_destination;

            expected_messages = new Random().nextInt(node.getMaxPerActive() - node.getMinPerActive() + 1)
                                + node.getMinPerActive();

            System.out.println("Expected message: " + expected_messages);

            while(exp_msg_counter < expected_messages && node.getNode_status().equals(Node.status.active)){
                try {
                    selected_destination = neighbors[new Random().nextInt(neighbors.length)];
                    System.out.println("Selected Destination: " + selected_destination);

                    if (exp_msg_counter != 0) Thread.sleep(node.getMinSendDelay());

                    sending_message(node, selected_destination,
                            neighbor_info.get(selected_destination)[0],
                            neighbor_info.get(selected_destination)[1],
                            com_msg_packaging.action_options.MAP_request);

                    if(node.getMessage_sent() +1 >= node.getMaxNumber()) node.setNode_status(Node.status.passive);

                    exp_msg_counter++;
                    Read_MAP_Message(node);
                }catch (Exception e){e.printStackTrace();}

            System.out.println("Status: " + node.getNode_status());

            }

            Read_MAP_Message(node);

            System.out.println("sent message number: " + node.getMessage_sent());
        }

    }

    public static void Read_MAP_Message(Node node){
        while(!node.getMAP_buffer().isEmpty()){
            System.out.println("Receive message from Node " + node.getMAP_buffer().get(0).getSender() +
                    " with logical time: " + Arrays.toString(node.getMAP_buffer().get(0).getSender_logical_time()));
            node.getMAP_buffer().remove(0);
        }
    }
}
