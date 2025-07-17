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

import c4.champions.Champions;
import c4.champions.common.util.JsonUtil;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class AffixFilter {

    private final String[] entityBlacklist;
    private final String[] alwaysOnEntity;
    private final int tier;

    public AffixFilter(boolean enabled, String[] entityBlacklist, String[] alwaysOnEntity, int tier) {
        this.entityBlacklist = entityBlacklist;
        this.alwaysOnEntity = alwaysOnEntity;
        this.tier = tier;
    }


    public String[] getEntityBlacklist() {
        return entityBlacklist;
    }

    public String[] getAlwaysOnEntity() {
        return alwaysOnEntity;
    }

    public int getTier() {
        return tier;
    }

    //Manager
    public static final Map<Class<? extends Entity>, EnumSet<EnumAffix>> ENTITY_AFFIX_MAP = new HashMap<>();
    public static final Map<Class<? extends Entity>, BitSet> ENTITY_INCOMPATS_MAP = new HashMap<>(16);

    public static void buildAffixFilters() {
        Map<String, AffixFilter> filters = JsonUtil.fromJson(
            new TypeToken<Map<String, AffixFilter>>(){},
            new File(Loader.instance().getConfigDir(), Champions.MODID + "/affixes.json"),
            new HashMap<String, AffixFilter>() {{
                put("DEFAULT", new AffixFilter(true, new String[]{}, new String[]{}, 0));
            }}
        );

        for (String key : filters.keySet()) {
            if(key.equals("DEFAULT")){continue;}
            AffixFilter filter = filters.get(key);
            EnumAffix affix = EnumAffix.getAffix(key);
            affix.filter = filter;
            for (String entityName : filter.getAlwaysOnEntity()) {
                Class<? extends Entity> entityClass = EntityList.getClass(new ResourceLocation(entityName));
                ENTITY_AFFIX_MAP
                    .computeIfAbsent(entityClass, clazz -> EnumSet.noneOf(EnumAffix.class))
                    .add(affix);
            }
            for (String entityName : filter.getEntityBlacklist()) {
                Class<? extends Entity> entityClass = EntityList.getClass(new ResourceLocation(entityName));
                ENTITY_INCOMPATS_MAP
                    .computeIfAbsent(entityClass, clazz -> new BitSet(EnumAffix.length))
                    .set(affix.ordinal());
            }
        }
        for(EnumAffix affix : EnumAffix.values){
            if(affix.filter == null){
                affix.filter = filters.get("DEFAULT");
            }
        }
    }
}
