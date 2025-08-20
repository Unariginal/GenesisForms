package me.unariginal.genesisforms.mixin;

import com.cobblemon.mod.common.battles.runner.graal.GraalShowdownUnbundler;
import me.unariginal.genesisforms.GenesisForms;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Adapted from GMG/Mega Showdown @ yajatkaul
 */
@Mixin(GraalShowdownUnbundler.class)
public class ShowdownUnbundlerMixin {
    @Unique
    private boolean loaded = false;

    @Inject(method = "attemptUnbundle", at = @At("TAIL"), remap = false)
    private void replaceScripts(CallbackInfo info) {
        if(!loaded){
            loaded = true;
            Path showdown_sim = Path.of("./showdown/sim");
            Path showdown_data = Path.of("./showdown/data");
            Path showdown_dir = Path.of("./showdown");

            try {
                Files.createDirectories(showdown_sim);
                Files.createDirectories(showdown_data);

                replaceFile("/showdown_scripts/battle-action.js", showdown_sim.resolve("battle-actions.js"));
                replaceFile("/showdown_scripts/conditions.js", showdown_data.resolve("conditions.js")); // ?
                replaceFile("/showdown_scripts/index.js", showdown_dir.resolve("index.js"));
                replaceFile("/showdown_scripts/side.js", showdown_sim.resolve("side.js"));
                GenesisForms.LOGGER.info("[Genesis] Showdown files loaded!");

                // TESTING AUTOMATIC CUSTOM MEGAS
                // Adding mega stone item
//                Path itemsFilePath = showdown_data.resolve("items.js");
//                String itemsFileContent = new String(Files.readAllBytes(itemsFilePath));
//                if (!itemsFileContent.contains("miloticite")) {
//                    String modifiedItemsFileContent = itemsFileContent.replace("const Items = {",
//                            "const Items = {\n" +
//                                    "  miloticite: {\n" +
//                                    "    name: \"Miloticite\",\n" +
//                                    "    spritenum: 575,\n" +
//                                    "    megaStone: \"Milotic-Mega\",\n" +
//                                    "    megaEvolves: \"Milotic\",\n" +
//                                    "    itemUser: [\"Milotic\"],\n" +
//                                    "    onTakeItem(item, source) {\n" +
//                                    "      if (item.megaEvolves === source.baseSpecies.baseSpecies)\n" +
//                                    "        return false;\n" +
//                                    "      return true;\n" +
//                                    "    },\n" +
//                                    "    num: 674,\n" +
//                                    "    gen: 6,\n" +
//                                    "    isNonstandard: \"Past\"\n" +
//                                    "  },"
//                    );
//                    Files.write(itemsFilePath, modifiedItemsFileContent.getBytes());
//                }
            } catch (IOException e) {
                GenesisForms.LOGGER.error("[Genesis] Showdown files failed to load!");
            }
        }
    }

    @Unique
    private void replaceFile(String resourcePath, Path targetPath) {
        try (InputStream inputStream = this.getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                GenesisForms.LOGGER.error("[Genesis] Resource {} not found!", resourcePath);
                return;
            }
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            GenesisForms.LOGGER.info("[Genesis] Loaded showdown override file: {}", targetPath);
        } catch (IOException e) {
            GenesisForms.LOGGER.error("[Genesis] Failed to copy showdown override file {}!{}", resourcePath, e.getMessage());
        }
    }
}