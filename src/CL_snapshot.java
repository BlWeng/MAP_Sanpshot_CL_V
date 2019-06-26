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
        //while(true) {
        if(this.play_role.equals(roles.initiator)) {
            //System.out.println("===============In initiator=================");
            //while (!node.getOk_for_next_global_snapshot())
                //System.out.print("Waiting global snapshot permission!\r");
            //node.setOk_for_next_global_snapshot(false);

            //if(node.getGlobal_snapshot_touchdown()) {

                for (int i = 0; i < 5; i++) {
                    node.setGlobal_snapshot_complete(false);
                    while (!node.getGlobal_snapshot_complete()) {
                        global_snapshot(this.node, this.play_role);
                        //System.out.println("Unprocessed message size: " + node.getBuffer().size());
                    }
                    System.out.println("Global snapshot " + (i + 1) + " completed!");
                    try {
                        Thread.sleep(node.getSnapshotDelay());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                CL_snapshot.Next_Candidate(node);
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>Picked Candidate: " + node.getNext_candidate());
                if(!node.getNext_candidate().equals("0"))
                CL_snapshot.converge_cast_dyn(node, com_msg_packaging.action_options.Okay_for_next);
            //}

        //else node.setGlobal_snapshot_touchdown(false);
        }

        if(this.play_role.equals(roles.respondent)) {

            while (!node.getGlobal_snapshot_touchdown()) {
                //System.out.println("==========In Respondent=============");
                global_snapshot(this.node, this.play_role);
                //System.out.println("Unprocessed message size: " + node.getBuffer().size());
            }

            node.setGlobal_snapshot_touchdown(false);
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

            Global_Snapshot_Consistent_Verification(node);

            File_Interface.write_file(node);

            node.setGlobal_snapshot_complete(true);

            CL_snapshot.Reset_CL_snapshot(node);

            node.setHas_replied(false);
        }
        //System.out.println("Buffer size in CL: " + node.getBuffer());
        //if(play_role.equals(roles.respondent)){
            while(!node.getBuffer().isEmpty() ) {
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
                                !node.getParent().equals("Global_Snapshot_Initiator") &&
                                !node.getHas_replied()) {
                            CL_snapshot.converge_cast_dyn(node, com_msg_packaging.action_options.Maker_reply);
                            //System.out.println("Reset in Reply");
                            CL_snapshot.Reset_CL_snapshot(node);
                            //node.resetBuffer();
                            node.setHas_replied(true);
                        }
                        //}
                        break;
                    case Convergecast_visited:
                        //if(node.getVisited()) {
                        //if(node.getExpectehid_cld_message() != 0) {
                            System.out.println("Before subtraction: " + node.getExpected_child_message());
                            node.setExpected_child_message_unit_decrease();
                            System.out.println("Expected message in visited zone: " + node.getExpected_child_message());
                            if (node.getExpected_child_message() == 0 && !node.getHas_replied()) {
                                CL_snapshot.converge_cast_dyn(node, com_msg_packaging.action_options.Maker_reply);
                                //System.out.println("Reset in visited");
                                CL_snapshot.Reset_CL_snapshot(node);
                                //node.resetBuffer();
                                //node.setVisited(false);
                                node.setHas_replied(true);
                            }
                            //}
                        //}
                        break;
                }
            }

            //CL_snapshot.Reset_CL_snapshot(node);
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

            case Okay_for_next:
                com_msg_packaging okay_msg = new com_msg_packaging(
                        node.getNid(), node.getLogical_time(),
                        node.getNext_candidate(),
                        node.getNeighbors_information().get(node.getNext_candidate())[0],
                        node.getNeighbors_information().get(node.getNext_candidate())[1],
                        com_msg_packaging.action_options.Okay_for_next);
                com_requester okay_info = new com_requester(okay_msg);
                okay_info.send();
                break;

            case Touchdown:
                Vector<String> target = new Vector<>();
                for(int i=0 ; i < node.getNode_numbers() ; i++){
                    if(!String.valueOf(i).equals(node)) target.add(String.valueOf(i));
                }
                for (String t_receiver : target) {
                    com_msg_packaging finish_msg = new com_msg_packaging(
                            node.getNid(), node.getLogical_time(),
                            t_receiver,
                            node.getNeighbors_information().get(t_receiver)[0],
                            node.getNeighbors_information().get(t_receiver)[1],
                            com_msg_packaging.action_options.Inform_finish);
                    com_requester inform_finish = new com_requester(finish_msg);
                    inform_finish.send();
                }
                break;

        }
    }

    public static void Next_Candidate(Node node){
        String next_one;
        if(node.getNid().equals(String.valueOf(node.getNode_numbers()-1)))
            next_one = "0";
        else
            next_one = String.valueOf(Integer.parseInt(node.getNid()) + 1);

        node.setNext_candidate(next_one);
    }

    public static void Reset_CL_snapshot(Node node){
        node.resetExpected_child_message();
        node.resetReceived_child_message();
        node.setVisited(false);
        node.setParent("");
        node.resetSnapshot();
        //node.resetBuffer();
        node.setHas_replied(false);
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

    public static void Global_Snapshot_Consistent_Verification(Node node){

        boolean temp = true;
        for(int j = 0; j < node.getNode_numbers(); j++) {
            for (Map.Entry<String, int[]> entry : node.getSnapshot().entrySet()) {
                if(node.getGlobal_snapshot()[j] < entry.getValue()[j]) temp = false;
            }
        }

        if(!temp) {
            System.out.println("Global Snapshot is not consistent!");
            node.setConsistency(false);
        }

        else {
            System.out.println("Global Snapshot is consistent!");
            node.setConsistency(true);
        }

    }

    public static void Global_snapshot_activator(Node node, CL_snapshot.roles play_role){
        if(play_role.equals(roles.initiator)) {
            CL_snapshot initiator = new CL_snapshot(node, CL_snapshot.roles.initiator);
            Thread Global_snapshot_initiator = new Thread(initiator);
            Global_snapshot_initiator.start();
        }

        if(play_role.equals(roles.respondent)) {
            CL_snapshot respondent = new CL_snapshot(node, CL_snapshot.roles.respondent);
            Thread Global_snapshot_respondent = new Thread(respondent);
            Global_snapshot_respondent.start();
        }
    }
}
