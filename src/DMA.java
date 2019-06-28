import java.util.Arrays;
import java.util.HashMap;

public class DMA {

    public enum enquiry{
        Create_Database,
        Show_Database_Result,
        Configurate_System_Environment
    }

    public static void Database_Inquiry_Administrator(Node node, enquiry option){

        switch (option){
            case Create_Database:
                Database_creation(node);
                break;
            case Show_Database_Result:
                Show_Database_Result(node.getDatabase());
                break;
            case Configurate_System_Environment:
                Node_Setup(node);
                break;

        }


    }
    public static void Database_creation(Node node){
        HashMap<Node.data_category, HashMap<String, String[]>> database = File_Interface.read_configuration();
        node.setDatabase(database);
    }

    public static void Show_Database_Result( HashMap<Node.data_category, HashMap<String, String[]>> database){
        System.out.println("\n=================================");
        System.out.println("Database Testing in Main Process:");
        System.out.println("=================================");
        for(Node.data_category key: database.keySet()){
            System.out.println("\nDatabase: " + key );
            for(String key_sub: database.get(key).keySet()){
                System.out.println("Sub_Data: " + key_sub + " -> "
                        + Arrays.deepToString(database.get(key).get(key_sub)));
            }
        }
        System.out.println("Test: " + database.get(Node.data_category.server_specification).get("Host")[0]);
    }

    public static void Node_Setup(Node node){
        Node_System_Configuration_Retrieval(node);
        Node_System_Configuration(node);
    }

    public static void Node_System_Configuration_Retrieval(Node node){

        // complete_neighbor_information
        HashMap<String, String[]> neighbors_information = new HashMap<>();

        for(String neighbor : node.getDatabase().get(Node.data_category.node_neighbors).get(node.getNid()))
        {
            neighbors_information.put(neighbor,
                    node.getDatabase().get(Node.data_category.node_specification).get(neighbor).clone());
        }
        for(String key:neighbors_information.keySet()){
            System.out.println(key + " : " + Arrays.toString(neighbors_information.get(key)));
        }

        node.setNeighbors_information(neighbors_information);
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        node.setNode_config(node.getDatabase().get(Node.data_category.node_specification).get(node.getNid()));

        node.setSys_setup(node.getDatabase().get(Node.data_category.server_specification).get("Host"));
    }


    public static void Node_System_Configuration(Node node){
        node.setNode_ip(node.getNode_config()[0]);
        node.setPort(node.getNode_config()[1]);
        node.setlogical_time(Integer.parseInt(node.getSys_setup()[0]) );
        node.setNode_numbers( Integer.parseInt(node.getSys_setup()[0]) );
        node.setMinPerActive( Integer.parseInt(node.getSys_setup()[1]) );
        node.setMaxPerActive( Integer.parseInt(node.getSys_setup()[2]) );
        node.setMinSendDelay( Integer.parseInt(node.getSys_setup()[3]) );
        node.setSnapshotDelay( Integer.parseInt(node.getSys_setup()[4]) );
        node.setMaxNumber( Integer.parseInt(node.getSys_setup()[5]) );
        node.initialize_Global_snapshot(node.getNode_numbers());
        node.initialize_Expected_child_message_unit_decrease(node.getNeighbors_information().size());

    }


}
