package com.facebook.tracery.command;

import com.beust.jcommander.JCommander;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractCommand {
  protected final Logger logger = LogManager.getLogger(getClass());
  protected final JCommander jcommander;

  public AbstractCommand(JCommander jcommander) {
    this.jcommander = jcommander;
    this.jcommander.addCommand(getName(), this);
  }

  public abstract String getName();

  @SuppressFBWarnings("DM_EXIT")
  protected void usage() {
    jcommander.usage();
    System.exit(1);
  }

  /**
   * Execute the command.
   *
   * @throws Exception on failure.
   */
  public abstract void run() throws Exception;
}
