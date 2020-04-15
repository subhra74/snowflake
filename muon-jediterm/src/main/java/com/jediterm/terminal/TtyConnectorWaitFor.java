package com.jediterm.terminal;

//import com.google.common.base.Predicate;
import org.apache.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Predicate;

public class TtyConnectorWaitFor {
  private static final Logger LOG = Logger.getLogger(TtyConnectorWaitFor.class);

  private final Future<?> myWaitForThreadFuture;
  private final BlockingQueue<Predicate<Integer>> myTerminationCallback = new ArrayBlockingQueue<Predicate<Integer>>(1);

  public void detach() {
    myWaitForThreadFuture.cancel(true);
  }


  public TtyConnectorWaitFor(final TtyConnector ttyConnector, final ExecutorService executor) {
    myWaitForThreadFuture = executor.submit(new Runnable() {
      @Override
      public void run() {
        int exitCode = 0;
        try {
          while (true) {
            try {
              exitCode = ttyConnector.waitFor();
              break;
            }
            catch (InterruptedException e) {
              LOG.debug(e);
            }
          }
        }
        finally {
          try {
            if (!myWaitForThreadFuture.isCancelled()) {
              myTerminationCallback.take().test(exitCode);
            }
          }
          catch (InterruptedException e) {
            LOG.info(e);
          }
        }
      }
    });
  }

  public void setTerminationCallback(Predicate<Integer> r) {
    myTerminationCallback.offer(r);
  }
}
