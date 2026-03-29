# Duo Mode World Authoring

## Geometry

Approved lane flow:

1. `Blue start lane`
2. `Green start lane`
3. `Shared segment`
4. `Blue split return`
5. `Green split return`
6. `Final merged lane`
7. `Hollow`

Use the approved blueprint as the source silhouette:

- [duplo_duo_mode_blueprint.svg](/e:/Hytale%20mods/bank_defense_autogen/docs/duplo_duo_mode_blueprint.svg)

## Required `map.json` Fields

These fields must be authored for Duo mode to boot on the real prefab:

- `duoPlayerStartBlue`
- `duoPlayerStartBlueYaw`
- `duoPlayerStartGreen`
- `duoPlayerStartGreenYaw`
- `duoSpawnPointBlue`
- `duoSpawnPointGreen`
- `duoBankCenter`
- `duoVaultPoint`
- `duoSealNodeBluePoint`
- `duoSealNodeGreenPoint`
- `duoRouteBlue`
- `duoRouteGreen`
- `duoChestSpawnPoints`

## Route Authoring Notes

- `duoRouteBlue` must encode:
  - blue start
  - shared segment
  - blue split return
  - final merged lane
- `duoRouteGreen` must encode:
  - green start
  - shared segment
  - green split return
  - final merged lane
- Both routes should end at the same `duoVaultPoint`.
- Keep the final merged stretch long enough for:
  - `Node Arbiter` forward reform
  - final boss focus fire
  - super tower payoff

## Required `build_slots.json` Metadata

Every Duo slot must be tagged with:

- `layout`
- `ownerTeam`
- `segment`

### Valid `layout`

- `duo`
- `both`
- `solo`

### Valid `ownerTeam`

- `blue`
- `green`
- `shared`

### Recommended `segment`

- `blue_start`
- `green_start`
- `shared_entry`
- `blue_return`
- `green_return`
- `final_shared`

## Slot Counts

Approved totals:

- `12` blue personal tower slots
- `12` green personal tower slots
- `4` blue trap slots
- `4` green trap slots
- `3` shared super slots

## Ownership Rules

- Blue personal towers:
  - `layout = duo`
  - `ownerTeam = blue`
- Green personal towers:
  - `layout = duo`
  - `ownerTeam = green`
- Shared super towers:
  - `layout = duo`
  - `ownerTeam = shared`
- Shared-in-solo legacy slots should stay `layout = solo` unless intentionally reused.

## Boss Authoring Notes

### Rift Twins

- Spawn one on the blue lane and one on the green lane.
- They should have enough spacing to make the vulnerability swap readable.

### Node Arbiter

- Early lane lengths should be mirrored.
- The final merged lane must have enough spare distance so forward reform feels meaningful.

### Seal Master

- `duoSealNodeBluePoint` and `duoSealNodeGreenPoint` should sit near each side's buildable defense cluster.
- Do not place seal-node points on the final merged lane.

## Asset Restriction

Do not draw new textures or new art for Duo world production in this pass.

Allowed:

- existing prefabs
- existing models
- existing materials
- recolor where needed

Not allowed:

- new painted boss textures
- new custom UI illustrations
- new decorative art pass
