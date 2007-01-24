package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.support.PropertyChangeSupport;
import org.noos.xing.mydoggy.plaf.ui.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.PanelUI;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

/**
 * @author Angelo De Caro
 */
public class DockedContainer implements PropertyChangeListener, ToolWindowContainer {
    private static ResourceBundle resourceBundle = ResourceBoundles.getResourceBundle();
    private static IconProvider iconProvider;

    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;

    private JPanel container;

    protected JPanel applicationBar;
    protected JLabel applicationBarTitle;

    private Component focusRequester;

    protected JButton floatingButton;
    protected JButton pinButton;
    protected JButton dockButton;
    protected JButton hideButton;

    private MouseAdapter applicationBarMouseAdapter;
    private PropertyChangeSupport propertyChangeSupport;

    boolean valueAdjusting;

    public DockedContainer(ToolWindowDescriptor descriptor) {
        this.descriptor = descriptor;
        this.toolWindow = descriptor.getToolWindow();

        initDockedComponents();
        initDockedListeners();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChangeEvent(evt);
    }

    public Container getContentContainer() {
        return container;
    }

    public void updateUI() {
        SwingUtilities.updateComponentTreeUI(getContentContainer());
    }

    public void uninstall() {
        Component toolWindowCmp = descriptor.getComponent();
        toolWindowCmp.removeMouseListener(applicationBarMouseAdapter);
    }

    protected void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(property, listener);
    }

    protected void removePropertyChangeListener(String property, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(property, listener);
    }

    protected void setPinVisible(boolean visible) {
        pinButton.setVisible(visible);
        ((TableLayout) applicationBar.getLayout()).setColumn(7, (visible) ? 17 : 0);
    }

    protected void setFloatingVisible(boolean visible) {
        floatingButton.setVisible(visible);
        ((TableLayout) applicationBar.getLayout()).setColumn(5, (visible) ? 17 : 0);
    }

    protected void setDockedVisible(boolean visible) {
        dockButton.setVisible(visible);
        ((TableLayout) applicationBar.getLayout()).setColumn(3, (visible) ? 17 : 0);
    }

    protected void setSliding() {
        dockButton.setIcon(iconProvider.docked);
        dockButton.setToolTipText(ResourceBoundles.getResourceBundle().getString("@@tool.tooltip.dock"));
    }

    protected void setDocked() {
        dockButton.setIcon(iconProvider.sliding);
        dockButton.setToolTipText(ResourceBoundles.getResourceBundle().getString("@@tool.tooltip.undock"));
    }

    protected void setFix() {
        floatingButton.setIcon(iconProvider.fix);
        floatingButton.setToolTipText(ResourceBoundles.getResourceBundle().getString("@@tool.tooltip.fix"));
    }

    protected void setFloating() {
        floatingButton.setIcon(iconProvider.floating);
        floatingButton.setToolTipText(ResourceBoundles.getResourceBundle().getString("@@tool.tooltip.float"));
    }

	private void initDockedComponents() {
        iconProvider = new IconProvider();
        propertyChangeSupport = new PropertyChangeSupport();

        applicationBarMouseAdapter = new ApplicationBarMouseAdapter();
        ActionListener applicationBarActionListener = new ApplicationBarActionListener();

        // Container
        container = new JPanel(new TableLayout(new double[][]{{TableLayout.FILL}, {16, TableLayout.FILL}}));
        container.setBorder(new LineBorder(Color.GRAY, 1, true, 3, 3));
        container.setFocusCycleRoot(false);

		// Application Bar
        applicationBar = new JPanel(new TableLayout(new double[][]{{3, TableLayout.FILL, 3, 15, 2, 15, 2, 15, 2, 15, 3}, {1, 14, 1}})) {
            public void setUI(PanelUI ui) {
                if (ui instanceof ApplicationBarPanelUI)
                    super.setUI(ui);
            }
        };
        applicationBar.setBorder(null);
        applicationBar.setEnabled(false);
        applicationBar.setUI(new ApplicationBarPanelUI(descriptor, this));

        // Title
        applicationBarTitle = new JLabel(toolWindow.getTitle(), JLabel.LEFT);
        applicationBarTitle.setForeground(Color.WHITE);
        applicationBarTitle.setBackground(Color.LIGHT_GRAY);
        applicationBarTitle.setOpaque(false);
        applicationBarTitle.addMouseListener(applicationBarMouseAdapter);

        // Buttons
        hideButton = renderApplicationButton("hideToolWindow", "visible", applicationBarActionListener, "@@tool.tooltip.hide");
        pinButton = renderApplicationButton("autohideOff", "pin", applicationBarActionListener, "@@tool.tooltip.unpin");
        floatingButton = renderApplicationButton("floating", "floating", applicationBarActionListener, "@@tool.tooltip.float");
        dockButton = renderApplicationButton("sliding", "undock", applicationBarActionListener, "@@tool.tooltip.undock");

        // Set ApplicationBar content
        applicationBar.add(applicationBarTitle, "1,1");
        applicationBar.add(dockButton, "3,1");
        applicationBar.add(floatingButton, "5,1");
        applicationBar.add(pinButton, "7,1");
        applicationBar.add(hideButton, "9,1");

        Component toolWindowCmp = descriptor.getComponent();
        toolWindowCmp.addMouseListener(applicationBarMouseAdapter);

        // Set Container content
        container.add(applicationBar, "0,0");
        container.add(toolWindowCmp, "0,1");

        focusRequester = SwingUtil.findFocusable(toolWindowCmp);
        if (focusRequester == null) {
            hideButton.setFocusable(true);
            focusRequester = hideButton;
        }
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", new FocusOwnerPropertyChangeListener());
    }

    private void initDockedListeners() {
//        addPropertyChangeListener("focusOwner", new FocusOwnerPropertyChangeListener());
        addPropertyChangeListener("active", new ActivePropertyChangeListener());
        addPropertyChangeListener("anchor", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
//                if (evt.getSource() == descriptor)
//                    descriptor.setDivederLocation(-1);
            }
        });
        addPropertyChangeListener("autoHide", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                boolean newValue = ((Boolean) evt.getNewValue());

                if (newValue) {
                    pinButton.setIcon(iconProvider.autoHideOn);
                    pinButton.setToolTipText(resourceBundle.getString("@@tool.tooltip.pin"));
                } else {
                    pinButton.setIcon(iconProvider.autoHideOff);
                    pinButton.setToolTipText(resourceBundle.getString("@@tool.tooltip.unpin"));
                }
            }
        });
        addPropertyChangeListener("type", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                if (evt.getNewValue() == ToolWindowType.DOCKED) {
                    configureDockedIcons();
                }
            }
        });
	}

    private JButton renderApplicationButton(String iconName, String actionCommnad, ActionListener actionListener, String tooltip) {
        JButton button = new ApplicationButton();
        button.setUI((ButtonUI) BasicButtonUI.createUI(button));
        button.setName(actionCommnad);
        button.setRolloverEnabled(true);
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setBorderPainted(false);
        button.setActionCommand(actionCommnad);
        button.addActionListener(actionListener);
        button.setToolTipText(resourceBundle.getString(tooltip));
        button.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/" + iconName + ".png"));

        return button;
    }

    private void configureDockedIcons() {
        setPinVisible(true);
        setFloating();
        setFloatingVisible(true);
        setDockedVisible(true);
        setDocked();
    }


    class ApplicationBarMouseAdapter extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            toolWindow.setActive(true);
        }
    }

    class ApplicationBarActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            toolWindow.setActive(true);

            String actionCommnad = e.getActionCommand();
            if ("visible".equals(actionCommnad)) {
                toolWindow.setVisible(false);
            } else if ("pin".equals(actionCommnad)) {
                toolWindow.setAutoHide(!toolWindow.isAutoHide());
            } else if ("floating".equals(actionCommnad)) {
                ToolWindowType type = toolWindow.getType();
                if (type == ToolWindowType.FLOATING || type == ToolWindowType.FLOATING_FREE) {
                    toolWindow.setType(ToolWindowType.DOCKED);
                } else if (type == ToolWindowType.DOCKED || type == ToolWindowType.SLIDING) {
                    toolWindow.setType(descriptor.isFloatingWindow() ? ToolWindowType.FLOATING_FREE : ToolWindowType.FLOATING);
                }
            } else if ("undock".equals(actionCommnad)) {
                ToolWindowType type = toolWindow.getType();
                if (type == ToolWindowType.DOCKED) {
                    toolWindow.setType(ToolWindowType.SLIDING);
                } else if (type == ToolWindowType.SLIDING) {
                    toolWindow.setType(ToolWindowType.DOCKED);
                }
            }
        }

    }


	class FocusOwnerPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (!toolWindow.isVisible())
                return;

            Component component = (Component) evt.getNewValue();
            if (component == null) return;
            if (component instanceof JRootPane) return;

			valueAdjusting = true;

//            System.out.println("component = " + component);

            if (isInternalComponent(component)) {
                toolWindow.setActive(true);
                if (focusRequester == null)
                    focusRequester = component;
                else {
                    if (!(focusRequester instanceof ApplicationButton))
                        focusRequester = component;
                    else
                        focusRequester.requestFocusInWindow();
                }
            } else {
                toolWindow.setActive(false);
                if (toolWindow.isAutoHide())
                    toolWindow.setVisible(false);
            }

            valueAdjusting = false;
        }

        protected final boolean isInternalComponent(Component component) {
            if (component == null)
                return false;
            Component traverser = component;
            while (traverser.getParent() != null) {
                if (traverser.getParent() == container) {
                    return true;
                }
                traverser = traverser.getParent();
            }
            return false;
        }

    }

    class ActivePropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() != descriptor)
                return;

            boolean active = (Boolean) evt.getNewValue();
            applicationBar.setEnabled(active);

            if (active) {
                if (toolWindow.isAutoHide()) {
                    pinButton.setIcon(iconProvider.autoHideOn);
                } else
                    pinButton.setIcon(iconProvider.autoHideOff);

                hideButton.setIcon(iconProvider.hideToolWindow);

                if (toolWindow.getType() == ToolWindowType.SLIDING) {
                    dockButton.setIcon(iconProvider.docked);
                } else
                    dockButton.setIcon(iconProvider.sliding);

                if (toolWindow.getType() == ToolWindowType.FLOATING) {
                    floatingButton.setIcon(iconProvider.fix);
                } else
                    floatingButton.setIcon(iconProvider.floating);

            } else {
                if (toolWindow.isAutoHide()) {
                    pinButton.setIcon(iconProvider.autoHideOnInactive);
                } else
                    pinButton.setIcon(iconProvider.autoHideOffInactive);

                hideButton.setIcon(iconProvider.hideToolWindowInactive);

                if (toolWindow.getType() == ToolWindowType.SLIDING) {
                    dockButton.setIcon(iconProvider.dockedInactive);
                } else
                    dockButton.setIcon(iconProvider.slidingInactive);

                if (toolWindow.getType() == ToolWindowType.FLOATING) {
                    floatingButton.setIcon(iconProvider.fixInactive);
                } else
                    floatingButton.setIcon(iconProvider.floatingInactive);
            }

            if (active && focusRequester != null && !valueAdjusting) {
                SwingUtil.requestFocus(focusRequester);
            }
        }
    }

    static class ApplicationButton extends JButton {
        public void setUI(ButtonUI ui) {
            super.setUI((ButtonUI) BasicButtonUI.createUI(this));
            setRolloverEnabled(true);
            setOpaque(false);
            setFocusPainted(false);
            setFocusable(false);
            setBorderPainted(false);
        }
    }

	/**
	 * TODO: find a best form for this.
	 */
	static class IconProvider {
        Icon docked;
        Icon dockedInactive;

        Icon sliding;
        Icon slidingInactive;

        Icon floating;
        Icon floatingInactive;

        Icon fix;
        Icon fixInactive;

        Icon autoHideOn;
        Icon autoHideOnInactive;

        Icon autoHideOff;
        Icon autoHideOffInactive;

        Icon hideToolWindow;
        Icon hideToolWindowInactive;


        public IconProvider() {
            sliding = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/sliding.png");
            slidingInactive = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/slidingInactive.png");

            floating = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/floating.png");
            floatingInactive = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/floatingInactive.png");

            fix = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/fix.png");
            fixInactive = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/fixInactive.png");

            docked = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/docked.png");
            dockedInactive = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/dockedInactive.png");

            autoHideOn = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOn.png");
            autoHideOnInactive = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOnInactive.png");

            autoHideOff = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOff.png");
            autoHideOffInactive = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOffInactive.png");

            hideToolWindow = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/hideToolWindow.png");
            hideToolWindowInactive = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/hideToolWindowInactive.png");
        }

    }
}
