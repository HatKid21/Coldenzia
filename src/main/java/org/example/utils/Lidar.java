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
import java.util.Random;

public class Lidar {

    private static final long DOT_SPAWN_DELAY = 100;
    private static final double SPREAD_FACTOR = 0.03;
    private static final Vec DOT_SCALE = new Vec(0.05);
    private static final double STEP = 0.1;
    private static final int AMOUNT_OF_DOTS = 5000;

    private static final int HALF_WIDTH = 15;
    private static final int HALF_HEIGHT = 15;

    private static final int RADIUS = 25;
    private static final int ROTATION_PRECISION = 12;
    private static final double PI = Math.PI;

    private static final long RANDOM_DOT_SPAWN_DELAY = 10;

    private static final Random random = new Random();

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
        int numberOfDots = 150;
        int iter = 0;
        for (int i = 0; i < numberOfDots;i++){
            double dxFactor = random.nextInt(-HALF_WIDTH,HALF_WIDTH) * SPREAD_FACTOR;
            double dyFactor = random.nextInt(-HALF_HEIGHT,HALF_HEIGHT)*SPREAD_FACTOR;
            singleShot(dxFactor,dyFactor,TaskSchedule.millis(DOT_SPAWN_DELAY*iter));
            iter++;
        }
    }

    private void circleShot(){
       long iter = 0;
       singleShot(0,0,TaskSchedule.nextTick());
       for (int r = 1; r < RADIUS; r++){
           for (int i = 0; i < 2* ROTATION_PRECISION; i++) {
               double dxFactor = Math.cos(i * PI / ROTATION_PRECISION)*r*SPREAD_FACTOR;
               double dyFactor = Math.sin(i*PI / ROTATION_PRECISION)*r*SPREAD_FACTOR;
               singleShot(dxFactor,dyFactor, TaskSchedule.millis(DOT_SPAWN_DELAY*iter));
           }
           iter++;
       }
    }

    private void wallShot(){
        long iter = 0;
        for (int y = HALF_HEIGHT; y > -HALF_HEIGHT; y--){
            for (int x = -HALF_WIDTH; x < HALF_WIDTH; x++){
                double dxFactor = x*SPREAD_FACTOR;
                double dyFactor = y*SPREAD_FACTOR;
                singleShot(dxFactor,dyFactor,TaskSchedule.millis(DOT_SPAWN_DELAY*iter));
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

    private void singleShot(double dxFactor,double dyFactor,TaskSchedule delay){
        MinecraftServer.getSchedulerManager().scheduleTask(
                () ->{
                        Pos playerPos = player.getPosition().withPitch(0).withYaw(0);
                        playerPos = playerPos.add(new Pos(0,player.getEyeHeight(),0));
                        Basis basis = new Basis(player.getPosition().direction());
                        Vec offsetX = basis.getRight().mul(dxFactor);
                        Vec offsetY = basis.getUp().mul(dyFactor);
                        Vec offset = offsetX.add(offsetY);
                        Pos collisionPos = rayCast(playerPos,basis.getDirection().add(offset));
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
        meta.setBrightness(15,15);
        if (entities.size() > AMOUNT_OF_DOTS){
            entities.poll().remove();
        }
        entity.setInstance(player.getInstance(),pos);

    }

}
