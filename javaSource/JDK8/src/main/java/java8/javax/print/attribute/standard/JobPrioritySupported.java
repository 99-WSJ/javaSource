/*
 * Copyright (c) 2000, 2004, Oracle and/or its affiliates. All rights reserved.
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
package java8.javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.SupportedValuesAttribute;
import javax.print.attribute.standard.JobPriority;

/**
 * Class JobPrioritySupported is an integer valued printing attribute class
 * that specifies whether a Print Service instance supports the {@link
 * JobPriority JobPriority} attribute and the number of different job priority
 * levels supported.
 * <P>
 * The client can always specify any {@link JobPriority JobPriority} value
 * from 1 to 100 for a job. However, the Print Service instance may support
 * fewer than 100 different job priority levels. If this is the case, the
 * Print Service instance automatically maps the client-specified job priority
 * value to one of the supported job priority levels, dividing the 100 job
 * priority values equally among the available job priority levels.
 * <P>
 * <B>IPP Compatibility:</B> The integer value gives the IPP integer value.
 * The category name returned by <CODE>getName()</CODE> gives the IPP
 * attribute name.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public final class JobPrioritySupported extends IntegerSyntax
    implements SupportedValuesAttribute {

    private static final long serialVersionUID = 2564840378013555894L;


    /**
     * Construct a new job priority supported attribute with the given integer
     * value.
     *
     * @param  value  Number of different job priority levels supported.
     *
     * @exception  IllegalArgumentException
     *     (Unchecked exception) Thrown if <CODE>value</CODE> is less than 1
     *     or greater than 100.
     */
    public JobPrioritySupported(int value) {
        super (value, 1, 100);
    }

    /**
     * Returns whether this job priority supported attribute is equivalent to
     * the passed in object. To be equivalent, all of the following conditions
     * must be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class JobPrioritySupported.
     * <LI>
     * This job priority supported attribute's value and
     * <CODE>object</CODE>'s value are equal.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this job
     *          priority supported attribute, false otherwise.
     */
    public boolean equals (Object object) {

        return (super.equals(object) &&
               object instanceof javax.print.attribute.standard.JobPrioritySupported);
    }


    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class JobPrioritySupported, the
     * category is class JobPrioritySupported itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.JobPrioritySupported.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class JobPrioritySupported, the
     * category name is <CODE>"job-priority-supported"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "job-priority-supported";
    }

}
