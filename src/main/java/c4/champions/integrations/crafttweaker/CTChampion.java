package c4.champions.integrations.crafttweaker;

import c4.champions.integrations.gamestages.ChampionStages;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityLiving;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.EntityLiving;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.champion.Champions")
public class CTChampion {
    public static RankAttributor<EntityLiving> rankAttributor = null;
    public static AffixAttributor<EntityLiving> affixAttributor = null;

    @ZenMethod
    public static void addStage(String stage, String entity) {
        ChampionStages.addStage(entity, stage);
    }

    @ZenMethod
    public static void addStage(String stage, String entity, int dimension) {
        ChampionStages.addStage(entity, stage, dimension);
    }

    @ZenMethod
    public static void addStage(String stage, int tier) {
        ChampionStages.addTierStage(tier, stage);
    }

    @ZenMethod
    public static void addStage(String stage, int tier, int dimension) {
        ChampionStages.addTierStage(tier, stage, dimension);
    }

    @ZenMethod
    public static void setRankAttributor(RankAttributor<IEntityLiving> func){
        rankAttributor = entity -> func.apply(CraftTweakerMC.getIEntityLiving(entity));
    }

    @ZenMethod
    public static void setAffixAttributor(AffixAttributor<IEntityLiving> func){
        affixAttributor = (entity, rank, amount) -> func.apply(CraftTweakerMC.getIEntityLiving(entity), rank, amount);
    }

    @FunctionalInterface
    public interface RankAttributor<T>{
        int apply(T entity);
    }
    @FunctionalInterface
    public interface AffixAttributor<T>{
        String[] apply(T entity, int rank, int amount);
    }
}
