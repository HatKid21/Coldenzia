package org.example.utils;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public class Matrix {

    private final Vec[] matrix = new Vec[3];

    public Matrix(Vec row1, Vec row2, Vec row3){
        matrix[0] = row1;
        matrix[1] = row2;
        matrix[2] = row3;
    }

    public Point transform(Point point){
        return new Pos(matrix[0].dot(point.asVec()),matrix[1].dot(point.asVec()),matrix[2].dot(point.asVec()));
    }


}
