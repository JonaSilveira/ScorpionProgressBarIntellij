import com.intellij.ide.ui.LafManager;

import javax.swing.*;

public class ScorpionApplicationComponent {

    public ScorpionApplicationComponent() {
        LafManager.getInstance().addLafManagerListener(__ -> updateProgressBarUi());
        updateProgressBarUi();
    }

    private void updateProgressBarUi() {
        UIManager.put("ProgressBarUI", ScorpionProgressBarUi.class.getName());
        UIManager.getDefaults().put(ScorpionProgressBarUi.class.getName(), ScorpionProgressBarUi.class);
    }
}
