package ru.spb.hibissscus.client;

import javax.swing.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Show balloon with text on the mouse position
 */
class BalloonWorker extends SwingWorker<Void, Void> {

    final static int[] nextId = new int[1];
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(BalloonWorker.class);
    int id;
    /**
     * Text, which we would like to represent on the screen
     */
    private String text;

    /**Position where show this balloon*/
    private Point point;
    /**
     * Special gap which we add to the point position
     */
    private int gap = 7;
    /**
     * SWT component to display
     */
    private Display display;
    /**
     * Special swt shell to make it possible dispaly our balloon
     */
    private Shell shell;

    /**
     * Constructor
     *
     * @param text
     */
    public BalloonWorker(String text) {
        this.text = text;
    }

    public Display getDisplay() {
        return display;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    @Override
    protected Void doInBackground() throws Exception {

        id = nextId[0]++;
        LOG.debug("display with id:" + id + " run.");

        display = new Display();
        shell = new Shell(display);

        final ToolTip tip = new ToolTip(shell, SWT.BALLOON
                | SWT.ICON_INFORMATION);
        tip.setMessage(text);

        // show in mousec position
        if (point == null) {
            point = display.getCursorLocation();
        }

        tip.setLocation(point.x, point.y + gap);
        tip.setVisible(true);
        point = null;

        while (!isCancelled() && tip.isVisible()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        this.dispose();
        return null;
    }

    public void cancel() {
        this.dispose();
        super.cancel(true);
    }

    @Override
    protected void finalize() throws Throwable {
        this.dispose();
        super.finalize();
    }

    private void dispose() {
        if (display != null && !display.isDisposed())
            display.syncExec(new Runnable() {
                public void run() {
                    if (shell != null)
                        shell.dispose();
                    if (display != null)
                        display.dispose();
                    LOG.debug("display with id:" + id + " dispose.");
                }
            });
    }
}
