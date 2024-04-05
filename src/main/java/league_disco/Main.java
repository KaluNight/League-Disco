package league_disco;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.stelar7.api.r4j.impl.lol.liveclient.LiveClientDataAPI;
import no.stelar7.api.r4j.pojo.lol.liveclient.ActiveGameClientPlayer;
import no.stelar7.api.r4j.pojo.lol.liveclient.ActiveGamePlayer;
import no.stelar7.api.r4j.pojo.lol.liveclient.events.GameEvent;
import no.stelar7.api.r4j.pojo.lol.replay.ReplayTeamType;

public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static final String BRIDGE_IP = "192.168.2.2";

  public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {

    do {
      ActiveGameClientPlayer currentPlayer = waitForAGame();

      ArrayList<GameEvent> events = new ArrayList<>();

      gameLoop(currentPlayer, events);
    }while(true);

  }

  private static void gameLoop(ActiveGameClientPlayer currentPlayer, ArrayList<GameEvent> events) throws InterruptedException, ExecutionException, IOException {
    LightsManager lightManager = new LightsManager(currentPlayer.getSummonerName(), BRIDGE_IP);

    boolean gameEnded = false;

    do {
      try {
        List<GameEvent> newEvents = getNewEvents(events);

        newEvents.forEach(lightManager::handleEvent);

        PlayerStatus status = getPlayerStatus(currentPlayer);
        if(!lightManager.isEffectRunning() && status == PlayerStatus.DEAD) {
          lightManager.triggerEffect(CurrentLightEffect.DEAD);
        }
        
        if ((lightManager.getCurrentEffectAsked() == CurrentLightEffect.DEAD || 
            (lightManager.getCurrentEffectAsked() == CurrentLightEffect.CHAMPION_KILL && !lightManager.isEffectRunning())) && status == PlayerStatus.ALIVE) {
          lightManager.triggerEffect(CurrentLightEffect.ALIVE);
        } 

        Thread.sleep(500);
      } catch (InterruptedException e) {
        logger.error("Error while being in game!", e);
        Thread.currentThread().interrupt();
      } catch (GameEndedException e) {
        gameEnded = true;
      }

    }while(!gameEnded);
  }

  private static PlayerStatus getPlayerStatus(ActiveGameClientPlayer currentPlayer) {
    final AtomicBoolean isDead = new AtomicBoolean(false);

    List<ActiveGamePlayer> players = LiveClientDataAPI.getGameData().getAllPlayers();

    for (ActiveGamePlayer player : players) {
      if (player.getSummonerName().equals(currentPlayer.getSummonerName().split("#")[0])) {
        if (player.isDead()) {
          isDead.set(true);
        }
      }
    }

    if (isDead.get()) {
      return PlayerStatus.DEAD;
    } else {
      return PlayerStatus.ALIVE;
    }
  }

  private static List<GameEvent> getNewEvents(ArrayList<GameEvent> events) throws GameEndedException {
    List<GameEvent> eventsReceived = LiveClientDataAPI.getEventData();

    if (eventsReceived.isEmpty() && !events.isEmpty()) { // End of game
      logger.info("Game ended!");
      throw new GameEndedException();
    }

    List<GameEvent> newEvents = new ArrayList<>();

    for (GameEvent event : eventsReceived) {
      if (!events.contains(event)) {
        logger.debug("New event: {}", event);
        newEvents.add(event);
      }
    }

    events.addAll(newEvents);

    return newEvents;
  }

  private static ActiveGameClientPlayer waitForAGame() {
    logger.info("Waiting for a game to start...");

    ActiveGameClientPlayer currentPlayer = null;
    do {
      currentPlayer = LiveClientDataAPI.getActivePlayer();

      try {
        Thread.sleep(Duration.ofSeconds(5).toMillis());
      } catch (InterruptedException e) {
        logger.error("Error while waiting for a game to start!", e);
        Thread.currentThread().interrupt();
      }
    }while (currentPlayer == null);

    logger.info("Game started! Current player: {}.", currentPlayer.getSummonerName());

    return currentPlayer;
  }
}
