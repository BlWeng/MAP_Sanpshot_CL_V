import java.util.*;

public class CL_snapshot implements Runnable{

    public enum roles{
        initiator,
        respondent
    }

    public roles play_role;

    private Node node;

    CL_snapshot(Node in_node, roles in_play_role){
        this.node = in_node;
        this.play_role = in_play_role;
    }

    @Override
    public void run() {
        while(true) {
            global_snapshot(this.node, this.play_role);
            //System.out.println("Unprocessed message size: " + node.getBuffer().size());
        }
    }

    public static void global_snapshot(Node node, roles play_role){
        //System.out.println("Expect_MSG: " + node.getExpected_child_message());
        //System.out.println("Received_msg: " + node.getReceived_child_message());
        if(play_role.equals(roles.initiator) && !node.getVisited()) {
            Reset_CL_snapshot(node);
            System.out.println("GS_testing");
            node.setVisited(true);
            node.setParent("Global_Snapshot_Initiator");
            converge_cast_dyn(node, com_msg_packaging.action_options.Maker_request);
            while (node.getSnapshot().size() != node.getNode_numbers()) {
                System.out.print("Waiting global snapshot completed! ; " + node.getSnapshot() + "\r");
            }

            Global_Snapshot_Generator(node);

            Global_Sanpshot_Consistent_Verification(node);

        }
        //System.out.println("Buffer size in CL: " + node.getBuffer());
        //if(play_role.equals(roles.respondent)){
            while(!node.getBuffer().isEmpty()) {
                System.out.println("===========================================================================");
                //System.out.println("First Element Buffer in CL: " + node.getBuffer().firstElement().getSender());
                System.out.println("First Element Buffer in CL: " + node.getBuffer().get(0).getSender());
                System.out.println("Buffer size in CL: " + node.getBuffer().size());
                com_msg_packaging processing_message = new com_msg_packaging(node.getBuffer_popout());
                node.setProcessing_message(processing_message);
                System.out.println("Current Processing msg: "+ processing_message.getSender() +" : "+processing_message.getAct_selected());
                System.out.println("Processing msg act: " + processing_message.getAct_selected());
                System.out.println("Parent: " + node.getParent());
                System.out.println("Visited: " + node.getVisited());
                //switch (in.getAct_selected()) {
                switch (processing_message.getAct_selected()) {
                    case Maker_request:
                        //node.setParent(in.getSender());
                        //if (!node.getParent().equals("Global_Snapshot_Initiator")) {
                            //System.out.println("Parent: " + node.getParent());
                            //System.out.println("Visited: " + node.getVisited());
                            //if( !node.getParent().equals(node.getNid()) ) {
                            System.out.println("Before Preparing propagation");
                            if (!node.getVisited()) {
                                CL_snapshot.Reset_CL_snapshot(node);
                                node.setParent(processing_message.getSender());
                                node.setVisited(true);
                                node.setSnapshot(node.getNid(), node.getLogical_time());
                                System.out.println("Preparing propagation");
                                CL_snapshot.converge_cast_dyn(node, com_msg_packaging.action_options.Maker_request);
                                System.out.println("Temp Snapshot Result in non-Visited: " + node.getSnapshot());
                            }
                            else if (node.getVisited() && !node.getParent().equals("Global_Snapshot_Initiator")) {
                                System.out.println("Temp Snapshot Result in Visited: " + node.getSnapshot());
                                System.out.println("Parent in non-Visited: " + node.getParent());
                                System.out.println("Expected message: " + node.getExpected_child_message());
                                CL_snapshot.converge_cast_dyn(node, com_msg_packaging.action_options.Convergecast_visited);

                            }
                        //}

                        break;

                    case Maker_reply:
                        //if(node.getVisited()) {
                        //node.setSnapshot(in.getSender(), in.getSender_logical_time());
                        System.out.println("Receive snapshot: " + processing_message.getSender());
                        node.MergeSnapshot(processing_message.getSender_snapshot_set());
                        node.setReceived_child_message_unit_increase();
                        System.out.println("Global Snapshot in reply : " + node.getSnapshot());
                        if (node.getExpected_child_message() == node.getReceived_child_message() &&
                                !node.getParent().equals("Global_Snapshot_Initiator")) {
                            CL_snapshot.converge_cast_dyn(node, com_msg_packaging.action_options.Maker_reply);
                            //System.out.println("Reset in Reply");
                            CL_snapshot.Reset_CL_snapshot(node);
                        }
                        //}
                        break;
                    case Convergecast_visited:
                        //if(node.getVisited()) {
                        System.out.println("Before subtraction: "+ node.getExpected_child_message());
                        node.setExpected_child_message_unit_decrease();
                        System.out.println("Expected message in visited zone: " + node.getExpected_child_message());
                        if (node.getExpected_child_message() == 0 ) {
                            CL_snapshot.converge_cast_dyn(node, com_msg_packaging.action_options.Maker_reply);
                            //System.out.println("Reset in visited");
                            CL_snapshot.Reset_CL_snapshot(node);
                        }
                        //}
                        break;
                }
            }
        //}

    }

    public static void converge_cast_dyn(Node node, com_msg_packaging.action_options action){
        switch(action) {
            case Maker_request:
                node.setLogical_time_unit_increase();
                //node.resetExpected_child_message();
                System.out.println("Broadcasting............");
                //node.setParent("Global_Snapshot_Initiator");
                //node.setParent(node.getNid());
                System.out.println("Parent before sending: " + node.getParent());
                node.setSnapshot(node.getNid(), node.getLogical_time());
                for (String receiver : node.getNeighbors_information().keySet()) {
                    com_msg_packaging maker_init_msg = new com_msg_packaging(
                            node.getNid(), node.getLogical_time(),
                            receiver,
                            node.getNeighbors_information().get(receiver)[0],
                            node.getNeighbors_information().get(receiver)[1],
                            com_msg_packaging.action_options.Maker_request);
                    com_requester maker_initiator = new com_requester(maker_init_msg);
                    maker_initiator.send();
                }
                break;
            case Maker_reply:
                com_msg_packaging maker_reply_msg_set = new com_msg_packaging(
                        node.getNid(), node.getLogical_time(), node.getSnapshot(),
                        node.getParent(),
                        node.getNeighbors_information().get(node.getParent())[0],
                        node.getNeighbors_information().get(node.getParent())[1],
                        com_msg_packaging.action_options.Maker_reply);
                com_requester maker_reply_set = new com_requester(maker_reply_msg_set);
                maker_reply_set.send();
                break;
            case Convergecast_visited:
                com_msg_packaging maker_visited_msg = new com_msg_packaging(
                        node.getNid(), node.getLogical_time(),
                        node.getProcessing_message().getSender(),
                        node.getNeighbors_information().get(node.getProcessing_message().getSender())[0],
                        node.getNeighbors_information().get(node.getProcessing_message().getSender())[1],
                        com_msg_packaging.action_options.Convergecast_visited);
                com_requester maker_visited = new com_requester(maker_visited_msg);
                maker_visited.send();
                break;
        }
    }




    public static void Reset_CL_snapshot(Node node){
        node.resetExpected_child_message();
        node.resetReceived_child_message();
        node.setVisited(false);
        node.setParent("");
        node.resetSnapshot();
    }

    public static void Global_Snapshot_Generator(Node node){
        System.out.println("Global Snapshot Set: " + node.getSnapshot().entrySet());

        int temp = 0;
        for(int i = 0; i < node.getNode_numbers(); i++) {
            temp = 0;
            for (Map.Entry<String, int[]> entry : node.getSnapshot().entrySet()) {
                if(temp <= entry.getValue()[i]) temp = entry.getValue()[i];
            }
            node.setGlobal_snapshot(i, temp);
        }
        System.out.println("Result of Global Snapshot: " + Arrays.toString(node.getGlobal_snapshot()));
    }

    public static void Global_Sanpshot_Consistent_Verification(Node node){

        boolean temp = true;
        for(int j = 0; j < node.getNode_numbers(); j++) {
            for (Map.Entry<String, int[]> entry : node.getSnapshot().entrySet()) {
                if(node.getGlobal_snapshot()[j] < entry.getValue()[j]) temp = false;
            }
        }

        if(!temp) {
            System.out.println("Global Snapshot is not consistent!");
        }

        else {
            System.out.println("Global Snapshot is consistent!");
        }

    }
}
