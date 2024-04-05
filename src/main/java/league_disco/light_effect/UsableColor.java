package league_disco.light_effect;

import java.awt.Color;
import io.github.c0urante.joplin.HueColor;
import io.github.c0urante.joplin.Rgb;

public class UsableColor extends Color {

  public UsableColor(int r, int g, int b) {
    super(r, g, b);
  }

  private static final long serialVersionUID = -5527418870651873304L;
  
  public HueColor toHueColor() {
    return new Rgb(this);
  }
  
  public io.github.zeroone3010.yahueapi.Color toYahueColor() {
    return io.github.zeroone3010.yahueapi.Color.of(getRed(), getGreen(), getBlue());
  }

}
