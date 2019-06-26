import java.util.Arrays;
import java.util.HashMap;

public class main {

    public static void main(String[] args) {

        String node_ID = args[0];
        String status_cmd = args[1];

        HashMap<File_Interface.data_category, HashMap<String, String[]>> database = File_Interface.read_configuration();

        System.out.println("\n=================================");
        System.out.println("Database Testing in Main Process:");
        System.out.println("=================================");
        for(File_Interface.data_category key: database.keySet()){
            System.out.println("\nDatabase: " + key );
            for(String key_sub: database.get(key).keySet()){
                System.out.println("Sub_Data: " + key_sub + " -> "
                        + Arrays.deepToString(database.get(key).get(key_sub)));

            }

        }

        System.out.println("Test: " + database.get(File_Interface.data_category.server_specification).get("Host")[0]);



/*
        Node node = new Node("0", database.get(File_Interface.data_category.node_specification).get("0"),
                database.get(File_Interface.data_category.server_specification).get("Host"));


        MAP_Protocol.server_establisher(node);
        MAP_Protocol.MAP_protocol_executor(node, database.get(File_Interface.data_category.node_neighbors).get("0"),
                database.get(File_Interface.data_category.node_specification));

*/
/*
        Node node = new Node( node_ID, database.get(File_Interface.data_category.node_specification).get(node_ID),
                database.get(File_Interface.data_category.server_specification).get("Host"));
*/
        HashMap<String, String[]> neighbors_information = new HashMap<>();

        for(String neighbor : database.get(File_Interface.data_category.node_neighbors).get(node_ID))
        {
            neighbors_information.put(neighbor, database.get(File_Interface.data_category.node_specification).get(neighbor).clone());
        }
        for(String key:neighbors_information.keySet()){
            System.out.println(key + " : " + Arrays.toString(neighbors_information.get(key)));
        }
        Node node = new Node( node_ID, database.get(File_Interface.data_category.node_specification).get(node_ID),
                database.get(File_Interface.data_category.server_specification).get("Host"), neighbors_information);

        if(status_cmd.equals("a")) node.setNode_status(Node.status.active);

        MAP_Protocol.server_establisher(node);

/*
        while(node.getMessage_sent() +1 < node.getMaxNumber())
        MAP_Protocol.MAP_protocol_executor(node, database.get(File_Interface.data_category.node_neighbors).get(node_ID),
                database.get(File_Interface.data_category.node_specification));
*/
        if(node.getNid().equals("0")) {
            //CL_snapshot.global_snapshot(node, CL_snapshot.roles.initiator);
            CL_snapshot initiator = new CL_snapshot(node, CL_snapshot.roles.initiator);
            Thread Global_snapshot_initiator = new Thread(initiator);
            Global_snapshot_initiator.start();
        }
        //else {
            CL_snapshot respondent = new CL_snapshot(node, CL_snapshot.roles.respondent);
            Thread Global_snapshot_respondent = new Thread(respondent);
            Global_snapshot_respondent.start();
        //}


    }

    public static void system_connection_checker(){

    }
}