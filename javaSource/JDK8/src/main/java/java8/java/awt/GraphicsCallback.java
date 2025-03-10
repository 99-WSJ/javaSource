/*
 * Copyright (c) 1999, 2000, Oracle and/or its affiliates. All rights reserved.
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

import sun.awt.SunGraphicsCallback;

import java.awt.Graphics;
import java.awt.peer.LightweightPeer;


abstract class GraphicsCallback extends SunGraphicsCallback {

    static final class PaintCallback extends java.awt.GraphicsCallback {
        private static java.awt.GraphicsCallback.PaintCallback instance = new java.awt.GraphicsCallback.PaintCallback();

        private PaintCallback() {}
        public void run(Component comp, Graphics cg) {
            comp.paint(cg);
        }
        static java.awt.GraphicsCallback.PaintCallback getInstance() {
            return instance;
        }
    }
    static final class PrintCallback extends java.awt.GraphicsCallback {
        private static java.awt.GraphicsCallback.PrintCallback instance = new java.awt.GraphicsCallback.PrintCallback();

        private PrintCallback() {}
        public void run(Component comp, Graphics cg) {
            comp.print(cg);
        }
        static java.awt.GraphicsCallback.PrintCallback getInstance() {
            return instance;
        }
    }
    static final class PaintAllCallback extends java.awt.GraphicsCallback {
        private static java.awt.GraphicsCallback.PaintAllCallback instance = new java.awt.GraphicsCallback.PaintAllCallback();

        private PaintAllCallback() {}
        public void run(Component comp, Graphics cg) {
            comp.paintAll(cg);
        }
        static java.awt.GraphicsCallback.PaintAllCallback getInstance() {
            return instance;
        }
    }
    static final class PrintAllCallback extends java.awt.GraphicsCallback {
        private static java.awt.GraphicsCallback.PrintAllCallback instance = new java.awt.GraphicsCallback.PrintAllCallback();

        private PrintAllCallback() {}
        public void run(Component comp, Graphics cg) {
            comp.printAll(cg);
        }
        static java.awt.GraphicsCallback.PrintAllCallback getInstance() {
            return instance;
        }
    }
    static final class PeerPaintCallback extends java.awt.GraphicsCallback {
        private static java.awt.GraphicsCallback.PeerPaintCallback instance = new java.awt.GraphicsCallback.PeerPaintCallback();

        private PeerPaintCallback() {}
        public void run(Component comp, Graphics cg) {
            comp.validate();
            if (comp.peer instanceof LightweightPeer) {
                comp.lightweightPaint(cg);
            } else {
                comp.peer.paint(cg);
            }
        }
        static java.awt.GraphicsCallback.PeerPaintCallback getInstance() {
            return instance;
        }
    }
    static final class PeerPrintCallback extends java.awt.GraphicsCallback {
        private static java.awt.GraphicsCallback.PeerPrintCallback instance = new java.awt.GraphicsCallback.PeerPrintCallback();

        private PeerPrintCallback() {}
        public void run(Component comp, Graphics cg) {
            comp.validate();
            if (comp.peer instanceof LightweightPeer) {
                comp.lightweightPrint(cg);
            } else {
                comp.peer.print(cg);
            }
        }
        static java.awt.GraphicsCallback.PeerPrintCallback getInstance() {
            return instance;
        }
    }
    static final class PaintHeavyweightComponentsCallback
        extends java.awt.GraphicsCallback
    {
        private static PaintHeavyweightComponentsCallback instance =
            new PaintHeavyweightComponentsCallback();

        private PaintHeavyweightComponentsCallback() {}
        public void run(Component comp, Graphics cg) {
            if (comp.peer instanceof LightweightPeer) {
                comp.paintHeavyweightComponents(cg);
            } else {
                comp.paintAll(cg);
            }
        }
        static PaintHeavyweightComponentsCallback getInstance() {
            return instance;
        }
    }
    static final class PrintHeavyweightComponentsCallback
        extends java.awt.GraphicsCallback
    {
        private static PrintHeavyweightComponentsCallback instance =
            new PrintHeavyweightComponentsCallback();

        private PrintHeavyweightComponentsCallback() {}
        public void run(Component comp, Graphics cg) {
            if (comp.peer instanceof LightweightPeer) {
                comp.printHeavyweightComponents(cg);
            } else {
                comp.printAll(cg);
            }
        }
        static PrintHeavyweightComponentsCallback getInstance() {
            return instance;
        }
    }
}
