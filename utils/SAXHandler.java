package utils;

import controller.GraphController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import model.Arc;
import model.Graph;
import model.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import view.DrawnArc;
import view.DrawnNode;
import view.GraphPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SAXHandler extends DefaultHandler {
    private GraphPane graphPane = null;
    private String graphName;

    private ObservableList<DrawnArc> arcs = FXCollections.observableArrayList();
    private ObservableList<DrawnNode> nodes = FXCollections.observableArrayList();

    private StringBuilder data = null;

    private String name;
    private int identifier;
    private double centerX;
    private double centerY;
    private Color nodeColor;
    private Map<Integer, DrawnNode> identifiers = new HashMap<>();

    private int beginIdentifier;
    private int endIdentifier;
    private boolean isDirected;
    private Color arcColor;

    public GraphPane getGraphPane() {
        getResultGraphPane();
        return graphPane;
    }

    private void getResultGraphPane() {
        Graph graph = new Graph(graphName);

        graphPane.getDrawnNodes().addAll(nodes);
        graphPane.getDrawnArcs().addAll(arcs);

        for (DrawnNode drawnNode : nodes) {
            graph.getNodes().add(drawnNode.getSourceNode());
            graphPane.getPane().getChildren().addAll(
                    drawnNode.getShape(), drawnNode.getName(), drawnNode.getIdentifier()
            );
        }

        for (DrawnArc drawnArc : arcs) {
            graph.getArcs().add(drawnArc.getSourceArc());
            graphPane.getPane().getChildren().addAll(drawnArc.getLine(), drawnArc.getArrow());
        }

        for (DrawnNode drawnNode : nodes) {
            drawnNode.getShape().toFront();
        }

        GraphController graphController = new GraphController(graph);
        graphPane.setGraphController(graphController);
    }

    boolean bNode = false;
    boolean bName = false;
    boolean bIdentifier = false;
    boolean bCenterX = false;
    boolean bCenterY = false;
    boolean bNodeColor = false;
    boolean bArc = false;
    boolean bBeginIdentifier = false;
    boolean bEndIdentifier = false;
    boolean bisDirect = false;
    boolean bArcColor = false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (qName.equalsIgnoreCase("Graph")) {
            // create a new Employee and put it in Map
            // initialize Employee object
            graphPane = new GraphPane();
        } else if (qName.equalsIgnoreCase("Name")) {
            // set boolean values for fields, will be used in setting Employee variables
            bName = true;
        } else if (qName.equalsIgnoreCase("Identifier")) {
            bIdentifier = true;
        } else if (qName.equalsIgnoreCase("CenterX")) {
            bCenterX = true;
        } else if (qName.equalsIgnoreCase("CenterY")) {
            bCenterY = true;
        } else if (qName.equalsIgnoreCase("Begin_identifier")) {
            bBeginIdentifier = true;
        } else if (qName.equalsIgnoreCase("End_identifier")) {
            bEndIdentifier = true;
        } else if (qName.equalsIgnoreCase("isDirect")) {
            bisDirect = true;
        } else if (qName.equalsIgnoreCase("Node")) {
            bNode = true;
        } else if (qName.equalsIgnoreCase("Arc")) {
            bArc = true;
        } else if (qName.equalsIgnoreCase("Node_color")) {
            bNodeColor = true;
        } else if (qName.equalsIgnoreCase("Arc_color")) {
            bArcColor = true;
        }

        if (attributes.getLength() != 0 && qName.equals("Graph")) {
            graphName = attributes.getValue("name");
        }

        // create the data container
        data = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (bNode) {
            if(bName){
                name = data.toString();
                bName = false;
            }
            if(bIdentifier){
                identifier = Integer.parseInt(data.toString());
                bIdentifier = false;
            }
            if(bCenterX){
                centerX = Double.parseDouble(data.toString());
                bCenterX = false;
            }
            if(bCenterY){
                centerY = Double.parseDouble(data.toString());
                bCenterY = false;
            }
            if(bNodeColor){
                nodeColor = Color.web(data.toString());
                bNodeColor = false;
            }
        }
        if (bArc) {
            if(bBeginIdentifier){
                beginIdentifier = Integer.parseInt(data.toString());
                bBeginIdentifier = false;
            }
            if(bEndIdentifier){
                endIdentifier = Integer.parseInt(data.toString());
                bEndIdentifier = false;
            }
            if(bisDirect){
                isDirected = data.toString().equals("true");
                bisDirect = false;
            }
            if(bArcColor){
                arcColor = data.toString().equals("null")?Color.BLACK:Color.web(data.toString());
                bArcColor = false;
            }
        }

        if (qName.equalsIgnoreCase("Node")) {
            // add Employee object to list
            DrawnNode drawnNode = new DrawnNode(new Node(name));
            drawnNode.getShape().setCenterX(centerX);
            drawnNode.getShape().setCenterY(centerY);
            drawnNode.getShape().setFill(nodeColor);
            nodes.add(drawnNode);
            identifiers.put(identifier,drawnNode);

            bNode = false;
        }

        if (qName.equalsIgnoreCase("Arc")) {
            // add Employee object to list
            DrawnArc drawnArc = new DrawnArc(
                    new Arc(
                            identifiers.get(beginIdentifier).getSourceNode(),
                            identifiers.get(endIdentifier).getSourceNode(),
                            isDirected
                            ),
                    identifiers.get(beginIdentifier),
                    identifiers.get(endIdentifier)
            );
            drawnArc.getLine().setStroke(arcColor);
            arcs.add(drawnArc);

            bArc = false;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        data.append(new String(ch, start, length));
    }
}
