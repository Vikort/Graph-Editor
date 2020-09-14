package view;

import controller.GraphController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import model.Arc;
import model.Node;

import java.util.ArrayList;
import java.util.List;

import static view.DrawnNode.CIRCLE_RADIUS;
import static view.Main.WINDOW_HEIGHT;
import static view.Main.WINDOW_WIDTH;

public class GraphPane {
    public enum ActionType{
        POINTER,
        ADD_UNORIENTED_ARC,
        ADD_ORIENTED_ARC,
        ADD_NODE,
        DELETE,
        COLOR
    }
    
    private ColorPicker colorPicker = new ColorPicker();

    private GraphController graphController;
    private ActionType actionType;

    private DrawnNode beginForArc;
    private DrawnNode endForArc;

    private ObservableList<DrawnNode> drawnNodes;
    private ObservableList<DrawnArc> drawnArcs;

    private boolean isNodesForArcSelected = false;

    private Pane pane;

    public GraphPane() {
        this.graphController = null;
        actionType = ActionType.POINTER;

        beginForArc = null;
        endForArc = null;

        drawnNodes = FXCollections.observableArrayList();
        drawnArcs = FXCollections.observableArrayList();

        pane = new Pane();
        configurePane();
    }

    public GraphPane(GraphController graphController) {
        this.graphController = graphController;
        actionType = ActionType.POINTER;

        beginForArc = null;
        endForArc = null;

        drawnNodes = FXCollections.observableArrayList();
        drawnArcs = FXCollections.observableArrayList();

        pane = new Pane();
        configurePane();
    }

    public Pane getPane() {
        return pane;
    }

    public GraphController getGraphController() {
        return graphController;
    }

    public void setGraphController(GraphController graphController) {
        this.graphController = graphController;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public ObservableList<DrawnNode> getDrawnNodes() {
        return drawnNodes;
    }

    public ObservableList<DrawnArc> getDrawnArcs() {
        return drawnArcs;
    }

    public DrawnNode getDrawnNode(Node node){
        for(DrawnNode drawnNode: drawnNodes){
            if(drawnNode.getSourceNode().equals(node))
                return drawnNode;
        }
        return new DrawnNode(node);
    }


    private void configurePane() {
        pane.setPrefSize(WINDOW_WIDTH, 4 * WINDOW_HEIGHT / 5);
        pane.setFocusTraversable(true);
        Rectangle clip = new Rectangle(pane.getWidth(), pane.getHeight());

        pane.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            clip.setWidth(newValue.getWidth());
            clip.setHeight(newValue.getHeight());
        });

        pane.setClip(clip);

        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, nodeAddingEventHandler);
        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, arcAddingEventHandler);
        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, nodeOrArcRemovingEventHandler);
        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, nodeOrArcColoringEventHandler);
        pane.addEventHandler(KeyEvent.KEY_PRESSED, nodeRenamingEventHandler);
        //pane.addEventHandler(KeyEvent.KEY_PRESSED, getNodeDegreeEventHandler);
    }

    private EventHandler<MouseEvent> nodeAddingEventHandler = e -> {
        if(e.getButton().equals(MouseButton.PRIMARY) && actionType == ActionType.ADD_NODE){
            Node node = new Node();
            graphController.addNode(node);

            DrawnNode shapeNode = new DrawnNode(node);
            shapeNode.getShape().setCenterX(e.getX());
            shapeNode.getShape().setCenterY(e.getY());

            drawnNodes.add(shapeNode);
            pane.getChildren().addAll(shapeNode.getIdentifier(),shapeNode.getName(),shapeNode.getShape());
        }
    };

    private EventHandler<MouseEvent> arcAddingEventHandler = e -> {
        if(e.getButton().equals(MouseButton.PRIMARY) && (actionType == ActionType.ADD_ORIENTED_ARC || actionType == ActionType.ADD_UNORIENTED_ARC)){
            for (DrawnNode drawnNode : drawnNodes) {
                if (drawnNode.isFocused()) {
                    if (!isNodesForArcSelected) {
                        beginForArc = drawnNode;
                        isNodesForArcSelected = true;
                        return;
                    } else {
                        endForArc = drawnNode;
                        isNodesForArcSelected = false;
                        break;
                    }
                }
            }

            if ((beginForArc == null)
                    || (endForArc == null)) {

                beginForArc = null;
                endForArc = null;
                isNodesForArcSelected = false;
                actionType = ActionType.POINTER;
                return;
            }

            if(actionType == ActionType.ADD_ORIENTED_ARC || actionType == ActionType.ADD_UNORIENTED_ARC){
                Arc arc;
                Arc inverseArc = new Arc(new Node(),new Node(),true);
                if(actionType == ActionType.ADD_ORIENTED_ARC){
                    arc = new Arc(beginForArc.getSourceNode(),endForArc.getSourceNode(),true);
                } else {
                    arc = new Arc(beginForArc.getSourceNode(),endForArc.getSourceNode(),false);
                    inverseArc = new Arc(endForArc.getSourceNode(),beginForArc.getSourceNode(),false);
                }

                if ((graphController.getArcs().contains(arc))
                        || (graphController.getArcs().contains(inverseArc))) {
                    isNodesForArcSelected = false;
                    beginForArc = null;
                    endForArc = null;
                    return;
                }

                graphController.addArc(arc);

                DrawnArc arcShape = new DrawnArc(arc,beginForArc,endForArc);
                drawnArcs.add(arcShape);
                pane.getChildren().addAll(arcShape.getLine(), arcShape.getArrow(), arcShape.getLoop());
                beginForArc.getShape().toFront();
                endForArc.getShape().toFront();

                beginForArc = null;
                endForArc = null;

            }
        }
    };

    // Removing the selected node with/or incident arcs from pane & graph with DELETE key pressed
    private EventHandler<MouseEvent> nodeOrArcRemovingEventHandler = e -> {
        if (e.getButton().equals(MouseButton.PRIMARY) && actionType == ActionType.DELETE){
            for (DrawnNode drawnNode : drawnNodes) {
                if (drawnNode.isFocused()) {
                    graphController.removeNode(drawnNode.getSourceNode());
                    drawnNodes.remove(drawnNode);
                    pane.getChildren().removeAll(
                            drawnNode.getShape(),
                            drawnNode.getName(),
                            drawnNode.getIdentifier()
                    );

                    ObservableList<DrawnArc> arcsToRemove = FXCollections.observableArrayList();

                    for (DrawnArc drawnArc : drawnArcs) {
                        if (drawnArc.getSourceArc().getBeginVertex().equals(drawnNode.getSourceNode())
                                || drawnArc.getSourceArc().getEndVertex().equals(drawnNode.getSourceNode())) {

                            arcsToRemove.add(drawnArc);
                        }
                    }

                    drawnArcs.removeAll(arcsToRemove);

                    for (DrawnArc drawnArc : arcsToRemove) {
                        pane.getChildren().removeAll(drawnArc.getLine(), drawnArc.getArrow());
                    }
                    return;
                }
            }

            for (DrawnArc drawnArc : drawnArcs) {
                if (drawnArc.isFocused()) {
                    if (!drawnArc.getSourceArc().isDirect()){
                        graphController.removeArc(new Arc(drawnArc.getSourceArc().getEndVertex(),
                                drawnArc.getSourceArc().getBeginVertex(),drawnArc.isFocused()));
                    }

                    graphController.removeArc(drawnArc.getSourceArc());
                    drawnArcs.remove(drawnArc);
                    pane.getChildren().removeAll(drawnArc.getLine(), drawnArc.getArrow(), drawnArc.getLoop());
                    return;
                }
            }
        }
    };

    // Coloring of a node or an arc in focus with Color actionType
    private EventHandler<MouseEvent> nodeOrArcColoringEventHandler = e -> {
        if(e.getButton().equals(MouseButton.PRIMARY) && actionType == ActionType.COLOR){
            for (DrawnNode drawnNode : drawnNodes) {
                if (drawnNode.isFocused()) {
                    colorPicker.setOnAction(actionEvent -> {
                        drawnNode.getShape().setFill(colorPicker.getValue());
                    });

                    Alert colorChooseDialog = createEmptyDialog(colorPicker, "Color choosing");
                    colorChooseDialog.getButtonTypes().add(ButtonType.APPLY);
                    colorChooseDialog.show();
                    break;
                }
            }

            for (DrawnArc drawnArc : drawnArcs) {
                if (drawnArc.isFocused()) {
                    colorPicker.setOnAction(actionEvent -> {
                        drawnArc.getLine().setStroke(colorPicker.getValue());
                        drawnArc.getLoop().setStroke(colorPicker.getValue());
                        drawnArc.getArrow().setStroke(colorPicker.getValue());
                        drawnArc.getArrow().setFill(colorPicker.getValue());
                    });

                    Alert colorChoose = createEmptyDialog(colorPicker, "Color choosing");
                    colorChoose.getButtonTypes().add(ButtonType.APPLY);
                    colorChoose.show();
                    break;
                }
            }
        }
        
    };

    // Renaming of a node in focus with I key pressed
    private EventHandler<KeyEvent> nodeRenamingEventHandler = e -> {
        if(actionType == ActionType.POINTER && e.getCode().equals(KeyCode.I)){
            for (DrawnNode drawnNode : drawnNodes) {
                if (drawnNode.isFocused()) {
                    TextField newName = new TextField();

                    GridPane gridPane = new GridPane();
                    gridPane.add(new Label("New name"), 0, 0);
                    gridPane.add(newName, 1, 0);
                    GridPane.setMargin(newName, new Insets(CIRCLE_RADIUS));

                    Alert renameDialog = createEmptyDialog(gridPane, "Node renaming");

                    ButtonType RENAME = new ButtonType("Rename");
                    renameDialog.getButtonTypes().add(RENAME);

                    ((Button) renameDialog.getDialogPane().lookupButton(RENAME)).setOnAction(actionEvent -> {
                        drawnNode.getSourceNode().setName(newName.getText());
                        drawnNode.setName(newName.getText());
                    });

                    renameDialog.show();
                    break;
                }
            }
        }
    };

    public void removeLoops() {
        if (graphController.getGraph().containsLoop()) {
            List<DrawnArc> DrawnArcsToRemove = new ArrayList<>();

            for (DrawnArc loop : drawnArcs) {
                if (loop.getSourceArc().getBeginVertex().equals(loop.getSourceArc().getEndVertex())) {
                    DrawnArcsToRemove.add(loop);
                    pane.getChildren().removeAll(loop.getArrow(), loop.getLine(), loop.getLoop());
                }
            }

            drawnArcs.removeAll(DrawnArcsToRemove);
        }
    }


    public void performKeyAction(KeyEvent event) {
        switch (actionType) {
            case POINTER: {
                if (event.getCode().equals(KeyCode.I)) {
                    nodeRenamingEventHandler.handle(event);
                }
            }
        }
    }

    private Alert createEmptyDialog(javafx.scene.Node content, String title) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title);

        alert.getDialogPane().setContent(content);

        return alert;
    }

    public DrawnNode focusedNode() {
        for (DrawnNode drawnNode : drawnNodes) {
            if (drawnNode.isFocused()) {
                return drawnNode;
            }
        }

        return null;
    }
}
