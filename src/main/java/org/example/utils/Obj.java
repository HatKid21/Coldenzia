package org.example.utils;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.Material;
import org.example.components.ObjRenderer;

import java.util.*;

public class Obj {

    private final String name;
    private final List<Point> baseVertices;
    private final List<List<Integer>> faces;
    private final Set<Pair> edges;

    private List<Point> transformedVertices;
    private boolean isTransformed = false;

    private Pos currentPos;

    private static final double SCALE = 5;
    private static final Material VERTEX_MATERIAL = Material.DIAMOND_BLOCK;
    private static final double VERTEX_SCALE = 0.01;
    

    public Obj(String name,List<Point> vertices, List<List<Integer>> faces){
        this.name = name;
        this.baseVertices = vertices;
        this.faces = faces;
        this.edges = initEdges();
    }
    
    private Set<Pair> initEdges(){
        Set<Pair> edges = new HashSet<>();
        for (List<Integer> face : faces){
            for (int i = 0; i < face.size();i++){
                Pair edge = new Pair(face.get(i) - 1,face.get((i+1)% face.size()) - 1);
                edges.add(edge);
            }
        }
        return edges;
    }

    public void spawn(ObjRenderer renderer, Instance instance, Pos pos){
        for (Point vertex : baseVertices) {
            Entity displayVertex = new Entity(EntityType.BLOCK_DISPLAY);
            BlockDisplayMeta blockDisplayMeta = (BlockDisplayMeta) displayVertex.getEntityMeta();
            displayVertex.setNoGravity(true);
            blockDisplayMeta.setBlockState(Objects.requireNonNull(VERTEX_MATERIAL.block()));
            blockDisplayMeta.setScale(new Vec(VERTEX_SCALE));
            Pos posWithOffset = pos.add(vertex.mul(SCALE));
            displayVertex.setInstance(instance, posWithOffset);
        }
        this.currentPos = pos;
        renderer.registerObj(this);
    }

    public String getName() {
        return name;
    }

    public List<List<Integer>> getFaces() {
        return faces;
    }

    public List<Point> getVertices() {
        if (isTransformed){
            return transformedVertices;
        }
        return baseVertices;
    }

    public void transform(Matrix transformation){
        List<Point> transformedVertices = new ArrayList<>();
        for (int i = 0; i < baseVertices.size();i++){
            transformedVertices.add(transformation.transform(baseVertices.get(i)));
        }
        isTransformed = true;
        this.transformedVertices = transformedVertices;
    }

    public void cancelTransformation(){
        transformedVertices = null;
        isTransformed = false;
    }

    public boolean isTransformed() {
        return isTransformed;
    }

    public Set<Pair> getEdges() {
        return edges;
    }

    public Point getVertexByIndex(int index){
        if (isTransformed){
            return transformedVertices.get(index).mul(SCALE);
        }
        return baseVertices.get(index).mul(SCALE);
    }

    public Pos getPos(){
        return currentPos;
    }

}
