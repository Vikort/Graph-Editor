package model;

import javafx.collections.ListChangeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncidenceMatrix {
    private Graph graph;
    private Map<Node, List<Arc>> incidenceMatrix;
    private int[][]matrix;

    public IncidenceMatrix(Graph graph){
        this.graph = graph;
        incidenceMatrix = new HashMap<>();
        configureIncidenceList();
        configureMatrix();
    }

    private List<Arc> setIncidentArcsFor(Node node) {
        List<Arc> incidents = new ArrayList<>();

        for (Arc arc : graph.getArcs()) {
            if (arc.getBeginVertex().equals(node)) {
                incidents.add(arc);
            }else if(arc.getEndVertex().equals(node) && !arc.isDirect()){
                incidents.add(arc);
            }
        }

        //incidenceLists.put(node, incidents);

        return incidents;
    }

    public Map<Node, List<Arc>> getIncidenceMatrix() {
        return incidenceMatrix;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    private void configureIncidenceList() {
        for (Node node : graph.getNodes()) {
            incidenceMatrix.put(node, setIncidentArcsFor(node));
        }

        graph.getArcs().addListener((ListChangeListener) changeList -> {
            incidenceMatrix.clear();
            for (Node node : graph.getNodes()) {
                incidenceMatrix.put(node, setIncidentArcsFor(node));
            }
            configureMatrix();
        });

        graph.getNodes().addListener((ListChangeListener) changeList -> {
            incidenceMatrix.clear();
            for (Node node : graph.getNodes()) {
                incidenceMatrix.put(node, setIncidentArcsFor(node));
            }
            configureMatrix();
        });
    }

    private void configureMatrix(){
        int rows = graph.getNodes().size();
        int columns = graph.getArcs().size();
        matrix = new int[rows][columns];
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                matrix[i][j] = 0;
            }
        }

        for(Node node: incidenceMatrix.keySet()){
            for(Arc arc: incidenceMatrix.get(node)){
                int i = graph.getNodes().indexOf(arc.getBeginVertex());
                int j = graph.getNodes().indexOf(arc.getEndVertex());
                int m = graph.getArcs().indexOf(arc);
                if(arc.isDirect()){
                    matrix[i][m] = 1;
                    matrix[j][m] = -1;
                }else{
                    matrix[i][m] = 1;
                    matrix[j][m] = 1;
                }
            }
        }
    }


    @Override
    public String toString() {
        String toString = "";
        int rows = graph.getNodes().size();
        int columns = graph.getArcs().size();

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                toString = toString.concat(String.valueOf(matrix[i][j]) + " ");
            }
            toString = toString.concat("\n");
        }

        return toString;
    }
}
