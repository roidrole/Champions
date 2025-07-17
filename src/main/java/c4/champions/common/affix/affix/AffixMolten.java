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
import c4.champions.common.capability.IChampionship;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.Iterator;

public class AffixMolten extends AffixBase {

    public AffixMolten() {
        super("molten", AffixCategory.OFFENSE);
    }

    @Override
    public void onJoinWorld(EntityLiving entity, IChampionship cap, EntityJoinWorldEvent evt) {
        entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 40, 0, true, false));
        entity.setPathPriority(PathNodeType.WATER, -1.0F);
        entity.setPathPriority(PathNodeType.LAVA, 8.0F);
        entity.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
        entity.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);

        Iterator<EntityAITasks.EntityAITaskEntry> iterator = entity.tasks.taskEntries.iterator();

        while (iterator.hasNext()) {
            EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry = iterator.next();
            EntityAIBase entityaibase = entityaitasks$entityaitaskentry.action;

            if (entityaibase instanceof EntityAIFleeSun || entityaibase instanceof EntityAIRestrictSun) {
                iterator.remove();
            }
        }

        if (entity.getNavigator() instanceof PathNavigateGround) {
            ((PathNavigateGround) entity.getNavigator()).setAvoidSun(false);
        }
    }

    @Override
    public void onUpdate(EntityLiving entity, IChampionship cap, LivingEvent.LivingUpdateEvent evt) {

        if (!entity.world.isRemote) {

            if (entity.ticksExisted % 20 == 0) {
                entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 40, 0, true, false));
            }

            if (!ConfigHandler.affix.molten.waterResistant && entity.isWet()) {
                entity.attackEntityFrom(DamageSource.DROWN, 1.0F);
            }
        }

        if (!entity.isImmuneToFire()) {
            entity.isImmuneToFire = true;
        }
    }

    @Override
    public void onAttack(EntityLiving entity, IChampionship cap, LivingAttackEvent evt) {
        evt.getEntityLiving().setFire(10);
        evt.getSource().setMagicDamage();
    }
}
