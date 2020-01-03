package namewakander;

import com.google.common.collect.Lists;
import namewakander.utils.StringUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

import static namewakander.ConfigUtils.COMMON;

/**
 * Created by A.K. on 2019/09/17.
 */
public class RegisteredObjectBuilder<T extends IForgeRegistryEntry<T>> extends ObjectListBuilder<T> {
  public static final RegisteredObjectBuilder<Effect> EFFECT_REGISTERED_OBJECT_BUILDER = new RegisteredObjectBuilder<>(ForgeRegistries.POTIONS, "Effects", "RegistryName, EffectType, Color",
          (effect -> {
            String str = String.format("%s, %s",
                    effect.getEffectType().toString(), effect.getLiquidColor());
            return new IdNameObj<>(effect.getRegistryName(), str);
          }));
  public static final RegisteredObjectBuilder<Enchantment> ENCHANTMENT_REGISTERED_OBJECT_BUILDER = new RegisteredObjectBuilder<>(ForgeRegistries.ENCHANTMENTS, "Enchantments", "RegistryName, ModId, UnlocalizedName, LocalizedName",
          (enchantment -> {
            String str = String.format("%s, %s, %s",
                    enchantment.getRegistryName().getNamespace(), enchantment.getName(),
                    StringUtils.translateToLocal(enchantment.getName()));
            return new IdNameObj<>(enchantment.getRegistryName(), str);
          }));

  public static final RegisteredObjectBuilder<Biome> BIOME_REGISTERED_OBJECT_BUILDER = new RegisteredObjectBuilder<>(ForgeRegistries.BIOMES, "Biomes", "RegistryName, ModId, UnlocalizedName, LocalizedName",
          (biome -> {
            String str = String.format("%s, %s, %s", biome.getRegistryName().getNamespace(),
                    biome.getDisplayName().getString(),
                    StringUtils.translateToLocal(biome.getDisplayName().getString()));
            return new IdNameObj<>(biome.getRegistryName(), str);
          }));

  public static final RegisteredObjectBuilder<Potion> POTION_REGISTERED_OBJECT_BUILDER = new RegisteredObjectBuilder<>(ForgeRegistries.POTION_TYPES, "Potions", "RegistryName, Effects",
          (potion -> {
            StringJoiner joiner = new StringJoiner(",");
            potion.getEffects().forEach(e -> joiner.add(e.getEffectName()));
            String str = String.format("Effects:{%s}", joiner.toString());
            return new IdNameObj<>(potion.getRegistryName(), str);
          }));

  public static final RegisteredObjectBuilder<Fluid> FLUID_REGISTERED_OBJECT_BUILDER = new RegisteredObjectBuilder<>(ForgeRegistries.FLUIDS, "Fluids", "RegistryName, BucketItemName",
          (fluid -> {
            Item bucket = fluid.getFilledBucket();
            String unlocalizedName = String.format("item.%s.%s", bucket.getRegistryName().getNamespace(), bucket.getRegistryName().getPath());
            String str = String.format("%s", StringUtils.translateToLocal(unlocalizedName));
            return new IdNameObj<>(fluid.getRegistryName(), str);
          }));
  public static final RegisteredObjectBuilder<EntityType<?>> ENTITY_TYPE_REGISTERED_OBJECT_BUILDER = new RegisteredObjectBuilder<>(ForgeRegistries.ENTITIES, "EntityTypes", "RegistryName, UnlocalizedName, LocalizedName(if exist)",
          (entityType -> {
            String rl = entityType.getRegistryName().toString();
            String entityName = "entity." + rl;
            String str = String.format("%s, %s, %s", rl, entityName, StringUtils.translateToLocal(entityName));
            return new IdNameObj<>(entityType.getRegistryName(), str);
          }));
  public static final RegisteredObjectBuilder<DimensionType> DIMENSION_TYPE_REGISTERED_OBJECT_BUILDER = new RegisteredObjectBuilder<>(RegistryManager.VANILLA.getRegistry(DimensionType.class), "Dimensions", "RegistryName, id, Name",
          (dimensionType -> {
            String str = String.format("%s, %s", ((DimensionType)dimensionType).getId(), dimensionType.toString());
            return new IdNameObj<>(dimensionType.getRegistryName(), str);
          }));

  public static final RegisteredObjectBuilder<SoundEvent> SOUND_EVENT_REGISTERED_OBJECT_BUILDER = new RegisteredObjectBuilder<>(ForgeRegistries.SOUND_EVENTS, "SoundEvents", "RegistryName",
          (soundEvent -> new IdNameObj<>(soundEvent.getRegistryName(), "")));

  public static final RegisteredObjectBuilder<VillagerProfession> VILLAGER_PROFESSION_REGISTERED_OBJECT_BUILDER = new RegisteredObjectBuilder<>(ForgeRegistries.PROFESSIONS, "Professions", "RegistryName, PointOfInterestType, CultivatingItem, CultivatingBlock",
          villagerProfession -> {
            StringJoiner itemJoiner = new StringJoiner("/");
            villagerProfession.func_221146_c().asList().forEach(item -> itemJoiner.add(item.getRegistryName().toString()));
            String cultivatedItem = itemJoiner.toString();
            StringJoiner blockJoiner = new StringJoiner("/");
            villagerProfession.func_221150_d().asList().forEach(block -> blockJoiner.add(block.getRegistryName().toString()));
            String cultivatedBlock = blockJoiner.toString();
            String str = String.format("%s, %s, %s", villagerProfession.getPointOfInterest().toString(), cultivatedItem, cultivatedBlock);
            return new IdNameObj<>(villagerProfession.getRegistryName(), str);
          });

  private final List<IdNameObj<ResourceLocation>> list = Lists.newArrayList();
  private IForgeRegistry<T> registry;
  private Function<T, IdNameObj<ResourceLocation>> transformer;
  private String fileName;
  private String description;

  public RegisteredObjectBuilder(IForgeRegistry<T> registry, String fileName, String description, Function<T, IdNameObj<ResourceLocation>> transformer) {
    this.registry = registry;
    this.fileName = fileName;
    this.description = description;
    this.transformer = transformer;
  }

  @Override
  void create() {
    registry.forEach(this::addName);
  }

  @Override
  void writeToFile() {
    Collections.sort(list);
    printList(fileName + COMMON.ext,
            list,
            description,
            COMMON.outputCalculation);
  }

  @Override
  void addName(T t) {
    if (Objects.nonNull(t.getRegistryName())) {
      list.add(transformer.apply(t));
    }
  }
}
