# HSProgression Documentation

HSProgression adds a progression system to islands with SuperiorSkyblock2.  
Dependencies: HSNodes, HSFishing, WildStacker, SuperiorSkyblock2.  
It adds four progression paths: /is mining/slayer/farming/fishing.  
Each progression path is the same concept, you need to farm a certain amount of one thing to unlock the next.

## Detecting Progression
Progression is detected in different ways.  
**Crops:** BlockGrowEvent and BlockSpreadEvent
**Nodes:** NodeMineEvent from HSNodes  
**"Fish":** FishCaughtEvent from HSFishing  
**Slaying:** EntityDeathEvent  
  
To reduce the likelihood of players progressing things out of order, we disallow them to place certain things before they are unlocked.  
For example, they can only place node types that they have unlocked, and the same goes for spawners and crops.  
Fishing is different, as the drops are not purely sellables, but can be custom items as well. So, we go by total items caught on the island.

## Config

Configurations for progression paths are defined in separate files.  
For things that are shared, look in config.yml. For specifics, look for the file named after the command.  
Example: `farming.yml` Is where you can find the configuration for the farming path.  
  
### Placeholders
Placeholders are present in every config file. There are comments showing where they can be used, and what they do.

## Util/API

The majority of access to the data within code is from `HSProgressionAPI`.
Anywhere configurations/data is accessed uses the API.

## Fishing Buffs
Fishing buffs are added to HSFishing through this plugin. When fishing on an island that has reached certain milestones, the player may receive buff(s) to their fishing.
Currently, this is all handled in the `PlayerFishHandlers` class, with a few extra global values (enums).

## Events

`IslandProgressedEvent`- called when an island is ready to be upgraded with money.  
`IslandNodeBreakEvent` - called when an island has been upgraded and unlocked the next tier in a progression line.  

## Data Storage

Current, all island data is stored in `islands.yml`, and is accessed/written with the API.

### Staff documentation
[This](https://docs.google.com/document/d/11qbgXMpqp4EFc7B_-gSXF8uJapG3yHoju-it_ez6UZk/edit?usp=sharing) documentation is less technical and more from a configuration/overview standpoint.