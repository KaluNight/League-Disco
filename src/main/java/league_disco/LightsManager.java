package league_disco;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.c0urante.joplin.HueEntertainmentClient;
import io.github.zeroone3010.yahueapi.Color;
import io.github.zeroone3010.yahueapi.HueBridgeConnectionBuilder;
import io.github.zeroone3010.yahueapi.v2.Hue;
import io.github.zeroone3010.yahueapi.v2.Light;
import io.github.zeroone3010.yahueapi.v2.UpdateState;
import io.github.zeroone3010.yahueapi.v2.domain.update.EffectType;
import league_disco.light_effect.LightEffect;
import no.stelar7.api.r4j.pojo.lol.liveclient.events.ChampionKillEvent;
import no.stelar7.api.r4j.pojo.lol.liveclient.events.GameEvent;

public class LightsManager {
  
  private static final String PHILIPS_HUE_API_KEY_TXT = "apiKey.txt";
  private String player;
  private CurrentLightEffect currentLightEffect;
  
  private Hue hue;
  
  private List<Light> lightsStrips = new ArrayList<>();
  private List<Light> roofLights = new ArrayList<>();
  private List<Light> deskLights = new ArrayList<>();
  
  private CurrentLightEffect currentEffectAsked = null;
  private LightEffect currentEffect = null;
  private Thread currentEffectThread = null;
  
  private static final Logger logger = LoggerFactory.getLogger(LightsManager.class);
  
  private LightsManager(String player) {
    this.player = player;
    this.currentLightEffect = CurrentLightEffect.ALIVE;
  }
  
  public LightsManager(String player, String bridgeIp) throws InterruptedException, ExecutionException, IOException {
    this(player);
    hue = new Hue(bridgeIp, getHueKey(bridgeIp));
  }
  
  private static String getHueKey(String bridgeIp) throws InterruptedException, ExecutionException {
    String key = FileUtil.readFile(PHILIPS_HUE_API_KEY_TXT);
    
    if (!key.isBlank()) {
      return key;
    }
    
    final String appName = "League-Disco";
    final CompletableFuture<String> apiKey = new HueBridgeConnectionBuilder(bridgeIp).initializeApiConnection(appName);

    logger.info("Push the button on your Hue Bridge to get the API key!");
    
    key = apiKey.get();
    FileUtil.writeFile(PHILIPS_HUE_API_KEY_TXT, key);
    
    return key;
  }
  
  public void discoverLights() {
    Map<UUID, Light> allLighs = hue.getLights();
    
    for (Light light : allLighs.values()) {
      if(light.getName().contains("lightstrip")) {
        lightsStrips.add(light);
      }
      
      if (light.getName().contains("roof")) {
        roofLights.add(light);
      }
      
      if (light.getName().contains("direct-desk")) {
        deskLights.add(light);
      }
    }
  }
  
  public void handleEvent(GameEvent event) {
    
    if(event == null) { // Test
      triggerEffect(CurrentLightEffect.ALIVE);
    }
    
    if (event instanceof ChampionKillEvent kill) {
      if (kill.getKillerName().equals(player.split("#")[0])) {
        triggerEffect(CurrentLightEffect.CHAMPION_KILL);
      } else if (kill.getVictimName().equals(player.split("#")[0])){
        triggerEffect(CurrentLightEffect.CHAMPION_DEATH);
      }
    }
  }
  
  public boolean isEffectRunning() {
    return currentEffect != null && currentEffect.isRunning();
  }

  public synchronized void triggerEffect(CurrentLightEffect lightEffectAsked) {
    if(currentEffect != null) {
      currentEffect.stop();
    }
    
    if (currentEffectThread != null) {
      try {
        currentEffectThread.join();
      } catch (InterruptedException e) {
        logger.error("Error while stopping current effect", e);
        Thread.currentThread().interrupt();
      }
    }
    
    currentEffectAsked = lightEffectAsked;
    currentEffect = lightEffectAsked.getEffect(lightsStrips, roofLights, deskLights);
    currentEffectThread = Thread.ofVirtual().start(currentEffect);
  }

  public CurrentLightEffect getCurrentEffectAsked() {
    return currentEffectAsked;
  }
  
}
