package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private String name;

    private ObservableList<Node> nodes;
    private ObservableList<Arc> arcs;

    public Graph(String name){
        this.name = name;
        nodes = FXCollections.observableList(new ArrayList<>());
        arcs = FXCollections.observableList(new ArrayList<>());
    }

    public Graph(){
        this("");
    }



    public ObservableList<Arc> getArcs() {
        return arcs;
    }

    public ObservableList<Node> getNodes() {
        return nodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Arc getArc(Node begin, Node end){
        for(Arc arc: arcs){
            if(arc.getBeginVertex().equals(begin) && arc.getEndVertex().equals(end))
                return arc;
        }
        return null;
    }

    public Node getNode(String name){
        for(Node node: nodes){
            if(node.getName().equals(name))
                return node;
        }
        return null;
    }

    public List<Arc> loops() {
        List<Arc> loops = new ArrayList<>();

        for (Arc arc : arcs) {
            if (arc.getBeginVertex().equals(arc.getEndVertex())) {
                loops.add(arc);
            }
        }

        return loops;
    }

    public boolean containsLoop(){
        for(Arc arc: arcs){
            if(arc.getBeginVertex().equals(arc.getEndVertex()))
                return true;
        }
        return false;
    }
}
