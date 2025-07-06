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

package c4.champions.common.util;

import c4.champions.Champions;
import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.affix.filter.AffixFilterManager;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.potion.PotionPlague;
import c4.champions.common.rank.Rank;
import c4.champions.common.rank.RankManager;
import c4.champions.integrations.crafttweaker.CTChampion;
import c4.champions.integrations.gamestages.ChampionStages;
import c4.champions.integrations.scalinghealth.ChampionDifficulty;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.silentchaos512.scalinghealth.api.ScalingHealthAPI;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Level;

import java.util.*;
import java.util.stream.Collectors;

public class ChampionHelper {

    public static Random rand = new Random();

    private static Set<Integer> dimensions = Sets.newHashSet();
    private static Set<ResourceLocation> mobs = Sets.newHashSet();
    private static Map<Integer, List<LootData>> drops = Maps.newHashMap();
    private static Map<ResourceLocation, Tuple<Integer, Integer>> champions = Maps.newHashMap();

    public static boolean isValidChampion(final Entity entity) {
        return entity instanceof EntityLiving && isValidEntity(entity);
    }

    public static Rank generateRank(final EntityLiving entityLivingIn) {
        ImmutableSortedMap<Integer, Rank> ranks = RankManager.getRanks();
        if(CTChampion.rankAttributor != null){
            int outputTier = CTChampion.rankAttributor.apply(entityLivingIn);
            if (outputTier == 0) {
                return RankManager.getEmptyRank();
            }
            return ranks.get(outputTier);
        }
        int firstTier = ranks.firstKey();
        int finalTier = ranks.lastKey();
        int outputTier = 0;
        float chance;

        //Check for mobs which are always champions
        Tuple<Integer, Integer> curated = champions.get(EntityList.getKey(entityLivingIn));
        if (curated != null){
            if(curated.getFirst() > 0 && curated.getSecond() == 0){
                return ranks.get(curated.getFirst());
            }
            finalTier = curated.getSecond();
            firstTier = curated.getFirst();
        } else {
            //We want always champions to bypass beacon restriction
            if(nearActiveBeacon(entityLivingIn)){
                return RankManager.getEmptyRank();
            }
        }



        for (Integer tier : ranks.keySet().tailSet(firstTier).headSet(finalTier, true)) {
            Rank thisRank = ranks.get(tier);
            if(thisRank.isDimensionsWhitelist() != ArrayUtils.contains(thisRank.getDimensions(), entityLivingIn.dimension)){
                continue; //Break if still decrease chance
            }
            if (Champions.isGameStagesLoaded && !ChampionStages.isValidTier(tier, entityLivingIn)) {
                break;
            }
            chance = thisRank.getChance();
            if (Champions.isScalingHealthLoaded) {
                double modifier = ChampionDifficulty.getSpawnModifier(tier);
                double difficulty = ScalingHealthAPI.getAreaDifficulty(entityLivingIn.world, entityLivingIn.getPosition());
                chance += (float) (modifier * difficulty);
            }

            if (rand.nextFloat() < chance) {
                outputTier = tier;
            } else {
                break;
            }
        }

        if (outputTier == 0) {
            return RankManager.getEmptyRank();
        }
        return ranks.get(outputTier);
    }

    public static String generateRandomName() {
        int langSize = 24;
        int randomPrefix = rand.nextInt(langSize + ConfigHandler.championNames.length);
        int randomSuffix = rand.nextInt(langSize + ConfigHandler.championNameSuffixes.length);
        String prefix;
        String suffix;
        String header = Champions.MODID + ".%s.%d";

        if (randomPrefix < langSize) {
            prefix = new TextComponentTranslation(String.format(header, "prefix", randomPrefix)).getFormattedText();
        } else {
            prefix = ConfigHandler.championNames[randomPrefix - langSize];
        }

        if (randomSuffix < langSize) {
            suffix = new TextComponentTranslation(String.format(header, "suffix", randomSuffix)).getFormattedText();
        } else {
            String configSuffix = ConfigHandler.championNameSuffixes[randomSuffix - langSize];
            if (!configSuffix.isEmpty()) {
                suffix = configSuffix.charAt(0) == ',' ? configSuffix : " " + configSuffix;
            } else {
                suffix = "";
            }
        }
        return prefix + suffix;
    }

    public static Set<String> generateAffixes(Rank rank, EntityLiving entityLivingIn, String... presets) {
        int size = rank.getAffixes();
        int tier = rank.getTier();
        if(CTChampion.affixAttributor != null){
            return Sets.newHashSet(Arrays.asList(CTChampion.affixAttributor.apply(entityLivingIn, tier, size)));
        }
        Set<String> affixList = Sets.newHashSet();
        //Can't directly set a new HashMap() because arrays are not copied
        //Could I use a bitmap of enum ordinals instead?
        Map<AffixCategory, Set<String>> categoryMap = AffixRegistry.getCategoryMap().entrySet().stream().collect(
            Collectors.toMap(Map.Entry::getKey, e -> Sets.newHashSet(e.getValue())));


        //Handle any preset affixes
        Set<String> curatedPresets = Sets.newHashSet(presets);
        curatedPresets.addAll(AffixFilterManager.getPresetAffixesForEntity(entityLivingIn));
        curatedPresets.forEach(s -> {
            AffixBase aff = AffixRegistry.getAffix(s);

            if (aff != null) {
                AffixCategory cat = aff.getCategory();
                Set<String> availableAffixes = categoryMap.get(cat);

                if (availableAffixes != null && availableAffixes.contains(s)) {
                    availableAffixes.remove(s);
                    boolean added = false;
                    AffixBase affix = AffixRegistry.getAffix(s);
                    if (affix != null) {
                        boolean flag = true;
                        //Check for incompatible affixes
                        for (String s1 : affixList) {
                            if (!affix.isCompatibleWith(AffixRegistry.getAffix(s1))) {
                                flag = false;
                                break;
                            }
                        }

                        if (flag) {
                            affixList.add(s);
                            added = true;
                        }
                    }

                    if (added && (availableAffixes.isEmpty() || cat != AffixCategory.OFFENSE)) {
                        categoryMap.remove(cat);
                    }
                }
            }
        });


        while (!categoryMap.isEmpty() && affixList.size() < size) {
            //Get random category
            AffixCategory[] categories = categoryMap.keySet().toArray(new AffixCategory[0]);
            AffixCategory randomCategory = categories[rand.nextInt(categories.length)];
            //Get all affixes for that category
            Set<String> affixes = categoryMap.get(randomCategory);

            if (!affixes.isEmpty()) {
                //Get random affix
                int element = rand.nextInt(affixes.size());
                Iterator<String> iter = affixes.iterator();

                for (int i = 0; i < element; i++) {
                    iter.next();
                }
                String id = iter.next();
                boolean added = false;

                //Filter through for validity
                AffixBase affix = AffixRegistry.getAffix(id);

                if (affix != null && affix.canApply(entityLivingIn) && AffixFilterManager.isValidAffix(affix,
                        entityLivingIn, tier)) {
                    boolean flag = true;
                    //Check for incompatible affixes
                    for (String s : affixList) {

                        if (!affix.isCompatibleWith(AffixRegistry.getAffix(s))) {
                            flag = false;
                            break;
                        }
                    }

                    if (flag) {
                        affixList.add(id);
                        added = true;
                    }
                }

                //Remove entire category only if the affix was actually added and the category is limited
                if (added && randomCategory != AffixCategory.OFFENSE) {
                    categoryMap.remove(randomCategory);
                } //Otherwise, remove the affix from the running set and then remove the category if it's now empty
                else {
                    affixes.remove(id);

                    if (affixes.isEmpty()) {
                        categoryMap.remove(randomCategory);
                    }
                }
            }
        }
        return affixList;
    }

    public static boolean isElite(Rank rank) {
        return rank != null && rank.getTier() > 0;
    }

    private static boolean nearActiveBeacon(final EntityLiving entityLivingIn) {
        int range = ConfigHandler.beaconRange;

        if (range <= 0) {
            return false;
        }

        for (TileEntity te : entityLivingIn.world.tickableTileEntities) {
            BlockPos pos = te.getPos();

            if (entityLivingIn.getDistanceSq(pos) <= range * range && te instanceof TileEntityBeacon) {

                if (((TileEntityBeacon)te).isComplete) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isValidEntity(Entity entity) {
        ResourceLocation rl = EntityList.getKey(entity);

        if (rl == null) {
            return false;
        } else if (mobs.isEmpty()) {
            return true;
        } else if (ConfigHandler.mobPermission == ConfigHandler.PermissionMode.BLACKLIST) {
            return !mobs.contains(rl);
        } else {
            return mobs.contains(rl);
        }
    }

    public static boolean isValidDimension(int dim) {

        if (dimensions.isEmpty()) {
            return true;
        } else if (ConfigHandler.dimensionPermission == ConfigHandler.PermissionMode.BLACKLIST) {
            return !dimensions.contains(dim);
        } else {
            return dimensions.contains(dim);
        }
    }

    public static void parseConfigs() {

        Potion potion = Potion.getPotionFromResourceLocation(ConfigHandler.affix.plagued.infectPotion);

        if (potion != null) {
            PotionPlague.setInfectionPotion(potion);
        }

        for (String s : ConfigHandler.dimensionList) {

            try {
                dimensions.add(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                Champions.logger.log(Level.ERROR, "Non-integer found in dimension config! " + s);
            }
        }

        for (String s : ConfigHandler.mobList) {
            ResourceLocation rl = new ResourceLocation(s);

            if (EntityList.getEntityNameList().contains(rl)) {
                mobs.add(rl);
            } else {
                Champions.logger.log(Level.ERROR, "Invalid entity found in mob config! " + s);
            }
        }

        for (String s : ConfigHandler.championsList) {
            String[] args = s.split(";");
            ResourceLocation rl = new ResourceLocation(args[0]);
            int minTier = args.length > 1 ? Integer.parseInt(args[1]) : 0;
            int maxTier = args.length > 2 ? Integer.parseInt(args[2]) : 0;

            if (EntityList.getEntityNameList().contains(rl)) {
                champions.put(rl, new Tuple<>(minTier, maxTier));
            } else {
                Champions.logger.log(Level.ERROR, "Invalid entity found in champions list config! " + s);
            }
        }

        for (String s : ConfigHandler.lootDrops) {
            String[] parsed = s.split(";");

            if (parsed.length > 0) {
                int tier;
                ItemStack stack;
                int metadata = 0;
                int stackSize = 1;
                boolean enchant = false;
                int weight = 1;

                if (parsed.length < 2) {
                    Champions.logger.log(Level.ERROR, s + " needs at least a tier and an item name");
                    continue;
                }

                try {
                    tier = Integer.parseInt(parsed[0]);
                } catch (NumberFormatException e) {
                    Champions.logger.log(Level.ERROR, parsed[0] + " is not a valid tier");
                    continue;
                }

                Item item = Item.getByNameOrId(parsed[1]);

                if (item == null) {
                    Champions.logger.log(Level.ERROR, "Item not found!" + parsed[1]);
                    continue;
                }

                if (parsed.length > 2) {

                    try {
                        metadata = Integer.parseInt(parsed[2]);
                    } catch (NumberFormatException e) {
                        Champions.logger.log(Level.ERROR, parsed[2] + " is not a valid metadata");
                    }

                    if (parsed.length > 3) {

                        try {
                            stackSize = Integer.parseInt(parsed[3]);
                        } catch (NumberFormatException e) {
                            Champions.logger.log(Level.ERROR, parsed[3] + " is not a valid stack creeperStrength");
                        }

                        if (parsed.length > 4) {

                            if (parsed[4].equalsIgnoreCase("true")) {
                                enchant = true;
                            }

                            if (parsed.length > 5) {
                                try {
                                    weight = Integer.parseInt(parsed[5]);
                                } catch (NumberFormatException e) {
                                    Champions.logger.log(Level.ERROR, parsed[5] + " is not a valid weight");
                                }
                            }
                        }
                    }
                }
                stack = new ItemStack(item, stackSize, metadata);
                drops.computeIfAbsent(tier, list -> Lists.newArrayList()).add(new LootData(stack, enchant, weight));
            }
        }
    }

    public static List<ItemStack> getLootDrops(int tier) {
        double totalWeight;
        List<LootData> data = Lists.newArrayList(drops.getOrDefault(tier, Lists.newArrayList()));
        List<ItemStack> drops = new ArrayList<>();

        if (data.isEmpty()) {
            return drops;
        }

        int amount = ConfigHandler.lootScaling ? tier : 1;

        for (int i = 0; i < amount; i++) {
            totalWeight = 0;

            for (LootData loot : data) {
                totalWeight += loot.weight;
            }
            double random = rand.nextDouble() * totalWeight;
            double countWeight = 0;

            for (LootData loot : data) {
                countWeight += loot.weight;

                if (countWeight >= random) {
                    drops.add(loot.getLootStack());
                    break;
                }
            }
        }

        return drops;
    }

    private static class LootData {

        private ItemStack stack;
        private boolean enchant;
        private int weight;

        LootData(ItemStack stack, boolean enchant, int weight) {
            this.stack = stack;
            this.enchant = enchant;
            this.weight = weight;
        }

        public ItemStack getLootStack() {
            ItemStack loot = stack.copy();

            if (enchant) {
                EnchantmentHelper.addRandomEnchantment(rand, loot, 30, true);
            }
            return loot;
        }
    }
}
