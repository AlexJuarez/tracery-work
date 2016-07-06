package com.facebook.tracery;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Parameters(commandDescription = "Tracery service.")
public class Main {
  private Logger logger = LogManager.getRootLogger();

  @Parameter(names = "--help", help = true, hidden = true)
  public boolean help = false;

  /* package */ boolean run(JCommander jcommander, String[] args) {
    try {
      jcommander.parse(args);
    } catch (ParameterException pex) {
      System.err.println(pex.getLocalizedMessage());
      System.err.println();
      jcommander.usage();
      return false;
    }

    if (help) {
      jcommander.usage();
      return true;
    }

    logger.info("Hello world!");
    logger.debug("Bugs? What bugs?");
    logger.error("We've got problems.");

    return true;
  }

  public static void main(String[] args) {
    Main main = new Main();
    JCommander jcommander = new JCommander(main);
    if (!main.run(jcommander, args)) {
      System.exit(1);
    }
  }
}

