package MealPlanner.Forms.Components;

import javax.swing.*;
import java.awt.*;

public class PlaceholderTextField extends JTextField {
    private String placeholder;

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(final String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);

        if (placeholder == null || placeholder.isEmpty() || !getText().isEmpty()) {
            return;
        }

        final Graphics2D graphics2D = (Graphics2D) graphics;

        Font previousFont = graphics.getFont();
        graphics.setFont(previousFont.deriveFont(Font.ITALIC));

        Color previousColor = graphics.getColor();
        graphics.setColor(getDisabledTextColor());

        RenderingHints previousHints = graphics2D.getRenderingHints();
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int height = getHeight();
        int padding = (height - getFont().getSize()) / 2;
        graphics.drawString(placeholder, getInsets().left, height - padding - 1);

        graphics2D.setRenderingHints(previousHints);
        graphics.setColor(previousColor);
        graphics.setFont(previousFont);
    }
}