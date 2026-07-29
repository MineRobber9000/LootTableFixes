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