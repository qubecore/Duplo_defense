# Duo Mode Dev Plan

## Scope

This document fixes the approved design for `Duplo TD Duo Mode` and records what is already implemented in code.

No new art, textures, or bespoke assets are introduced for Duo. The implementation only reuses existing models, UI, colors, and nameplates.

## Approved Rules

- One shared `Hollow`.
- One shared victory and one shared defeat.
- Map flow:
  - blue/green split start
  - one shared segment
  - split again into two fronts
  - final merged lane
  - `Hollow`
- Each player has `12` personal tower slots.
- Trap slots are split `4 / 4` between players.
- `3` super towers are shared.
- Both players can build, upgrade, use, and sell shared super towers.
- Gold from every killed enemy is split `50 / 50`.
- Shards are shared for the whole Duo world progression.
- Chests open once and reward both players.
- Reward draft after module waves:
  - show `4` options
  - first player picks `1`
  - second player picks `1` from the remaining pool
  - `1` becomes a team bonus
  - `1` burns
- Ally pick notifications must be visible in UI.
- If one Duo player disconnects, the match is automatically lost.

## Boss Pack v1

### Rift Twins

- Two linked bosses.
- Only one twin is damageable at a time.
- Vulnerability swaps every few seconds.
- When one twin dies, the survivor gains `x1.25` move speed.

### Node Arbiter

- Starts on one front.
- After death, reforms on the mirrored front.
- Repeats this for `3` extra lives.
- The `4th` life is final.
- If it already reached the final merged lane, it no longer switches sides and instead reforms a few blocks forward on the same final lane.

### Seal Master

- Applies random curse debuffs for the next `5` waves after death.
- If killed before `33%` path progress, applies `3` curses.
- If killed between `33%` and `66%`, applies `2` curses.
- If killed after `66%`, applies `1` curse.
- After crossing `66%` route progress, gains `+20%` move speed.

## Implementation Status

### Stage 1

Done:

- Duo blueprint file exists.
- Duo technical rules are documented.
- Runtime now understands Duo-specific map fields and slot metadata.

### Stage 2

Done in runtime:

- Duo match mode selection.
- Team assignment (`blue` / `green`).
- Shared Hollow and shared defeat flow.
- Shared world shard progression for Duo.
- Per-front slot ownership and shared super slots.
- 50/50 income split.

Blocked by prefab authoring:

- Final Duo `map.json` geometry.
- Final Duo `build_slots.json` positions and ownership tags.

### Stage 3

Done:

- Duo reward draft flow.
- Turn-based pick order.
- Team bonus and burned card handling.
- Ally-pick notification text in the reward page.
- Shared chests and split economy handling.

### Stage 4

Done:

- Rift Twins logic.
- Node Arbiter extra-life reform logic.
- Seal Master post-death curse logic.
- Duo boss pool routing for boss waves.
- Localization and enemy catalog entries for Duo bosses.

### Stage 5

Done in code:

- Disconnect auto-defeat.
- Duo economy command handling.
- Shared super-sell support.
- Seal Master curse penalties applied to the next five waves.

Still needed during map production:

- Real in-world Duo routes.
- Real slot placement pass on the prefab.
- Final tuning after live tests.

## Current Technical Limitation

The Duo runtime is implemented, but the actual Duo arena still depends on authored world data:

- `resources/bank_defense/map.json`
- `resources/bank_defense/build_slots.json`

Until those files contain the real Duo route and slot coordinates, Duo should be treated as an implementation-complete runtime with pending prefab/world authoring.

## Next Authoring Step

Use [DUO_MODE_WORLD_AUTHORING.md](/e:/Hytale%20mods/bank_defense_autogen/docs/DUO_MODE_WORLD_AUTHORING.md) while building the real Duo prefab and filling the world JSON.
