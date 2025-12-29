package org.example;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.*;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import org.example.components.LidarComponent;
import org.example.components.ObjRenderer;
import org.example.utils.Lidar;
import org.example.utils.Obj;

public class Main {

    static void main() {
        MinecraftServer server = MinecraftServer.init();
        MinecraftServer.getConnectionManager().setPlayerProvider(CustomPlayer::new);

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer container = instanceManager.createInstanceContainer();

        container.setGenerator(unit -> unit.modifier().fillHeight(0,40, Block.GRASS_BLOCK));

        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(AsyncPlayerConfigurationEvent.class, event ->{
            final Player player = event.getPlayer();
            event.setSpawningInstance(container);
            player.setRespawnPoint(new Pos(0,42,0));
        });

        eventHandler.addListener(PlayerSpawnEvent.class, event ->{
            final Player player = event.getPlayer();
            player.setGameMode(GameMode.CREATIVE);
            player.getInventory().addItemStack(ToolsAssets.lidarTool);
        });

        ObjRenderer listener = new ObjRenderer(container);
        listener.turnOn();

        LidarComponent lidarComponent = new LidarComponent();
        lidarComponent.turnOn();

        eventHandler.addListener(PlayerSwapItemEvent.class, event ->{
            final Player player = event.getPlayer();
            Pos pos = player.getPosition().withPitch(0).withYaw(0);
            Instance instance = player.getInstance();
            Obj obj = ObjParser.readObj("data/Monkey.obj");
            obj.spawn(listener,instance,pos);
            player.sendMessage("Event");
        });

        container.setChunkSupplier(LightingChunk::new);

        server.start("0.0.0.0",25565);
    }
}

