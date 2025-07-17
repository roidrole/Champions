package c4.champions.integrations.crafttweaker;

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

//Class to hold all CT Events this mod uses
//They're just wrappers for the real thing, but can be newed from the actual event
//Because I can't create a CT event with a forge one...
public class CTEvent {
    @ZenClass("mods.champion.events.EntityEvent")
    @ZenRegister
    public static class CTEntityEvent extends CTEvent {
        private final EntityEvent event;

        public CTEntityEvent(EntityEvent event){
            this.event = event;
        }

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

        public IDamageSource getSource() {
            return CraftTweakerMC.getIDamageSource(event.getSource());
        }

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

        public IDamageSource getSource() {
            return CraftTweakerMC.getIDamageSource(event.getSource());
        }

        public float getAmount() {
            return event.getAmount();
        }

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

        public float getAmount() {
            return event.getAmount();
        }

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

        public IDamageSource getSource() {
            return CraftTweakerMC.getIDamageSource(event.getSource());
        }

        public float getAmount() {
            return event.getAmount();
        }

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

        public IEntity getAttacker() {
            return CraftTweakerMC.getIEntity(event.getAttacker());
        }

        public float getStrength() {
            return event.getStrength();
        }

        public double getRatioX() {
            return event.getRatioX();
        }

        public double getRatioZ() {
            return event.getRatioZ();
        }

        public Entity getOriginalAttacker() {
            return event.getOriginalAttacker();
        }

        public float getOriginalStrength() {
            return event.getOriginalStrength();
        }

        public double getOriginalRatioX() {
            return event.getOriginalRatioX();
        }

        public double getOriginalRatioZ() {
            return event.getOriginalRatioZ();
        }

        public void setAttacker(IEntity attacker) {
            event.setAttacker(CraftTweakerMC.getEntity(attacker));
        }

        public void setStrength(float strength) {
            event.setStrength(strength);
        }

        public void setRatioX(double ratioX) {
            event.setRatioX(ratioX);
        }

        public void setRatioZ(double ratioZ) {
            event.setRatioZ(ratioZ);
        }

    }

}
