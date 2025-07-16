
import mods.champion.IAffix;
import mods.champion.AffixBuilder;
import crafttweaker.entity.IEntityLiving;
import crafttweaker.event.EntityLivingUpdateEvent;
import mods.champion.IChampionship;

val myAffix as AffixBuilder = AffixBuilder.createAffix("super_cool", "cc");
    myAffix.setOnUpdate(function(entity as IEntityLiving, cap as IChampionship, evt as EntityLivingUpdateEvent){
        print("it works!");
    });
myAffix.register();