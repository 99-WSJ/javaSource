/*
 * Copyright (c) 1999, 2008, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.management;


import javax.management.BadAttributeValueExpException;
import javax.management.BadBinaryOpValueExpException;
import javax.management.BadStringOperationException;
import javax.management.InvalidApplicationException;
import javax.management.NumericValueExp;
import javax.management.ObjectName;
import javax.management.QueryEval;
import javax.management.QueryExp;
import javax.management.StringValueExp;
import javax.management.ValueExp;

/**
 * This class is used by the query-building mechanism to represent binary
 * operations.
 * @serial include
 *
 * @since 1.5
 */
class InQueryExp extends QueryEval implements QueryExp {

    /* Serial version */
    private static final long serialVersionUID = -5801329450358952434L;

    /**
     * @serial The {@link ValueExp} to be found
     */
    private ValueExp val;

    /**
     * @serial The array of {@link ValueExp} to be searched
     */
    private ValueExp[]  valueList;


    /**
     * Basic Constructor.
     */
    public InQueryExp() {
    }

    /**
     * Creates a new InQueryExp with the specified ValueExp to be found in
     * a specified array of ValueExp.
     */
    public InQueryExp(ValueExp v1, ValueExp items[]) {
        val       = v1;
        valueList = items;
    }


    /**
     * Returns the checked value of the query.
     */
    public ValueExp getCheckedValue()  {
        return val;
    }

    /**
     * Returns the array of values of the query.
     */
    public ValueExp[] getExplicitValues()  {
        return valueList;
    }

    /**
     * Applies the InQueryExp on a MBean.
     *
     * @param name The name of the MBean on which the InQueryExp will be applied.
     *
     * @return  True if the query was successfully applied to the MBean, false otherwise.
     *
     * @exception BadStringOperationException
     * @exception BadBinaryOpValueExpException
     * @exception BadAttributeValueExpException
     * @exception InvalidApplicationException
     */
    public boolean apply(ObjectName name)
    throws BadStringOperationException, BadBinaryOpValueExpException,
        BadAttributeValueExpException, InvalidApplicationException  {
        if (valueList != null) {
            ValueExp v      = val.apply(name);
            boolean numeric = v instanceof javax.management.NumericValueExp;

            for (ValueExp element : valueList) {
                element = element.apply(name);
                if (numeric) {
                    if (((javax.management.NumericValueExp) element).doubleValue() ==
                        ((javax.management.NumericValueExp) v).doubleValue()) {
                        return true;
                    }
                } else {
                    if (((StringValueExp) element).getValue().equals(
                        ((StringValueExp) v).getValue())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns the string representing the object.
     */
    public String toString()  {
        return val + " in (" + generateValueList() + ")";
    }


    private String generateValueList() {
        if (valueList == null || valueList.length == 0) {
            return "";
        }

        final StringBuilder result =
                new StringBuilder(valueList[0].toString());

        for (int i = 1; i < valueList.length; i++) {
            result.append(", ");
            result.append(valueList[i]);
        }

        return result.toString();
    }

 }
