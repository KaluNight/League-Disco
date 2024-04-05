package league_disco;

import java.io.IOException;

import io.github.c0urante.joplin.HueEntertainmentClient;

public class EntertainementApi {

  public static HueEntertainmentClient createClient(String brigeIp) throws IOException, InterruptedException {
    // Instantiate the client
    HueEntertainmentClient client = HueEntertainmentClient.builder()
        .host(brigeIp)
        .username(FileUtil.readSpecificLine("entApiKey.txt", 0))
        .clientKey(FileUtil.readSpecificLine("entApiKey.txt", 1))
        .entertainmentArea(FileUtil.readSpecificLine("entApiKey.txt", 3))
        .build();

    // Use the bridge REST API to turn on streaming
    // This method must be called before light colors can be set
    client.initializeStream();

    return client;
  }
}
