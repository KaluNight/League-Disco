package league_disco.light_effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.c0urante.joplin.HueEntertainmentClient;
import io.github.c0urante.joplin.Light;

public class IndependentLightControlChampionDeath implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(IndependentLightControlChampionDeath.class);

  private static final Random random = new Random();
  
  private static final ConcurrentHashMap<Integer, UsableColor> colorToSend = new ConcurrentHashMap<>();
  
  private final int bulbId;
  private final AtomicBoolean running;

  public IndependentLightControlChampionDeath(int bulbId, AtomicBoolean running) {
    this.bulbId = bulbId;
    this.running = running;
  }

  @Override
  public void run() {
    try {
      while (running.get()) { // Replace with a suitable condition for stopping
        // Each light starts off
        int intensity = 0;
        UsableColor color = new UsableColor(0, 0, intensity);
        sendLightColor(bulbId, color);

        // Randomly decide when the light wakes up
        Thread.sleep(random.nextInt(5000)); // waits for 0 to 5 seconds

        // Gradually increase brightness to max
        for (intensity = 0; intensity <= 100; intensity += 5) { // Example max intensity is 50 for dim effect
          color = new UsableColor(0, 0, intensity); // Shades of blue
          sendLightColor(bulbId, color);
          Thread.sleep(100); // Adjust for smoother transition
        }

        // Gradually decrease brightness back to 0
        for (; intensity >= 0; intensity -= 5) {
          color = new UsableColor(0, 0, intensity);
          sendLightColor(bulbId, color);
          Thread.sleep(100); // Adjust for smoother transition
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      logger.error("Error while running IndependentLightControlChampionDeath", e);
    }
  }
  
  private void sendLightColor(int bulbId, UsableColor color) {
    colorToSend.put(bulbId, color);
  }
  
  public static void applyLightColor(HueEntertainmentClient client) {
    List<Light> lights = new ArrayList<>();
    
    for (Entry<Integer, UsableColor> entry : colorToSend.entrySet()) {
      Light light = new Light(entry.getKey(), entry.getValue().toHueColor());
      lights.add(light);
    }
    
    try {
      client.sendLights(lights);
    } catch (Exception e) {
      logger.error("Error while updating lights", e);
    }
  }
}
