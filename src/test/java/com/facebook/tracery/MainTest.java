package com.facebook.tracery;

import com.beust.jcommander.JCommander;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MainTest {
  private Main main;
  private JCommander jcommander;

  @Before
  public void setup() {
    main = new Main();
    jcommander = new JCommander(main);
  }

  @Test
  public void mainHelpShouldCallUsage() {
    String[] args = {"--help"};
    boolean status = main.run(jcommander, args);

    Assert.assertTrue(main.help);
    Assert.assertEquals(true, status);
  }
}
