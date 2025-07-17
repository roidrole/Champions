
import mods.champion.IAffix;
import mods.champion.AffixBuilder;
import crafttweaker.entity.IEntityLiving;
import mods.champion.IChampionship;
import mods.champion.events.LivingEvent;

val myAffix as AffixBuilder = AffixBuilder.createAffix("super_cool", "cc");
    myAffix.setOnUpdate(function(entity as IEntityLiving, cap as IChampionship, evt as LivingEvent){
        if(!evt.getEntity().world.isRemote()){
            print(evt.getEntityLiving().displayName);
        }
    });
myAffix.register();