package org.noos.xing.mydoggy.mydoggyset;

import org.noos.xing.mydoggy.itest.impl.ListInteractiveTestRunner;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyInteractiveTester {

    public static void main(String[] args) {
        ListInteractiveTestRunner runner = new ListInteractiveTestRunner();
/*
        runner.addInteractiveTest(new ToolVisisbleInteractiveTest());
        runner.addInteractiveTest(new PreviewInteractiveTest());
        runner.addInteractiveTest(new DragInteractiveTest());
        runner.addInteractiveTest(new SlidingTypeInteractiveTest());
        runner.addInteractiveTest(new FloatingMoveInteractiveTest());
*/
        runner.run();
    }

/*
    public static abstract class MyDoggyInteractiveTest implements InteractiveTest {
        MyDoggySet myDoggySet;

        public String getName() {
            return null;
        }

        public String getDescription() {
            return null;
        }

        public Container setup() {
            myDoggySet = new MyDoggySet();
            myDoggySet.setUp();
            myDoggySet.start();

            return myDoggySet.getFrame();
        }

        public void dispose() {
            myDoggySet.dispose();
        }

        protected void moveToolTo(InteractiveUI interactiveUI, String toolId, ToolWindowAnchor anchor) {
            InteractiveMouse mouse = interactiveUI.getInteractiveMouse();

            mouse.moveTo("toolWindow.rb." + toolId);
            interactiveUI.delay(500);
            mouse.press(InteractiveMouse.Type.LEFT);

            mouse.moveTo("toolWindowManager.bar." + anchor.toString(), 10, 15);
            interactiveUI.delay(500);
            mouse.release();
            interactiveUI.delay(500);
        }
    }

    public static class PreviewInteractiveTest extends MyDoggyInteractiveTest {

        public void execute(InteractiveUI interactiveUI) {
            InteractiveMouse mouse = interactiveUI.getInteractiveMouse();
            InteractiveAssertor assertor = interactiveUI.getInteractiveAssertor();

            ToolWindow toolWindow = myDoggySet.getToolWindowManager().getDockableDelegator("Tool 1");

            moveToolTo(interactiveUI, "Tool 7", ToolWindowAnchor.BOTTOM);

            DockedTypeDescriptor manager = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
            manager.setPreviewEnabled(true);
            manager.setPreviewDelay(1000);

            mouse.moveTo("toolWindow.rb.Tool 6");
            interactiveUI.delay(1100);

            assertor.askForConfirm("Is preview visible?");

            moveToolTo(interactiveUI, "Tool 1", ToolWindowAnchor.RIGHT);
            moveToolTo(interactiveUI, "Tool 3", ToolWindowAnchor.RIGHT);

            mouse.moveTo("toolWindow.rb.Tool 1");
            interactiveUI.delay(1100);

            assertor.askForConfirm("Is preview visible?");

            mouse.moveTo("toolWindowManager.mainContainer");

            interactiveUI.delay(1000);

            assertor.askForConfirm("Is preview not visible?");
        }
    }

    public static class ToolVisisbleInteractiveTest extends MyDoggyInteractiveTest {

        public void execute(InteractiveUI interactiveUI) {
            InteractiveMouse mouse = interactiveUI.getInteractiveMouse();
            InteractiveAssertor assertor = interactiveUI.getInteractiveAssertor();

            mouse.click("toolWindow.rb.Tool 1", InteractiveMouse.Type.LEFT);
            interactiveUI.delay(1000);

            mouse.click("toolWindow.rb.Tool 3", InteractiveMouse.Type.LEFT);
            interactiveUI.delay(1000);

            assertor.askForConfirm("Is behaviuor correct?");
        }
    }

    public static class DragInteractiveTest extends MyDoggyInteractiveTest {

        public void execute(InteractiveUI interactiveUI) {
            InteractiveMouse mouse = interactiveUI.getInteractiveMouse();
            InteractiveAssertor assertor = interactiveUI.getInteractiveAssertor();

            mouse.moveTo("toolWindow.rb.Tool 1");
            interactiveUI.delay(500);
            mouse.press(InteractiveMouse.Type.LEFT);

            mouse.moveTo("toolWindowManager.bar.RIGHT", 10, 80);
            interactiveUI.delay(500);
            mouse.release();

            interactiveUI.delay(500);

            mouse.moveTo("toolWindow.rb.Tool 1");
            mouse.press(InteractiveMouse.Type.LEFT);

            mouse.moveTo("toolWindowManager.bar.LEFT", 10, 80);
            interactiveUI.delay(500);
            mouse.release();

            assertor.askForConfirm("Is behaviuor correct?");
        }

    }

    public static class SlidingTypeInteractiveTest extends MyDoggyInteractiveTest {

        public void execute(InteractiveUI interactiveUI) {
            InteractiveMouse mouse = interactiveUI.getInteractiveMouse();
            InteractiveAssertor assertor = interactiveUI.getInteractiveAssertor();

            // Validate initiale state...
            assertor.assertTrue("Invalid state",
                                     myDoggySet.getToolWindowManager().getDockableDelegator("Tool 1").getType() != ToolWindowType.SLIDING);

            mouse.moveTo("toolWindow.rb.Tool 1");
            mouse.click(InteractiveMouse.Type.LEFT);
            interactiveUI.delay(1000);

            mouse.moveTo("toolWindow.dockButton.Tool 1");
            mouse.click(InteractiveMouse.Type.LEFT);
            interactiveUI.delay(1000);

            assertor.assertTrue("Invalid state",
                                     myDoggySet.getToolWindowManager().getDockableDelegator("Tool 1").getType() == ToolWindowType.SLIDING);
        }

    }

    public static class FloatingMoveInteractiveTest extends MyDoggyInteractiveTest {

        public void execute(InteractiveUI interactiveUI) {
            InteractiveMouse mouse = interactiveUI.getInteractiveMouse();
            InteractiveAssertor assertor = interactiveUI.getInteractiveAssertor();

            // Validate initiale state...
            mouse.moveTo("toolWindow.rb.Tool 1");
            mouse.click(InteractiveMouse.Type.LEFT);
            interactiveUI.delay(1000);

            mouse.moveTo("toolWindow.floatingButton.Tool 1");
            mouse.click(InteractiveMouse.Type.LEFT);
            interactiveUI.delay(1000);

            interactiveUI.importRoot("toolWindow.floating.window.Tool 1");

            mouse.moveTo("toolWindow.bar.Tool 1");
            mouse.press(InteractiveMouse.Type.LEFT);
            interactiveUI.delay(1000);

            Point wLocation = ((FloatingTypeDescriptor) myDoggySet.getToolWindowManager().getDockableDelegator("Tool 1").getTypeDescriptor(ToolWindowType.FLOATING)).getLocation();
            wLocation.x -= 50;
            wLocation.y -= 50;

            mouse.moveTo(wLocation.x, wLocation.y);
            mouse.release();
            interactiveUI.delay(1000);

            assertor.askForConfirm("Is behaviuor correct?");
        }

    }
*/
}
