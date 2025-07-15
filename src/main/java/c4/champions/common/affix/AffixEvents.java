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
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.rank.Rank;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Tuple;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class AffixEvents {

    @SubscribeEvent
    public void onLivingJoinWorld(EntityJoinWorldEvent evt) {

        if (ChampionHelper.isValidChampion(evt.getEntity())) {
            EntityLiving living = (EntityLiving)evt.getEntity();
            IChampionship chp = CapabilityChampionship.getChampionship(living);

            if (chp != null) {

                chp.getAffixes().stream().forEach(ordinal ->
                    EnumAffix.getAffix(ordinal).onSpawn(living, chp)
                );
                Rank rank = chp.getRank();

                if (!living.world.isRemote && ChampionHelper.isElite(rank)) {
                    List<Tuple<Potion, Integer>> potions = RankManager.getPotionsForTier(rank.getTier());

                    if (potions != null) {

                        for (Tuple<Potion, Integer> potion : potions) {
                            living.addPotionEffect(new PotionEffect(potion.getFirst(), 200, potion.getSecond()));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent evt) {

        if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
            EntityLiving living = (EntityLiving)evt.getEntityLiving();
            IChampionship chp = CapabilityChampionship.getChampionship(living);

            if (chp != null) {
                chp.getAffixes().stream().forEach(ordinal ->
                    EnumAffix.getAffix(ordinal).onUpdate(living, chp)
                );
                Rank rank = chp.getRank();

                if (ChampionHelper.isElite(rank)) {

                    if (living.world.isRemote && !ConfigHandler.hideEffects) {
                        Champions.proxy.generateRankParticle(living, rank.getColor());
                    } else if (living.ticksExisted % 100 == 0) {
                        List<Tuple<Potion, Integer>> potions = RankManager.getPotionsForTier(rank.getTier());

                        if (potions != null) {

                            for (Tuple<Potion, Integer> potion : potions) {
                                living.addPotionEffect(new PotionEffect(potion.getFirst(), 200, potion.getSecond()));
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttacked(LivingAttackEvent evt) {

        if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
            EntityLiving living = (EntityLiving)evt.getEntityLiving();
            IChampionship chp = CapabilityChampionship.getChampionship(living);

            if (chp != null) {
                chp.getAffixes().stream().forEach(ordinal ->
                    EnumAffix.getAffix(ordinal).onAttacked(living, chp, evt.getSource(), evt.getAmount(), evt)
                );
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent evt) {

        if (evt.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)evt.getSource().getTrueSource();
            if (ChampionHelper.isValidChampion(entityLivingBase)) {
                EntityLiving living = (EntityLiving)entityLivingBase;
                IChampionship chp = CapabilityChampionship.getChampionship(living);

                if (chp != null) {

                    chp.getAffixes().stream().forEach(ordinal ->
                        EnumAffix.getAffix(ordinal).onAttack(living, chp, evt.getEntityLiving(), evt.getSource(), evt.getAmount(), evt)
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingWasHurt(LivingHurtEvent evt) {

        if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
            float amount = evt.getAmount();
            float newAmount;

            EntityLiving living = (EntityLiving)evt.getEntityLiving();
            IChampionship chp = CapabilityChampionship.getChampionship(living);

            if (chp != null) {

                newAmount = chp.getAffixes().stream()
                    .mapToObj(ordinal -> EnumAffix.getAffix(ordinal).onHurt(living, chp, evt.getSource(), amount, amount))
                    .filter(f -> f != amount)
                    .findAny()
                    .orElse(amount)
                ;
                evt.setAmount(newAmount);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamaged(LivingDamageEvent evt) {

        if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
            float amount = evt.getAmount();
            float newAmount;

            EntityLiving living = (EntityLiving)evt.getEntityLiving();
            IChampionship chp = CapabilityChampionship.getChampionship(living);

            if (chp != null) {
                newAmount = chp.getAffixes().stream()
                    .mapToObj(ordinal -> EnumAffix.getAffix(ordinal).onDamaged(living, chp, evt.getSource(), amount, amount))
                    .filter(f -> f != amount)
                    .findAny()
                    .orElse(amount)
                ;
                evt.setAmount(newAmount);
            }
        }
    }

    @SubscribeEvent
    public void onLivingKnockback(LivingKnockBackEvent evt) {

        if (ChampionHelper.isValidChampion(evt.getOriginalAttacker())) {
            EntityLiving living = (EntityLiving)evt.getOriginalAttacker();
            IChampionship chp = CapabilityChampionship.getChampionship(living);

            if (chp != null) {
                chp.getAffixes().stream().forEach(ordinal ->
                    EnumAffix.getAffix(ordinal).onKnockback(living, chp, evt)
                );
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent evt) {

        if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
            EntityLiving living = (EntityLiving)evt.getEntityLiving();
            IChampionship chp = CapabilityChampionship.getChampionship(living);

            if (chp != null) {

                chp.getAffixes().stream().forEach(ordinal ->
                    EnumAffix.getAffix(ordinal).onDeath(living, chp, evt.getSource(), evt)
                );
            }
        }
    }

    @SubscribeEvent
    public void onLivingHeal(LivingHealEvent evt) {

        if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
            float amount = evt.getAmount();
            float newAmount;

            EntityLiving living = (EntityLiving)evt.getEntityLiving();
            IChampionship chp = CapabilityChampionship.getChampionship(living);

            if (chp != null) {
                newAmount = chp.getAffixes().stream()
                    .mapToObj(ordinal -> EnumAffix.getAffix(ordinal).onHealed(living, chp,  amount, amount))
                    .filter(f -> f != amount)
                    .findAny()
                    .orElse(amount)
                ;
                evt.setAmount(newAmount);
            }
        }
    }
}
