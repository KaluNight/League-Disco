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

public class IndependentLightControlChampionAlive implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(IndependentLightControlChampionAlive.class);

  private static final ConcurrentHashMap<Integer, UsableColor> colorToSend = new ConcurrentHashMap<>();

  private final int bulbId;
  private final AtomicBoolean running;

  public IndependentLightControlChampionAlive(int bulbId, AtomicBoolean running) {
    this.bulbId = bulbId;
    this.running = running;
  }

  @Override
  public void run() {
    try {
      while (running.get()) {
        // Gradually transition through warm colors
        for (int intensity = 100; intensity <= 200; intensity += 2) {
          UsableColor color = generateWarmColor(intensity);
          sendLightColor(bulbId, color);
          Thread.sleep(100); // Smooth transition
        }
        for (int intensity = 200; intensity >= 100; intensity -= 2) {
          UsableColor color = generateWarmColor(intensity);
          sendLightColor(bulbId, color);
          Thread.sleep(100); // Smooth transition
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      logger.error("Error while running PlayerAliveEffect", e);
    }
  }

  private UsableColor generateWarmColor(int intensity) {
    // Generate a warm color based on intensity
    // Adjust these values to change the color range
    int red = (intensity < 75) ? intensity * 2 : 255;
    int green = (intensity < 85) ? intensity : 200;
    int blue = 50; // Constant to maintain a warm tone
    return new UsableColor(red, green, blue);
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
