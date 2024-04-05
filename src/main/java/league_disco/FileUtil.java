package league_disco;

import java.io.BufferedReader;
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

  /**
   * Reads a specific line from a file
   * @param fileName
   * @param lineNumber Starts from 0
   * @return
   */
  public static String readSpecificLine(String fileName, int lineNumber) {
    String line = null;
    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
      for (int i = 0; i <= lineNumber; i++) {
        if ((line = reader.readLine()) == null) {
          break;
        }
      }
    } catch (Exception e) {
      logger.error("Error while reading from file", e);
    }
    return line != null ? line : "";
  }

}
