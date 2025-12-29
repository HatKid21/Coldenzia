package org.example;

import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.example.utils.Lidar;

public class CustomPlayer extends net.minestom.server.entity.Player {

    private final Lidar lidar;

    public CustomPlayer(PlayerConnection playerConnection, GameProfile gameProfile) {
        super(playerConnection, gameProfile);
        lidar = new Lidar(this);
    }

    public Lidar getLidar() {
        return lidar;
    }

}
