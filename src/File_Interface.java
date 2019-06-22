import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class File_Interface {
    public enum data_category{
        server_specification,
        node_specification,
        node_neighbors

    }

    public static HashMap<data_category, HashMap<String, String[]>> read_configuration(){
        String fileName = System.getProperty("user.dir")+"/configuration.txt";
        List<String> lines;
        
        HashMap<data_category, HashMap<String, String[]> > database = new HashMap<>();
        HashMap<String, String[]> server_spec_subset = new HashMap<>();
        HashMap<String, String[]> node_spec_subset = new HashMap<>();
        HashMap<String, String[]> node_neighbors_spec_subset = new HashMap<>();


        try
        {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);

            for(int i = 0 ; i < lines.size(); ++i){



                if(lines.get(i).matches("#.*") && lines.get(i).contains("global parameters")){
                    ++i;
                    server_spec_subset.put("Host", lines.get(i).split(" "));

                }
                if(lines.get(i).matches("^#.*")) ++i;
                else {
                    if (lines.get(i).contains("listenPort") || lines.get(i).contains("dc")) {
                        String[] temp_dc = lines.get(i).replaceFirst("#.*", "").split(" ");
                        node_spec_subset.put(temp_dc[0], Arrays.copyOfRange(temp_dc, 1, temp_dc.length));
                    }

                    if (lines.get(i).contains("neighbors")) {
                        String nodeID = lines.get(i).substring(lines.get(i).lastIndexOf(" ") + 1);
                        String[] temp_nb = lines.get(i).replaceFirst("#.*", "").split(" ");
                        node_neighbors_spec_subset.put(nodeID, temp_nb);
                    }
                }

            }

/*
            Iterator<String> itr = lines.iterator();
            ArrayList<String[]> temp = new ArrayList<>();
            String temp_e;

            while (itr.hasNext()){
                temp_e = itr.next();
                System.out.println(temp_e);
                temp.add(temp_e.split(" "));

            }

            for(String[] t: temp) {
                System.out.println("Target: " + Arrays.deepToString(t) + " Size: " + t.length);
            }

            for(String key: server_spec_subset.keySet()){
                System.out.println("Server_Sub_Data: " + key + " -> " + Arrays.toString(server_spec_subset.get(key)));
            }

            for(String key: client_spec_subset.keySet()){
                System.out.println("Client_Sub_Data: " + key + " -> " + Arrays.toString(client_spec_subset.get(key)));
            }

            for(String key: client_neighbors_spec_subset.keySet()){
                System.out.println("Neighbors_Sub_Data: " + key + " -> " + Arrays.toString(client_neighbors_spec_subset.get(key)));
            }
*/
            database.put(data_category.server_specification, server_spec_subset);
            database.put(data_category.node_specification, node_spec_subset);
            database.put(data_category.node_neighbors, node_neighbors_spec_subset);

/*
            for(data_category key: database.keySet()){
                System.out.println("Database: " + key );
                for(String key_sub: database.get(key).keySet()){
                    System.out.println("Sub_Data: " + key_sub + " -> "
                                        + Arrays.deepToString(database.get(key).get(key_sub)));
                }

            }
*/

        }

        catch (IOException e)
        {

            // do something
            e.printStackTrace();
        }

        return database;
    }


}