package league_disco;

import java.util.List;

import io.github.zeroone3010.yahueapi.v2.Light;
import league_disco.light_effect.ChampionAliveLightEffect;
import league_disco.light_effect.ChampionDeathLightEffect;
import league_disco.light_effect.ChampionKillLightEffect;
import league_disco.light_effect.ChampionWaitRespawnEffect;
import league_disco.light_effect.LightEffect;
import league_disco.light_effect.TestLightEffect;

public enum CurrentLightEffect {
  ALIVE(),
  DEAD(),
  CHAMPION_KILL(),
  CHAMPION_DEATH(),
  TEST();

  public LightEffect getEffect(List<Light> lightsStrips, List<Light> roofLights, List<Light> deskLights) {
    return switch (this) {
    case ALIVE -> new ChampionAliveLightEffect(lightsStrips, roofLights, deskLights);
    case DEAD -> new ChampionWaitRespawnEffect(lightsStrips, roofLights, deskLights);
    case CHAMPION_KILL -> new ChampionKillLightEffect(lightsStrips, roofLights, deskLights);
    case CHAMPION_DEATH -> new ChampionDeathLightEffect(lightsStrips, roofLights, deskLights);
    case TEST -> new TestLightEffect(lightsStrips, roofLights, deskLights);
    };
  }

}
