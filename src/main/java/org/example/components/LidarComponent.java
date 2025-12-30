package org.example.components;

import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerStartSneakingEvent;
import net.minestom.server.event.player.PlayerStopSneakingEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import org.example.CustomPlayer;
import org.example.ToolsAssets;
import org.example.utils.Lidar;

public class LidarComponent extends AbstractComponent {

    public LidarComponent(){
        eventHandler.addListener(PlayerUseItemEvent.class, event ->{
            if (isOn) {
                final Player player = event.getPlayer();
                if (event.getItemStack().equals(ToolsAssets.lidarTool)){
                    CustomPlayer customPlayer = (CustomPlayer) player;
                    customPlayer.getLidar().shot();
                }
            }
        });

        eventHandler.addListener(PlayerStartSneakingEvent.class, event ->{
            if (isOn){
                final Player player = event.getPlayer();
                if (player.getItemInMainHand().equals(ToolsAssets.lidarTool)){
                    CustomPlayer customPlayer = (CustomPlayer) player;
                    customPlayer.getLidar().setModeChangeState(true);
                }
            }
        });

        eventHandler.addListener(PlayerStopSneakingEvent.class, event ->{
            if (isOn){
                CustomPlayer player = (CustomPlayer) event.getPlayer();
                Lidar lidar = player.getLidar();
                if (lidar.isModeChangeState()){
                    lidar.setModeChangeState(false);
                }
            }
        });

        eventHandler.addListener(PlayerChangeHeldSlotEvent.class, event ->{
            if (isOn){
                CustomPlayer player = (CustomPlayer) event.getPlayer();
                Lidar lidar = player.getLidar();
                if (event.getItemInNewSlot().equals(ToolsAssets.lidarTool)){
                    player.sendMessage(lidar.getLidarModeString());
                }

                if (!event.getItemInOldSlot().equals(ToolsAssets.lidarTool)){
                    return;
                }

                if (!lidar.isModeChangeState()){
                    return;
                }

                byte oldSlot = event.getOldSlot();
                byte newSlot = event.getNewSlot();
                // < 0 -> right
                if (oldSlot - newSlot < 0){
                    lidar.switchModeRight();
                } else{ // > 0 -> left
                    lidar.switchModeLeft();
                }
                event.setCancelled(true);
            }
        });

    }

}
