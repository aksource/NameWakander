package NameWakander;

import com.google.common.collect.Lists;
import net.minecraft.init.Biomes;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.biome.Biome;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static NameWakander.NameWakander.ext;

public class BiomeListBuilder extends ObjectListBuilder {
    private List<IdNameObj<Integer>> biomeIdList = Lists.newArrayList();
    @Override
    void create() {
        for (Biome biome : Biome.REGISTRY) {
            if (biome != null) {
                addBiomeName(biome);
            }
        }
    }

    @Override
    void writeToFile() {
        Collections.sort(biomeIdList);
        printList("BiomeIDs" + ext, biomeIdList, true);
    }

    @SuppressWarnings("Deprecated")
    private void addBiomeName(Biome biome) {
        String str = String.format("%s, %s", Optional.ofNullable(biome.getRegistryName()).orElse(Biomes.DEFAULT.getRegistryName()).toString(), I18n.translateToLocal(biome.getRegistryName().toString()));
        biomeIdList.add(new IdNameObj<>(Biome.getIdForBiome(biome), str));
    }
}
