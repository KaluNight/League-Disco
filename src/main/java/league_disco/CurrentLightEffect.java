package league_disco;

public enum CurrentLightEffect {
  ALIVE(getAliveEffect()),
  DEAD(getDeadEffect()),
  CHAMPION_KILL(getChampionKillEffect()),
  CHAMPION_DEATH(getChampionDeathEffect());
  
  private Runnable effect;
  
  private CurrentLightEffect(Runnable effect) {
    this.effect = effect;
  }
  
  public Runnable getEffect() {
    return effect;
  }
  
  private static Runnable getAliveEffect() {
    return () -> {
      // do something
    };
  }
  
  private static Runnable getDeadEffect() {
    return () -> {
      // do something
    };
  }
  
  private static Runnable getChampionKillEffect() {
    return () -> {
      // do something
    };
  }
  
  private static Runnable getChampionDeathEffect() {
    return () -> {
      // do something
    };
  }
  
}
