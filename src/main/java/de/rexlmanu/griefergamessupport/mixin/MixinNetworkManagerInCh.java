package de.rexlmanu.griefergamessupport.mixin;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import de.rexlmanu.griefergamessupport.ViaVersionAddon;
import de.rexlmanu.griefergamessupport.handler.CommonTransformer;
import de.rexlmanu.griefergamessupport.handler.ViaDecodeHandler;
import de.rexlmanu.griefergamessupport.handler.ViaEncodeHandler;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import java.util.logging.Logger;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/network/NetworkManager$1")
public class MixinNetworkManagerInCh {

  @Inject(method = "initChannel", at = @At(value = "TAIL"), remap = false)
  private void onInitChannel(Channel channel, CallbackInfo ci) {
    if (channel instanceof SocketChannel
        && Minecraft.getInstance().getCurrentServerData() != null) {
      String serverIP = Minecraft.getInstance().getCurrentServerData().serverIP;
      System.out.println(serverIP);
      if (!ViaVersionAddon.checkWhitelist(serverIP)) {
        ViaVersionAddon.getInstance().setVersion(ViaVersionAddon.SHARED_VERSION);
        return;
      }
      Logger.getLogger(getClass().getSimpleName())
          .info("Found server " + serverIP + ", enabling viaversion");
      UserConnection user = new UserConnectionImpl(channel, true);
      new ProtocolPipelineImpl(user);

      channel.pipeline()
          .addBefore("encoder", CommonTransformer.HANDLER_ENCODER_NAME, new ViaEncodeHandler(user))
          .addBefore("decoder", CommonTransformer.HANDLER_DECODER_NAME, new ViaDecodeHandler(user));
    }
  }

}
