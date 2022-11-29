package de.rexlmanu.griefergamessupport;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import de.rexlmanu.griefergamessupport.loader.AddonBackwardsLoader;
import de.rexlmanu.griefergamessupport.loader.AddonRewindLoader;
import de.rexlmanu.griefergamessupport.loader.AddonViaProviderLoader;
import de.rexlmanu.griefergamessupport.platform.AddonInjector;
import de.rexlmanu.griefergamessupport.platform.AddonPlatform;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import lombok.Getter;
import lombok.Setter;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;

@Getter
public class ViaVersionAddon extends LabyModAddon {

  public static final String NAME = "GrieferGamesSupport";
  public static final List<String> SERVERS = Arrays.asList(
      "griefergames.net"
  );

  public static final int SHARED_VERSION = 754;

  @Getter
  private static ViaVersionAddon instance;

  public static boolean checkWhitelist(String input) {
    if(!input.contains(".")) return false;
    String[] parts = input.split("\\.");
    if(parts.length <= 2) {
      return SERVERS.contains(input);
    }
    String domain = parts[parts.length - 2] + "." + parts[parts.length - 1];
    return SERVERS.contains(domain);
  }

  private final CompletableFuture<Void> initFuture = new CompletableFuture<>();

  private ExecutorService executorService;
  private EventLoop eventLoop;

  private File dataFolder;

  @Getter
  @Setter
  private int version;

  public ViaVersionAddon() {
    ViaVersionAddon.instance = this;

    ThreadFactory factory = new ThreadFactoryBuilder()
        .setDaemon(true)
        .setNameFormat("GrieferGamesSupport-%d")
        .build();
    this.executorService = Executors.newFixedThreadPool(
        8,
        factory

    );

    this.eventLoop = new DefaultEventLoop(factory).next();
    this.eventLoop.submit(initFuture::join);

    this.dataFolder = new File(NAME.toLowerCase());

    this.version = SHARED_VERSION;

    this.dataFolder.mkdir();
  }

  @Override
  public void onEnable() {
    try {
      Via.init(
          ViaManagerImpl.builder()
              .injector(new AddonInjector())
              .platform(new AddonPlatform(this.dataFolder))
              .loader(new AddonViaProviderLoader())
              .build()
      );
      MappingDataLoader.enableMappingsCache();
      ((ViaManagerImpl) Via.getManager()).init();
    } catch (IllegalArgumentException e) {
      System.out.println("ViaVersion already initialized");
    }

    new AddonBackwardsLoader(new File(this.dataFolder, "backwards"));
    new AddonRewindLoader(new File(this.dataFolder, "viarewind"));

    this.initFuture.complete(null);
  }

  @Override
  public void loadConfig() {

  }

  @Override
  protected void fillSettings(List<SettingsElement> list) {
  }
}
