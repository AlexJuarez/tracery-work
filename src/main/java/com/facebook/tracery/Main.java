package com.facebook.tracery;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.facebook.tracery.command.InsertTraceCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Parameters(commandDescription = "Tracery service.")
public class Main {
  private Logger logger = LogManager.getRootLogger();

  @Parameter(names = "--help", help = true, hidden = true)
  public boolean help = false;

  /* package */ boolean run(JCommander jcommander, String[] args) {

    InsertTraceCommand cmdInsertTrace = new InsertTraceCommand(jcommander);
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

    String command = jcommander.getParsedCommand();
    if (command == null) {
      jcommander.usage();
      return false;
    }

    try {
      if (command.equals(cmdInsertTrace.getName())) {
        cmdInsertTrace.run();
        return true;
      } else {
        jcommander.usage();
      }
    } catch (Exception ex) {
      logger.error(ex.getLocalizedMessage());
    }
    return false;
  }

  public static void main(String[] args) {
    Main main = new Main();
    JCommander jcommander = new JCommander(main);
    if (!main.run(jcommander, args)) {
      System.exit(1);
    }
  }
}

