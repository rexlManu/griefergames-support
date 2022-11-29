package de.rexlmanu.griefergamessupport.loader;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.bungee.providers.BungeeMovementTransmitter;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import de.rexlmanu.griefergamessupport.ViaVersionAddon;

public class AddonViaProviderLoader implements ViaPlatformLoader {
    @Override
    public void load() {
        Via.getManager().getProviders().use(MovementTransmitterProvider.class, new BungeeMovementTransmitter());
        Via.getManager().getProviders().use(VersionProvider.class, new BaseVersionProvider() {
            @Override
            public int getClosestServerProtocol(UserConnection connection) throws Exception {
                if (connection.isClientSide())
                    return ViaVersionAddon.getInstance().getVersion();
                return super.getClosestServerProtocol(connection);
            }
        });
    }

    @Override
    public void unload() {

    }
}
