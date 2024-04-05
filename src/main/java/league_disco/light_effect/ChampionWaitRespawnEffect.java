package league_disco.light_effect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.c0urante.joplin.HueEntertainmentClient;
import io.github.zeroone3010.yahueapi.v2.Light;
import league_disco.EntertainementApi;
import league_disco.Main;

public class ChampionWaitRespawnEffect extends LightEffect {

  private List<Thread> threads = new ArrayList<>();
  private AtomicBoolean runningInternal = new AtomicBoolean(true);

  public ChampionWaitRespawnEffect(List<Light> lightsStrips, List<Light> roofLights,
      List<Light> deskLights) {
    super("Wait respawn effect", lightsStrips, roofLights, deskLights);
  }

  @Override
  public void run() {
    HueEntertainmentClient clientInternal = null;

    try {
      clientInternal = EntertainementApi.createClient(Main.BRIDGE_IP);

      int[] bulbs = {0, 1, 2, 3, 4}; // Buble id seems to be not the same as the color number
      for (int bulb : bulbs) {
        Thread thread = Thread.ofVirtual().start(new IndependentLightControlChampionDeath(bulb, runningInternal));
        threads.add(thread);
      }
      
      while (running) {
        IndependentLightControlChampionDeath.applyLightColor(clientInternal);
        try {
          Thread.sleep(25);
        } catch (InterruptedException e) {
          logger.error("Error while sleeping", e);
          Thread.currentThread().interrupt();
        }
      }

      runningInternal.set(false);

      for (Thread thread : threads) {
        thread.interrupt();
      }
      
      logger.info("ChampionWaitRespawnEffect finished");

    } catch (Exception e) {
      logger.error("Error while running ChampionWaitRespawnEffect", e);
    } finally {
      try {
        if(clientInternal != null) {
          clientInternal.close();
        }
      } catch (IOException e) {
        logger.error("Error while closing client", e);
      }
    }
  }

}
