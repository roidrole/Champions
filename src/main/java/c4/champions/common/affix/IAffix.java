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

package c4.champions.common.affix;

import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/*
 * Only Used for EnumAffix and AffixBase to have the same methods
 * Addons : have an object implementing IAffix (presumably extending AffixBase) and add it to EnumAffix by calling EnumHelper.addEnum() with a new IAffix() object
 */

@ZenClass("mods.champion.IAffix")
@ZenRegister
public interface IAffix {

    @ZenMethod
    String getIdentifier();

    AffixCategory getCategory();

    @ZenMethod("getCategory")
    @SuppressWarnings("unused")
    static String getCategoryCT(IAffix affix){
        return affix.getCategory().name().toLowerCase();
    }

    void onInitialSpawn(EntityLiving entity, IChampionship cap);

    void onJoinWorld(EntityLiving entity, IChampionship cap, EntityJoinWorldEvent evt);

    void onUpdate(EntityLiving entity, IChampionship cap, LivingEvent.LivingUpdateEvent evt);

    void onAttack(EntityLiving entity, IChampionship cap, LivingAttackEvent evt);

    void onAttacked(EntityLiving entity, IChampionship cap, LivingAttackEvent evt);

    void onHurt(EntityLiving entity, IChampionship cap, LivingHurtEvent evt);

    void onHealed(EntityLiving entity, IChampionship cap, LivingHealEvent evt);

    void onDamaged(EntityLiving entity, IChampionship cap, LivingDamageEvent evt);

    void onDeath(EntityLiving entity, IChampionship cap, LivingDeathEvent evt);

    void onKnockback(EntityLiving entity, IChampionship cap, LivingKnockBackEvent evt);

    boolean canApply(EntityLiving entity);

    @ZenMethod
    boolean isCompatibleWith(IAffix affix);

    @ZenMethod
    int getTier();
}
