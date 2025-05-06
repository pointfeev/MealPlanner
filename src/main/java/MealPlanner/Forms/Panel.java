package MealPlanner.Forms;

import javax.swing.*;
import java.awt.*;

public abstract class Panel {
    public void updateSize(JPanel contentPane) {
        contentPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, contentPane.getPreferredSize().height));
    }
}
