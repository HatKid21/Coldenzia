package org.example.utils;

public enum LidarMode {

    WALL("Wall",1),
    SPHERE("Sphere",2),
    RANDOM("Random",3);

    private final int id;
    private final String name;

    LidarMode(String name,int id){
        this.id = id;
        this.name = name;
    };

    public int getId() {
        return id;
    }

    public static LidarMode getModeById(int id){
        LidarMode[] array = LidarMode.values();
        id %= array.length;
        return array[id];
    }

    @Override
    public String toString(){
        return name;
    }

}
