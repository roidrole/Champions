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

package c4.champions.common.capability;

import c4.champions.common.affix.EnumAffix;
import c4.champions.common.rank.Rank;
import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class Championship implements IChampionship {

    private Map<String, NBTTagCompound> affixData = new HashMap<>();
    private BitSet affixes = new BitSet(EnumAffix.length);
    private Rank rank = null;
    private String name;

    public Championship() {}

    @Override
    public Rank getRank() {
        return rank;
    }

    @Override
    public void setRank(Rank rank) {
        this.rank = rank;
    }

    @Override
    public BitSet getAffixes() {
        return affixes;
    }

    @Override
    public void setAffixes(BitSet affixes){
        this.affixes = affixes;
    }
    @Override
    public void setAffixes(EnumSet<EnumAffix> affixes) {
        affixes.forEach(affix -> this.affixes.set(affix.ordinal()));
    }

    @Override
    public void setAffixData(Map<String, NBTTagCompound> affixes) {
        this.affixData = affixes;
    }
    @Override
    public void setAffixData(String identifier, NBTTagCompound compound) {
        NBTTagCompound data = affixData.get(identifier);
        if(data == null){
            affixData.put(identifier, compound);
        } else {
            data.merge(compound);
        }
    }

    @Override
    public ImmutableMap<String, NBTTagCompound> getAffixData() {
        return ImmutableMap.copyOf(affixData);
    }
    @Override
    @Nullable
    public NBTTagCompound getAffixData(String identifier) {
        return affixData.getOrDefault(identifier, new NBTTagCompound());
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
