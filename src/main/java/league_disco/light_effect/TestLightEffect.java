package league_disco.light_effect;

import java.util.ArrayList;
import java.util.List;

import io.github.zeroone3010.yahueapi.Color;
import io.github.zeroone3010.yahueapi.v2.Light;

public class TestLightEffect extends LightEffect {

  private static final List<Color> movingGradient = new ArrayList<>();

  private static final List<java.awt.Color> threeColors = new ArrayList<>();

  static {
    movingGradient.addAll(List.of(
        Color.of(255, 215, 0), // Doré
        Color.of(255, 223, 36), // Doré clair
        Color.of(255, 230, 72), // Transition doré vers blanc
        Color.of(255, 238, 144), // Blanc doré
        Color.of(255, 247, 210)  // Presque blanc
        ));

    threeColors.addAll(List.of(
        new java.awt.Color(255, 0, 0), // Rouge
        new java.awt.Color(0, 255, 0), // Vert
        new java.awt.Color(0, 0, 255) // Bleu
        ));
  }

  public TestLightEffect(List<Light> lightsStrips, List<Light> roofLights, List<Light> deskLights) {
    super("Test Light Effect", lightsStrips, roofLights, deskLights);
  }

  @Override
  public void run() {
    while(running) {
      //applyEffect(lightsStrips, new UpdateState().gradient(movingGradient));
      
      
      rotateColor(movingGradient);
      rotateColorAWT(threeColors);
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }

  private void rotateColor(List<Color> gradient) {
    gradient.add(0, gradient.remove(gradient.size() - 1));
  }
  
  private void rotateColorAWT(List<java.awt.Color> gradient) {
    gradient.add(0, gradient.remove(gradient.size() - 1));
  }
}
