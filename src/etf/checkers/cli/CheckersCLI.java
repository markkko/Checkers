package etf.checkers.cli;

import java.awt.event.*;
import javax.swing.event.*;

import etf.checkers.*;

/**
 * This text-only user interface shows a checkers game without graphics. This
 * interface does not respond to user input. This interface must be launched
 * with <code>moveOnClick</code> disabled. 
 * <p>
 * This view automatically quits itself when the game transitions into the 
 * <code>FINISHED</code> or <code>INVALID</code> states. This view terminates 
 * the game on <code>SIGTERM</code>. 
 */
public class CheckersCLI implements ChangeListener
{
    protected CheckersModel model;
    protected CheckersController ctl;

    protected Thread persistThread;

    /**
     * Constructs a CheckersCLI visualizing the specified <code>CheckersModel</code>.
     * @param model         the <code>CheckersModel</code> to visualize
     * @param ctl           the <code>CheckersController</code> to act with
     */
    public CheckersCLI(CheckersModel model, CheckersController ctl)
    {
        this.model = model;
        this.ctl = ctl;

        model.addChangeListener(this);

        /* Create non-daemon thread so Checkers does not quit right away */
        persistThread = new Thread()
        {
            public synchronized void run() {
                try {
                    wait();
                } catch (InterruptedException e) {}
            }
        };
        persistThread.start();

        /* Automatically terminate the game on SIGTERM */
        Thread shutdownThread = new Thread()
        {
            public void run() { CheckersCLI.this.ctl.terminateGame(); }
        };
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    protected void update()
    {
        /* Quit if FINISHED or INVALID by allowing the only remaining 
         * non-daemon thread to exit. */
        if ( model.getState() == CheckersModel.FINISHED ||
             model.getState() == CheckersModel.INVALID)
            synchronized(persistThread)
            {
                persistThread.notify();
            }
    }

    public void stateChanged(ChangeEvent e)
    {
        update();
    }

    /**
     * Returns the model this UI visualizes.
     * @return          the <code>CheckersModel</code> this UI visualizes
     */
    public CheckersModel getModel() { return model; }

    /**
     * Returns the controller this UI interacts with.
     * @return          the <code>CheckersController</code> this UI interacts with
     */
    public CheckersController getController() { return ctl; }

    /**
     * Launches a CheckersCLI with the specified <code>CheckersModel</code>.
     * This method blocks until the UI is fully loaded.
     * @param cm        the <code>CheckersModel</code> that the UI will visualize
     * @param ctl       the <code>CheckersController</code> the UI will act with
     * @return          a reference to the UI this method creates
     */
    public static CheckersCLI launch(CheckersModel cm, CheckersController ctl)
    {
        return new CheckersCLI(cm, ctl);
    }
}
