package namewakander;

import com.google.common.collect.Lists;
import namewakander.utils.StringUtils;
import net.minecraft.world.biome.Biome;

import java.util.Collections;
import java.util.List;

import static namewakander.NameWakander.ext;

public class BiomeListBuilder extends ObjectListBuilder {
    private final List<IdNameObj<Integer>> biomeIdList = Lists.newArrayList();

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
        printList("BiomeIDs" + ext,
                biomeIdList,
                "UniqueId, ModId, UnlocalizedName, LocalizedName",
                true);
    }

    private void addBiomeName(Biome biome) {
        if (biome.getRegistryName() != null) {
            String str = String.format("%s, %s, %s", biome.getRegistryName().getResourceDomain(), biome.getRegistryName().toString(), StringUtils.translateToLocal(biome.getRegistryName().toString()));
            biomeIdList.add(new IdNameObj<>(Biome.getIdForBiome(biome), str));
        }
    }
}
