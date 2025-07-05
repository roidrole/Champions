package c4.champions.integrations.crafttweaker;

import c4.champions.integrations.gamestages.ChampionStages;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.entity.IEntityLiving;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.EntityLiving;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.function.Function;

@ZenRegister
@ZenClass("mods.champion.Champions")
public class CTChampion {
    public static Function<EntityLiving, Integer> rankAttributor = null;

    @ZenMethod
    public static void addStage(String stage, IEntityDefinition entity, @Optional Integer dimension) {
        if(dimension == null){
            ChampionStages.addStage(entity.getId(), stage);
        } else {
            ChampionStages.addStage(entity.getId(), stage, dimension);
        }
    }

    @ZenMethod
    public static void addStage(String stage, int tier, @Optional Integer dimension) {
        if(dimension == null){
            ChampionStages.addTierStage(tier, stage);
        } else {
            ChampionStages.addTierStage(tier, stage, dimension);
        }
    }

    @ZenMethod
    public static void setRankAttributor(RankAttributor func){
        rankAttributor = entity -> func.apply(CraftTweakerMC.getIEntityLiving(entity));
    }

    @FunctionalInterface
    public interface RankAttributor{
        int apply(IEntityLiving entity);
    }
}
