package view;

import controller.GraphController;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import model.Graph;

import java.util.HashMap;
import java.util.Map;

import static view.Main.WINDOW_HEIGHT;
import static view.Main.WINDOW_WIDTH;

public class GraphTabBar {
    private static final int TAB_TITLE_WIDTH = 100;

    private TabPane tabPane;
    private Map<Tab, GraphPane> managingGraphs;

    private StatusBar statusBar;
    private GraphToolBar graphToolBar;


    public GraphTabBar(GraphToolBar graphToolBar, StatusBar statusBar) {
        tabPane = new TabPane();
        managingGraphs = new HashMap<>();

        this.graphToolBar = graphToolBar;
        this.statusBar = statusBar;

        configureTabPane();
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public Map<Tab, GraphPane> getManagingGraphs() {
        return managingGraphs;
    }

    /*
     *      Configs
     */

    private void configureTabPane() {
        tabPane.setPrefSize(WINDOW_WIDTH, 4 * WINDOW_HEIGHT / 5);
        tabPane.setTabMaxWidth(TAB_TITLE_WIDTH);
        tabPane.setTabMinWidth(TAB_TITLE_WIDTH);

        tabPane.getSelectionModel().selectedItemProperty().addListener(e -> {
            try {
                graphToolBar.updateSource(currentGraphPane());
                statusBar.updateSource(currentGraphPane().getGraphController());
            } finally {
                return;
            }
        });

        tabPane.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            try {
                statusBar.updateDegreeLabel(currentGraphPane().focusedNode().getSourceNode());
            } finally {
                return;
            }
        });
        tabPane.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            try {
                if(e.getCode() == KeyCode.I)
                currentGraphPane().performKeyAction(e);
            } finally {
                return;
            }
        });
    }


    public void newTab(Graph newGraph) {
        Tab tab = new Tab(newGraph.getName());

        GraphController graphController = new GraphController(newGraph);
        GraphPane graphPane = new GraphPane(graphController);

        tab.setContent(graphPane.getPane());
        managingGraphs.put(tab, graphPane);

        tab.setOnClosed(e -> {
            managingGraphs.remove(tab);
        });

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void newTab(GraphPane graphPane) {
        Tab tab = new Tab(graphPane.getGraphController().getGraph().getName());

        tab.setContent(graphPane.getPane());
        managingGraphs.put(tab, graphPane);

        tab.setOnClosed(e -> {
            managingGraphs.remove(tab);
        });

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public GraphPane currentGraphPane() {
        return managingGraphs.get(tabPane.getSelectionModel().getSelectedItem());
    }

    public GraphPane getGraphPaneAtTab(String name) {
        for (Tab tab : managingGraphs.keySet()) {
            if (tab.getText().equals(name)) {
                return managingGraphs.get(tab);
            }
        }

        return null;
    }
}
