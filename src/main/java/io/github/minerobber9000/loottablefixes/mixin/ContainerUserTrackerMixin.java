package io.github.minerobber9000.loottablefixes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.minerobber9000.loottablefixes.mixinsupport.IContainerUserTracker;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;

// These subclasses of RandomizableContainerBlockEntity define their own `startOpen` and `stopOpen` implementations,
// which don't call `super.(start/stop)Open` (why should they? it's not like that function's been defined in a
// meaningful way further up-chain...). So, mix into them and copy the logic from RandomizableContainerMixin.

@Mixin({ChestBlockEntity.class, BarrelBlockEntity.class, ShulkerBoxBlockEntity.class})
public abstract class ContainerUserTrackerMixin implements RandomizableContainer {
    @Inject(
        method = "startOpen(Lnet/minecraft/world/entity/ContainerUser;)V",
        at = @At("HEAD")
    )
    public void loot_table_fixes$$startOpen(ContainerUser user, CallbackInfo ci) {
        RandomizableContainer rc = this;
        if (rc.getLevel()==null || rc.getLevel().isClientSide()) return;
        IContainerUserTracker tracker = (IContainerUserTracker) this;
        tracker.getUsers().add(user);
    }

    @Inject(
        method = "stopOpen(Lnet/minecraft/world/entity/ContainerUser;)V",
        at = @At("HEAD")
    )
    public void loot_table_fixes$$stopOpen(ContainerUser user, CallbackInfo ci) {
        RandomizableContainer rc = this;
        if (rc.getLevel()==null || rc.getLevel().isClientSide()) return;
        IContainerUserTracker tracker = (IContainerUserTracker) this;
        tracker.getUsers().remove(user);
    }

}
