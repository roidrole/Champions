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
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

public class AffixFilterManager {

    private static AffixFilter[] FILTERS;
    private static final Map<Class<? extends Entity>, EnumSet<EnumAffix>> ENTITY_AFFIX_MAP = new HashMap<>();
    private static final Map<Class<? extends Entity>, BitSet> ENTITY_INCOMPATS_MAP = new HashMap<>(16);

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
    public static EnumSet<EnumAffix> getPresetAffixesForEntity(Entity entity) {
        return ENTITY_AFFIX_MAP.getOrDefault(entity.getClass(), EnumSet.noneOf(EnumAffix.class));
    }
    @Nonnull
    public static BitSet getIncompatAffixesForEntity(Entity entity) {
        return ENTITY_INCOMPATS_MAP.getOrDefault(entity.getClass(), new BitSet(EnumAffix.length));
    }

    public static void readAffixFiltersFromJson() {
        FILTERS = JsonUtil.fromJson(
            TypeToken.get(AffixFilter[].class),
            new File(Loader.instance().getConfigDir(), Champions.MODID + "/affixes.json"),
            buildDefaultAffixFilters()
        );

        for (AffixFilter filter : FILTERS) {
            for (String entityName : filter.getAlwaysOnEntity()) {
                Class<? extends Entity> entityClass = EntityList.getClass(new ResourceLocation(entityName));
                ENTITY_AFFIX_MAP
                    .computeIfAbsent(entityClass, clazz -> EnumSet.noneOf(EnumAffix.class))
                    .add(filter.getAffix());
            }
            for (String entityName : filter.getEntityBlacklist()) {
                Class<? extends Entity> entityClass = EntityList.getClass(new ResourceLocation(entityName));
                ENTITY_INCOMPATS_MAP
                    .computeIfAbsent(entityClass, clazz -> new BitSet(EnumAffix.length))
                    .set(filter.getAffix().ordinal());
            }
        }
    }

    private static AffixFilter[] buildDefaultAffixFilters() {
        return Arrays.stream(EnumAffix.values)
            .map(affix -> new AffixFilter(affix, true, new String[]{}, new String[]{}, affix.getTier()))
            .toArray(AffixFilter[]::new);
    }
}
