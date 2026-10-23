package com.example.adventure.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

public class ShadowShardEntity extends HostileEntity {
    public ShadowShardEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }
}
