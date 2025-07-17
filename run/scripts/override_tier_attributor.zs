#norun
import crafttweaker.entity.IEntityLiving;

mods.champion.Champions.addStage("my_stage", 3);
mods.champion.Champions.addStage("my_stage", 2, 0);

mods.champion.Champions.setRankAttributor(function(entity as IEntityLiving){
    return 1;
});