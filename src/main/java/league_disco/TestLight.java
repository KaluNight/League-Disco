package league_disco;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class TestLight {

  public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
    testHTTP();
  }

  private static void testHTTP() throws InterruptedException, ExecutionException, IOException {
    LightsManager lightEffects = new LightsManager("KaluNight", "192.168.2.2");

    lightEffects.discoverLights();

    System.out.println("LightEffects instance created" + lightEffects);

    lightEffects.handleEvent(null);

    while (true) {
      Thread.sleep(500);
    }
  }
}
