package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;

import java.util.ArrayList;
import java.util.List;

public class GraphController {
    private Graph graph;
    private IncidenceMatrix incidenceMatrix;
    private List<Path> chains;
    private Path minPath;
    private int distance;

    public GraphController(Graph graph){
        this.graph = graph;
        incidenceMatrix = new IncidenceMatrix(graph);
        chains = new ArrayList<>();
    }

    public Graph getGraph() {
        return graph;
    }

    public int getDistance() {
        return distance;
    }

    public ObservableList<Node> getNodes() { return graph.getNodes(); }

    public ObservableList<Arc> getArcs() { return graph.getArcs(); }

    public List<Path> getChains() {
        return chains;
    }

    public Path getMinPath() {
        return minPath;
    }

    public IncidenceMatrix getIncidenceMatrix() {
        return incidenceMatrix;
    }

    public void addArc(Arc arc){
        graph.getArcs().add(arc);
    }

    public void addNode(Node node){
        graph.getNodes().add(node);
    }

    public void removeNode(Node node) {
        graph.getNodes().remove(node);

        ObservableList<Arc> arcsToRemove = FXCollections.observableArrayList();

        for (Arc arc : graph.getArcs()) {
            if (arc.getBeginVertex().equals(node) || arc.getEndVertex().equals(node)) {
                arcsToRemove.add(arc);
            }
        }

        graph.getArcs().removeAll(arcsToRemove);
    }

    public void removeArc(Arc arc) {
        graph.getArcs().remove(arc);
    }


    //degree of node
    public int outDegreeOf(Node node){
        int degree = 0;

        for(Arc arc: graph.getArcs()){
            if(arc.getBeginVertex().equals(node))
                degree++;
            else if(arc.getEndVertex().equals(node) && !arc.isDirect())
                degree++;
        }

        return degree;
    }

    //degree of node
    public int inDegreeOf(Node node){
        int degree = 0;

        for(Arc arc: graph.getArcs()){
            if(arc.getEndVertex().equals(node) && arc.isDirect())
                degree++;
        }

        return degree;
    }

    //algorithms

    //check for graph complete
    public boolean isComplete(){
        if(graph.containsLoop()){
            return false;
        }
        for(Node node: graph.getNodes()){
            if(outDegreeOf(node) != graph.getNodes().size() - 1){
                return false;
            }
        }
        return true;
    }

    //make graph complete
    public void makeComplete(){
        List<Arc> arcsToDelete = new ArrayList<>();

        for (Node begin : graph.getNodes()) {
            for (Node end : graph.getNodes()) {
                if (begin.equals(end)) {
                    continue;
                }

                if (!graph.getArcs().contains(graph.getArc(begin,end))) {
                    if(graph.getArcs().contains(graph.getArc(end,begin))){
                        if(graph.getArc(end,begin).isDirect()){
                            graph.getArcs().add(new Arc(begin, end,true));
                        }
                    }else{
                        graph.getArcs().add(new Arc(begin, end,true));
                    }
                }
            }
        }

        for (Arc arc : graph.getArcs()) {
            if (arc.getBeginVertex().equals(arc.getEndVertex())) {
                arcsToDelete.add(arc);
            }
        }

        graph.getArcs().removeAll(arcsToDelete);
    }

    //find Euler path ( use with checkForEulerCycle() )
    public Path findEulerPath(Node node, Path path, int[][] matrix){
        for(Arc arc : graph.getArcs()){
            if(matrix[graph.getNodes().indexOf(node)][graph.getArcs().indexOf(arc)] != 0) {
                int i = graph.getNodes().indexOf(arc.getBeginVertex());
                int j = graph.getNodes().indexOf(arc.getEndVertex());
                int m = graph.getArcs().indexOf(arc);
                if(!arc.isDirect()){
                    matrix[i][m] = 0;
                    matrix[j][m] = 0;
                    if(arc.getBeginVertex().equals(node))
                        findEulerPath(arc.getEndVertex(), path, matrix);
                    else if(arc.getEndVertex().equals(node))
                        findEulerPath(arc.getBeginVertex(), path, matrix);
                }else if(arc.isDirect() && arc.getBeginVertex().equals(node)){
                    matrix[i][m] = 0;
                    matrix[j][m] = 0;
                    findEulerPath(arc.getEndVertex(), path, matrix);
                }
            }
        }
        path.getPath().add(node);
        return path;
    }
    //check for Euler cycle
    public boolean checkForEulerCycle(){
        for(Node node: graph.getNodes()){
            if((outDegreeOf(node)-inDegreeOf(node)) % 2 != 0) return false;
        }
        return true;
    }

    //find all paths between begin and end nodes
    private void DFSchain(Node begin, Node end, boolean[] color, Path path){
        path = new Path(path.getPath());
        path.getPath().add(begin);
        if(begin.equals(end))
            chains.add(path);

        for(Arc arc: graph.getArcs()){
            if(arc.getBeginVertex().equals(begin)){
                if(!color[graph.getArcs().indexOf(arc)]){
                    color[graph.getArcs().indexOf(arc)] = true;
                    DFSchain(arc.getEndVertex(),end,color,path);
                    color[graph.getArcs().indexOf(arc)] = false;
                }
            }
            if(arc.getEndVertex().equals(begin) && !arc.isDirect()){
                if(!color[graph.getArcs().indexOf(arc)]){
                    color[graph.getArcs().indexOf(arc)] = true;
                    DFSchain(arc.getBeginVertex(),end,color,path);
                    color[graph.getArcs().indexOf(arc)] = false;
                }
            }
        }
    }
    //find minPath and distance between begin and end
    public void findDistancePaths(Node begin, Node end){
        chains.clear();
        boolean[] color = new boolean[graph.getArcs().size()];
        DFSchain(begin,end,color,new Path());
        minPath = chains.get(0);
        for(Path path: chains){
            if(minPath.getPath().size()>path.getPath().size()){
                minPath = path;
            }
        }
        distance = minPath.getPath().size() - 1;
    }
}
