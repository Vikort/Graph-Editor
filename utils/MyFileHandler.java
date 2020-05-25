package utils;

import model.Graph;
import org.xml.sax.SAXException;
import view.GraphPane;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MyFileHandler {
    private DOMPars domPars = null;
    private GraphPane graphPane = new GraphPane();

    public MyFileHandler(){}

    public void openFile(File file)
    {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            SAXHandler saxHandler = new SAXHandler();
            saxParser.parse(file, saxHandler);
            //Get Persons list
            graphPane = saxHandler.getGraphPane();
            //print person information
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFile(File file) {domPars = new DOMPars(graphPane,file);}

    public GraphPane getGraphPane() {
        return graphPane;
    }

    public void setGraphPane(GraphPane graphPane) {
        this.graphPane = graphPane;
    }
}
