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

package c4.champions.common.affix.core;

import c4.champions.common.ConfigHandler;
import c4.champions.common.affix.AffixFilter;
import c4.champions.common.affix.IAffix;
import c4.champions.common.capability.IChampionship;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;

public class AffixBase implements IAffix {

    private final String identifier;
    private final AffixCategory category;
    private final int tier;

    public AffixBase(String identifier, AffixCategory category) {
        this(identifier, category, 1);
    }

    public AffixBase(String identifier, AffixCategory category, int tier) {
        this.identifier = identifier;
        this.category = category;
        this.tier = tier;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public AffixCategory getCategory() {
        return category;
    }

    @Override
    public void onInitialSpawn(EntityLiving entity, IChampionship cap) {

    }

    @Override
    public void onJoinWorld(EntityLiving entity, IChampionship cap, EntityJoinWorldEvent evt) {

    }

    @Override
    public void onUpdate(EntityLiving entity, IChampionship cap, LivingEvent.LivingUpdateEvent evt) {

    }

    @Override
    public void onAttack(EntityLiving entity, IChampionship cap, LivingAttackEvent evt) {

    }

    @Override
    public void onAttacked(EntityLiving entity, IChampionship cap, LivingAttackEvent evt) {

    }

    @Override
    public void onHurt(EntityLiving entity, IChampionship cap, LivingHurtEvent evt) {

    }

    @Override
    public void onDamaged(EntityLiving entity, IChampionship cap, LivingDamageEvent evt) {

    }

    @Override
    public void onHealed(EntityLiving entity, IChampionship cap, LivingHealEvent evt) {

    }

    @Override
    public void onKnockback(EntityLiving entity, IChampionship cap, LivingKnockBackEvent evt) {

    }

    @Override
    public void onDeath(EntityLiving entity, IChampionship cap, LivingDeathEvent evt) {

    }

    @Override
    public boolean canApply(EntityLiving entity) {
        return true;
    }

    @Override
    public AffixFilter getFilter() {
        return null;
    }

    @Override
    public boolean isCompatibleWith(IAffix affix) {
        return affix != this;
    }

    public static boolean isValidAffixTarget(EntityLiving mob, EntityLivingBase target, boolean checkSight) {

        if (target == null || !target.isEntityAlive()) {
            return false;
        }

        if (checkSight && !mob.canEntityBeSeen(target)) {
            return false;
        }
        IAttributeInstance iattributeinstance = mob.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
        double targetRange = iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
        targetRange = ConfigHandler.affix.abilityRange == 0 ? targetRange : Math.min(targetRange, ConfigHandler.affix
                .abilityRange);
        return mob.getDistance(target) <= targetRange;
    }
}
