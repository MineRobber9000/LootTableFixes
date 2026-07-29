package io.github.minerobber9000.loottablefixes.mixinsupport;

import java.util.Set;

import net.minecraft.world.entity.ContainerUser;

public interface IContainerUserTracker {
    Set<ContainerUser> getUsers();
}
