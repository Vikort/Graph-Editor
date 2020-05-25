package utils;

import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import view.DrawnArc;
import view.DrawnNode;
import view.GraphPane;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class DOMPars {
    public DOMPars(GraphPane graphPane, File file){
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            //Корневой элемент
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("Graph");
            rootElement.setAttribute("name",graphPane.getGraphController().getGraph().getName());
            document.appendChild(rootElement);
            Element nodes = document.createElement("Nodes");
            rootElement.appendChild(nodes);
            for (DrawnNode node: graphPane.getDrawnNodes()){
                Element nodeElement = document.createElement("Node");

                Element name = document.createElement("Name");
                name.appendChild(document.createTextNode(node.getSourceNode().getName()));

                Element identifier = document.createElement("Identifier");
                identifier.appendChild(document.createTextNode(String.valueOf(node.getSourceNode().getIdentifier())));

                Element centerX = document.createElement("CenterX");
                centerX.appendChild(document.createTextNode(String.valueOf(node.getShape().getCenterX())));

                Element centerY = document.createElement("CenterY");
                centerY.appendChild(document.createTextNode(String.valueOf(node.getShape().getCenterY())));

                Element nodeColor = document.createElement("Node_color");
                nodeColor.appendChild(document.createTextNode(String.valueOf(node.getShape().getFill())));

                nodeElement.appendChild(name);
                nodeElement.appendChild(identifier);
                nodeElement.appendChild(centerX);
                nodeElement.appendChild(centerY);
                nodeElement.appendChild(nodeColor);

                nodes.appendChild(nodeElement);
            }

            Element arcs = document.createElement("Arcs");
            rootElement.appendChild(arcs);

            for (DrawnArc arc: graphPane.getDrawnArcs()){
                Element arcElement = document.createElement("Arc");

                Element beginNode = document.createElement("Begin_node");

                Element identifier = document.createElement("Begin_identifier");
                identifier.appendChild(document.createTextNode(String.valueOf(arc.getBegin().getSourceNode().getIdentifier())));
                beginNode.appendChild(identifier);

                Element endNode = document.createElement("End_node");

                Element identifier2 = document.createElement("End_identifier");
                identifier2.appendChild(document.createTextNode(String.valueOf(arc.getEnd().getSourceNode().getIdentifier())));
                endNode.appendChild(identifier2);

                Element isDirect = document.createElement("isDirect");
                isDirect.appendChild(document.createTextNode(arc.getSourceArc().isDirect() ? "true" : "false"));

                Element arcColor = document.createElement("Arc_color");
                String color = String.valueOf(Color.BLACK);
                if(arc.getLine().getStroke() != Color.BLACK)
                    color = String.valueOf(arc.getLine().getStroke());
                else if (arc.getLoop().getStroke() != Color.BLACK){
                    color = String.valueOf(arc.getLoop().getStroke());
                }
                arcColor.appendChild(document.createTextNode(color));

                arcElement.appendChild(beginNode);
                arcElement.appendChild(endNode);
                arcElement.appendChild(isDirect);
                arcElement.appendChild(arcColor);

                arcs.appendChild(arcElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(file);

            transformer.transform(domSource, streamResult);
        }
        catch (ParserConfigurationException | TransformerException pce)
        {
            System.out.println(pce.getLocalizedMessage());
            pce.printStackTrace();
        }
    }
}
