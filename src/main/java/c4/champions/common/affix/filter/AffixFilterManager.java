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

package c4.champions.common.affix.filter;

import c4.champions.Champions;
import c4.champions.common.affix.EnumAffix;
import c4.champions.common.util.JsonUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.Set;

public class AffixFilterManager {

    private static AffixFilter[] FILTERS;
    private static final Map<String, Set<EnumAffix>> ENTITY_AFFIX_MAP = Maps.newHashMap();
    private static final Map<Class<? extends Entity>, BitSet> ENTITY_INCOMPATS_MAP = Maps.newHashMap();

    @Nullable
    public static AffixFilter getAffixFilter(int ordinal) {
        return FILTERS[ordinal];
    }
    @Nullable
    public static AffixFilter getAffixFilter(String identifier) {
        return FILTERS[EnumAffix.valueOf(identifier.toUpperCase()).ordinal()];
    }

    public static boolean isValidTier(EnumAffix affix, int tier) {
        AffixFilter filter = getAffixFilter(affix.ordinal());
        if(filter == null){
            return affix.getTier() <= tier;
        }
        return filter.isEnabled() && filter.getTier() <= tier;
    }

    @Nonnull
    public static Set<EnumAffix> getPresetAffixesForEntity(Entity entity) {
        ResourceLocation rl = EntityList.getKey(entity);
        return rl != null ? ENTITY_AFFIX_MAP.getOrDefault(rl.toString(), Sets.newHashSet()) : Sets.newHashSet();
    }
    @Nonnull
    public static BitSet getIncompatAffixesForEntity(Entity entity) {
        System.out.println(ENTITY_INCOMPATS_MAP);
        return ENTITY_INCOMPATS_MAP.computeIfAbsent(entity.getClass(), k -> new BitSet(EnumAffix.length){{
            //Builds the incompats by scanning all filters and adding the corresponding to the BitSet, caching the result
            //Supposes that FILTERS and EnumAffix has the same ordinal/index.
            ResourceLocation entityKey = EntityList.getKey(k);
            if(entityKey != null){
                String thisId = entityKey.toString();
                for (int i = 0; i < FILTERS.length; i++) {
                    for(String id : FILTERS[i].getEntityBlacklist()){
                        if(id.equals(thisId)){
                            this.set(i);
                        }
                    }
                }
            }
        }});
    }

    public static void readAffixFiltersFromJson() {
        FILTERS = JsonUtil.fromJson(TypeToken.get(AffixFilter[].class), new File(Loader.instance()
            .getConfigDir(), Champions.MODID + "/affixes.json"), buildDefaultAffixFilters());

        for (AffixFilter filter : FILTERS) {
            for (String entityName : filter.getAlwaysOnEntity()) {
                Set<EnumAffix> affixes = ENTITY_AFFIX_MAP.getOrDefault(entityName, Sets.newHashSet());
                affixes.add(filter.getAffix());
                ENTITY_AFFIX_MAP.putIfAbsent(entityName, affixes);
            }
        }
    }

    private static AffixFilter[] buildDefaultAffixFilters() {
        return Arrays.stream(EnumAffix.values)
            .map(affix -> new AffixFilter(affix, true, new String[]{}, new String[]{}, affix.getTier()))
            .toArray(AffixFilter[]::new);
    }
}
