package me.fril.proxy;

import me.fril.common.init.RObjects;

/**
 * Created by Sub
 * on 17/09/2018.
 */
public class CommonProxy implements IProxy {

    @Override
    public void preInit() {

    }

    @Override
    public void init() {
        RObjects.advancements();
    }

    @Override
    public void postInit() {

    }
}