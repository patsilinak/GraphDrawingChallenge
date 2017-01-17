package graphdrawingchallenge;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.utils.IListEnumerable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JFileChooser;

/**
 *
 * @author Panagiotis
 */
public class FileParser {
    
    private IGraph graph;
    private File file;
    
    private int numberOfNodes;
    ArrayList<INode> nodeList;
   
    //Ακέραιος που περιγράφει σε τι φάσει διαβάσματος του αρχείου
    //είμαστε.
    private int parseStatus = 0;
    
    //test
    private int offset = 20;
           
    public FileParser(IGraph inGraph){
        
        graph = inGraph;        
        //nodeList = new ArrayList<INode>();
    }
    
    public void fileReader() throws IOException{
        
        if(!fileOpener())
            return;
        
        //FIXME is this enough ??
        nodeList = new ArrayList<INode>();

      /*  for(IEdge edge : graph.getEdges())
            graph.remove(edge);*/
      //FIXME-USE THIS: IGraph.clear() 
        while(graph.getEdges().size() != 0)
            graph.remove(graph.getEdges().last());  
      
        while(graph.getNodes().size() != 0)
            graph.remove(graph.getNodes().last());
        parseStatus = 0;
        
        
        try (Scanner scanner =  new Scanner(file)){
            while (scanner.hasNextLine()){
                addLine(scanner.nextLine());
            }
        }
    }
    
    private boolean fileOpener(){
        JFileChooser fc = new JFileChooser();
        int fileVal = fc.showOpenDialog(fc);
            
        if (fileVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            return true;
        }
        return false;
    }
    
    private void addLine(String line){
        
        if (line.charAt(0) == '#')
            return;
        
        Scanner scanner = new Scanner(line);
        scanner.useDelimiter(" ");
        
        switch (parseStatus){
            case 0: numberOfNodes = parseInt(line);
                parseStatus = 1;
                break;
            case 1: //FIXME shady stuff!!!
                INode tempNode = graph.createNode(
                        new PointD(parseInt(scanner.next())*offset, 
                                -1*parseInt(scanner.next())*offset));
                
                nodeList.add(tempNode);
                if (nodeList.size() == numberOfNodes)
                    parseStatus = 2;
                
                break;
            case 2:
                graph.createEdge(nodeList.get(parseInt(scanner.next())), 
                        nodeList.get(parseInt(scanner.next())));
        }
        
    }
    
    public void fileSaver(IGraph graph){
        JFileChooser chooser = new JFileChooser();
        //File saveFile;
        
        BufferedWriter buffer = null;
        FileWriter writer = null;
        
        int fileVal = chooser.showSaveDialog(chooser);
        
        try {
            writer = new FileWriter(chooser.getSelectedFile());
            buffer = new BufferedWriter(writer);
            buffer.write(graphStringCalculator(graph));
    
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (buffer != null)
                    buffer.close();
                if (writer != null)
                    writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
    
    private String graphStringCalculator(IGraph graph){
        String output = new String();
        IListEnumerable<INode> nodes = graph.getNodes();
        IListEnumerable<IEdge> edges = graph.getEdges();
        output += "# Lines starting with # are comments and ignored\r\n" + 
                  "# First value is the number of nodes (N)\r\n";
        output += nodes.size();
        output += "\r\n";
        output += "# Next N numbers describe the node locations.\r\n";
        
        //FIXME REMOVE OFFSETS!!
        //temp:
        ArrayList<INode> nodeList= new ArrayList<>();
        for(INode node : nodes){
            output += (int) node.getLayout().getCenter().x / offset;
            output += ' ';
            int sign = (node.getLayout().getCenter().y != 0 ? -1 : 1); //TEMP!!!
            output += sign * (int) node.getLayout().getCenter().y / offset;
            output += "\r\n";
            nodeList.add(node);
        }
        output += "# Remaining lines are the edges.\r\n" +
                  "# The first value is the source node index.\r\n" +
                  "# The second value is the target node index.";
        
        
        for(IEdge edge : edges){
            output +="\r\n";
            output += nodeList.indexOf(edge.getSourceNode());
            output += ' ';
            output += nodeList.indexOf(edge.getTargetNode());
        }
        
        return output;
    }   
}
