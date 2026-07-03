package io.github.minerobber9000.loottablefixes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootParams;

@Mixin(AdvancementRewards.class)
public class AdvancementRewardsMixin {
    @Redirect(method = "grant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootParams$Builder;create(Lnet/minecraft/util/context/ContextKeySet;)Lnet/minecraft/world/level/storage/loot/LootParams;"))
    public LootParams create(LootParams.Builder object, final ContextKeySet contextKeySet, ServerPlayer player) {
        return object.withLuck(player.getLuck()).create(contextKeySet);
    }
}
