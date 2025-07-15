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
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

import java.util.BitSet;
import java.util.EnumMap;

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

    public static void postInit() {
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

    public static EnumAffix getAffix(String identifier){
        return EnumAffix.valueOf(identifier.toUpperCase());
    }
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
    public void onSpawn(EntityLiving entity, IChampionship cap) {
        this.affix.onSpawn(entity, cap);
    }

    @Override
    public void onUpdate(EntityLiving entity, IChampionship cap) {
        this.affix.onUpdate(entity, cap);
    }

    @Override
    public void onAttack(EntityLiving entity, IChampionship cap, EntityLivingBase target, DamageSource source, float amount, LivingAttackEvent evt) {
        this.affix.onAttack(entity, cap, target, source, amount, evt);
    }

    @Override
    public void onAttacked(EntityLiving entity, IChampionship cap, DamageSource source, float amount, LivingAttackEvent evt) {
        this.affix.onAttacked(entity, cap, source, amount, evt);
    }

    @Override
    public float onHurt(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount) {
        return this.affix.onHurt(entity, cap, source, amount, newAmount);
    }

    @Override
    public float onHealed(EntityLiving entity, IChampionship cap, float amount, float newAmount) {
        return this.affix.onHealed(entity, cap, amount, newAmount);
    }

    @Override
    public float onDamaged(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount) {
        return this.affix.onDamaged(entity, cap, source, amount, newAmount);
    }

    @Override
    public void onDeath(EntityLiving entity, IChampionship cap, DamageSource source, LivingDeathEvent evt) {
        this.affix.onDeath(entity, cap, source, evt);
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
