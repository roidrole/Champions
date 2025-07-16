package c4.champions.common.affix.core;

import c4.champions.common.affix.EnumAffix;
import c4.champions.common.affix.IAffix;
import c4.champions.common.capability.IChampionship;
import c4.champions.integrations.crafttweaker.CTEvent;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityLiving;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.eventhandler.Event;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.champion.AffixBuilder")
public class AffixBuilder implements IAffix{

    private final String identifier;
    private final AffixCategory category;
    private final int tier;

    public AffixBuilder(String identifier, AffixCategory category, int tier) {
        this.identifier = identifier;
        this.category = category;
        this.tier = tier;
    }

    public ChampionInitialSpawn onInitialSpawn = ((entity, cap) -> {});
    public ChampionEventHandler<EntityJoinWorldEvent> onJoinWorld = ChampionEventHandler.empty;
    public ChampionEventHandler<LivingEvent.LivingUpdateEvent> onUpdate = ChampionEventHandler.empty;
    public ChampionEventHandler<LivingAttackEvent> onAttack = ChampionEventHandler.empty;
    public ChampionEventHandler<LivingAttackEvent> onAttacked = ChampionEventHandler.empty;
    public ChampionEventHandler<LivingHurtEvent> onHurt = ChampionEventHandler.empty;
    public ChampionEventHandler<LivingHealEvent> onHealed = ChampionEventHandler.empty;
    public ChampionEventHandler<LivingDamageEvent> onDamaged = ChampionEventHandler.empty;
    public ChampionEventHandler<LivingDeathEvent> onDeath = ChampionEventHandler.empty;
    public ChampionEventHandler<LivingKnockBackEvent> onKnockback = ChampionEventHandler.empty;
    public BooleanFunction<EntityLiving> canApply = BooleanFunction.alwaysTrue;
    public BooleanFunction<IAffix> compatibleWith = BooleanFunction.alwaysTrue;



    @ZenMethod
    @SuppressWarnings("unused")
    public static AffixBuilder createAffix(String identifier, String category, @Optional int tier){
        if(tier == 0){tier = 1;}
        AffixCategory cat = AffixCategory.valueOf(category.toUpperCase());
        return new AffixBuilder(identifier, cat, tier);
    }

    @SuppressWarnings("unused")
    @ZenMethod
    public void register(){
        EnumHelper.addEnum(EnumAffix.class, this.identifier.toUpperCase(), new Class[]{IAffix.class}, this);
    }

    //CT Methods
    //Basically boilerplate to convert MC types into CT types. Painfully.
    @ZenMethod
    @SuppressWarnings("unused")
    public void setOnInitialSpawn(CTChampionInitialSpawn handler){
        this.onInitialSpawn = (entity, chp) -> handler.apply(CraftTweakerMC.getIEntityLiving(entity), chp);
    }
    @ZenMethod
    @SuppressWarnings("unused")
    public void setOnJoinWorld(CTChampionEventHandler<CTEvent.CTJoinWorld> handler){
        this.onJoinWorld = (entity, chp, evt) ->
            handler.apply(CraftTweakerMC.getIEntityLiving(entity), chp, new CTEvent.CTJoinWorld(evt));
    }
    @ZenMethod
    @SuppressWarnings("unused")
    public void setOnUpdate(CTChampionEventHandler<CTEvent.CTLivingEvent> handler){
        this.onUpdate = (entity, chp, evt) ->
            handler.apply(CraftTweakerMC.getIEntityLiving(entity), chp, new CTEvent.CTLivingEvent(evt));
    }
    @ZenMethod
    @SuppressWarnings("unused")
    public void setOnAttack(CTChampionEventHandler<CTEvent.CTAttackEvent> handler){
        this.onAttack = (entity, chp, evt) ->
            handler.apply(CraftTweakerMC.getIEntityLiving(entity), chp, new CTEvent.CTAttackEvent(evt));
    }
    @ZenMethod
    @SuppressWarnings("unused")
    public void setOnAttacked(CTChampionEventHandler<CTEvent.CTAttackEvent> handler){
        this.onAttacked = (entity, chp, evt) ->
            handler.apply(CraftTweakerMC.getIEntityLiving(entity), chp, new CTEvent.CTAttackEvent(evt));
    }
    @ZenMethod
    @SuppressWarnings("unused")
    public void setOnHurt(CTChampionEventHandler<CTEvent.CTHurtEvent> handler){
        this.onHurt = (entity, chp, evt) ->
            handler.apply(CraftTweakerMC.getIEntityLiving(entity), chp, new CTEvent.CTHurtEvent(evt));
    }
    @ZenMethod
    @SuppressWarnings("unused")
    public void setOnHealed(CTChampionEventHandler<CTEvent.CTHealEvent> handler){
        this.onHealed = (entity, chp, evt) ->
            handler.apply(CraftTweakerMC.getIEntityLiving(entity), chp, new CTEvent.CTHealEvent(evt));
    }
    @ZenMethod
    @SuppressWarnings("unused")
    public void setOnDamaged(CTChampionEventHandler<CTEvent.CTDamageEvent> handler){
        this.onDamaged = (entity, chp, evt) ->
            handler.apply(CraftTweakerMC.getIEntityLiving(entity), chp, new CTEvent.CTDamageEvent(evt));
    }
    @ZenMethod
    @SuppressWarnings("unused")
    public void setOnDeath(CTChampionEventHandler<CTEvent.CTDeathEvent> handler){
        this.onDeath = (entity, chp, evt) ->
            handler.apply(CraftTweakerMC.getIEntityLiving(entity), chp, new CTEvent.CTDeathEvent(evt));
    }
    @ZenMethod
    @SuppressWarnings("unused")
    public void setOnKnockback(CTChampionEventHandler<CTEvent.CTKnockbackEvent> handler){
        this.onKnockback = (entity, chp, evt) ->
            handler.apply(CraftTweakerMC.getIEntityLiving(entity), chp, new CTEvent.CTKnockbackEvent(evt));
    }
    @ZenMethod
    @SuppressWarnings("unused")
    public void setCanApply(BooleanFunction<IEntityLiving> function){
        this.canApply = (entity) -> function.apply(CraftTweakerMC.getIEntityLiving(entity));
    }
    @ZenMethod
    @SuppressWarnings("unused")
    public void setCompatibleWith(BooleanFunction<IAffix> function){
        this.compatibleWith = function;
    }




    //IAffix Getters
    @Override
    public String getIdentifier() {
        return identifier;
    }
    @Override
    public AffixCategory getCategory() {
        return category;
    }
    @Override
    public int getTier() {
        return tier;
    }

    //IAffix methods
    @Override
    public void onInitialSpawn(EntityLiving entity, IChampionship cap) {
        this.onInitialSpawn.apply(entity, cap);
    }
    @Override
    public void onJoinWorld(EntityLiving entity, IChampionship cap, EntityJoinWorldEvent evt) {
        this.onJoinWorld.apply(entity, cap, evt);
    }
    @Override
    public void onUpdate(EntityLiving entity, IChampionship cap, LivingEvent.LivingUpdateEvent evt) {
        this.onUpdate.apply(entity, cap, evt);
    }
    @Override
    public void onAttack(EntityLiving entity, IChampionship cap, LivingAttackEvent evt) {
        this.onAttack.apply(entity, cap, evt);
    }
    @Override
    public void onAttacked(EntityLiving entity, IChampionship cap, LivingAttackEvent evt) {
        this.onAttacked.apply(entity, cap, evt);
    }
    @Override
    public void onHurt(EntityLiving entity, IChampionship cap, LivingHurtEvent evt) {
        this.onHurt.apply(entity, cap, evt);
    }
    @Override
    public void onHealed(EntityLiving entity, IChampionship cap, LivingHealEvent evt) {
        this.onHealed.apply(entity, cap, evt);
    }
    @Override
    public void onDamaged(EntityLiving entity, IChampionship cap, LivingDamageEvent evt) {
        this.onDamaged.apply(entity, cap, evt);
    }
    @Override
    public void onDeath(EntityLiving entity, IChampionship cap, LivingDeathEvent evt) {
        this.onDeath.apply(entity, cap, evt);
    }
    @Override
    public void onKnockback(EntityLiving entity, IChampionship cap, LivingKnockBackEvent evt) {
        this.onKnockback.apply(entity, cap, evt);
    }
    @Override
    public boolean canApply(EntityLiving entity){
        return this.canApply.apply(entity);
    }
    @Override
    public boolean isCompatibleWith(IAffix affix) {
        return this.compatibleWith.apply(affix);
    }


    //Functional interfaces - Helpers
    @FunctionalInterface
    public interface ChampionInitialSpawn{
        void apply(EntityLiving entity, IChampionship cap);
    }
    @FunctionalInterface
    public interface ChampionEventHandler<E extends Event>{
        void apply(EntityLiving entity, IChampionship cap, E evt);
        ChampionEventHandler empty = (entity, cap, event) -> {};
    }
    @FunctionalInterface
    public interface BooleanFunction<E>{
        boolean apply(E param);
        BooleanFunction alwaysTrue = arg -> true;
    }
    @FunctionalInterface
    public interface CTChampionInitialSpawn{
        void apply(IEntityLivingBase entity, IChampionship cap);
    }
    @FunctionalInterface
    public interface CTChampionEventHandler<E extends CTEvent>{
        void apply(IEntityLiving entity, IChampionship cap, E evt);
    }
}
