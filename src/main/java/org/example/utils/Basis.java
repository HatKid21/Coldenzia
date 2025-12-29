package org.example.utils;

import net.minestom.server.coordinate.Vec;

public class Basis {

    private static final Vec WORLD_Y = new Vec(0,1,0);
    private static final Vec WORLD_X = new Vec(1,0,0);

    private final Vec direction;
    private final Vec right;
    private final Vec up;


    public Basis(Vec direction){
        this.direction = direction.normalize();
        if (!direction.equals(WORLD_Y)){
            right =  direction.cross(WORLD_Y).normalize();
        } else{
            right = direction.cross(WORLD_X).normalize();
        }
        up = right.cross(direction).normalize();

    }

    public Vec getDirection() {
        return direction;
    }

    public Vec getRight() {
        return right;
    }

    public Vec getUp() {
        return up;
    }
}
