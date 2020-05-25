package view;

import controller.GraphController;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import model.Node;

public class StatusBar {
    private static final String NODES_COUNT = "Nodes count: ";
    private static final String ARCS_COUNT = "Arcs count: ";
    private static final String NODE_DEGREE = "Node degree: ";

    private GraphController graphController;

    private ToolBar statusBar;
    private Label nodesCount;
    private Label arcsCount;
    private Label nodeDegree;


    public StatusBar() {
        this.graphController = null;

        statusBar = new ToolBar();
        configureStatusBar();
    }

    public ToolBar getStatusBar() {
        return statusBar;
    }

    public void updateSource(GraphController graphController) {
        removeListeners();
        this.graphController = graphController;
        updateLabels();
        addListeners();
    }


    private void configureStatusBar() {
        nodesCount = new Label(NODES_COUNT + 0);
        arcsCount = new Label(ARCS_COUNT + 0);
        nodeDegree = new Label("");


        statusBar.getItems().addAll(
                nodesCount,
                new Separator(),
                arcsCount,
                new Separator(),
                nodeDegree
        );
    }

    private void removeListeners() {
        try {
            graphController.getNodes().removeListener(nodesCountListener);
            graphController.getArcs().removeListener(arcsCountListener);
        } finally {
            return;
        }
    }

    private void addListeners() {
        graphController.getNodes().addListener(nodesCountListener);
        graphController.getArcs().addListener(arcsCountListener);
    }

    private void updateLabels() {
        nodesCount.setText(NODES_COUNT + String.valueOf(graphController.getNodes().size()));
        arcsCount.setText(ARCS_COUNT + String.valueOf(graphController.getArcs().size()));
        updateDegreeLabel(null);
    }
    public void updateDegreeLabel(Node node){
        if(node != null)
            nodeDegree.setText(NODE_DEGREE + graphController.degreeOf(node));
        else
            nodeDegree.setText("");
    }

    private ListChangeListener nodesCountListener = change -> {
        nodesCount.setText(NODES_COUNT + String.valueOf(graphController.getNodes().size()));
    };

    private ListChangeListener arcsCountListener = change -> {
        arcsCount.setText(ARCS_COUNT + String.valueOf(graphController.getArcs().size()));
    };
}
