# Consideration

These are bugs that I either won't fix or am still considering whether/how to fix.

## Legend

- "Created": When the bug report was created.
- "Updated": The last time the bug report was updated (Jira metadata). Serves as a "as of" date for the "Resolved" item.
- "Resolved": If/when the bug was resolved.
- "Versions": A range of Minecraft versions which the bug is reported to exist in.

## Bugs

### [MC-149589][]: "The wither boss will always drops a nether star regardless of its DeathLootTable data"
Created: 4/25/2019, 1:22:51 AM  
Updated: 6/30/2026, 3:23:44 AM  
Resolved: Unresolved  
Versions: 1.14 - present

#### Bug

The nether star dropped by a wither is hardcoded, and cannot be modified/changed by a loot table (indeed, the wither's default loot table is empty).

#### Why does it happen?

The reason why the nether star is hardcoded is because the game sets the nether star to have an "extended" life-time (10 minutes instead of the standard 5).

#### Why isn't it fixed?

I'm not really sure what would be the best way to fix it. Should I make *all* items dropped by a Wither have extended life-time? Should I make it so just nether stars have extended life-time?

[MC-149589]: https://bugs.mojang.com/browse/MC/issues/MC-149589

### [MC-184348][]: "Luck attribute doesn't apply if loot table is changed while viewing container"
Created: 5/17/2020, 10:12:56 PM  
Updated: 11/12/2025, 3:23:38 PM  
Resolved: Unresolved  
Versions: 20w20b - Present

#### Bug

If a player opens a chest, and then the chest is given a `LootTable` via commands/some other method, the loot table resolves without the player's luck (and probably other attributes too).

#### Why does it happen?

Probably the same reason breaking a container didn't apply the player context.

#### Why isn't it fixed?

I've been working away at this mod for hours and it was enough of a pain finding the source of the broken container bug, I'd quite like to sleep.

[MC-184348]: https://bugs.mojang.com/browse/MC/issues/MC-184348

### [MC-212671][]: "Loot table tag entry with "expand": true does not apply functions"
Created: 1/23/2021, 7:23:51 PM  
Updated: 4/26/2025, 3:20:32 PM  
Resolved: Unresolved  
Versions: 21w03a - present

#### Bug

When a loot entry uses the `tag` type with `expand` set to `true`, the functions on the `tag` entry aren't copied to the individual `item` entries.

#### Why does it happen?

I dunno, I want to sleep.

#### Why isn't it fixed?

Not sure why it happens yet.

[MC-212671]: https://bugs.mojang.com/browse/MC/issues/MC-212671