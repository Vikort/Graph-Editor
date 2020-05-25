package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;
import utils.MyFileHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static view.DrawnNode.CIRCLE_RADIUS;


public class MainWindow {
    private MenuBar menuBar;
    private GraphTabBar graphTabBar;
    private GraphToolBar graphToolBar;
    private StatusBar statusBar;
    private Stage stage;

    private VBox root;

    public MainWindow(Stage stage) {
        menuBar = new MenuBar();
        configureMenu();
        graphToolBar = new GraphToolBar();
        statusBar = new StatusBar();
        graphTabBar = new GraphTabBar(graphToolBar, statusBar);
        this.stage = stage;
        root = new VBox();
        configureVBox();
    }

    public VBox getVBox() {
        return root;
    }

    private void configureVBox() {
        root.getChildren().addAll(
                menuBar,
                graphToolBar.getToolBar(),
                graphTabBar.getTabPane(),
                statusBar.getStatusBar()
        );
    }

    private void configureMenu(){
        Menu fileMenu = new Menu("File");
        Menu algorithmsMenu = new Menu("Algorithms");

        //files
        MenuItem newGraph = new MenuItem("New graph");
        MenuItem saveGraph = new MenuItem("Save graph");
        MenuItem openGraph = new MenuItem("Open graph");

        newGraph.setOnAction(newGraphEventHandler);
        saveGraph.setOnAction(saveGraphEventHandler);
        openGraph.setOnAction(openGraphEventHandler);

        //algorithms
        //MenuItem loops = new MenuItem("Create loop");
        MenuItem info = new MenuItem("Graph info");
        MenuItem makeComplete = new MenuItem("Make graph complete");
        MenuItem findEulerCycle = new MenuItem("Find Euler cycle");
        MenuItem findAllPaths = new MenuItem("Find all paths between two nodes");
        MenuItem findDistance = new MenuItem("Find distance between two nodes");

        info.setOnAction(infoEventHandler);
        makeComplete.setOnAction(makeCompleteEventHandler);
        findEulerCycle.setOnAction(eulerEventHandler);
        findAllPaths.setOnAction(allPathsEventHandler);
        findDistance.setOnAction(distanceEventHandler);

        fileMenu.getItems().addAll(newGraph,saveGraph,openGraph);
        algorithmsMenu.getItems().addAll(info,makeComplete,findEulerCycle,findAllPaths,findDistance);
        menuBar.getMenus().addAll(fileMenu,algorithmsMenu);
    }

    private Alert createEmptyDialog(javafx.scene.Node content, String title) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title);

        alert.getDialogPane().setContent(content);

        return alert;
    }

    // Creating of a new graph
    private EventHandler<ActionEvent> newGraphEventHandler = e -> {
        TextField name = new TextField();

        GridPane gridPane = new GridPane();
        gridPane.add(new Label("Graph name"), 0, 0);
        gridPane.add(name, 1, 0);
        GridPane.setMargin(name, new Insets(CIRCLE_RADIUS));

        Alert newGraphDialog = createEmptyDialog(gridPane, "New graph");

        ButtonType CREATE = new ButtonType("Create");
        newGraphDialog.getButtonTypes().add(CREATE);

        ((Button) newGraphDialog.getDialogPane().lookupButton(CREATE)).setOnAction(actionEvent -> {
            Graph newGraph = new Graph(name.getText());

            if (!isGraphAlreadyExist(newGraph.getName())) {
                graphTabBar.newTab(newGraph);
            } else {
                newGraphDialog.show();
            }
        });

        newGraphDialog.show();
    };

    // Saving of a graph
    private EventHandler<ActionEvent> saveGraphEventHandler = e -> {
        File selectedFile = hndlSaveFile(stage);

        if (selectedFile != null) {
            MyFileHandler fileHandler = new MyFileHandler();
            fileHandler.setGraphPane(graphTabBar.currentGraphPane());
            fileHandler.saveFile(selectedFile);
        }
    };

    // Opening of a graph
    private EventHandler<ActionEvent> openGraphEventHandler = e -> {
        File selectedFile = hndlOpenFile(this.stage);

        if (selectedFile != null) {
            MyFileHandler fileHandler = new MyFileHandler();
            fileHandler.openFile(selectedFile);
            GraphPane namedGraphPane = fileHandler.getGraphPane();

            if (!isGraphAlreadyExist(namedGraphPane.getGraphController().getGraph().getName())) {
                graphTabBar.newTab(namedGraphPane);
            }
        }
    };

    private EventHandler<ActionEvent> infoEventHandler = e -> {
        String graphName = graphTabBar.currentGraphPane().getGraphController().getGraph().getName();
        List<Node> nodes = graphTabBar.currentGraphPane().getGraphController().getNodes();
        List<Arc> arcs = graphTabBar.currentGraphPane().getGraphController().getArcs();
        Map <Node,Integer> degrees = new HashMap<>();
        for(Node node: nodes){
            degrees.put(node,graphTabBar.currentGraphPane().getGraphController().degreeOf(node));
        }
        IncidenceMatrix matrix = graphTabBar.currentGraphPane().getGraphController().getIncidenceMatrix();
        String isCompleted = graphTabBar.currentGraphPane().getGraphController().isComplete() ? "The graph is complete" : "The graph isn't complete";
        String result = graphName + "\nNodes:\n";
        for(Node node: nodes){
            result = result.concat(node + "\n");
        }
        result = result.concat("Arcs:\n");
        for(Arc arc: arcs){
            result = result.concat(arc + "\n");
        }
        result = result.concat("Degrees:\n");
        for(Node node: degrees.keySet()){
            result = result.concat(node + ": " + degrees.get(node) + "\n");
        }
        result = result.concat("Incidence matrix:\n" + matrix + isCompleted);
        Alert infoDialog = createEmptyDialog(new Label(result),"Graph info");

        ButtonType OK = new ButtonType("OK");
        infoDialog.getButtonTypes().add(OK);

        infoDialog.showAndWait();
    };

    // Making graph complete
    private EventHandler<ActionEvent> makeCompleteEventHandler = e -> {
        GraphPane currentGraphPane = graphTabBar.currentGraphPane();
        currentGraphPane.removeLoops();
        currentGraphPane.getGraphController().makeComplete();

        for (Arc arc : currentGraphPane.getGraphController().getArcs()) {
            DrawnArc newInverse = new DrawnArc(
                    new Arc(arc.getEndVertex(), arc.getBeginVertex(),
                            false),
                    new DrawnNode(arc.getEndVertex()),
                    new DrawnNode(arc.getBeginVertex())
            );

            if (currentGraphPane.getDrawnArcs().indexOf(newInverse) != -1) {
                DrawnArc inverseFound = currentGraphPane.getDrawnArcs()
                        .get(currentGraphPane.getDrawnArcs().indexOf(newInverse));

                currentGraphPane.getPane().getChildren()
                        .remove(inverseFound.getArrow()); // kaef

                continue;
            }

            DrawnNode newBegin = currentGraphPane.getDrawnNode(arc.getBeginVertex());
            DrawnNode newEnd = currentGraphPane.getDrawnNode(arc.getEndVertex());

            DrawnArc newPrime = new DrawnArc(
                    arc,
                    newBegin,
                    newEnd
            );

            if (currentGraphPane.getDrawnArcs().indexOf(newPrime) != -1) {
                continue;
            }

            currentGraphPane.getPane().getChildren().add(newPrime.getLine());
            currentGraphPane.getDrawnArcs().add(newPrime);
        }

        for (DrawnNode DrawnNode : currentGraphPane.getDrawnNodes()) {
            DrawnNode.getShape().toFront();
        }
    };

    private EventHandler<ActionEvent> eulerEventHandler = e -> {
        if(graphTabBar.currentGraphPane().getGraphController().checkForEulerCycle()){
            ArrayList<Path> paths = new ArrayList<>();
            for(Node node: graphTabBar.currentGraphPane().getGraphController().getNodes()){
                IncidenceMatrix matrix = new IncidenceMatrix(graphTabBar.currentGraphPane().getGraphController().getGraph());
                paths.add(graphTabBar.currentGraphPane().getGraphController().findEulerPath(node,new Path(),matrix));
            }
            String result = "Euler cycles: " + "\n";
            for(Path path: paths){
                result = result.concat(path + "\n");
            }
            Alert eulerDialog = createEmptyDialog(new Label(result), "Euler cycle");
            ButtonType OK = new ButtonType("OK");
            eulerDialog.getButtonTypes().add(OK);
            eulerDialog.showAndWait();
        }else{
            Alert eulerDialog = createEmptyDialog(new Label("There are no Euler cycle in the graph"), "Euler cycle");
            ButtonType OK = new ButtonType("OK");
            eulerDialog.getButtonTypes().add(OK);
            eulerDialog.showAndWait();
        }
    };

    private EventHandler<ActionEvent> allPathsEventHandler = e -> {
        ComboBox<String> firstNodeName = new ComboBox<>();
        ComboBox<String> secondNodeName = new ComboBox<>();

        for (DrawnNode drawnNode : graphTabBar.currentGraphPane().getDrawnNodes()) {
            firstNodeName.getItems().add(drawnNode.getSourceNode().toString());
            secondNodeName.getItems().add(drawnNode.getSourceNode().toString());
        }

        GridPane gridPane = new GridPane();
        gridPane.add(new Label("Source node:"), 0, 0);
        gridPane.add(new Label("Destination node:"), 1, 0);
        gridPane.add(firstNodeName, 0, 1);
        gridPane.add(secondNodeName, 1, 1);
        GridPane.setMargin(firstNodeName, new Insets(CIRCLE_RADIUS));
        GridPane.setMargin(secondNodeName, new Insets(CIRCLE_RADIUS));

        Alert distanceDialog = createEmptyDialog(gridPane, "Paths between two nodes");

        ButtonType GET = new ButtonType("Get");
        distanceDialog.getButtonTypes().add(GET);

        ((Button) distanceDialog.getDialogPane().lookupButton(GET)).setOnAction(actionEvent -> {
            Node begin = new Node();
            Node end = new Node();

            for (DrawnNode drawnNode : graphTabBar.currentGraphPane().getDrawnNodes()) {
                if (drawnNode.getSourceNode().toString().equals(
                        firstNodeName.getSelectionModel().getSelectedItem())) {
                    begin = drawnNode.getSourceNode();
                }

                if (drawnNode.getSourceNode().toString().equals(
                        secondNodeName.getSelectionModel().getSelectedItem())) {
                    end = drawnNode.getSourceNode();
                }
            }

            graphTabBar.currentGraphPane().getGraphController().findDistancePaths(begin,end);
            List<Path> paths = graphTabBar.currentGraphPane().getGraphController().getChains();

            Label distanceText = new Label();
            Path minPath = graphTabBar.currentGraphPane().getGraphController().getMinPath();
            String result = "Paths between " + begin + " and " + end + " is " + "\n";

            for(Path path: paths){
                result = result.concat(path + "\n");
            }
            result = result.concat("Min path is " + minPath);
            distanceText.setText(result);
            Alert distanceAsItIs = createEmptyDialog(distanceText, "Paths");
            distanceAsItIs.getButtonTypes().add(ButtonType.OK);

            distanceAsItIs.show();
        });

        distanceDialog.show();
    };

    // Distance between two specified nodes
    private EventHandler<ActionEvent> distanceEventHandler = e -> {
        ComboBox<String> firstNodeName = new ComboBox<>();
        ComboBox<String> secondNodeName = new ComboBox<>();

        for (DrawnNode drawnNode : graphTabBar.currentGraphPane().getDrawnNodes()) {
            firstNodeName.getItems().add(drawnNode.getSourceNode().toString());
            secondNodeName.getItems().add(drawnNode.getSourceNode().toString());
        }

        GridPane gridPane = new GridPane();
        gridPane.add(new Label("Source node:"), 0, 0);
        gridPane.add(new Label("Destination node:"), 1, 0);
        gridPane.add(firstNodeName, 0, 1);
        gridPane.add(secondNodeName, 1, 1);
        GridPane.setMargin(firstNodeName, new Insets(CIRCLE_RADIUS));
        GridPane.setMargin(secondNodeName, new Insets(CIRCLE_RADIUS));

        Alert distanceDialog = createEmptyDialog(gridPane, "Distance between two nodes");

        ButtonType GET = new ButtonType("Get");
        distanceDialog.getButtonTypes().add(GET);

        ((Button) distanceDialog.getDialogPane().lookupButton(GET)).setOnAction(actionEvent -> {
            Node begin = new Node();
            Node end = new Node();

            for (DrawnNode drawnNode : graphTabBar.currentGraphPane().getDrawnNodes()) {
                if (drawnNode.getSourceNode().toString().equals(
                        firstNodeName.getSelectionModel().getSelectedItem())) {
                    begin = drawnNode.getSourceNode();
                }

                if (drawnNode.getSourceNode().toString().equals(
                        secondNodeName.getSelectionModel().getSelectedItem())) {
                    end = drawnNode.getSourceNode();
                }
            }

            graphTabBar.currentGraphPane().getGraphController().findDistancePaths(begin,end);
            Integer distance = graphTabBar.currentGraphPane().getGraphController().getDistance();
            Path path = graphTabBar.currentGraphPane().getGraphController().getMinPath();

            Label distanceText = new Label();
            distanceText.setText("Distance between " + begin + " and " + end + " is " + distance + "\n" +
                    "Path is " + path);
            Alert distanceAsItIs = createEmptyDialog(distanceText, "Distance");
            distanceAsItIs.getButtonTypes().add(ButtonType.OK);

            distanceAsItIs.show();
        });

        distanceDialog.show();
    };

    private File hndlOpenFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();//Класс работы с диалогом выборки и сохранения
        fileChooser.setTitle("Open Document");//Заголовок диалога
        //fileChooser.setInitialDirectory(new File("./Tables"));
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Graphs", "*.graph");//Расширение
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showOpenDialog(stage);//Указываем текущую сцену CodeNote.mainStage
    }

    private File hndlSaveFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();//Класс работы с диалогом выборки и сохранения
        fileChooser.setTitle("Save Document");//Заголовок диалога
        //fileChooser.setInitialDirectory(new File(""));
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Graphs", "*.graph");//Расширение
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showSaveDialog(stage);//Указываем текущую сцену CodeNote.mainStage
    }

    private boolean isGraphAlreadyExist(String name) {
        for (Tab tab : graphTabBar.getManagingGraphs().keySet()) {
            if (tab.getText().equals(name)) {
                Alert error = createEmptyDialog(new Label("Such graph is already exists"), "Error");

                ButtonType OK = new ButtonType("OK");
                error.getButtonTypes().add(OK);

                error.showAndWait();

                return true;
            }
        }

        return false;
    }

}
