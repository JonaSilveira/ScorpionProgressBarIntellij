import com.intellij.openapi.ui.GraphicsConfig;
import com.intellij.openapi.util.ScalableIcon;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.GraphicsUtil;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import sun.swing.SwingUtilities2;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.*;

public class ScorpionProgressBarUi extends BasicProgressBarUI {
    private static final float ONE_OVER_SEVEN = 1f / 7;
    private static final Color TRANPARENT = new Color(0, 0, 0, 0);

    static {
    }

    @SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        c.setBorder(JBUI.Borders.empty().asUIResource());
        return new ScorpionProgressBarUi();
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(super.getPreferredSize(c).width, JBUI.scale(20));
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        progressBar.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                super.componentHidden(e);
            }
        });
    }

    private volatile int offset = 0;
    private volatile int offset2 = 0;
    private volatile int velocity = 10;
    private volatile int p = 0;
    @Override
    protected void paintIndeterminate(Graphics g2d, JComponent c) {
        if (!(g2d instanceof Graphics2D)) {
            return;
        }
        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth();//- (b.right + b.left);
        int barRectHeight = progressBar.getHeight();//- (b.top + b.bottom);

        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return;
        }

        int w = c.getWidth();
        int h = c.getPreferredSize().height;
        if (!isEven(c.getHeight() - h)) h++;

        final float R = JBUI.scale(8f);
        final float R2 = JBUI.scale(9f);

        final LinearGradientPaint baseRainbowPaint = new LinearGradientPaint(0, JBUI.scale(2), 0, h - JBUI.scale(6),
                new float[]{ONE_OVER_SEVEN * 1, ONE_OVER_SEVEN * 2, ONE_OVER_SEVEN * 3, ONE_OVER_SEVEN * 4, ONE_OVER_SEVEN * 5, ONE_OVER_SEVEN * 6, ONE_OVER_SEVEN * 7},
                new Color[]{TRANPARENT, TRANPARENT, TRANPARENT, TRANPARENT, TRANPARENT, TRANPARENT, TRANPARENT});

        Graphics2D g = (Graphics2D) g2d;

        g.setColor(new JBColor(Gray._240.withAlpha(50), Gray._128.withAlpha(50)));
        g.setPaint(baseRainbowPaint);

        if (c.isOpaque()) {
            g.fillRect(0, (c.getHeight() - h) / 2, w, h);
        }
        g.setColor(new JBColor(Gray._165.withAlpha(50), Gray._88.withAlpha(50)));
        final GraphicsConfig config = GraphicsUtil.setupAAPainting(g);
        g.translate(0, (c.getHeight() - h) / 2);

        Paint old = g.getPaint();
        g.setPaint(baseRainbowPaint);
        g.setPaint(old);


        g.setPaint(Gray._128);


        Container parent = c.getParent();
        Color background = parent != null ? parent.getBackground() : UIUtil.getPanelBackground();
        g.setPaint(background);

        g.translate(0, -(c.getHeight() - h) / 2);

        // Deal with possible text painting
        if (progressBar.isStringPainted()) {
            if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight, boxRect.x, boxRect.width);
            } else {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight, boxRect.y, boxRect.height);
            }
        }

        //button
        offset = (offset + 1) % getPeriodLength();
        offset2 += velocity;

        if(offset2<=70){
            ScorpionIcons.SCORPION_CHAIN.paintIcon(progressBar, g, offset2 - JBUI.scale(1024-32), -JBUI.scale(6));
            ScorpionIcons.SUB_INDO.paintIcon(progressBar, g, barRectWidth - JBUI.scale(32), 0);
        }
        else {
            ScorpionIcons.CHAIN_VOLTA.paintIcon(progressBar, g,barRectWidth - JBUI.scale(1022) , -JBUI.scale(6));
        }
        ScorpionIcons.SCORPION.paintIcon(progressBar, g, -JBUI.scale(0), 0);


        config.restore();
    }

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        if (!(g instanceof Graphics2D)) {
            return;
        }

        if (progressBar.getOrientation() != SwingConstants.HORIZONTAL || !c.getComponentOrientation().isLeftToRight()) {
            super.paintDeterminate(g, c);
            return;
        }
        final GraphicsConfig config = GraphicsUtil.setupAAPainting(g);
        Insets b = progressBar.getInsets(); // area for border
        int w = progressBar.getWidth();
        int h = progressBar.getPreferredSize().height;
        if (!isEven(c.getHeight() - h)) h++;

        int barRectWidth = w;//- (b.right + b.left);
        int barRectHeight = h;//- (b.top + b.bottom);

        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return;
        }

        int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

        Container parent = c.getParent();
        Color background = parent != null ? parent.getBackground() : UIUtil.getPanelBackground();

        g.setColor(background);
        Graphics2D g2 = (Graphics2D) g;
        if (c.isOpaque()) {
            g.fillRect(0, 0, w, h);
        }

        final float R = JBUI.scale(8f);
        final float R2 = JBUI.scale(9f);
        final float off = JBUI.scale(1f);

        //background
        g2.translate(0, (c.getHeight() - h) / 2);
        g2.setColor(progressBar.getForeground());
        g2.setColor(background);

        //progress bar
        g2.setPaint(new LinearGradientPaint(0, JBUI.scale(2), 0, h - JBUI.scale(6),
                new float[]{ONE_OVER_SEVEN * 1, ONE_OVER_SEVEN * 2, ONE_OVER_SEVEN * 3, ONE_OVER_SEVEN * 4, ONE_OVER_SEVEN * 5, ONE_OVER_SEVEN * 6, ONE_OVER_SEVEN * 7},
                new Color[]{TRANPARENT, TRANPARENT, TRANPARENT, TRANPARENT, TRANPARENT, TRANPARENT, TRANPARENT}));
        g2.fill(new RoundRectangle2D.Float(2f * off, 2f * off, amountFull - JBUI.scale(5f), h - JBUI.scale(5f), JBUI.scale(7f), JBUI.scale(7f)));
        g2.translate(0, -(c.getHeight() - h) / 2);

        // Deal with possible text painting
        if (progressBar.isStringPainted()) {
            paintString(g, b.left, b.top,
                    barRectWidth, barRectHeight,
                    amountFull, b);
        }
        //button
        offset = (offset + 1) % getPeriodLength();
        offset2 += velocity;

        if(offset2<=70){
            ScorpionIcons.SCORPION_CHAIN.paintIcon(progressBar, g, offset2 - JBUI.scale(1024-32), -JBUI.scale(6));
            ScorpionIcons.SUB_INDO.paintIcon(progressBar, g, barRectWidth - JBUI.scale(32), 0);
        }
        else {
            ScorpionIcons.CHAIN_VOLTA.paintIcon(progressBar, g,barRectWidth - JBUI.scale(1020), -JBUI.scale(6));
        }
        ScorpionIcons.SCORPION.paintIcon(progressBar, g, -JBUI.scale(0), 0);
        config.restore();
    }

    private void paintString(Graphics g, int x, int y, int w, int h, int fillStart, int amountFull) {
        if (!(g instanceof Graphics2D)) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        String progressString = progressBar.getString();
        g2.setFont(progressBar.getFont());
        Point renderLocation = getStringPlacement(g2, progressString,
                x, y, w, h);
        Rectangle oldClip = g2.getClipBounds();

        if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
            g2.setColor(getSelectionBackground());
            SwingUtilities2.drawString(progressBar, g2, progressString,
                    renderLocation.x, renderLocation.y);
            g2.setColor(getSelectionForeground());
            g2.clipRect(fillStart, y, amountFull, h);
            SwingUtilities2.drawString(progressBar, g2, progressString,
                    renderLocation.x, renderLocation.y);
        } else { // VERTICAL
            g2.setColor(getSelectionBackground());
            AffineTransform rotate =
                    AffineTransform.getRotateInstance(Math.PI / 2);
            g2.setFont(progressBar.getFont().deriveFont(rotate));
            renderLocation = getStringPlacement(g2, progressString,
                    x, y, w, h);
            SwingUtilities2.drawString(progressBar, g2, progressString,
                    renderLocation.x, renderLocation.y);
            g2.setColor(getSelectionForeground());
            g2.clipRect(x, fillStart, w, amountFull);
            SwingUtilities2.drawString(progressBar, g2, progressString,
                    renderLocation.x, renderLocation.y);
        }
        g2.setClip(oldClip);
    }

    @Override
    protected int getBoxLength(int availableLength, int otherDimension) {
        return availableLength;
    }

    protected int getPeriodLength() {
        return JBUI.scale(16);
    }

    private static boolean isEven(int value) {
        return value % 2 == 0;
    }

}
