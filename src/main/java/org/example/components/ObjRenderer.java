package org.example.components;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.example.utils.Matrix;
import org.example.utils.Obj;
import org.example.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ObjRenderer extends AbstractComponent {

    private final List<Obj> objects;

    private static final double STEP = 2;
    private static final Particle LINE_PARTICLE = Particle.DUST.withColor(NamedTextColor.GREEN);
    private static final long RENDER_RATE_MS  = 800;

    private final Runnable renderLogic;
    private Task renderTask;

    private final AtomicInteger inc = new AtomicInteger(0);

    public ObjRenderer(Instance instance){
        objects = new ArrayList<>();

        renderLogic = () -> {
            int val = inc.getAndAdd(1);
            Matrix matrix = new Matrix(
                    new Vec(Math.cos(val),0,Math.sin(val)),
                    new Vec(0,1,0),
                    new Vec(-Math.sin(val),0,Math.cos(val))
            );
            for (Obj obj : objects){
                obj.transform(matrix);
                for (Pair edge : obj.getEdges()) {
                    Pos start = obj.getVertexByIndex(edge.first()).asPos().add(obj.getPos());
                    Pos end = obj.getVertexByIndex(edge.second()).asPos().add(obj.getPos());
                    double length = end.distance(start);
                    Vec dir = end.sub(start).asVec().normalize().mul(STEP);

                    int count = (int) (length / STEP);

                    for (int i = 0; i <= count; i++) {
                        Pos curPos = start.add(dir.mul(i));

                        ParticlePacket packet = new ParticlePacket(
                                LINE_PARTICLE,
                                curPos,
                                new Pos(0, 0, 0),
                                0,
                                1
                        );
                        instance.sendGroupedPacket(packet);
                    }
                }
            }

        };

    }

    @Override
    public void turnOff() {
        super.turnOff();
        if (renderTask != null && renderTask.isAlive()){
            renderTask.cancel();
            renderTask = null;
        }

    }

    @Override
    public void turnOn() {
        super.turnOn();
        renderTask = MinecraftServer.getSchedulerManager().scheduleTask(renderLogic, TaskSchedule.nextTick(),TaskSchedule.millis(RENDER_RATE_MS));
    }

    public void registerObj(Obj obj){
        objects.add(obj);
    }

    public void unregisterLine(Obj obj){
        objects.remove(obj);
    }

}
