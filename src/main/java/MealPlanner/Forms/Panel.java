package MealPlanner.Forms;

import javax.swing.*;
import java.awt.*;

public abstract class Panel {
    public void updateSize(JPanel contentPane) {
        Dimension preferredSize = contentPane.getPreferredSize();
        contentPane.setMaximumSize(new Dimension(preferredSize.width, preferredSize.height));
    }
}
