package org.example.utils;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayDeque;
import java.util.Queue;

public class Lidar {

    private static final long DOT_SPAWN_DELAY = 100;
    private static final double SPREAD_FACTOR = 0.03;
    private static final Vec DOT_SCALE = new Vec(0.05);
    private static final double STEP = 0.1;
    private static final int AMOUNT_OF_DOTS = 500;

    private final Queue<Entity> entities = new ArrayDeque<>();

    private LidarMode currentMode;

    private boolean modeChangeState = false;

    private final Player player;

    public Lidar(Player player){
        this.player = player;
        currentMode = LidarMode.WALL;
    }

    public void shot(){
        switch (currentMode){
            case LidarMode.WALL -> wallShot();
            case LidarMode.CIRCLE -> circleShot();
            case LidarMode.RANDOM -> randomShot();
            default -> wallShot();
        }
    }

    private void randomShot(){
        player.sendMessage("In progress!");
    }

    private void circleShot(){
       Basis basis = new Basis(player.getPosition().direction());
       long iter = 0;
       int radius = 10;
       int precision = 12;
       double pi = Math.PI;
       for (int r = 0; r < radius; r++){
           for (int i = 0; i < 2* precision; i++) {
               Vec offsetX = basis.getRight().mul(Math.cos(i * pi / precision)).mul(r*SPREAD_FACTOR);
               Vec offsetY = basis.getUp().mul(Math.sin(i * pi / precision)).mul(r*SPREAD_FACTOR);
               Vec offset = offsetX.add(offsetY);
               singleShot(offset, TaskSchedule.millis(DOT_SPAWN_DELAY*iter));
               iter++;
           }
       }
    }

    private void wallShot(){
        Basis basis = new Basis(player.getPosition().direction());
        long iter = 0;
        for (int y = 5; y > -5; y--){
            for (int x = -5; x < 5; x++){
                Vec offset = basis.getRight().mul(x*SPREAD_FACTOR);
                offset = offset.add(basis.getUp().mul(y*SPREAD_FACTOR));
                singleShot(offset,TaskSchedule.millis(DOT_SPAWN_DELAY*iter));
            }
            iter++;
        }
    }

    public LidarMode getCurrentMode(){
        return currentMode;
    }

    public String getLidarModeString(){
        return currentMode.toString();
    }

    public void setModeChangeState(boolean modeChangeState){
        this.modeChangeState = modeChangeState;
    }

    public boolean isModeChangeState() {
        return modeChangeState;
    }

    public void switchModeRight(){
        currentMode = LidarMode.getModeById(currentMode.getId()+1);
    }

    public void switchModeLeft(){
        currentMode = LidarMode.getModeById(currentMode.getId()-1);
    }

    private Pos rayCast(Pos pos,Vec direction){
        direction = direction.normalize().mul(STEP);
        Instance instance = player.getInstance();
        Pos prevPos = pos;
        for (int i = 0; i < 1000; i++){
            if (!instance.getBlock(pos).equals(Block.AIR)){
                return prevPos;
            }
            prevPos = pos;
            pos = pos.add(direction);
        }
        return null;
    }

    private void singleShot(Vec dx,TaskSchedule delay){
        MinecraftServer.getSchedulerManager().scheduleTask(
                () ->{
                        Pos playerPos = player.getPosition().withPitch(0).withYaw(0);
                        playerPos = playerPos.add(new Pos(0,player.getEyeHeight(),0));
                        Vec direction = player.getPosition().direction();
                        Pos collisionPos = rayCast(playerPos,direction.add(dx));
                        if (collisionPos != null){
                            createDot(collisionPos);
                        }
                    },
                delay,
                TaskSchedule.stop()
        );
    }

    private void createDot(Pos pos){
        Entity entity = new Entity(EntityType.BLOCK_DISPLAY);
        BlockDisplayMeta meta = (BlockDisplayMeta)  entity.getEntityMeta();
        meta.setBlockState(Block.WHITE_CONCRETE);
        entity.setNoGravity(true);
        meta.setScale(DOT_SCALE);
        entities.add(entity);
        if (entities.size() > AMOUNT_OF_DOTS){
            entities.poll().remove();
        }
        entity.setInstance(player.getInstance(),pos);

    }

}
