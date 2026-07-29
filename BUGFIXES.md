# Bug Fixes

The Minecraft bugs (listed by Mojira number `MC-XXXXXX`) that this mod fixes, how long those bugs have been reported, and how this mod fixes them.

## Legend

- "Created": When the bug report was created.
- "Updated": The last time the bug report was updated (Jira metadata). Serves as a "as of" date for the "Resolved" item.
- "Resolved": If/when the bug was resolved.
- "Versions": A range of Minecraft versions which the bug is reported to exist in.

## Bugs

### [MC-262347][]: "Block loot tables ignore Luck despite entity context being provided"
Created: 5/4/2023, 10:17:54 AM  
Updated: 6/17/2026, 10:18:13 AM  
Resolved: Unresolved  
Versions: 1.19.4 - present

#### Bug

Loot tables for blocks ignore all effects of the Luck attribute (`bonus_rolls`/`quality`). This is despite block loot tables providing the breaker of the block as the `"this"` entity.

#### Why does it happen?

Luck is passed into loot tables separately from any entity context (so, in actuality, providing the `"this"` entity has nothing to do with this bug, other than proving that luck affecting these loot tables was intended). Specifically, the code providing a `LootParams` or `LootParams.Builder` needs to call `withLuck`, passing in the player's luck. Code to do this exists in chests and other randomizable containers, but is missing from the `getDrops` method of the `Block` class.

#### How did I fix it?

I used a mixin to inject into the `getDrops` method. Outside of replacing the `return` statement with a call to `cir.setReturnValue`, it was a one-line fix:

```java
        if (breaker instanceof Player player) params = params.withLuck(player.getLuck());
```

[MC-262347]: https://bugs.mojang.com/browse/MC/issues/MC-262347

### [MC-110336][]: "Breaking a container before opening it bypasses quality check" / [MC-156705][]: "Player conditions for chest loot tables don't work when breaking the chest"
Created: 11/18/2016, 4:39:04 PM / 7/13/2019, 10:05:49 PM  
Updated: 4/26/2025, 8:21:32 AM / 4/26/2025, 11:11:54 AM  
Resolved: Unresolved / Unresolved  
Versions: 1.11 - present / 1.14.4-pre5 - present

#### Bug

When a chest (or other randomizable container) has a loot table specified, but hasn't generated that loot (i.e; before the chest is opened), breaking the chest generates drops without taking the player into account (luck attribute, entity conditions, etc).

#### Why does it happen?

Dropping the contents of a randomizable container happens outside of the context of actually breaking it, specifically in `LevelChunk#setBlockState` once it's determined that the new block state can't keep the old block state's entity (because what *was* a container is now air). This calls a function, `BlockEntity#preRemoveSideEffects`, and then removes the block entity. This function checks whether the block entity itself is a container, and if so, calls a utility function `Containers.dropContents`, passing in the level, position, and the block entity itself, cast to `Container`.

This utility function iterates over `container.getContainerSize`, calling a `dropItemStack` function for each item in the container. However, this would cause a problem if we did exactly what causes this bug: breaking a chest with loot that hasn't been generated yet. As a result, `RandomizableContainerBlockEntity` has a failsafe: calling any `Container` methods on it when the loot table hasn't been finalized will result in the loot table being generated *without* a player context (since in most cases, the player context isn't available).

#### How did I fix it?

I used a mixin to inject code into `playerWillDestroy`, a function called on the `Block` subclass before the game actually processes breaking the block. This code checks if the block entity at the position is a `RandomizableContainerBlockEntity`, and if so, resolves it with the player as context.

If I were working at Mojang, this could also be a simple fix, since `Block#playerWillDestroy` is used in the vanilla game to handle angering Piglins when breaking a chest; a similar case could be added for if the block has a `RandomizableContainerBlockEntity` and, if so, unpacking the loot table with the player as context. (Alternatively, this special-case could be added to `ServerPlayerGameMode#destroyBlock`, which has the BlockEntity anyways.)

[MC-110336]: https://bugs.mojang.com/browse/MC/issues/MC-110336
[MC-156705]: https://bugs.mojang.com/browse/MC/issues/MC-156705

### [MC-120523][]: "/loot items and "loot" reward for advancements doesn't get the luck of the player nor damage source context for the Looting enchant"
Created: 9/3/2017, 1:02:26 AM  
Updated: 4/26/2025, 8:50:42 AM  
Resolved: Unresolved  
Versions: 1.12.1 - present

#### Bug

Loot tables for advancement rewards ignore all effects of the Luck attribute (`bonus_rolls`/`quality`), as well as any looting enchantments on the player's weapon.

#### Why does it happen?

The same reason MC-262347 happened; the luck needs to be passed in separately (as does the weapon).

#### How did I fix it?

I used a mixin (¡qué sorpresa!) to add the player's luck to the `LootParams`. (I could have also added the player as `LootContextParams.ATTACKING_ENTITY` to add the ability to use Looting, but I actually kind of agree with Mojang on that not being supported.)

[MC-120523]: https://bugs.mojang.com/browse/MC/issues/MC-120523

### [MC-184348][]: "Luck attribute doesn't apply if loot table is changed while viewing container"
Created: 5/17/2020, 10:12:56 PM  
Updated: 11/12/2025, 3:23:38 PM  
Resolved: Unresolved  
Versions: 20w20b - Present

#### Bug

If a player opens a chest, and then the chest is given a `LootTable` via commands/some other method, the loot table resolves without the player's luck (and probably other attributes too).

#### Why does it happen?

It's the same root cause as the above bug where breaking a container didn't apply the player context; `RandomizableContainerBlockEntity` falls back to unpacking the loot table without a player if it needs to get items and the loot table isn't already unpacked. Chests, barrels and such do unpack the loot table with the player, but only once on open.

As one user on Mojira pointed out, one reason this bug hasn't been fixed in Vanilla is likely because it's unclear how the situation of multiple players having a chest open should be solved.

#### How did I fix it?

I made `RandomizableContainer` track whoever had the container open, and then if a player isn't provided to `unpackLootTable`, it resolves the player as follows:

- If there's only one player with the chest open, use them.
- If there's multiple players with the chest open, use one of the players with the most Luck.

[MC-184348]: https://bugs.mojang.com/browse/MC/issues/MC-184348

### [MC-212671][]: "Loot table tag entry with "expand": true does not apply functions"
Created: 1/23/2021, 7:23:51 PM  
Updated: 4/26/2025, 3:20:32 PM  
Resolved: Unresolved  
Versions: 21w03a - present

#### Bug

When a loot entry uses the `tag` type with `expand` set to `true`, the functions on the `tag` entry aren't copied to the individual `item` entries.

#### Why does it happen?

The default behavior of `LootPoolSingletonContainer` wraps the `createItemStack` method of the `LootPoolSingletonContainer` (the base class of all of the loot pool entry types) in a `LootPoolEntry` object which applies the loot functions to the item stack.

However, in the case of a `tag` entry (helpfully named `TagEntry`) with `expand` set to `true`, the subclass (named `TagEntry`) just returns the items from the tag, without applying the loot functions to them.

#### How did I fix it?

I used mixins and access wideners to replace the vanilla logic with a custom `LootPoolEntry` implementation which takes the tag entry and the item as parameters and properly applies the loot functions to the item.

[MC-212671]: https://bugs.mojang.com/browse/MC/issues/MC-212671