/*
 * Copyright (c) 2005, 2006, Oracle and/or its affiliates. All rights reserved.
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
package java8.javax.swing.plaf.nimbus;

import javax.swing.plaf.nimbus.DropShadowEffect;
import java.awt.Color;

/**
 * InnerGlowEffect
 *
 * @author Created by Jasper Potts (Jun 21, 2007)
 */
class OuterGlowEffect extends javax.swing.plaf.nimbus.DropShadowEffect {
    OuterGlowEffect() {
        distance = 0;
        color = new Color(255, 255, 211);
    }
}
