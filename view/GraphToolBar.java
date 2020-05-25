package view;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import utils.ImageUtil;

public class GraphToolBar {
    private static final String PRESSED_BUTTON_STYLE = "-fx-border-color: linear-gradient(#e29f9f 0%, #d98585 49%, #c86367 50%, #c84951 100%); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 5, 5, 5, 5; " +
            "-fx-background-insets: 1, 1, 1, 1; ";

    private GraphPane graphPane;

    private Button pointer;
    private Button addOrientedArc;
    private Button addUnorientedArc;
    private Button addNode;
    private Button color;
    private Button delete;

    private ToolBar toolBar;


    public GraphToolBar() {
        this.graphPane = null;

        pointer = new Button();
        addOrientedArc = new Button();
        addUnorientedArc = new Button();
        addNode = new Button();
        color = new Button();
        delete = new Button();

        toolBar = new ToolBar();
        configureToolBar();
    }

    public ToolBar getToolBar() {
        return toolBar;
    }

    private void configureToolBar() {
        pointer.setStyle(PRESSED_BUTTON_STYLE);

        pointer.setGraphic(ImageUtil.getImage("/icon/pointer.png"));
        addUnorientedArc.setGraphic(ImageUtil.getImage("/icon/unorientedArc.png"));
        addOrientedArc.setGraphic(ImageUtil.getImage("/icon/orientedArc.png"));
        addNode.setGraphic(ImageUtil.getImage("/icon/vertex.png"));
        color.setGraphic(ImageUtil.getImage("/icon/color.png"));
        delete.setGraphic(ImageUtil.getImage("/icon/delete.png"));

        toolBar.getItems().addAll(pointer,
                addOrientedArc,
                addUnorientedArc,
                addNode,
                color,
                delete
        );
    }


    public void updateSource(GraphPane graphPane) {
        graphPane.getPane().removeEventHandler(Event.ANY, anyEventToListenPaneActionTypePerforming);

        this.graphPane = graphPane;

        updateEventHandlers();
    }

    private void updateEventHandlers() {
        graphPane.getPane().addEventHandler(Event.ANY, anyEventToListenPaneActionTypePerforming);

        pointer.setOnAction(e -> {
            pointer.setStyle(PRESSED_BUTTON_STYLE);
            addUnorientedArc.setStyle(null);
            addOrientedArc.setStyle(null);
            addNode.setStyle(null);
            color.setStyle(null);
            delete.setStyle(null);
            graphPane.setActionType(GraphPane.ActionType.POINTER);
            graphPane.getPane().requestFocus();
        });

        addUnorientedArc.setOnAction(e -> {
            pointer.setStyle(null);
            addUnorientedArc.setStyle(PRESSED_BUTTON_STYLE);
            addOrientedArc.setStyle(null);
            addNode.setStyle(null);
            color.setStyle(null);
            delete.setStyle(null);
            graphPane.setActionType(GraphPane.ActionType.ADD_UNORIENTED_ARC);
            graphPane.getPane().requestFocus();
        });

        addOrientedArc.setOnAction(e -> {
            pointer.setStyle(null);
            addUnorientedArc.setStyle(null);
            addOrientedArc.setStyle(PRESSED_BUTTON_STYLE);
            addNode.setStyle(null);
            color.setStyle(null);
            delete.setStyle(null);
            graphPane.setActionType(GraphPane.ActionType.ADD_ORIENTED_ARC);
            graphPane.getPane().requestFocus();
        });

        addNode.setOnAction(e -> {
            pointer.setStyle(null);
            addUnorientedArc.setStyle(null);
            addOrientedArc.setStyle(null);
            addNode.setStyle(PRESSED_BUTTON_STYLE);
            color.setStyle(null);
            delete.setStyle(null);
            graphPane.setActionType(GraphPane.ActionType.ADD_NODE);
            graphPane.getPane().requestFocus();
        });

        color.setOnAction(e -> {
            pointer.setStyle(null);
            addUnorientedArc.setStyle(null);
            addOrientedArc.setStyle(null);
            addNode.setStyle(null);
            color.setStyle(PRESSED_BUTTON_STYLE);
            delete.setStyle(null);
            graphPane.setActionType(GraphPane.ActionType.COLOR);
            graphPane.getPane().requestFocus();
        });

        delete.setOnAction(e -> {
            pointer.setStyle(null);
            addUnorientedArc.setStyle(null);
            addOrientedArc.setStyle(null);
            addNode.setStyle(null);
            color.setStyle(null);
            delete.setStyle(PRESSED_BUTTON_STYLE);
            graphPane.setActionType(GraphPane.ActionType.DELETE);
            graphPane.getPane().requestFocus();
        });
    }

    private EventHandler<Event> anyEventToListenPaneActionTypePerforming = e -> {
        switch (graphPane.getActionType()) {
            case POINTER: {
                pointer.setStyle(PRESSED_BUTTON_STYLE);
                addUnorientedArc.setStyle(null);
                addOrientedArc.setStyle(null);
                addNode.setStyle(null);
                color.setStyle(null);
                delete.setStyle(null);
                break;
            }
            case ADD_UNORIENTED_ARC: {
                pointer.setStyle(null);
                addUnorientedArc.setStyle(PRESSED_BUTTON_STYLE);
                addOrientedArc.setStyle(null);
                addNode.setStyle(null);
                color.setStyle(null);
                delete.setStyle(null);
                break;
            }
            case ADD_ORIENTED_ARC: {
                pointer.setStyle(null);
                addUnorientedArc.setStyle(null);
                addOrientedArc.setStyle(PRESSED_BUTTON_STYLE);
                addNode.setStyle(null);
                color.setStyle(null);
                delete.setStyle(null);
                break;
            }
            case ADD_NODE: {
                pointer.setStyle(null);
                addUnorientedArc.setStyle(null);
                addOrientedArc.setStyle(null);
                addNode.setStyle(PRESSED_BUTTON_STYLE);
                color.setStyle(null);
                delete.setStyle(null);
                break;
            }
            case COLOR: {
                pointer.setStyle(null);
                addUnorientedArc.setStyle(null);
                addOrientedArc.setStyle(null);
                addNode.setStyle(null);
                color.setStyle(PRESSED_BUTTON_STYLE);
                delete.setStyle(null);
                break;
            }
            case DELETE: {
                pointer.setStyle(null);
                addUnorientedArc.setStyle(null);
                addOrientedArc.setStyle(null);
                addNode.setStyle(null);
                color.setStyle(null);
                delete.setStyle(PRESSED_BUTTON_STYLE);
                break;
            }
        }
    };
}
