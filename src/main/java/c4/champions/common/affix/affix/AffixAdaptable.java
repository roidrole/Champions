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

import c4.champions.common.ConfigHandler;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.affix.core.AffixNBT;
import c4.champions.common.capability.IChampionship;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class AffixAdaptable extends AffixBase {

    public AffixAdaptable() {
        super("adaptable", AffixCategory.DEFENSE);
    }

    @Override
    public void onHurt(EntityLiving entity, IChampionship cap, LivingHurtEvent evt) {
        String type = evt.getSource().getDamageType();
        DamageType damageType = AffixNBT.getData(cap, this.getIdentifier(), DamageType.class);

        float newAmount = evt.getAmount();
        float amount = evt.getAmount();
        if (damageType.name.equalsIgnoreCase(type)) {
            newAmount -= amount * (float) ConfigHandler.affix.adaptable.damageReductionIncrement * damageType.count;
            damageType.count++;
        } else {
            damageType.name = type;
            damageType.count = 0;
        }
        damageType.saveData(entity);
        evt.setAmount(Math.max(amount * (float)(1.0f - ConfigHandler.affix.adaptable.maxDamageReduction), newAmount));
    }

    public static class DamageType extends AffixNBT {

        String name;
        int count;

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            name = tag.getString("name");
            count = tag.getInteger("count");
        }

        @Override
        public NBTTagCompound writeToNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("name", name);
            compound.setInteger("count", count);
            return compound;
        }
    }
}
