package org.example.components;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;

public interface Component {

    GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();

    void turnOn();
    void turnOff();

    boolean isOn();

}

