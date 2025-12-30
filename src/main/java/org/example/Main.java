package org.example;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.*;
import net.minestom.server.instance.*;
import net.minestom.server.instance.anvil.AnvilLoader;
import org.example.components.LidarComponent;
import org.example.components.ObjRenderer;
import org.example.utils.Obj;

import java.nio.file.Path;

public class Main {

    private static final Path WORLD_PATH = Path.of("data/worlds/Training_Ground");
    private static final Pos SPAWN_POS = new Pos(8.5,-60,8.5);
    
    static void main() {
        MinecraftServer server = MinecraftServer.init();
        MinecraftServer.getConnectionManager().setPlayerProvider(CustomPlayer::new);

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer container = instanceManager.createInstanceContainer();

        
        ChunkLoader loader = new AnvilLoader(WORLD_PATH);

        container.setChunkLoader(loader);

        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(AsyncPlayerConfigurationEvent.class, event ->{
            final Player player = event.getPlayer();
            event.setSpawningInstance(container);
            player.setRespawnPoint(SPAWN_POS);
        });

        eventHandler.addListener(PlayerSpawnEvent.class, event ->{
            final Player player = event.getPlayer();
            player.setGameMode(GameMode.CREATIVE);
                player.getInventory().addItemStack(ToolAssets.lidarTool);
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

