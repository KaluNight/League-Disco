package league_disco;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zeroone3010.yahueapi.HueBridgeConnectionBuilder;
import io.github.zeroone3010.yahueapi.v2.Hue;
import no.stelar7.api.r4j.pojo.lol.liveclient.events.ChampionKillEvent;
import no.stelar7.api.r4j.pojo.lol.liveclient.events.GameEvent;

public class LightEffects {
  
  private static final String PHILIPS_HUE_API_KEY_TXT = "apiKey.txt";
  private String player;
  private CurrentLightEffect currentLightEffect;
  
  private Hue hue;
  
  private static final Logger logger = LoggerFactory.getLogger(LightEffects.class);
  
  private LightEffects(String player) {
    this.player = player;
    this.currentLightEffect = CurrentLightEffect.ALIVE;
  }
  
  public LightEffects(String player, String bridgeIp) throws InterruptedException, ExecutionException {
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
  
  public void handleEvent(GameEvent event) {
    
    if (event instanceof ChampionKillEvent kill) {
      if (kill.getKillerName().equals(player)) {
        triggerEffect(CurrentLightEffect.CHAMPION_KILL);
      } else if (kill.getVictimName().equals(player)){
        triggerEffect(CurrentLightEffect.CHAMPION_DEATH);
      }
    }
    
  }

  private void triggerEffect(CurrentLightEffect lightEffectAsked) {
    
    
  }
}
