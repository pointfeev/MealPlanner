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
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(getDisabledTextColor());
        graphics2D.drawString(placeholder, getInsets().left, graphics.getFontMetrics().getMaxAscent() + getInsets().top);
    }
}