
package graphdrawingchallenge;

import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import crossings.finder.Crossing;
import static crossings.finder.CrossingsFinder.BFAllCrossings;
import static crossings.finder.CrossingsFinder.smallestAngle;
import embedders.AngleEmbedder;
import embedders.SpringEmbedder;
import static graphdrawingchallenge.tools.CrossingsMimimizer.minimizeCrossings;
import static graphdrawingchallenge.tools.randomizer.randomize;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;


/**
 *
 * @author Panagiotis
 */
public class GraphDrawingChallenge {
    
    //mabye should put in different class
    public static boolean blockNewCrossings = true;
    
    private  GraphComponent graphComponent;
    private IGraph graph;
    
    private final JFrame frame;

    private FileParser parser;
    //private CrossingsFinder crossingsFinder;
    ArrayList<Crossing> crossings;
    
    private final SpringEmbedder springEmbedder;
    private final AngleEmbedder angleEmbedder;



    public GraphDrawingChallenge(){  
            
        graphComponent = createGraphComponent();
        graphInitializer(graphComponent);
        
        frame = frameCreator();
        
        parser = new FileParser(graph);
        //crossingsFinder = new CrossingsFinder();
        springEmbedder = new SpringEmbedder(graph);
        angleEmbedder = new AngleEmbedder(graph);
    }
    
    private GraphComponent createGraphComponent(){
        GraphComponent returnComponent = new GraphComponent();
        returnComponent.setFileIOEnabled(true);
        returnComponent.setInputMode(new GraphEditorInputMode());
        return returnComponent;
    }
    
    private JFrame frameCreator(){
        JToolBar toolbar = new JToolBar();
 
        
        final JPopupMenu file = new JPopupMenu();
        
      //  JButton button = new JButton("OPEN");
      //  button.addActionListener(new ActionListener() { 
        file.add(new JMenuItem(new AbstractAction("Open") {
            public void actionPerformed(ActionEvent e) { 
                try {parser.fileReader();}catch(IOException exc){}
                graphComponent.fitGraphBounds();
                //graphComponent.fitContent();
            } 
      }));
     //   });
      //  toolbar.add(button);
        
        file.add(new JMenuItem(new AbstractAction("Save") {
            public void actionPerformed(ActionEvent e) { 
                parser.fileSaver(graph);
                graphComponent.fitGraphBounds();
                //graphComponent.fitContent();
            } 
        }));
  
        final JButton fileButton = new JButton("File");
        fileButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                file.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        toolbar.add(fileButton);
    
        
        
        final JPopupMenu preembedders = new JPopupMenu();   
    //    JButton button6 = new JButton("RANDOMIZE");
     //   button6.addActionListener(new ActionListener() {
        preembedders.add(new JMenuItem(new AbstractAction("Randomize") {
            public void actionPerformed(ActionEvent e) { 
                randomize(graph);
                graphComponent.fitGraphBounds();
                //graphComponent.fitContent();
            } 
        }));
        
//        JButton button7 = new JButton("Crossings Min?");
//        button7.addActionListener(new ActionListener() { 
        preembedders.add(new JMenuItem(new AbstractAction("Crossings Min?") {
            public void actionPerformed(ActionEvent e) { 
                minimizeCrossings(graph);
                graphComponent.fitGraphBounds();
                //graphComponent.fitContent();
            } 
        }));
        
        preembedders.add(new JMenuItem(new AbstractAction("Spring") {
            public void actionPerformed(ActionEvent e) { 
                springEmbedder.motion(5000);
                graphComponent.fitGraphBounds();
            } 
        }));
        
        final JButton preembeddersButton = new JButton("Pre-Embedders");
        preembeddersButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                preembedders.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        toolbar.add(preembeddersButton);
        
        final JPopupMenu embedders = new JPopupMenu();   
        embedders.add(new JMenuItem(new AbstractAction("Angle Spring") { 
            public void actionPerformed(ActionEvent e) { 
                angleEmbedder.motion(50, "Not", "nodeFirst");
                graphComponent.fitGraphBounds();
            } 
        }));
        
        embedders.add(new JMenuItem(new AbstractAction("Angle Spring Rotate All") { 
            public void actionPerformed(ActionEvent e) { 
                angleEmbedder.motion(50 , "rotateAll", "nodeFirst");
                graphComponent.fitGraphBounds();
            } 
        }));
        
        embedders.add(new JMenuItem(new AbstractAction("Angle Spring Simultaneous") { 
            public void actionPerformed(ActionEvent e) { 
                angleEmbedder.motion(50, "rotateAll", "velocityFirst");
                graphComponent.fitGraphBounds();
            } 
        }));
        
        embedders.add(new JMenuItem(new AbstractAction("Angle Spring Move Smallest") { 
            public void actionPerformed(ActionEvent e) { 
                angleEmbedder.motion(50, "rotateAll", "nodeFirst", "Smallest");
                graphComponent.fitGraphBounds();
            } 
        }));
        
        final JButton embeddersButton = new JButton("Embedders");
        embeddersButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                embedders.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        toolbar.add(embeddersButton);
        
        //final JPopupMenu tools = new JPopupMenu();   
        JButton button2 = new JButton("Smallest Angle");
        button2.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
                //crossings = crossingsFinder.BFAllCrossings(graph
                crossings = BFAllCrossings(graph);
                double min = smallestAngle(crossings);
                String z = "The smallest angle is: ";
                z += min;
                z += "°";
                z += "\n";
               /* int i =1;
                for (Crossing curr :crossings){
                    z+= "angle no";
                    z+= i;
                    z+= ": ";
                    z+= curr.getSineOfAngle();
                    z+= "\n";
                    i++;
                }*/
                JOptionPane.showMessageDialog(frame, z);
            } 
        });
        toolbar.add(button2);
        
        JCheckBox blockNewCrossingsCheckbox = new JCheckBox("Block New Crossigns", true);
        blockNewCrossingsCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                if (blockNewCrossingsCheckbox.isSelected())
                    blockNewCrossings = true; 
                else
                    blockNewCrossings = false; 
            }
        });
        toolbar.add(blockNewCrossingsCheckbox);
        
        JFrame frame = new JFrame("Graph Competition");
        frame.setSize(500, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(toolbar, BorderLayout.NORTH);
        frame.add(graphComponent, BorderLayout.CENTER);
        
        return frame;
    }
    
    private void graphInitializer(GraphComponent graphComponent){
         
        graph = graphComponent.getGraph();
        
        ShapeNodeStyle orangeNodeStyle = new ShapeNodeStyle();
        orangeNodeStyle.setShape(ShapeNodeShape.ELLIPSE);
        orangeNodeStyle.setPaint(Colors.ORANGE);
        //orangeNodeStyle.setPen(Pen.getTransparent());
        
        PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
        edgeStyle.setSourceArrow(IArrow.NONE);
        edgeStyle.setTargetArrow(IArrow.NONE);
        
        graph.getNodeDefaults().setStyle(orangeNodeStyle);
        graph.getEdgeDefaults().setStyle(edgeStyle);
    }

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GraphDrawingChallenge::new);
    }
}