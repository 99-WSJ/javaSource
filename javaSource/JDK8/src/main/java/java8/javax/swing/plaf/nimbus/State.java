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

import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.plaf.synth.SynthConstants;

/**
 * <p>Represents a built in, or custom, state in Nimbus.</p>
 *
 * <p>Synth provides several built in states, which are:
 * <ul>
 *  <li>Enabled</li>
 *  <li>Mouse Over</li>
 *  <li>Pressed</li>
 *  <li>Disabled</li>
 *  <li>Focused</li>
 *  <li>Selected</li>
 *  <li>Default</li>
 * </ul>
 *
 * <p>However, there are many more states that could be described in a LookAndFeel, and it
 * would be nice to style components differently based on these different states.
 * For example, a progress bar could be "indeterminate". It would be very convenient
 * to allow this to be defined as a "state".</p>
 *
 * <p>This class, State, is intended to be used for such situations.
 * Simply implement the abstract #isInState method. It returns true if the given
 * JComponent is "in this state", false otherwise. This method will be called
 * <em>many</em> times in <em>performance sensitive loops</em>. It must execute
 * very quickly.</p>
 *
 * <p>For example, the following might be an implementation of a custom
 * "Indeterminate" state for JProgressBars:</p>
 *
 * <pre><code>
 *     public final class IndeterminateState extends State&lt;JProgressBar&gt; {
 *         public IndeterminateState() {
 *             super("Indeterminate");
 *         }
 *
 *         &#64;Override
 *         protected boolean isInState(JProgressBar c) {
 *             return c.isIndeterminate();
 *         }
 *     }
 * </code></pre>
 */
public abstract class State<T extends JComponent>{
    static final Map<String, StandardState> standardStates = new HashMap<String, StandardState>(7);
    static final javax.swing.plaf.nimbus.State Enabled = new StandardState(SynthConstants.ENABLED);
    static final javax.swing.plaf.nimbus.State MouseOver = new StandardState(SynthConstants.MOUSE_OVER);
    static final javax.swing.plaf.nimbus.State Pressed = new StandardState(SynthConstants.PRESSED);
    static final javax.swing.plaf.nimbus.State Disabled = new StandardState(SynthConstants.DISABLED);
    static final javax.swing.plaf.nimbus.State Focused = new StandardState(SynthConstants.FOCUSED);
    static final javax.swing.plaf.nimbus.State Selected = new StandardState(SynthConstants.SELECTED);
    static final javax.swing.plaf.nimbus.State Default = new StandardState(SynthConstants.DEFAULT);

    private String name;

    /**
     * <p>Create a new custom State. Specify the name for the state. The name should
     * be unique within the states set for any one particular component.
     * The name of the state should coincide with the name used in UIDefaults.</p>
     *
     * <p>For example, the following would be correct:</p>
     * <pre><code>
     *     defaults.put("Button.States", "Enabled, Foo, Disabled");
     *     defaults.put("Button.Foo", new FooState("Foo"));
     * </code></pre>
     *
     * @param name a simple user friendly name for the state, such as "Indeterminate"
     *        or "EmbeddedPanel" or "Blurred". It is customary to use camel case,
     *        with the first letter capitalized.
     */
    protected State(String name) {
        this.name = name;
    }

    @Override public String toString() { return name; }

    /**
     * <p>This is the main entry point, called by NimbusStyle.</p>
     *
     * <p>There are both custom states and standard states. Standard states
     * correlate to the states defined in SynthConstants. When a UI delegate
     * constructs a SynthContext, it specifies the state that the component is
     * in according to the states defined in SynthConstants. Our NimbusStyle
     * will then take this state, and query each State instance in the style
     * asking whether isInState(c, s).</p>
     *
     * <p>Now, only the standard states care about the "s" param. So we have
     * this odd arrangement:</p>
     * <ul>
     *     <li>NimbusStyle calls State.isInState(c, s)</li>
     *     <li>State.isInState(c, s) simply delegates to State.isInState(c)</li>
     *     <li><em>EXCEPT</em>, StandardState overrides State.isInState(c, s) and
     *         returns directly from that method after checking its state, and
     *         does not call isInState(c) (since it is not needed for standard states).</li>
     * </ul>
     */
    boolean isInState(T c, int s) {
        return isInState(c);
    }

    /**
     * <p>Gets whether the specified JComponent is in the custom state represented
     * by this class. <em>This is an extremely performance sensitive loop.</em>
     * Please take proper precautions to ensure that it executes quickly.</p>
     *
     * <p>Nimbus uses this method to help determine what state a JComponent is
     * in. For example, a custom State could exist for JProgressBar such that
     * it would return <code>true</code> when the progress bar is indeterminate.
     * Such an implementation of this method would simply be:</p>
     *
     * <pre><code> return c.isIndeterminate();</code></pre>
     *
     * @param c the JComponent to test. This will never be null.
     * @return true if <code>c</code> is in the custom state represented by
     *         this <code>State</code> instance
     */
    protected abstract boolean isInState(T c);

    String getName() { return name; }

    static boolean isStandardStateName(String name) {
        return standardStates.containsKey(name);
    }

    static StandardState getStandardState(String name) {
        return standardStates.get(name);
    }

    static final class StandardState extends javax.swing.plaf.nimbus.State<JComponent> {
        private int state;

        private StandardState(int state) {
            super(toString(state));
            this.state = state;
            standardStates.put(getName(), this);
        }

        public int getState() {
            return state;
        }

        @Override
        boolean isInState(JComponent c, int s) {
            return (s & state) == state;
        }

        @Override
        protected boolean isInState(JComponent c) {
            throw new AssertionError("This method should never be called");
        }

        private static String toString(int state) {
            StringBuffer buffer = new StringBuffer();
            if ((state & SynthConstants.DEFAULT) == SynthConstants.DEFAULT) {
                buffer.append("Default");
            }
            if ((state & SynthConstants.DISABLED) == SynthConstants.DISABLED) {
                if (buffer.length() > 0) buffer.append("+");
                buffer.append("Disabled");
            }
            if ((state & SynthConstants.ENABLED) == SynthConstants.ENABLED) {
                if (buffer.length() > 0) buffer.append("+");
                buffer.append("Enabled");
            }
            if ((state & SynthConstants.FOCUSED) == SynthConstants.FOCUSED) {
                if (buffer.length() > 0) buffer.append("+");
                buffer.append("Focused");
            }
            if ((state & SynthConstants.MOUSE_OVER) == SynthConstants.MOUSE_OVER) {
                if (buffer.length() > 0) buffer.append("+");
                buffer.append("MouseOver");
            }
            if ((state & SynthConstants.PRESSED) == SynthConstants.PRESSED) {
                if (buffer.length() > 0) buffer.append("+");
                buffer.append("Pressed");
            }
            if ((state & SynthConstants.SELECTED) == SynthConstants.SELECTED) {
                if (buffer.length() > 0) buffer.append("+");
                buffer.append("Selected");
            }
            return buffer.toString();
        }
    }
}
