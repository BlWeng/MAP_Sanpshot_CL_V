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

        Node node = new Node( node_ID, database.get(File_Interface.data_category.node_specification).get(node_ID),
                database.get(File_Interface.data_category.server_specification).get("Host"));

        if(status_cmd.equals("a")) node.setNode_status(Node.status.active);

        MAP_Protocol.server_establisher(node);


        while(node.getMessage_sent() +1 < node.getMaxNumber())
        MAP_Protocol.MAP_protocol_executor(node, database.get(File_Interface.data_category.node_neighbors).get(node_ID),
                database.get(File_Interface.data_category.node_specification));

    }

    public static void system_connection_checker(){

    }
}