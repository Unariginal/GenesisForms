package me.unariginal.genesisforms.mixin;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class FixTheUhOhs {
    @Inject(method = "exceptionCaught", at = @At("HEAD"), cancellable = true)
    private void handleTeraPacketEncoderException(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
        if (ex instanceof EncoderException) {
            String errorMessage = ex.getMessage();

            if (errorMessage != null && errorMessage.contains("Failed to encode packet") && (errorMessage.contains("cobblemon:tera_type_update") || errorMessage.contains("minecraft:set_entity_data"))) {
                ci.cancel();
            }
        }
    }
}
