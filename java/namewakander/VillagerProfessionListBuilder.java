package namewakander;

import com.google.common.collect.Lists;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.util.List;
import java.util.Map;

import static namewakander.NameWakander.ext;

public class VillagerProfessionListBuilder extends ObjectListBuilder {
    private final List<String> villagerProfessionList = Lists.newArrayList();

    @Override
    void create() {
        for (Map.Entry<ResourceLocation, VillagerRegistry.VillagerProfession> entry : RegistryManager.ACTIVE.getRegistry(VillagerRegistry.VillagerProfession.class).getEntries()) {
            ResourceLocation regName = entry.getKey();
            VillagerRegistry.VillagerProfession profession = entry.getValue();
            int indexCareer = 0;
            VillagerRegistry.VillagerCareer initCareer = profession.getCareer(0);
            do {
                VillagerRegistry.VillagerCareer career = profession.getCareer(indexCareer);
                String careerName = career.getName();
                int tradeLevel = 0;
                while (career.getTrades(tradeLevel) != null) {
                    List<EntityVillager.ITradeList> tradeLists = career.getTrades(tradeLevel++);
                    if (tradeLists == null) continue;
                    for (EntityVillager.ITradeList tradeList : tradeLists) {
                        if (tradeList instanceof EntityVillager.EmeraldForItems) {
                            EntityVillager.EmeraldForItems emeraldForItems = (EntityVillager.EmeraldForItems) tradeList;
                            String s = String.format("%s,%s,Emerald:%d,%s->%s",
                                    regName.toString(), careerName, emeraldForItems.price.getFirst(), emeraldForItems.price.getSecond(),
                                    emeraldForItems.buyingItem.getRegistryName());
                            villagerProfessionList.add(s);
                        } else if (tradeList instanceof EntityVillager.ItemAndEmeraldToItem) {
                            EntityVillager.ItemAndEmeraldToItem itemAndEmeraldToItem = (EntityVillager.ItemAndEmeraldToItem) tradeList;
                            String s = String.format("%s,%s,Item:%s,Emerald:%d,%d->Item:%s,Emerald:%d,%d",
                                    regName.toString(), careerName,
                                    itemAndEmeraldToItem.buyingItemStack.getItem().getRegistryName(),
                                    itemAndEmeraldToItem.buyingPriceInfo.getFirst(), itemAndEmeraldToItem.buyingPriceInfo.getSecond(),
                                    itemAndEmeraldToItem.sellingItemstack.getItem().getRegistryName(),
                                    itemAndEmeraldToItem.sellingPriceInfo.getFirst(), itemAndEmeraldToItem.sellingPriceInfo.getSecond());
                            villagerProfessionList.add(s);
                        } else if (tradeList instanceof EntityVillager.ListEnchantedBookForEmeralds) {
                            String s = String.format("%s,%s,Item:%s->Emerald:?",
                                    regName.toString(), careerName, Items.ENCHANTED_BOOK.getRegistryName());
                            villagerProfessionList.add(s);
                        } else if (tradeList instanceof EntityVillager.ListEnchantedItemForEmeralds) {
                            EntityVillager.ListEnchantedItemForEmeralds enchantedItemForEmeralds = (EntityVillager.ListEnchantedItemForEmeralds) tradeList;
                            String s = String.format("%s,%s,Item:%s->Emerald:%d,%d",
                                    regName.toString(), careerName,
                                    enchantedItemForEmeralds.enchantedItemStack.getItem().getRegistryName(),
                                    enchantedItemForEmeralds.priceInfo.getFirst(), enchantedItemForEmeralds.priceInfo.getSecond());
                            villagerProfessionList.add(s);
                        } else if (tradeList instanceof EntityVillager.ListItemForEmeralds) {
                            EntityVillager.ListItemForEmeralds itemForEmeralds = (EntityVillager.ListItemForEmeralds) tradeList;
                            String s = String.format("%s,%s,Emerald:%d,%d->Item:%s",
                                    regName.toString(), careerName,
                                    itemForEmeralds.priceInfo.getFirst(), itemForEmeralds.priceInfo.getSecond(),
                                    itemForEmeralds.itemToBuy.getItem().getRegistryName()
                            );
                            villagerProfessionList.add(s);
                        }
                    }
                }
            } while (!initCareer.equals(profession.getCareer(++indexCareer)));
        }
    }

    @Override
    void writeToFile() {
        printNameList("VillagerProfessionList" + ext, villagerProfessionList, "ProfessionName, CareerName, TradeInfo", true);
    }

}
