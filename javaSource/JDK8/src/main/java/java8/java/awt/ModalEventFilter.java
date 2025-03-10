/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package java8.java.awt;

import sun.awt.AppContext;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

abstract class ModalEventFilter implements java.awt.EventFilter {

    protected Dialog modalDialog;
    protected boolean disabled;

    protected ModalEventFilter(Dialog modalDialog) {
        this.modalDialog = modalDialog;
        disabled = false;
    }

    Dialog getModalDialog() {
        return modalDialog;
    }

    public FilterAction acceptEvent(AWTEvent event) {
        if (disabled || !modalDialog.isVisible()) {
            return FilterAction.ACCEPT;
        }
        int eventID = event.getID();
        if ((eventID >= MouseEvent.MOUSE_FIRST &&
             eventID <= MouseEvent.MOUSE_LAST) ||
            (eventID >= ActionEvent.ACTION_FIRST &&
             eventID <= ActionEvent.ACTION_LAST) ||
            eventID == WindowEvent.WINDOW_CLOSING)
        {
            Object o = event.getSource();
            if (o instanceof sun.awt.ModalExclude) {
                // Exclude this object from modality and
                // continue to pump it's events.
            } else if (o instanceof Component) {
                Component c = (Component)o;
                while ((c != null) && !(c instanceof Window)) {
                    c = c.getParent_NoClientCode();
                }
                if (c != null) {
                    return acceptWindow((Window)c);
                }
            }
        }
        return FilterAction.ACCEPT;
    }

    protected abstract FilterAction acceptWindow(Window w);

    // When a modal dialog is hidden its modal filter may not be deleted from
    // EventDispatchThread event filters immediately, so we need to mark the filter
    // as disabled to prevent it from working. Simple checking for visibility of
    // the modalDialog is not enough, as it can be hidden and then shown again
    // with a new event pump and a new filter
    void disable() {
        disabled = true;
    }

    int compareTo(java.awt.ModalEventFilter another) {
        Dialog anotherDialog = another.getModalDialog();
        // check if modalDialog is from anotherDialog's hierarchy
        //   or vice versa
        Component c = modalDialog;
        while (c != null) {
            if (c == anotherDialog) {
                return 1;
            }
            c = c.getParent_NoClientCode();
        }
        c = anotherDialog;
        while (c != null) {
            if (c == modalDialog) {
                return -1;
            }
            c = c.getParent_NoClientCode();
        }
        // check if one dialog blocks (directly or indirectly) another
        Dialog blocker = modalDialog.getModalBlocker();
        while (blocker != null) {
            if (blocker == anotherDialog) {
                return -1;
            }
            blocker = blocker.getModalBlocker();
        }
        blocker = anotherDialog.getModalBlocker();
        while (blocker != null) {
            if (blocker == modalDialog) {
                return 1;
            }
            blocker = blocker.getModalBlocker();
        }
        // compare modality types
        return modalDialog.getModalityType().compareTo(anotherDialog.getModalityType());
    }

    static java.awt.ModalEventFilter createFilterForDialog(Dialog modalDialog) {
        switch (modalDialog.getModalityType()) {
            case DOCUMENT_MODAL: return new DocumentModalEventFilter(modalDialog);
            case APPLICATION_MODAL: return new ApplicationModalEventFilter(modalDialog);
            case TOOLKIT_MODAL: return new ToolkitModalEventFilter(modalDialog);
        }
        return null;
    }

    private static class ToolkitModalEventFilter extends java.awt.ModalEventFilter {

        private AppContext appContext;

        ToolkitModalEventFilter(Dialog modalDialog) {
            super(modalDialog);
            appContext = modalDialog.appContext;
        }

        protected FilterAction acceptWindow(Window w) {
            if (w.isModalExcluded(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE)) {
                return FilterAction.ACCEPT;
            }
            if (w.appContext != appContext) {
                return FilterAction.REJECT;
            }
            while (w != null) {
                if (w == modalDialog) {
                    return FilterAction.ACCEPT_IMMEDIATELY;
                }
                w = w.getOwner();
            }
            return FilterAction.REJECT;
        }
    }

    private static class ApplicationModalEventFilter extends java.awt.ModalEventFilter {

        private AppContext appContext;

        ApplicationModalEventFilter(Dialog modalDialog) {
            super(modalDialog);
            appContext = modalDialog.appContext;
        }

        protected FilterAction acceptWindow(Window w) {
            if (w.isModalExcluded(Dialog.ModalExclusionType.APPLICATION_EXCLUDE)) {
                return FilterAction.ACCEPT;
            }
            if (w.appContext == appContext) {
                while (w != null) {
                    if (w == modalDialog) {
                        return FilterAction.ACCEPT_IMMEDIATELY;
                    }
                    w = w.getOwner();
                }
                return FilterAction.REJECT;
            }
            return FilterAction.ACCEPT;
        }
    }

    private static class DocumentModalEventFilter extends java.awt.ModalEventFilter {

        private Window documentRoot;

        DocumentModalEventFilter(Dialog modalDialog) {
            super(modalDialog);
            documentRoot = modalDialog.getDocumentRoot();
        }

        protected FilterAction acceptWindow(Window w) {
            // application- and toolkit-excluded windows are blocked by
            // document-modal dialogs from their child hierarchy
            if (w.isModalExcluded(Dialog.ModalExclusionType.APPLICATION_EXCLUDE)) {
                Window w1 = modalDialog.getOwner();
                while (w1 != null) {
                    if (w1 == w) {
                        return FilterAction.REJECT;
                    }
                    w1 = w1.getOwner();
                }
                return FilterAction.ACCEPT;
            }
            while (w != null) {
                if (w == modalDialog) {
                    return FilterAction.ACCEPT_IMMEDIATELY;
                }
                if (w == documentRoot) {
                    return FilterAction.REJECT;
                }
                w = w.getOwner();
            }
            return FilterAction.ACCEPT;
        }
    }
}
