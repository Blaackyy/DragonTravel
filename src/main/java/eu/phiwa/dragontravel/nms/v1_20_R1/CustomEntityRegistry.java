package eu.phiwa.dragontravel.nms.v1_20_R1;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ambient.EntityBat;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.*;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.entity.decoration.*;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.item.EntityTNTPrimed;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.EntityVillagerTrader;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.vehicle.*;
import org.bukkit.entity.Camel;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class CustomEntityRegistry extends DefaultedMappedRegistry {
    private final BiMap<MinecraftKey, EntityTypes> entities = HashBiMap.create();
    private final BiMap<EntityTypes, MinecraftKey> entityClasses = this.entities.inverse();
    private final Map<EntityTypes, Integer> entityIds = Maps.newHashMap();
    private final RegistryMaterials<EntityTypes<?>> wrapped;

    public CustomEntityRegistry(DefaultedMappedRegistry<EntityTypes<?>> original) {
        //super(original.a().getNamespace(), null, null);
        //super(original.a().b(), null, null);
        super(original.a().b(), null, null, false);
        this.wrapped = original;
    }

    @Override
    public int a(Object key) {
        if (entityIds.containsKey(key)) {
            return entityIds.get(key);
        }
        //return key.hashCode();
        return wrapped.a((EntityTypes) key);
    }

    @Override
    public Optional a(RandomSource paramRandom) {
        return wrapped.a(paramRandom);
    }

    public EntityTypes findType(Class<?> search) {
        return minecraftClassMap.inverse().get(search);
        /*
        for (Object type : wrapped) {
            if (minecraftClassMap.get(type) == search) {
                return (EntityTypes) type;
            }
        }
        return null;
        */
    }

    //@Override
    public Object fromId(int var0) {
        //return this.wrapped.fromId(var0);
        return this.wrapped.a(var0);
    }

    //@Override
    public EntityTypes get(MinecraftKey key) {
        if (entities.containsKey(key)) {
            return entities.get(key);
        }

        //return wrapped.get(key);
        return wrapped.a(key);
    }

    //@Override
    public MinecraftKey getKey(Object value) {
        if (entityClasses.containsKey(value)) {
            return entityClasses.get(value);
        }

        //return wrapped.getKey((EntityTypes) value);
        return wrapped.b((EntityTypes) value);
    }

    //@Override
    public Optional getOptional(MinecraftKey var0) {
        if (entities.containsKey(var0)) {
            return Optional.of(entities.get(var0));
        }

        //return this.wrapped.getOptional(var0);
        return this.wrapped.b(var0);
    }

    public RegistryMaterials<EntityTypes<?>> getWrapped() {
        return wrapped;
    }

    @Override
    public Iterator<Object> iterator() {
        return (Iterator) wrapped.iterator();
    }

    //@Override
    public Set<Object> keySet() {
        //return (Set) wrapped.keySet();
        return (Set) wrapped.e();
    }

    public void put(int entityId, MinecraftKey key, EntityTypes entityClass) {
        entities.put(key, entityClass);
        entityIds.put(entityClass, entityId);
    }

    // replace regex
    // ([A-Z_]+).*?a\(E(.*?)::new.*?$
    // minecraftClassMap.put(EntityTypes.\1, E\2.class);
    private static final BiMap<EntityTypes, Class<?>> minecraftClassMap = HashBiMap.create();

    static {
        minecraftClassMap.put(EntityTypes.b, Allay.class);
        minecraftClassMap.put(EntityTypes.c, EntityAreaEffectCloud.class);
        minecraftClassMap.put(EntityTypes.d, EntityArmorStand.class);
        minecraftClassMap.put(EntityTypes.e, EntityTippedArrow.class);
        minecraftClassMap.put(EntityTypes.f, Axolotl.class);
        minecraftClassMap.put(EntityTypes.g, EntityBat.class);
        minecraftClassMap.put(EntityTypes.h, EntityBee.class);
        minecraftClassMap.put(EntityTypes.i, EntityBlaze.class);
        minecraftClassMap.put(EntityTypes.j, Display.BlockDisplay.class);
        minecraftClassMap.put(EntityTypes.k, EntityBoat.class);
        minecraftClassMap.put(EntityTypes.l, Camel.class);
        minecraftClassMap.put(EntityTypes.m, EntityCat.class);
        minecraftClassMap.put(EntityTypes.n, EntityCaveSpider.class);
        minecraftClassMap.put(EntityTypes.o, ChestBoat.class);
        minecraftClassMap.put(EntityTypes.p, EntityMinecartChest.class);
        minecraftClassMap.put(EntityTypes.q, EntityChicken.class);
        minecraftClassMap.put(EntityTypes.r, EntityCod.class);
        minecraftClassMap.put(EntityTypes.s, EntityMinecartCommandBlock.class);
        minecraftClassMap.put(EntityTypes.t, EntityCow.class);
        minecraftClassMap.put(EntityTypes.u, EntityCreeper.class);
        minecraftClassMap.put(EntityTypes.v, EntityDolphin.class);
        minecraftClassMap.put(EntityTypes.w, EntityHorseDonkey.class);
        minecraftClassMap.put(EntityTypes.x, EntityDragonFireball.class);
        minecraftClassMap.put(EntityTypes.y, EntityDrowned.class);
        minecraftClassMap.put(EntityTypes.z, EntityEgg.class);
        minecraftClassMap.put(EntityTypes.A, EntityGuardianElder.class);
        minecraftClassMap.put(EntityTypes.B, EntityEnderCrystal.class);
        minecraftClassMap.put(EntityTypes.C, EntityEnderDragon.class);
        minecraftClassMap.put(EntityTypes.D, EntityEnderPearl.class);
        minecraftClassMap.put(EntityTypes.E, EntityEnderman.class);
        minecraftClassMap.put(EntityTypes.F, EntityEndermite.class);
        minecraftClassMap.put(EntityTypes.G, EntityEvoker.class);
        minecraftClassMap.put(EntityTypes.H, EntityEvokerFangs.class);
        minecraftClassMap.put(EntityTypes.I, EntityThrownExpBottle.class);
        minecraftClassMap.put(EntityTypes.J, EntityExperienceOrb.class);
        minecraftClassMap.put(EntityTypes.K, EntityEnderSignal.class);
        minecraftClassMap.put(EntityTypes.L, EntityFallingBlock.class);
        minecraftClassMap.put(EntityTypes.M, EntityFireworks.class);
        minecraftClassMap.put(EntityTypes.N, EntityFox.class);
        minecraftClassMap.put(EntityTypes.O, Frog.class);
        minecraftClassMap.put(EntityTypes.P, EntityMinecartFurnace.class);
        minecraftClassMap.put(EntityTypes.Q, EntityGhast.class);
        minecraftClassMap.put(EntityTypes.R, EntityGiantZombie.class);
        minecraftClassMap.put(EntityTypes.S, GlowItemFrame.class);
        minecraftClassMap.put(EntityTypes.T, GlowSquid.class);
        minecraftClassMap.put(EntityTypes.U, Goat.class);
        minecraftClassMap.put(EntityTypes.V, EntityGuardian.class);
        minecraftClassMap.put(EntityTypes.W, EntityHoglin.class);
        minecraftClassMap.put(EntityTypes.X, EntityMinecartHopper.class);
        minecraftClassMap.put(EntityTypes.Y, EntityHorse.class);
        minecraftClassMap.put(EntityTypes.Z, EntityZombieHusk.class);
        minecraftClassMap.put(EntityTypes.aa, EntityIllagerIllusioner.class);
        minecraftClassMap.put(EntityTypes.ab, Interaction.class);
        minecraftClassMap.put(EntityTypes.ac, EntityIronGolem.class);
        minecraftClassMap.put(EntityTypes.ad, EntityItem.class);
        minecraftClassMap.put(EntityTypes.ae, Display.ItemDisplay.class);
        minecraftClassMap.put(EntityTypes.af, EntityItemFrame.class);
        minecraftClassMap.put(EntityTypes.ag, EntityLargeFireball.class);
        minecraftClassMap.put(EntityTypes.ah, EntityLeash.class);
        minecraftClassMap.put(EntityTypes.ai, EntityLightning.class);
        minecraftClassMap.put(EntityTypes.aj, EntityLlama.class);
        minecraftClassMap.put(EntityTypes.ak, EntityLlamaSpit.class);
        minecraftClassMap.put(EntityTypes.al, EntityMagmaCube.class);
        minecraftClassMap.put(EntityTypes.am, Marker.class);
        minecraftClassMap.put(EntityTypes.an, EntityMinecartRideable.class);
        minecraftClassMap.put(EntityTypes.ao, EntityMushroomCow.class);
        minecraftClassMap.put(EntityTypes.ap, EntityHorseMule.class);
        minecraftClassMap.put(EntityTypes.aq, EntityOcelot.class);
        minecraftClassMap.put(EntityTypes.ar, EntityPainting.class);
        minecraftClassMap.put(EntityTypes.as, EntityPanda.class);
        minecraftClassMap.put(EntityTypes.at, EntityParrot.class);
        minecraftClassMap.put(EntityTypes.au, EntityPhantom.class);
        minecraftClassMap.put(EntityTypes.av, EntityPig.class);
        minecraftClassMap.put(EntityTypes.aw, EntityPiglin.class);
        minecraftClassMap.put(EntityTypes.ax, EntityPiglinBrute.class);
        minecraftClassMap.put(EntityTypes.ay, EntityPillager.class);
        minecraftClassMap.put(EntityTypes.az, EntityPolarBear.class);
        minecraftClassMap.put(EntityTypes.aA, EntityPotion.class);
        minecraftClassMap.put(EntityTypes.aB, EntityPufferFish.class);
        minecraftClassMap.put(EntityTypes.aC, EntityRabbit.class);
        minecraftClassMap.put(EntityTypes.aD, EntityRavager.class);
        minecraftClassMap.put(EntityTypes.aE, EntitySalmon.class);
        minecraftClassMap.put(EntityTypes.aF, EntitySheep.class);
        minecraftClassMap.put(EntityTypes.aG, EntityShulker.class);
        minecraftClassMap.put(EntityTypes.aH, EntityShulkerBullet.class);
        minecraftClassMap.put(EntityTypes.aI, EntitySilverfish.class);
        minecraftClassMap.put(EntityTypes.aJ, EntitySkeleton.class);
        minecraftClassMap.put(EntityTypes.aK, EntityHorseSkeleton.class);
        minecraftClassMap.put(EntityTypes.aL, EntitySlime.class);
        minecraftClassMap.put(EntityTypes.aM, EntitySmallFireball.class);
        minecraftClassMap.put(EntityTypes.aN, Sniffer.class);
        minecraftClassMap.put(EntityTypes.aO, EntitySnowman.class);
        minecraftClassMap.put(EntityTypes.aP, EntitySnowball.class);
        minecraftClassMap.put(EntityTypes.aQ, EntityMinecartMobSpawner.class);
        minecraftClassMap.put(EntityTypes.aR, EntitySpectralArrow.class);
        minecraftClassMap.put(EntityTypes.aS, EntitySpider.class);
        minecraftClassMap.put(EntityTypes.aT, EntitySquid.class);
        minecraftClassMap.put(EntityTypes.aU, EntitySkeletonStray.class);
        minecraftClassMap.put(EntityTypes.aV, EntityStrider.class);
        minecraftClassMap.put(EntityTypes.aW, Tadpole.class);
        minecraftClassMap.put(EntityTypes.aX, Display.TextDisplay.class);
        minecraftClassMap.put(EntityTypes.aY, EntityTNTPrimed.class);
        minecraftClassMap.put(EntityTypes.aZ, EntityMinecartTNT.class);
        minecraftClassMap.put(EntityTypes.ba, EntityLlamaTrader.class);
        minecraftClassMap.put(EntityTypes.bb, EntityThrownTrident.class);
        minecraftClassMap.put(EntityTypes.bc, EntityTropicalFish.class);
        minecraftClassMap.put(EntityTypes.bd, EntityTurtle.class);
        minecraftClassMap.put(EntityTypes.be, EntityVex.class);
        minecraftClassMap.put(EntityTypes.bf, EntityVillager.class);
        minecraftClassMap.put(EntityTypes.bg, EntityVindicator.class);
        minecraftClassMap.put(EntityTypes.bh, EntityVillagerTrader.class);
        minecraftClassMap.put(EntityTypes.bi, Warden.class);
        minecraftClassMap.put(EntityTypes.bj, EntityWitch.class);
        minecraftClassMap.put(EntityTypes.bk, EntityWither.class);
        minecraftClassMap.put(EntityTypes.bl, EntitySkeletonWither.class);
        minecraftClassMap.put(EntityTypes.bm, EntityWitherSkull.class);
        minecraftClassMap.put(EntityTypes.bn, EntityWolf.class);
        minecraftClassMap.put(EntityTypes.bo, EntityZoglin.class);
        minecraftClassMap.put(EntityTypes.bp, EntityZombie.class);
        minecraftClassMap.put(EntityTypes.bq, EntityHorseZombie.class);
        minecraftClassMap.put(EntityTypes.br, EntityZombieVillager.class);
        minecraftClassMap.put(EntityTypes.bs, EntityPigZombie.class);
        minecraftClassMap.put(EntityTypes.bt, EntityHuman.class);
        minecraftClassMap.put(EntityTypes.bu, EntityFishingHook.class);
    }
}