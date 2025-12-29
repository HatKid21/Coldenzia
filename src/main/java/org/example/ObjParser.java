package org.example;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import org.example.utils.Obj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObjParser {

    private static final Logger LOGGER = Logger.getLogger(ObjParser.class.getName());

    public static Obj readObj(String path){
        String name = "";
        List<Point> vertices = new ArrayList<>();
        List<List<Integer>> faces = new ArrayList<>();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            while (line != null && !line.isEmpty()){
                if (line.startsWith("o")){
                    name = line.substring(2);
                }
                if (line.startsWith("v ")){
                   vertices.add(readVertex(line));
                }
                if (line.startsWith("f ")){
                    faces.add(readFace(line));
                }
                line = reader.readLine();
            }
        } catch (IOException e){
            LOGGER.log(Level.SEVERE,e.getMessage());
        }
        return new Obj(name,vertices,faces);
    }

    private static Point readVertex(String line){
        String[] coords = line.substring(2).split(" ");
        Point point = new Pos(Double.parseDouble(coords[0]),Double.parseDouble(coords[1]),Double.parseDouble(coords[2]));
        return point;
    }

    private static List<Integer> readFace(String line){
        String[] strIndexes = line.substring(2).split(" ");
        List<Integer> face = new ArrayList<>();
        for (String ind : strIndexes){
            if (ind.contains("/")){
                face.add(Integer.parseInt(ind.split("/")[0]));
            } else{
                face.add(Integer.parseInt(ind));
            }
        }
        return face;
    }

}
