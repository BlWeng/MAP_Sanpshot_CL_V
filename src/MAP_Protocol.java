import java.util.*;

public class MAP_Protocol {

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
        com_msg_packaging message =
                new com_msg_packaging(node.getNid(), node.getLogical_time(),
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
                            com_msg_packaging.action_options.snapshot_request);

                    if(node.getMessage_sent() +1 >= node.getMaxNumber()) node.setNode_status(Node.status.passive);

                    exp_msg_counter++;
                }catch (Exception e){e.printStackTrace();}

            System.out.println("Status: " + node.getNode_status());

            }

            System.out.println("sent message number: " + node.getMessage_sent());
        }

    }
}
