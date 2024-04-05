package league_disco.light_effect;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import io.github.c0urante.joplin.HueEntertainmentClient;
import io.github.zeroone3010.yahueapi.v2.Light;
import league_disco.EntertainementApi;
import league_disco.Main;

public class ChampionDeathLightEffect extends LightEffect {

  private static final Random random = new Random();
  
  public ChampionDeathLightEffect(List<Light> lightsStrips, List<Light> roofLights, List<Light> deskLights) {
    super("Champion Death Effect", lightsStrips, roofLights, deskLights);
  }
  
  @Override
  public void run() {
    try {
      HueEntertainmentClient client = EntertainementApi.createClient(Main.BRIDGE_IP);
      simulateThunderstorm(client);
      client.close();
      running = false;
    }catch (Exception e) {
      logger.error("Error while running ChampionDeathLightEffect", e);
    }
  }

  private static void simulateThunderstorm(HueEntertainmentClient client) throws InterruptedException, IOException {
    int[] bulbs = {1, 2, 3, 4, 5}; // The bulbs you're controlling

    for (int i = 0; i < 5; i++) { // Number of lightning strikes
        HashSet<Integer> flashedBulbs = new HashSet<>(); // Track flashed bulbs
        int intensity = 200 + random.nextInt(56); // Random intensity for the flash
        UsableColor flashColor = new UsableColor(intensity, intensity, intensity);

        // Flash each bulb randomly and record which bulbs have flashed
        for (int bulb : bulbs) {
            if (random.nextBoolean()) {
                client.sendColor(bulb, flashColor.toHueColor());
                flashedBulbs.add(bulb); // Record this bulb as having flashed
            }
        }

        Thread.sleep(10 + (long) random.nextInt(91)); // Short delay after the flash

        // Fade effect for flashed bulbs only
        for (int fadeIntensity = intensity; fadeIntensity >= 0; fadeIntensity -= 25) {
            UsableColor fadeColor = new UsableColor(fadeIntensity, fadeIntensity, fadeIntensity);
            for (int bulb : flashedBulbs) { // Only fade bulbs that have flashed
                client.sendColor(bulb, fadeColor.toHueColor());
            }
            Thread.sleep(10); // Short delay between each fade step to make it smooth
        }

        // Random pause before the next flash, making it feel more natural
        Thread.sleep(100 + (long) random.nextInt(901));
    }

    // Ensure everything is off after the storm
    for (int bulb : bulbs) {
        client.sendColor(bulb, new UsableColor(0, 0, 0).toHueColor());
    }
}
  
}
