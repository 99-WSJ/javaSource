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

import java.util.Locale;

import javax.print.attribute.Attribute;
import javax.print.attribute.TextSyntax;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.standard.DocumentName;

/**
 * Class JobName is a printing attribute class, a text attribute, that specifies
 * the name of a print job. A job's name is an arbitrary string defined by the
 * client. It does not need to be unique between different jobs. A Print Job's
 * JobName attribute is set to the value supplied by the client in the Print
 * Request's attribute set. If, however, the client does not supply a JobName
 * attribute in the Print Request, the printer, when it creates the Print Job,
 * must generate a JobName. The printer should generate the value of the Print
 * Job's JobName attribute from the first of the following sources that produces
 * a value: (1) the {@link DocumentName DocumentName} attribute of the first (or
 * only) doc in the job, (2) the URL of the first (or only) doc in the job, if
 * the doc's print data representation object is a URL, or (3) any other piece
 * of Print Job specific and/or document content information.
 * <P>
 * <B>IPP Compatibility:</B> The string value gives the IPP name value. The
 * locale gives the IPP natural language. The category name returned by
 * <CODE>getName()</CODE> gives the IPP attribute name.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public final class JobName extends TextSyntax
        implements PrintRequestAttribute, PrintJobAttribute {

    private static final long serialVersionUID = 4660359192078689545L;

    /**
     * Constructs a new job name attribute with the given job name and locale.
     *
     * @param  jobName  Job name.
     * @param  locale   Natural language of the text string. null
     * is interpreted to mean the default locale as returned
     * by <code>Locale.getDefault()</code>
     *
     * @exception  NullPointerException
     *     (unchecked exception) Thrown if <CODE>jobName</CODE> is null.
     */
    public JobName(String jobName, Locale locale) {
        super (jobName, locale);
    }

    /**
     * Returns whether this job name attribute is equivalent to the passed in
     * object. To be equivalent, all of the following conditions must be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class JobName.
     * <LI>
     * This job name attribute's underlying string and <CODE>object</CODE>'s
     * underlying string are equal.
     * <LI>
     * This job name attribute's locale and <CODE>object</CODE>'s locale are
     * equal.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this job name
     *          attribute, false otherwise.
     */
    public boolean equals(Object object) {
        return (super.equals(object) && object instanceof javax.print.attribute.standard.JobName);
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class JobName, the category is class JobName itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.JobName.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class JobName, the category name is <CODE>"job-name"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "job-name";
    }

}
