package league_disco;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.stelar7.api.r4j.impl.lol.liveclient.LiveClientDataAPI;
import no.stelar7.api.r4j.pojo.lol.liveclient.ActiveGameClientPlayer;
import no.stelar7.api.r4j.pojo.lol.liveclient.events.GameEvent;

public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  private static final String BRIDGE_IP = "";
    
  public static void main(String[] args) throws InterruptedException, ExecutionException {
    
    do {
      ActiveGameClientPlayer currentPlayer = waitForAGame();
   
      ArrayList<GameEvent> events = new ArrayList<>();

      gameLoop(currentPlayer, events);
    }while(true);

  }

  private static void gameLoop(ActiveGameClientPlayer currentPlayer, ArrayList<GameEvent> events) throws InterruptedException, ExecutionException {
    LightEffects lightEffects = new LightEffects(currentPlayer.getSummonerName(), BRIDGE_IP);
    
    do {
      
      List<GameEvent> newEvents = getNewEvents(events);
      
      newEvents.forEach(lightEffects::handleEvent);
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        logger.error("Error while being in game!", e);
        Thread.currentThread().interrupt();
      }
      
    }while(true);
  }

  private static List<GameEvent> getNewEvents(ArrayList<GameEvent> events) {
    List<GameEvent> eventsReceived = LiveClientDataAPI.getEventData();
    
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
