/*
 * Copyright (c) 1996, 2012, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.beans;

import java.awt.*;
import java.beans.EventSetDescriptor;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * Use the {@code BeanInfo} interface
 * to create a {@code BeanInfo} class
 * and provide explicit information about the methods,
 * properties, events, and other features of your beans.
 * <p>
 * When developing your bean, you can implement
 * the bean features required for your application task
 * omitting the rest of the {@code BeanInfo} features.
 * They will be obtained through the automatic analysis
 * by using the low-level reflection of the bean methods
 * and applying standard design patterns.
 * You have an opportunity to provide additional bean information
 * through various descriptor classes.
 * <p>
 * See the {@link SimpleBeanInfo} class that is
 * a convenient basic class for {@code BeanInfo} classes.
 * You can override the methods and properties of
 * the {@code SimpleBeanInfo} class to define specific information.
 * <p>
 * See also the {@link Introspector} class to learn more about bean behavior.
 */
public interface BeanInfo {

    /**
     * Returns the bean descriptor
     * that provides overall information about the bean,
     * such as its display name or its customizer.
     *
     * @return  a {@link BeanDescriptor} object,
     *          or {@code null} if the information is to
     *          be obtained through the automatic analysis
     */
    BeanDescriptor getBeanDescriptor();

    /**
     * Returns the event descriptors of the bean
     * that define the types of events fired by this bean.
     *
     * @return  an array of {@link EventSetDescriptor} objects,
     *          or {@code null} if the information is to
     *          be obtained through the automatic analysis
     */
    EventSetDescriptor[] getEventSetDescriptors();

    /**
     * A bean may have a default event typically applied when this bean is used.
     *
     * @return  index of the default event in the {@code EventSetDescriptor} array
     *          returned by the {@code getEventSetDescriptors} method,
     *          or -1 if there is no default event
     */
    int getDefaultEventIndex();

    /**
     * Returns descriptors for all properties of the bean.
     * <p>
     * If a property is indexed, then its entry in the result array
     * belongs to the {@link IndexedPropertyDescriptor} subclass
     * of the {@link PropertyDescriptor} class.
     * A client of the {@code getPropertyDescriptors} method
     * can use the {@code instanceof} operator to check
     * whether a given {@code PropertyDescriptor}
     * is an {@code IndexedPropertyDescriptor}.
     *
     * @return  an array of {@code PropertyDescriptor} objects,
     *          or {@code null} if the information is to
     *          be obtained through the automatic analysis
     */
    PropertyDescriptor[] getPropertyDescriptors();

    /**
     * A bean may have a default property commonly updated when this bean is customized.
     *
     * @return  index of the default property in the {@code PropertyDescriptor} array
     *          returned by the {@code getPropertyDescriptors} method,
     *          or -1 if there is no default property
     */
    int getDefaultPropertyIndex();

    /**
     * Returns the method descriptors of the bean
     * that define the externally visible methods supported by this bean.
     *
     * @return  an array of {@link MethodDescriptor} objects,
     *          or {@code null} if the information is to
     *          be obtained through the automatic analysis
     */
    MethodDescriptor[] getMethodDescriptors();

    /**
     * This method enables the current {@code BeanInfo} object
     * to return an arbitrary collection of other {@code BeanInfo} objects
     * that provide additional information about the current bean.
     * <p>
     * If there are conflicts or overlaps between the information
     * provided by different {@code BeanInfo} objects,
     * the current {@code BeanInfo} object takes priority
     * over the additional {@code BeanInfo} objects.
     * Array elements with higher indices take priority
     * over the elements with lower indices.
     *
     * @return  an array of {@code BeanInfo} objects,
     *          or {@code null} if there are no additional {@code BeanInfo} objects
     */
    java.beans.BeanInfo[] getAdditionalBeanInfo();

    /**
     * Returns an image that can be used to represent the bean in toolboxes or toolbars.
     * <p>
     * There are four possible types of icons:
     * 16 x 16 color, 32 x 32 color, 16 x 16 mono, and 32 x 32 mono.
     * If you implement a bean so that it supports a single icon,
     * it is recommended to use 16 x 16 color.
     * Another recommendation is to set a transparent background for the icons.
     *
     * @param  iconKind  the kind of icon requested
     * @return           an image object representing the requested icon,
     *                   or {@code null} if no suitable icon is available
     *
     * @see #ICON_COLOR_16x16
     * @see #ICON_COLOR_32x32
     * @see #ICON_MONO_16x16
     * @see #ICON_MONO_32x32
     */
    Image getIcon(int iconKind);

    /**
     * Constant to indicate a 16 x 16 color icon.
     */
    final static int ICON_COLOR_16x16 = 1;

    /**
     * Constant to indicate a 32 x 32 color icon.
     */
    final static int ICON_COLOR_32x32 = 2;

    /**
     * Constant to indicate a 16 x 16 monochrome icon.
     */
    final static int ICON_MONO_16x16 = 3;

    /**
     * Constant to indicate a 32 x 32 monochrome icon.
     */
    final static int ICON_MONO_32x32 = 4;
}
