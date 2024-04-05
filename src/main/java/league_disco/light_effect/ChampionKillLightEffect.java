package league_disco.light_effect;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.zeroone3010.yahueapi.v2.Light;
import io.github.zeroone3010.yahueapi.v2.UpdateState;
import league_disco.EntertainementApi;
import league_disco.Main;

public class ChampionKillLightEffect extends LightEffect {

  private static final List<UsableColor> poolOfColors = new ArrayList<>();

  private static final Random rand = new Random();

  static {
    poolOfColors.addAll(List.of(
        new UsableColor(255, 215, 0), // Doré
        new UsableColor(255, 223, 36), // Doré clair
        new UsableColor(255, 230, 72), // Transition doré vers blanc
        new UsableColor(255, 238, 144), // Blanc doré
        new UsableColor(255, 247, 210)  // Presque blanc
        )); 
  }

  public ChampionKillLightEffect(List<Light> lightsStrips, List<Light> roofLights,
      List<Light> deskLights) {
    super("Champion Kill Effect", lightsStrips, roofLights, deskLights);
  }

  @Override
  public void run() {
    try {
      List<UsableColor> fireworkColors = new ArrayList<>();

      client = EntertainementApi.createClient(Main.BRIDGE_IP);
      firstFewSec(fireworkColors);
      client.close();
      afterFirstWave(fireworkColors);
      running = false;
    } catch (Exception e) {
      logger.error("Error while running ChampionKillLightEffect", e);
    }
  }

  private void afterFirstWave(List<UsableColor> fireworkColors) {
    LocalDateTime start = LocalDateTime.now();

    List<io.github.zeroone3010.yahueapi.Color> lightStripColors = new ArrayList<>();

    while (running && LocalDateTime.now().isBefore(start.plusSeconds(7))) {
      lightStripColors.add(poolOfColors.get(rand.nextInt(0, poolOfColors.size())).toYahueColor());
      lightStripColors.add(poolOfColors.get(rand.nextInt(0, poolOfColors.size())).toYahueColor());
      lightStripColors.add(poolOfColors.get(rand.nextInt(0, poolOfColors.size())).toYahueColor());
      lightStripColors.add(poolOfColors.get(rand.nextInt(0, poolOfColors.size())).toYahueColor());
      lightStripColors.add(poolOfColors.get(rand.nextInt(0, poolOfColors.size())).toYahueColor());
      
      for (Light light : roofLights) {
        applyFade(new UpdateState().color(poolOfColors.get(rand.nextInt(0, poolOfColors.size())).toYahueColor()).brightness(70), light);
      }
      
      applyFade(new UpdateState().color(poolOfColors.get(rand.nextInt(0, poolOfColors.size())).toYahueColor()).brightness(50), deskLights);
      applyFade(new UpdateState().gradient(lightStripColors).brightness(100), lightsStrips);

      lightStripColors.clear();
      fireworkColors.clear();
      try {
        Thread.sleep(250);
      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }

  private void firstFewSec(List<UsableColor> fireworkColors) {
    LocalDateTime start = LocalDateTime.now();

    do {
      fireworkColors.add(poolOfColors.get(rand.nextInt(0, poolOfColors.size())));
      fireworkColors.add(poolOfColors.get(rand.nextInt(0, poolOfColors.size())));
      fireworkColors.add(poolOfColors.get(rand.nextInt(0, poolOfColors.size())));
      fireworkColors.add(poolOfColors.get(rand.nextInt(0, poolOfColors.size())));

      try {
        applyAllLightChange(fireworkColors);
      } catch (IOException e) {
        logger.error("Error while applying light change", e);
        // Not log
      }

      fireworkColors.clear();
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }while (LocalDateTime.now().isBefore(start.plusSeconds(2)) && running);
  }

}
