package c4.champions.integrations.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IWorld;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

//Class to hold all CT Events this mod uses
//They're just wrappers for the real thing, but can be newed from the actual event
//Because I can't create a CT event with a forge one...
public abstract class CTEvent {
    //CT really wasn't build for this...
    //Default methods throw an error, subclasses override them
    @ZenMethod
    public IEntity getEntity(){
        CraftTweakerAPI.logError("This event type doesn't support getEntity", new NoSuchFieldException());
        return null;
    }
    @ZenMethod
    public IEntityLivingBase getEntityLiving(){
        CraftTweakerAPI.logError("This event type doesn't support getEntity", new NoSuchFieldException());
        return null;
    }
    @ZenMethod
    public IWorld getWorld(){
        CraftTweakerAPI.logError("This event type doesn't support getWorld", new NoSuchFieldException());
        return null;
    }
    @ZenMethod
    public IDamageSource getSource(){
        CraftTweakerAPI.logError("This event type doesn't support getSource", new NoSuchFieldException());
        return null;
    }
    @ZenMethod
    public float getAmount(){
        CraftTweakerAPI.logError("This event type doesn't support getAmount", new NoSuchFieldException());
        return 0;
    }
    @ZenMethod
    public void setAmount(float amount) {
        CraftTweakerAPI.logError("This event type doesn't support setAmount", new NoSuchFieldException());
    }
    @ZenMethod
    public IEntity getAttacker(){
        CraftTweakerAPI.logError("This event type doesn't support getAttacker", new NoSuchFieldException());
        return null;
    }
    @ZenMethod
    public float getStrength(){
        CraftTweakerAPI.logError("This event type doesn't support getStrength", new NoSuchFieldException());
        return 0;
    }
    @ZenMethod
    public double getRatioX(){
        CraftTweakerAPI.logError("This event type doesn't support getRatioX", new NoSuchFieldException());
        return 0;
    }
    @ZenMethod
    public double getRatioZ(){
        CraftTweakerAPI.logError("This event type doesn't support getRatioZ", new NoSuchFieldException());
        return 0;
    }
    @ZenMethod
    public Entity getOriginalAttacker(){
        CraftTweakerAPI.logError("This event type doesn't support getOriginalAttacker", new NoSuchFieldException());
        return null;
    }
    @ZenMethod
    public float getOriginalStrength(){
        CraftTweakerAPI.logError("This event type doesn't support getOriginalStrength", new NoSuchFieldException());
        return 0;
    }
    @ZenMethod
    public double getOriginalRatioX(){
        CraftTweakerAPI.logError("This event type doesn't support getOriginalRatioX", new NoSuchFieldException());
        return 0;
    }
    @ZenMethod
    public double getOriginalRatioZ(){
        CraftTweakerAPI.logError("This event type doesn't support getOriginalRatioZ", new NoSuchFieldException());
        return 0;
    }
    @ZenMethod
    public void setAttacker(IEntity attacker){
        CraftTweakerAPI.logError("This event type doesn't support setAttacker", new NoSuchFieldException());
    }
    @ZenMethod
    public void setStrength(float strength){
        CraftTweakerAPI.logError("This event type doesn't support setStrength", new NoSuchFieldException());
    }
    @ZenMethod
    public void setRatioX(double ratioX){
        CraftTweakerAPI.logError("This event type doesn't support setRatioX", new NoSuchFieldException());
    }
    @ZenMethod
    public void setRatioZ(double ratioZ){
        CraftTweakerAPI.logError("This event type doesn't support setRatioZ", new NoSuchFieldException());
    }

    @ZenClass("mods.champion.events.EntityEvent")
    @ZenRegister
    public static class CTEntityEvent extends CTEvent {
        private final EntityEvent event;

        public CTEntityEvent(EntityEvent event){
            this.event = event;
        }

        @ZenMethod
        @Override
        public IEntity getEntity(){
            return CraftTweakerMC.getIEntity(event.getEntity());
        }
    }

    @ZenClass("mods.champion.events.LivingEvent")
    @ZenRegister
    public static class CTLivingEvent extends CTEntityEvent{
        private final LivingEvent event;

        public CTLivingEvent(LivingEvent event) {
            super(event);
            this.event = event;
        }

        @Override
        public IEntityLivingBase getEntityLiving(){
            return CraftTweakerMC.getIEntityLivingBase(event.getEntityLiving());
        }
    }

    @ZenClass("mods.champion.events.JoinWorldEvent")
    @ZenRegister
    public static class CTJoinWorld extends CTEntityEvent {
        private final EntityJoinWorldEvent event;

        public CTJoinWorld(EntityJoinWorldEvent event){
            super(event);
            this.event = event;
        }

        @Override
        public IWorld getWorld(){
            return CraftTweakerMC.getIWorld(event.getWorld());
        }
    }

    @ZenClass("mods.champion.events.AttackEvent")
    @ZenRegister
    public static class CTAttackEvent extends CTLivingEvent{
        private final LivingAttackEvent event;

        public CTAttackEvent(LivingAttackEvent event) {
            super(event);
            this.event = event;
        }

        @Override
        public IDamageSource getSource() {
            return CraftTweakerMC.getIDamageSource(event.getSource());
        }

        @Override
        public float getAmount() {
            return event.getAmount();
        }
    }

    @ZenClass("mods.champion.events.HurtEvent")
    @ZenRegister
    public static class CTHurtEvent extends CTLivingEvent{
        private final LivingHurtEvent event;

        public CTHurtEvent(LivingHurtEvent event) {
            super(event);
            this.event = event;
        }

        @Override
        public IDamageSource getSource() {
            return CraftTweakerMC.getIDamageSource(event.getSource());
        }

        @Override
        public float getAmount() {
            return event.getAmount();
        }

        @Override
        public void setAmount(float amount) {
            event.setAmount(amount);
        }
    }

    @ZenClass("mods.champion.events.HealEvent")
    @ZenRegister
    public static class CTHealEvent extends CTLivingEvent{
        private final LivingHealEvent event;

        public CTHealEvent(LivingHealEvent event) {
            super(event);
            this.event = event;
        }

        @Override
        public float getAmount() {
            return event.getAmount();
        }

        @Override
        public void setAmount(float amount) {
            event.setAmount(amount);
        }
    }

    @ZenClass("mods.champion.events.DamageEvent")
    @ZenRegister
    public static class CTDamageEvent extends CTLivingEvent{
        private final LivingDamageEvent event;

        public CTDamageEvent(LivingDamageEvent event) {
            super(event);
            this.event = event;
        }

        @Override
        public IDamageSource getSource() {
            return CraftTweakerMC.getIDamageSource(event.getSource());
        }

        @Override
        public float getAmount() {
            return event.getAmount();
        }

        @Override
        public void setAmount(float amount) {
            event.setAmount(amount);
        }
    }

    @ZenClass("mods.champion.events.DeathEvent")
    @ZenRegister
    public static class CTDeathEvent extends CTLivingEvent{
        private final LivingDeathEvent event;

        public CTDeathEvent(LivingDeathEvent event) {
            super(event);
            this.event = event;
        }

        @Override
        public IDamageSource getSource() {
            return CraftTweakerMC.getIDamageSource(event.getSource());
        }
    }

    @ZenClass("mods.champion.events.KnockbackEvent")
    @ZenRegister
    public static class CTKnockbackEvent extends CTLivingEvent{
        private final LivingKnockBackEvent event;

        public CTKnockbackEvent(LivingKnockBackEvent event) {
            super(event);
            this.event = event;
        }

        @Override
        public IEntity getAttacker() {
            return CraftTweakerMC.getIEntity(event.getAttacker());
        }

        @Override
        public float getStrength() {
            return event.getStrength();
        }

        @Override
        public double getRatioX() {
            return event.getRatioX();
        }

        @Override
        public double getRatioZ() {
            return event.getRatioZ();
        }

        @Override
        public Entity getOriginalAttacker() {
            return event.getOriginalAttacker();
        }

        @Override
        public float getOriginalStrength() {
            return event.getOriginalStrength();
        }

        @Override
        public double getOriginalRatioX() {
            return event.getOriginalRatioX();
        }

        @Override
        public double getOriginalRatioZ() {
            return event.getOriginalRatioZ();
        }

        @Override
        public void setAttacker(IEntity attacker) {
            event.setAttacker(CraftTweakerMC.getEntity(attacker));
        }

        @Override
        public void setStrength(float strength) {
            event.setStrength(strength);
        }

        @Override
        public void setRatioX(double ratioX) {
            event.setRatioX(ratioX);
        }

        @Override
        public void setRatioZ(double ratioZ) {
            event.setRatioZ(ratioZ);
        }
    }

}
