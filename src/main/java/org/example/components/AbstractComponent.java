package org.example.components;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;

public class AbstractComponent implements Component {

    protected boolean isOn = false;
    protected static final GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();

    public AbstractComponent(){};

    public AbstractComponent(boolean on){
        this.isOn = true;
    }

    @Override
    public boolean isOn() {
        return isOn;
    }

    @Override
    public void turnOff() {
        isOn = false;
    }

    @Override
    public void turnOn() {
        isOn = true;
    }

}
