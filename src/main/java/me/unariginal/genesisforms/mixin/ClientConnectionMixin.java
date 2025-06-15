package me.unariginal.genesisforms.mixin;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Most of these probably don't need to be here...
 * Adapted from Niko's Tera! @ Nxkorasu
 */
@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "exceptionCaught", at = @At("HEAD"), cancellable = true)
    private void handlePacketEncoderException(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
        if (ex instanceof EncoderException) {
            String errorMessage = ex.getMessage();

            if (errorMessage != null &&
                    (
                            errorMessage.contains("cobblemon:tera_type_update") ||
                                    errorMessage.contains("minecraft:set_entity_data") ||
                                    errorMessage.contains("minecraft:set_equipment") ||
                                    errorMessage.contains("minecraft:custom_payload") ||
                                    errorMessage.contains("minecraft:system_chat")
                    )
            ) {
                ci.cancel();
            }
        }
    }
}