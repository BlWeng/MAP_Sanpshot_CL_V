import java.util.*;

public class CL_snapshot implements Runnable{
//public class CL_snapshot{
    public enum roles{
        initiator,
        respondent
    }

    public roles play_role;

    private Node node;

    public enum graph{
        root,
        child_visited,
        propagation,
    }

    private graph graph_role;

    private String initiator;

    private int snapshot_times;

    CL_snapshot(Node in_node, String in_initiator){
        this.node = in_node;
        //this.play_role = in_play_role;
        this.initiator = in_initiator;
        this.snapshot_times = 0;
    }

    public static void CL_snapshot_Thread_executor(Node node){
        CL_snapshot initiator = new CL_snapshot(node, "0");
        Thread Global_snapshot_respondent = new Thread(initiator);
        Global_snapshot_respondent.start();
    }

    @Override
    public void run(){
        //System.out.println("======Graph Test=======");
        while(!node.getGlobal_snapshot_touchdown()) {
            for (int i = 0; i < 10; i++) {
                node.setLocal_snapshot_time_addone();
                node.setInitiator(this.initiator);
                if (node.getNid().equals(this.initiator)) CL_snapshot.graph_establishing(node, CL_snapshot.graph.root);
                while (!node.getGlobal_snapshot_complete()) {
                    while (node.getSnapshot_buffer().size() == 0) {
                        //System.out.print("Waiting response........... Received snapshot "+ node.getSnapshot().entrySet().toString() +"\r");
                        System.out.print("Waiting response...........\r");
                    }

                    //if(!node.getBuffer().isEmpty()) processing_graph_message(node);
                    processing_graph_message(node);
                    if (node.getGlobal_snapshot_start_point() && node.getSnapshot().size() == node.getNode_numbers()) {
                        Global_Snapshot_Generator(node);
                        Global_Snapshot_Consistent_Verification(node);
                        MAP_protocol_termination_checker(node);
                        File_Interface.write_file(node);
                        node.setGlobal_snapshot_complete(true);
                    }

                }

                if (node.getGlobal_snapshot_start_point()) {
                    System.out.println("Complete >>>>>>>--- " + (i + 1) + " --->>>>>>>>>>>>>>>> Result: " +
                            Arrays.toString(node.getGlobal_snapshot()));
                    System.out.println("Buffer Size: " + node.getSnapshot_buffer().size());
                }
                Reset_CL_snapshot(node);
                try {
                    Thread.sleep(node.getSnapshotDelay());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            node.resetLocal_snapshot_time();
            node.setGlobal_snapshot_start_point(false);
            node.setGlobal_snapshot_times_addone();
            if (node.getGlobal_snapshot_times() == node.getNode_numbers()) node.setGlobal_snapshot_touchdown(true);
            this.initiator = String.valueOf(Integer.parseInt(this.initiator) + 1);
        }
    }


    public static void Reset_CL_snapshot(Node node){
        node.resetExpected_child_message();
        node.resetReceived_child_message();
        node.resetParent();
        node.setVisited(false);
        node.resetSnapshot();
        node.resetSnapshot_Buffer();
        node.setHas_replied(false);
        node.setGlobal_snapshot_complete(false);
        node.resetAsked_list();
    }

    public static void Global_Snapshot_Generator(Node node){
        //System.out.println("Global Snapshot Set: " + node.getSnapshot().entrySet());
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

    public static void MAP_protocol_termination_checker(Node node){
        if(node.getNode_status().equals(Node.status.passive) && node.getMAP_buffer().isEmpty())
            node.setMAP_protocol_termination(true);
        else
            node.setMAP_protocol_termination(false);
    }

    public static void graph_establishing(Node node, CL_snapshot.graph graph_role){
        switch (graph_role) {
            case root:
            case propagation:
                if(graph_role.equals(graph.root)){
                    node.setParent_addelement("root");
                    node.setGlobal_snapshot_start_point(true);
                    node.setVisited(true);
                    node.setSnapshot(node.getNid(), node.getLogical_time());
                }
                for (String receiver : node.getNeighbors_information().keySet()) {
                    if(!receiver.equals(node.getParent().lastElement())) {
                        com_msg_packaging establish_graph = new com_msg_packaging(
                                node.getNid(), node.getLogical_time(), node.getSnapshot(),
                                node.getParent(), node.getGlobal_snapshot_finished_list(),
                                receiver,
                                node.getNeighbors_information().get(receiver)[0],
                                node.getNeighbors_information().get(receiver)[1],
                                com_msg_packaging.action_options.Establish_graph,
                                node.getInitiator(),
                                node.getLocal_snapshot_time());
                        com_requester graph_establishing = new com_requester(establish_graph);
                        graph_establishing.send();
                    }
                }
                break;
            case child_visited:
                node.setExpected_child_message_unit_decrease();
                //System.out.println("Expect message " + node.getExpected_child_message());
                if(node.getExpected_child_message() == 0 && !node.getGlobal_snapshot_start_point()){
                    node.setLeaf(true);
                }
                break;
        }
    }

    public static void processing_graph_message(Node node){
        boolean ok = false;
        com_msg_packaging target = node.getSnapshot_Buffer_popout();

        while(!ok){
            while( !( target.getInitiator().equals(node.getInitiator()) && target.getSnapshot_it() == node.getLocal_snapshot_time()) ){
                //System.out.println("Target sender: " + target.getSender() + " ; " + "Caller: " + node.getInitiator());
                //System.out.println("Target it: " + target.getSnapshot_it() + "Local iteration: " + node.getLocal_snapshot_time());
                node.setSnapshot_buffer_pushin(target);
                target = new com_msg_packaging(node.getSnapshot_Buffer_popout());
            }
            ok = true;
        }


        if(target.getAct_selected().equals(com_msg_packaging.action_options.Maker_reply)){
            node.MergeSnapshot(target.getSender_snapshot_set());
            node.setReceived_child_message_unit_increase();
            if(node.getExpected_child_message() == node.getReceived_child_message()&&
                !node.getGlobal_snapshot_start_point()){
                send_Maker_reply_message(node);
            }
        }

        if(!node.getVisited()){
            //System.out.println("Receiving from parent: " + target.getParent_graph());
            node.setParent_addset(target.getParent_graph());
            node.setParent_addelement(target.getSender());
            node.setVisited(true);
            node.setAsked_list(target.getSender());
            node.setExpected_child_message_unit_decrease();
            node.setSnapshot(node.getNid(),node.getLogical_time());
            CL_snapshot.graph_establishing(node, CL_snapshot.graph.propagation);
        }
        else{
            //System.out.println("Before add list: " + node.getAsked_list());
            if(!node.getAsked_list().contains(target.getSender())) {
                node.setAsked_list(target.getSender());
                //System.out.println("After add list: " + node.getAsked_list());
                node.setExpected_child_message_unit_decrease();
                //System.out.println("Expect message " + node.getExpected_child_message());
                if (node.getExpected_child_message() == 0 && !node.getGlobal_snapshot_start_point()) {
                //if (node.getExpected_child_message() == 0 ) {
                    node.setLeaf(true);
                    //System.out.println("Parent List: " + node.getParent().toString());
                    send_Maker_reply_message(node);
                }

            }
        }
    }

    public static void send_Maker_reply_message(Node node){
        com_msg_packaging maker_reply_msg = new com_msg_packaging(
                node.getNid(), node.getLogical_time(), node.getSnapshot(),
                node.getParent(), node.getGlobal_snapshot_finished_list(),
                node.getParent().lastElement(),
                node.getNeighbors_information().get( node.getParent().lastElement())[0],
                node.getNeighbors_information().get( node.getParent().lastElement())[1],
                com_msg_packaging.action_options.Maker_reply,
                node.getInitiator(),
                node.getLocal_snapshot_time());
        com_requester maker_reply = new com_requester(maker_reply_msg);
        maker_reply.send();
        node.setGlobal_snapshot_complete(true);

    }
}
