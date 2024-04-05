package league_disco.light_effect;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.c0urante.joplin.HueEntertainmentClient;
import io.github.c0urante.joplin.Rgb;
import io.github.zeroone3010.yahueapi.v2.Light;
import io.github.zeroone3010.yahueapi.v2.UpdateState;

public abstract class LightEffect implements Runnable {

  protected static final Logger logger = LoggerFactory.getLogger(LightEffect.class);
  
  private final String name;
  protected List<Light> lightsStrips;
  protected List<Light> roofLights;
  protected List<Light> deskLights;
  protected HueEntertainmentClient client;
  
  protected boolean running = true;

  protected LightEffect(String name, List<Light> lightsStrips, List<Light> roofLights, List<Light> deskLights) {
    this.name = name;
    this.lightsStrips = lightsStrips;
    this.roofLights = roofLights;
    this.deskLights = deskLights;
  }
  
  protected void applyFade(UpdateState newState, List<Light> lights) {    
    for (Light light : lights) {
      light.setState(newState);
    }
  }
  
  protected void applyFade(UpdateState newState, Light... lights) {    
    applyFade(newState, List.of(lights));
  }
  
  protected void applySpotLightChange(List<UsableColor> colors) throws IOException {
    io.github.c0urante.joplin.Light light1 = new io.github.c0urante.joplin.Light(2, new Rgb(colors.get(0)));
    io.github.c0urante.joplin.Light light2 = new io.github.c0urante.joplin.Light(3, new Rgb(colors.get(1)));
    io.github.c0urante.joplin.Light light3 = new io.github.c0urante.joplin.Light(4, new Rgb(colors.get(2)));

    client.sendLights(light1, light2, light3);
  }
  
  protected void applyAllLightChange(List<UsableColor> colors) throws IOException {
    io.github.c0urante.joplin.Light lightStrip = new io.github.c0urante.joplin.Light(0, new Rgb(colors.get(0)));
    io.github.c0urante.joplin.Light lightStrip2 = new io.github.c0urante.joplin.Light(1, new Rgb(colors.get(0)));
    io.github.c0urante.joplin.Light light1 = new io.github.c0urante.joplin.Light(2, new Rgb(colors.get(1)));
    io.github.c0urante.joplin.Light light2 = new io.github.c0urante.joplin.Light(3, new Rgb(colors.get(2)));
    io.github.c0urante.joplin.Light light3 = new io.github.c0urante.joplin.Light(4, new Rgb(colors.get(3)));

    client.sendLights(lightStrip, lightStrip2, light1, light2, light3);
  }

  public String getName() {
    return name;
  }
  
  public void stop() {
    running = false;
  }
  
  public boolean isRunning() {
    return running;
  }
}
