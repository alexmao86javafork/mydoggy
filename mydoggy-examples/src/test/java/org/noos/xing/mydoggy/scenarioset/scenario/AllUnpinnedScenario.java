package org.noos.xing.mydoggy.scenarioset.scenario;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.scenario.AbstractScenario;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class AllUnpinnedScenario extends AbstractScenario {

    protected MyDoggyToolWindowManager toolWindowManager = new MyDoggyToolWindowManager();
    protected JFrame frame;

    public String getName() {
        return this.getClass().getName();
    }

    public Window launch() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setUp();
                start();
            }
        });
        return frame;
    }

    public String getDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return getName();
    }

    protected void setUp() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(640, 480);

        toolWindowManager.getContentManager().addContent("content", "", null, new JButton("HEello"), "");
        for (int i = 0; i < 5; i++) {
            ToolWindow tool = toolWindowManager.registerToolWindow("tool_" + i,
                                                                   null, null,
                                                                   new JTextArea("Hello World!!!"), ToolWindowAnchor.LEFT);
            tool.setAvailable(true);
            tool.setAutoHide(true);
        }

        frame.getContentPane().add(toolWindowManager);
    }

    protected void start() {
        SwingUtil.centrePositionOnScreen(frame);
        frame.setVisible(true);
    }

}