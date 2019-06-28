import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class File_Interface {

    public static HashMap<Node.data_category, HashMap<String, String[]>> read_configuration(){
        String fileName = System.getProperty("user.dir")+"/configuration.txt";
        List<String> lines;
        
        HashMap<Node.data_category, HashMap<String, String[]> > database = new HashMap<>();
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

            database.put(Node.data_category.server_specification, server_spec_subset);
            database.put(Node.data_category.node_specification, node_spec_subset);
            database.put(Node.data_category.node_neighbors, node_neighbors_spec_subset);

        }

        catch (IOException e)
        {

            e.printStackTrace();
        }

        return database;
    }

    public static void write_file(Node node) {
        try {
            //System.out.println("Enter write file.");
            //String home = System.getProperty("C:\\Users\\a62ba\\Desktop\\Spring_2019_CS6378_AOS_Undergoing\\AOS_Project2\\out\\production\\AOS_Project2");
            String cwd = System.getProperty("user.dir") + "\\configuration-" + node.getNid() + ".out";
            //String cwd = System.getProperty("user.home\\server01") + "\\record.txt";
            //String cwd = System.getProperty("\\home\\eng\\b\\bxw170030\\sever01") + "\\record.txt";
            //String cwd = System.getProperty("user.home") + "\\record.txt";
            File f = new File(cwd);
            if (!f.exists()) {
                f.createNewFile();
            }

            String output_message = "";

            for (int i : node.getGlobal_snapshot()) {
                output_message = output_message + i + " ";
            }

            if (node.getConsitency()) output_message = output_message + "; Global snapshot is consistent.";
            else output_message = output_message + "; Global snapshot is not consistent.";

            if(node.getMAP_protocol_termination()) output_message = output_message + " ; MAP protocol is terminated.";
            else output_message = output_message + " ; MAP protocol is not terminated.";

            PrintWriter out = new PrintWriter(new FileWriter(f, true));
            out.println(output_message);

            Thread.sleep(3);
            out.close();

            System.out.println("Exit file write.");

        }catch (Exception e) {e.printStackTrace();}

    }


}
