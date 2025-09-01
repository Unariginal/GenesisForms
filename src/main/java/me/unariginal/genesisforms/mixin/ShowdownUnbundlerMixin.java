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

@Mixin(GraalShowdownUnbundler.class)
public class ShowdownUnbundlerMixin {
    @Unique
    private boolean loaded = false;

    @Inject(method = "attemptUnbundle", at = @At("TAIL"), remap = false)
    private void replaceScripts(CallbackInfo info) {
        if(!loaded) {
            loaded = true;
            Path showdown_sim = Path.of("./showdown/sim");
            Path showdown_data = Path.of("./showdown/data");

            try {
                Files.createDirectories(showdown_sim);
                Files.createDirectories(showdown_data);

                replaceFile("/showdown_scripts/battle-action.js", showdown_sim.resolve("battle-actions.js"));
                replaceFile("/showdown_scripts/conditions.js", showdown_data.resolve("conditions.js")); // ?
                replaceFile("/showdown_scripts/side.js", showdown_sim.resolve("side.js"));
                GenesisForms.LOGGER.info("[Genesis] Showdown files loaded!");
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