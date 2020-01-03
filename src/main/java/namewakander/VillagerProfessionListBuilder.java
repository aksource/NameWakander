package namewakander;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import namewakander.utils.StringUtils;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

import static namewakander.ConfigUtils.COMMON;

public class VillagerProfessionListBuilder extends ObjectListBuilder<VillagerProfession> {

  private static final Random RND_CONST_WORST = new Random() {
    @Override
    public float nextFloat() {
      return 0;
    }

    @Override
    public int nextInt(int bound) {
      return 0;
    }
  };
  private static final Random RND_CONST_BEST = new Random() {
    @Override
    public float nextFloat() {
      return 1;
    }

    @Override
    public int nextInt(int bound) {
      return bound;
    }
  };
  private final List<String> villagerProfessionList = Lists.newArrayList();

  @Override
  void create() {
    ForgeRegistries.PROFESSIONS.forEach(this::addName);
    for (Map.Entry<ResourceLocation, VillagerProfession> entry : ForgeRegistries.PROFESSIONS.getEntries()) {
      ResourceLocation regName = entry.getKey();
      VillagerProfession profession = entry.getValue();
      String professionName = StringUtils.translateToLocal("entity.minecraft.villager." + profession.toString());
      Int2ObjectMap<VillagerTrades.ITrade[]> map = VillagerTrades.field_221239_a.get(profession);
      Function<MerchantOffer, String> merchantOfferFormatter = merchantOffer -> String.format("%s,%s,%s,%s", regName.toString(), professionName);
      map.forEach((id, trades) -> {
        for (VillagerTrades.ITrade trade : trades) {
          String s = merchantOfferFormatter.apply(trade.getOffer(null, new Random()));
          villagerProfessionList.add(s);
        }
      });
    }
  }

  @Override
  void writeToFile() {
    printNameList("VillagerProfessionList" + COMMON.ext, villagerProfessionList,
            "ProfessionName, CareerName, TradeInfo", true);
  }

  @Override
  void addName(VillagerProfession villagerProfession) {
    if (Objects.nonNull(villagerProfession.getRegistryName())) {
      String professionName = StringUtils.translateToLocal("entity.minecraft.villager." + villagerProfession.toString());
      Int2ObjectMap<VillagerTrades.ITrade[]> map = VillagerTrades.field_221239_a.get(villagerProfession);
      Function<MerchantOffer, String> merchantOfferFormatter = merchantOffer -> String.format("%s,%s,%s,%s", villagerProfession.getRegistryName().toString(), professionName);
      map.forEach((id, trades) -> {
        for (VillagerTrades.ITrade trade : trades) {
          String s = merchantOfferFormatter.apply(trade.getOffer(null, new Random()));
          villagerProfessionList.add(s);
        }
      });
    }
  }

}
