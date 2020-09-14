package view;

import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import model.Node;

public class DrawnNode {
    private Node sourceNode;

    private static final String FONT_FAMILY = "Helvetica";
    public static final Bloom BLOOM = new Bloom(0);
    public static final double FONT_WIDTH = 0.1;
    public static final int CIRCLE_RADIUS = 10;

    private boolean isFocused = false;
    private Circle shape;
    private Text name;
    private Text identifier;

    public DrawnNode(Node node){
        this.sourceNode = node;
        name = new Text(node.getName());
        identifier = new Text(String.valueOf(node.getIdentifier()));

        shape = new Circle(CIRCLE_RADIUS);
        configureShape();
        configureIdentifier();
        configureName();
    }

    public Node getSourceNode() {
        return sourceNode;
    }

    public Circle getShape() {
        return shape;
    }

    public Text getIdentifier() {
        return identifier;
    }

    public Text getName() {
        return name;
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    private void configureShape(){
        shape.setStrokeType(StrokeType.OUTSIDE);
        shape.setStrokeWidth(3);
        shape.setStroke(Color.BLACK);
        shape.setFill(Color.WHITE);

        // Node moving
        shape.setOnMouseDragged(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                shape.setCenterX(e.getX());
                shape.setCenterY(e.getY());
            }
        });

        // Node lightning when mouse entered
        shape.setOnMouseEntered(e -> {
            shape.setEffect(BLOOM);
            identifier.setText(String.valueOf("[" + sourceNode.getIdentifier()) + "]");
            isFocused = true;
        });

        // Remove lightning when mouse exited
        shape.setOnMouseExited(e -> {
            shape.setEffect(null);
            identifier.setText(null);
            isFocused = false;
        });
    }

    // Name configs: binding, formatting
    private void configureName() {
        name.setFont(Font.font(FONT_FAMILY, FontPosture.ITALIC, 15));
        name.setText(sourceNode.getName());
        name.setFill(Color.BLACK);
        name.setStrokeWidth(FONT_WIDTH);

        name.xProperty().bind(shape.centerXProperty().add(3 * CIRCLE_RADIUS / 2));
        name.yProperty().bind(shape.centerYProperty().add(-CIRCLE_RADIUS));
    }

    // Identifier configs: binding, formatting
    private void configureIdentifier() {
        identifier.setFont(Font.font(FONT_FAMILY, FontPosture.ITALIC, 15));
        identifier.setText("[" + sourceNode.getIdentifier() + "]");
        identifier.setFill(Color.GRAY);
        identifier.setStrokeWidth(FONT_WIDTH);

        identifier.xProperty().bind(name.xProperty());
        identifier.yProperty().bind(name.yProperty().add(3 * CIRCLE_RADIUS));
    }
}
