package model;

import java.util.Objects;

public class Arc {
    private Node beginVertex;
    private Node endVertex;
    private boolean isDirect;

    public Arc(Node begin, Node end, boolean isDirect){
        this.beginVertex = begin;
        this.endVertex = end;
        this.isDirect = isDirect;
    }

    public Node getBeginVertex() {
        return beginVertex;
    }

    public Node getEndVertex() {
        return endVertex;
    }

    public void setBeginVertex(Node beginVertex) {
        this.beginVertex = beginVertex;
    }

    public void setEndVertex(Node endVertex) {
        this.endVertex = endVertex;
    }

    public boolean isDirect(){
        return this.isDirect;
    }

    public void setDirected(boolean isDirect){
        this.isDirect = isDirect;
    }

    @Override
    public String toString() {
        return isDirect ?
                beginVertex + "->" + endVertex:
                beginVertex + "-" + endVertex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arc arcToCheck = (Arc) o;
        return  Objects.equals(beginVertex, arcToCheck.beginVertex) &&
                Objects.equals(endVertex, arcToCheck.endVertex);
    }
}
