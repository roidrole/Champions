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

import c4.champions.common.affix.affix.*;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethodStatic;

import java.util.BitSet;
import java.util.EnumMap;

@ZenExpansion("mods.champion.IAffix")
@ZenRegister
public enum EnumAffix implements IAffix{

    SHIELDING(new AffixShielding()),
    MOLTEN(new AffixMolten()),
    REFLECTING(new AffixReflecting()),
    VORTEX(new AffixVortex()),
    DAMPENING(new AffixDampening()),
    INFESTED(new AffixInfested()),
    JAILER(new AffixJailer()),
    ARCTIC(new AffixArctic()),
    DESECRATOR(new AffixDesecrator()),
    HASTY(new AffixHasty()),
    LIVELY(new AffixLively()),
    PLAGUED(new AffixPlagued()),
    ADAPTABLE(new AffixAdaptable()),
    SCRAPPER(new AffixScrapper()),
    KNOCKBACK(new AffixKnockback()),
    CINDER(new AffixCinder());

    public final IAffix affix;
    public BitSet incompats;
    public static final EnumMap<AffixCategory, BitSet> categorySetMap = new EnumMap<AffixCategory, BitSet>(AffixCategory.class){{
        for(AffixCategory category : AffixCategory.values){
            this.put(category, new BitSet(EnumAffix.length));
        }
    }};

    public static EnumAffix[] values;
    public static int length;

    EnumAffix(IAffix affix){
        this.affix = affix;
    }

    public static void registerCompats() {
        values = values();
        length = values.length;
        for (EnumAffix affix : EnumAffix.values) {
            affix.incompats = new BitSet(EnumAffix.length);
            for (EnumAffix affixComparing : EnumAffix.values) {
                if(!affix.affix.isCompatibleWith(affixComparing)){
                    affix.incompats.set(affixComparing.ordinal());
                }
            }
            categorySetMap.get(affix.getCategory()).set(affix.ordinal());
        }
    }

    @ZenMethodStatic
    public static EnumAffix getAffix(String identifier){
        return EnumAffix.valueOf(identifier.toUpperCase());
    }
    @ZenMethodStatic
    public static EnumAffix getAffix(int ordinal){
        return values[ordinal];
    }

    //IAffix methods
    @Override
    public String getIdentifier() {
        return this.affix.getIdentifier();
    }

    public AffixCategory getCategory(){
        return this.affix.getCategory();
    }

    @Override
    public void onInitialSpawn(EntityLiving entity, IChampionship cap) {
        this.affix.onInitialSpawn(entity, cap);
    }

    @Override
    public void onJoinWorld(EntityLiving entity, IChampionship cap, EntityJoinWorldEvent evt) {
        this.affix.onJoinWorld(entity, cap, evt);
    }

    @Override
    public void onUpdate(EntityLiving entity, IChampionship cap, LivingEvent.LivingUpdateEvent evt) {
        this.affix.onUpdate(entity, cap, evt);
    }

    @Override
    public void onAttack(EntityLiving entity, IChampionship cap, LivingAttackEvent evt) {
        this.affix.onAttack(entity, cap, evt);
    }

    @Override
    public void onAttacked(EntityLiving entity, IChampionship cap, LivingAttackEvent evt) {
        this.affix.onAttacked(entity, cap, evt);
    }

    @Override
    public void onHurt(EntityLiving entity, IChampionship cap, LivingHurtEvent evt) {
        this.affix.onHurt(entity, cap, evt);
    }

    @Override
    public void onHealed(EntityLiving entity, IChampionship cap, LivingHealEvent evt) {
        this.affix.onHealed(entity, cap, evt);
    }

    @Override
    public void onDamaged(EntityLiving entity, IChampionship cap, LivingDamageEvent evt) {
        this.affix.onDamaged(entity, cap, evt);
    }

    @Override
    public void onDeath(EntityLiving entity, IChampionship cap, LivingDeathEvent evt) {
        this.affix.onDeath(entity, cap, evt);
    }

    @Override
    public void onKnockback(EntityLiving entity, IChampionship cap, LivingKnockBackEvent evt) {
        this.affix.onKnockback(entity, cap, evt);
    }

    @Override
    public boolean canApply(EntityLiving entity) {
        return this.affix.canApply(entity);
    }

    @Override
    public boolean isCompatibleWith(IAffix affix) {
        if(affix instanceof EnumAffix){
            return !(incompats.get(((EnumAffix) affix).ordinal()));
        } else {
            return this.affix.isCompatibleWith(affix);
        }
    }

    @Override
    public int getTier() {
        return this.affix.getTier();
    }
}
