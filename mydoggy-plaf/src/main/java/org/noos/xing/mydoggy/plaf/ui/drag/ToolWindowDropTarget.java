package org.noos.xing.mydoggy.plaf.ui.drag;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class ToolWindowDropTarget extends DropTarget {

    public ToolWindowDropTarget(JComponent component, ToolWindowManager toolWindowManager, ToolWindowAnchor anchor) throws HeadlessException {
        super(component, DnDConstants.ACTION_MOVE, new ToolWindowDropTargetListener(component, toolWindowManager, anchor));
    }

    public static class ToolWindowDropTargetListener implements DropTargetListener, PropertyChangeListener {
        protected ToolWindowManager toolWindowManager;
        protected ToolWindowAnchor anchor;
        protected int anchorIndex;
        protected JComponent component;

        protected ToolWindow onToolWindow;
        protected ToolWindowAnchor dragAnchor;

        protected Border oldBorder;
        protected Border dragBorder = new LineBorder(Color.BLUE, 3);

        public ToolWindowDropTargetListener(JComponent component, ToolWindowManager toolWindowManager, ToolWindowAnchor anchor) {
            this.component = component;
            this.toolWindowManager = toolWindowManager;
            this.anchor = anchor;

            this.component.addPropertyChangeListener("dragAnchor", this);
            this.component.addPropertyChangeListener("dragToolWindow", this);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            if ("dragAnchor".equals(propertyName))
                this.dragAnchor = (ToolWindowAnchor) evt.getNewValue();
            else {
                this.onToolWindow = null;
            }
        }

        public void dragEnter(DropTargetDragEvent dtde) {
            if  (dtde.getDropAction() == DnDConstants.ACTION_MOVE &&
                 (dtde.getTransferable().isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF) ||
                  dtde.getTransferable().isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF) ||
                  dtde.getTransferable().isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF))
                 ) {
                onToolWindow = null;

                dtde.acceptDrag(dtde.getDropAction());

                if (component.getBorder() != dragBorder)
                    oldBorder = component.getBorder();
//                component.setBorder(dragBorder);

                putProperty("dragStart");
            } else
                dtde.rejectDrag();
        }

        public void dragOver(DropTargetDragEvent dtde) {
            Point location = dtde.getLocation();
            component.putClientProperty("dragOver", location);

            Component deepestCmp = SwingUtilities.getDeepestComponentAt(component, location.x, location.y);
            if (deepestCmp != null) {
                JComponent toolWindowContainer = (JComponent) SwingUtil.getParent(deepestCmp, "toolWindow.container.");
                if (toolWindowContainer != null) {
                    onToolWindow = (ToolWindow) toolWindowContainer.getClientProperty(ToolWindow.class);
                    anchorIndex = onToolWindow.getAnchorIndex();
                }
            } else {
                onToolWindow = null;
                anchorIndex = -1;
            }
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
            dragEnter(dtde);
        }

        public void dragExit(DropTargetEvent dte) {
//            component.setBorder(oldBorder);
            onToolWindow = null;
            oldBorder = null;
            putProperty("dragExit");
        }

        public void drop(DropTargetDropEvent dtde) {
            try {
                if (dtde.getDropAction() == DnDConstants.ACTION_MOVE) {
                    if  (dtde.getTransferable().isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF))  {
                        try {
                            Transferable transferable = dtde.getTransferable();
                            ToolWindow toolWindow = toolWindowManager.getToolWindow(
                                    dtde.getTransferable().getTransferData(MyDoggyTransferable.TOOL_WINDOW_ID_DF)
                            );

                            if (toolWindow != null) {
                                // Move tool to another anchor

                                // Chech if it was a tab
                                if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)) {
                                    // Remove from tab
                                    ToolWindowTab tab = toolWindowManager.getToolWindowTab(
                                            transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)
                                    );
                                    tab.getOwner().removeToolWindowTab(tab);
                                    toolWindow = (ToolWindow) tab.getDockableDelegator();
                                }

                                boolean oldAggregateMode = toolWindow.isAggregateMode();
                                toolWindow.setAggregateMode(true);
                                try {
                                    if (dragAnchor != null)   {
                                        switch (dragAnchor)  {
                                            case LEFT:
                                                if (onToolWindow != null) {
                                                    toolWindow.setAnchor(anchor, anchorIndex != -1 ? anchorIndex - 1 : -1);
                                                    toolWindow.aggregate(onToolWindow, AggregationPosition.LEFT);
                                                } else {
                                                    toolWindow.setAnchor(anchor, 0);
                                                    toolWindow.aggregate(AggregationPosition.LEFT);
                                                }
                                                break;
                                            case RIGHT :
                                                if (onToolWindow != null) {
                                                    toolWindow.setAnchor(anchor, anchorIndex != -1 ? anchorIndex + 1 : -1);
                                                    toolWindow.aggregate(onToolWindow, AggregationPosition.RIGHT);
                                                } else {
                                                    toolWindow.setAnchor(anchor);
                                                    toolWindow.aggregate(AggregationPosition.RIGHT);
                                                }
                                                break;
                                            case BOTTOM:
                                                if (onToolWindow != null) {
                                                    toolWindow.setAnchor(anchor, anchorIndex != -1 ? anchorIndex + 1 : -1);
                                                    toolWindow.aggregate(onToolWindow, AggregationPosition.BOTTOM);
                                                } else {
                                                    toolWindow.setAnchor(anchor);
                                                    toolWindow.aggregate(AggregationPosition.BOTTOM);
                                                }
                                                break;
                                            case TOP:
                                                if (onToolWindow != null) {
                                                    toolWindow.setAnchor(anchor, anchorIndex != -1 ? anchorIndex - 1 : -1);
                                                    toolWindow.aggregate(onToolWindow, AggregationPosition.TOP);
                                                } else {
                                                    toolWindow.setAnchor(anchor, 0);
                                                    toolWindow.aggregate(AggregationPosition.TOP);
                                                }
                                                break;
                                        }
                                    } else
                                        toolWindow.aggregate();
                                    toolWindow.setActive(true);
                                } finally {
                                    toolWindow.setAggregateMode(oldAggregateMode);
                                }

                                dtde.dropComplete(true);
                            } else
                                dtde.dropComplete(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                            dtde.dropComplete(false);
                        }
                    } else if  (dtde.getTransferable().isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF))  {
                        try {
                            Content content = toolWindowManager.getContentManager().getContent(
                                    dtde.getTransferable().getTransferData(MyDoggyTransferable.CONTENT_ID_DF)
                            );
                            if (content != null) {
                                toolWindowManager.getContentManager().removeContent(content);

                                if (content.getDockableDelegator() != null) {
                                    Dockable delegator = content.getDockableDelegator();

                                    if (delegator instanceof ToolWindow) {
                                        ToolWindow toolWindow = (ToolWindow) delegator;

                                        boolean oldAggregateMode = toolWindow.isAggregateMode();
                                        toolWindow.setAggregateMode(true);
                                        try {
                                            toolWindow.setAnchor(anchor, anchorIndex);

                                            if (dragAnchor != null)   {
                                                switch (dragAnchor)  {
                                                    case LEFT:
                                                        if (onToolWindow != null)
                                                            toolWindow.aggregate(onToolWindow, AggregationPosition.LEFT);
                                                        else
                                                            toolWindow.aggregate(AggregationPosition.LEFT);
                                                        break;
                                                    case RIGHT:
                                                        if (onToolWindow != null)
                                                            toolWindow.aggregate(onToolWindow, AggregationPosition.RIGHT);
                                                        else
                                                            toolWindow.aggregate(AggregationPosition.RIGHT);
                                                        break;
                                                    case BOTTOM:
                                                        if (onToolWindow != null)
                                                            toolWindow.aggregate(onToolWindow, AggregationPosition.BOTTOM);
                                                        else
                                                            toolWindow.aggregate(AggregationPosition.BOTTOM);
                                                        break;
                                                    case TOP:
                                                        if (onToolWindow != null)
                                                            toolWindow.aggregate(onToolWindow, AggregationPosition.TOP);
                                                        else
                                                            toolWindow.aggregate(AggregationPosition.TOP);
                                                        break;
                                                }
                                            } else
                                                toolWindow.aggregate();
                                            toolWindow.setActive(true);
                                        } finally {
                                           toolWindow.setAggregateMode(oldAggregateMode);
                                        }
                                    }
                                } else {
                                    // TODO : Need a tool window for delegation...
                                }

                                dtde.dropComplete(true);
                            } else
                                dtde.dropComplete(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                            dtde.dropComplete(false);
                        }
                    } else
                        dtde.rejectDrop();
                } else
                    dtde.rejectDrop();
            } finally {
                putProperty("dragEnd");

                // Restore component
                dragExit(dtde);
            }
        }

        protected void putProperty(String name) {
            Boolean value = (Boolean) component.getClientProperty(name);
            if (value != null)
                component.putClientProperty(name, !value);
            else
                component.putClientProperty(name, false);
        }
    }

}