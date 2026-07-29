package io.github.minerobber9000.loottablefixes.mixin;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import io.github.minerobber9000.loottablefixes.mixinsupport.IContainerUserTracker;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

@Mixin(RandomizableContainerBlockEntity.class)
public class RandomizableContainerBlockEntityMixin implements IContainerUserTracker {
    @Unique
    private final Set<ContainerUser> users = new HashSet<>();

    @Override
    public Set<ContainerUser> getUsers() {
        return users;
    }
}
