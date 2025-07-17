/*
 * Copyright (C) 2018-2019  C4
 *
 * This file is part of Champions, a mod made for Minecraft.
 *
 * Champions is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Champions is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Champions.  If not, see <https://www.gnu.org/licenses/>.
 */

package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.affix.core.AffixNBT;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityDefinition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.HashMap;
import java.util.Map;

@ZenClass("mods.champion.affixes.AffixInfested")
@ZenRegister
public class AffixInfested extends AffixBase {
    public static Map<Class<? extends EntityLiving>, Class<? extends Entity>> parasitesOverrides = new HashMap<Class<? extends EntityLiving>, Class<? extends Entity>>(8){{
        put(EntityEnderman.class, EntityEndermite.class);
        put(EntityShulker.class, EntityEndermite.class);
        put(EntityDragon.class, EntityEndermite.class);
    }};
    public AffixInfested() {
        super("infested", AffixCategory.OFFENSE);
    }

    @Override
    public void onInitialSpawn(EntityLiving entity, IChampionship cap) {
        AffixNBT.Integer buffer = AffixNBT.getData(cap, getIdentifier(), AffixNBT.Integer.class);
        buffer.num = Math.min(ConfigHandler.affix.infested.silverfishTotal, Math.max(1, (int)(entity.getMaxHealth()
                * ConfigHandler.affix.infested.silverfishPerHealth)));
        buffer.saveData(entity);
    }

    @Override
    public void onJoinWorld(EntityLiving entity, IChampionship cap, EntityJoinWorldEvent evt) {
        entity.tasks.addTask(0, new AISpawnParasite(entity));
    }

    @Override
    public void onHealed(EntityLiving entity, IChampionship cap, LivingHealEvent evt) {

        if (evt.getAmount() > 0 && entity.world.rand.nextFloat() < 0.5F) {
            AffixNBT.Integer buffer = AffixNBT.getData(cap, getIdentifier(), AffixNBT.Integer.class);
            buffer.num += 2;
            buffer.saveData(entity);
            if(buffer.num >= ConfigHandler.affix.infested.silverfishTotal - 1 && ConfigHandler.affix.infested.canHeal){
                return;
            }
            evt.setAmount(0);
        }
        return newAmount;
    }

    @Override
    public void onDeath(EntityLiving entity, IChampionship cap, LivingDeathEvent evt) {

        if (!entity.world.isRemote) {
            AffixNBT.Integer buffer = AffixNBT.getData(cap, getIdentifier(), AffixNBT.Integer.class);
            EntityLivingBase target = null;

            if (evt.getSource().getTrueSource() instanceof EntityLivingBase) {
                target = (EntityLivingBase) evt.getSource().getTrueSource();
            }
            boolean isEnder = entity instanceof EntityEnderman
             || entity instanceof EntityShulker
             || entity instanceof EntityEndermite
             || entity instanceof EntityDragon
            ;

            spawnParasites(entity.world, entity, target, buffer.num, parasitesOverrides.get(entity.getClass()));
        }
    }

    private int spawnParasites(World world, EntityLiving entity, EntityLivingBase target, int amount, Class<? extends Entity> parasite) {
        for (int i = 0; i < amount; i++) {
            Entity para;
            try {
                para = parasite.getConstructor(World.class).newInstance(world);
            } catch (Exception e) {
                para = new EntitySilverfish(world);
            }
            para.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
            if(para instanceof EntityLiving) {
                ((EntityLiving)para).setRevengeTarget(target);
                IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving)para);
                if (chp != null) {
                    chp.setRank(RankManager.getEmptyRank());
                }
            }

            world.spawnEntity(para);
            if(para instanceof EntityLiving) {
                ((EntityLiving)para).spawnExplosionParticle();
            }
        }
        return amount;
    }

    @Override
    public boolean canApply(EntityLiving entity) {
        return !parasitesOverrides.containsValue(entity.getClass()) && !(entity instanceof EntitySilverfish);
    }

    class AISpawnParasite extends EntityAIBase {

        private final EntityLiving entity;
        private int attackTime;

        public AISpawnParasite(EntityLiving entityLiving) {
            this.entity = entityLiving;
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase entitylivingbase = entity.getAttackTarget();
            return isValidAffixTarget(entity, entitylivingbase, true) && entitylivingbase.world.getDifficulty()
                    != EnumDifficulty.PEACEFUL;
        }

        @Override
        public void startExecuting() {
            this.attackTime = ConfigHandler.affix.infested.silverfishInterval;
        }

        @Override
        public void updateTask() {

            if (entity.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
                --this.attackTime;
                EntityLivingBase target = entity.getAttackTarget();
                IChampionship chp = CapabilityChampionship.getChampionship(entity);

                if (target != null && chp != null) {
                    entity.getLookHelper().setLookPositionWithEntity(target, 180.0F, 180.0F);
                    AffixNBT.Integer buffer = AffixNBT.getData(chp, getIdentifier(), AffixNBT.Integer.class);

                    if (this.attackTime <= 0 && buffer.num > 0) {
                        this.attackTime = ConfigHandler.affix.desecrator.attackInterval + entity.getRNG().nextInt(5) * 10;
                        int parasites = spawnParasites(entity.world, entity, target, ConfigHandler.affix.infested.silverfishAmount, parasitesOverrides.get(entity.getClass()));

                        buffer.num = Math.max(0, buffer.num - parasites);
                        buffer.saveData(entity);
                    }
                }
                super.updateTask();
            }
        }
    }

    @ZenMethod
    @SuppressWarnings({"unused", "unchecked"})
    public static void addParasiteOverride(IEntityDefinition entity, IEntityDefinition parasite){
        Class<? extends Entity> entityClass = ((EntityEntry)entity.getInternal()).getEntityClass();
        if(!ChampionHelper.isValidChampion(entityClass)){
            CraftTweakerAPI.logError("Entity "+entity+" is not a valid champion, can't add a parasite");
            return;
        }
        Class<? extends Entity> parasiteClass = ((EntityEntry)parasite.getInternal()).getEntityClass();
        parasitesOverrides.put((Class<? extends EntityLiving>)entityClass, parasiteClass);
    }
    @ZenMethod
    @SuppressWarnings("unused")
    public static void removeParasiteOverride(IEntityDefinition entity){
        parasitesOverrides.remove(((EntityEntry)entity.getInternal()).getEntityClass());
    }
}
