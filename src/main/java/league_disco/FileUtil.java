package league_disco;

import java.io.FileReader;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

  private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

  private FileUtil() {}
  
  public static void writeFile(String fileName, String content) {
    try (FileWriter fileWriter = new FileWriter(fileName)) {
      fileWriter.write(content);
    } catch (Exception e) {
      logger.error("Error while writing to file", e);
    }
  }
  
  public static String readFile(String fileName) {
    try (FileReader fileReader = new FileReader(fileName)) {
      StringBuilder content = new StringBuilder();
      int c;
      while ((c = fileReader.read()) != -1) {
        content.append((char) c);
      }
      return content.toString();
    } catch (Exception e) {
      logger.error("Error while reading from file", e);
      return "";
    }
  }
  
}
