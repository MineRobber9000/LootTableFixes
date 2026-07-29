package io.github.minerobber9000.loottablefixes.mixin;

import java.util.Iterator;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.minerobber9000.loottablefixes.mixinsupport.IContainerUserTracker;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;

@Mixin(RandomizableContainer.class)
public interface RandomizableContainerMixin extends RandomizableContainer, IContainerUserTracker {
    @Override
    default void startOpen(ContainerUser containerUser) {
        RandomizableContainer rc = this;
        if (rc.getLevel() != null && !rc.getLevel().isClientSide()) {
            getUsers().add(containerUser);
        }
    }

    @Override
    default void stopOpen(ContainerUser containerUser) {
        RandomizableContainer rc = this;
        if (rc.getLevel() != null && !rc.getLevel().isClientSide()) {
            getUsers().remove(containerUser);
        }
    }

    // Fixes MC-184348.
    @ModifyVariable(
        method = "unpackLootTable(Lnet/minecraft/world/entity/player/Player;)V",
        at = @At("HEAD"),
        argsOnly = true
    )
    default Player loot_table_fixes$$player_fallback(@Nullable Player player) {
        // if a player opened the container, leave it alone
        if (player != null) {
            return player;
        }

        // if we're client side, nothing's going to happen anyways
        RandomizableContainer container = this;
        if (container.getLevel()==null || container.getLevel().isClientSide()) {
            return player;
        }

        Iterator<ContainerUser> iter = getUsers().iterator();
        if (iter.hasNext()) { // any users of container?
            ContainerUser user = iter.next();
            if (!iter.hasNext()) { // single user of container, use them if they're a player
                if (user.getLivingEntity() instanceof Player _player) {
                    return _player;
                }
            } else { // multiple users of container, use the player with highest luck (whichever one comes first)
                float luck = Float.MIN_VALUE;
                if (user.getLivingEntity() instanceof Player _player) {
                    float playerLuck = _player.getLuck();
                    if (playerLuck > luck) {
                        player = _player;
                        luck = playerLuck;
                    }
                }
                while (iter.hasNext()) {
                    user = iter.next();
                    if (user.getLivingEntity() instanceof Player _player) {
                        float playerLuck = _player.getLuck();
                        if (playerLuck > luck) {
                            player = _player;
                            luck = playerLuck;
                        }
                    }
                }
            }
        }

        return player;
    }

}
