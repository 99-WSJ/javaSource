/*
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

/*
 *
 *
 *
 *
 *
 * Copyright (c) 2000 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See W3C License http://www.w3.org/Consortium/Legal/ for more
 * details.
 */

package java8.org.w3c.dom.html;

import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;

/**
 *  The <code>FORM</code> element encompasses behavior similar to a collection
 * and an element. It provides direct access to the contained input elements
 * as well as the attributes of the form element. See the  FORM element
 * definition in HTML 4.0.
 * <p>See also the <a href='http://www.w3.org/TR/2000/CR-DOM-Level-2-20000510'>Document Object Model (DOM) Level 2 Specification</a>.
 */
public interface HTMLFormElement extends HTMLElement {
    /**
     *  Returns a collection of all control elements in the form.
     */
    public HTMLCollection getElements();

    /**
     *  The number of form controls in the form.
     */
    public int getLength();

    /**
     *  Names the form.
     */
    public String getName();
    public void setName(String name);

    /**
     *  List of character sets supported by the server. See the
     * accept-charset attribute definition in HTML 4.0.
     */
    public String getAcceptCharset();
    public void setAcceptCharset(String acceptCharset);

    /**
     *  Server-side form handler. See the  action attribute definition in HTML
     * 4.0.
     */
    public String getAction();
    public void setAction(String action);

    /**
     *  The content type of the submitted form,  generally
     * "application/x-www-form-urlencoded".  See the  enctype attribute
     * definition in HTML 4.0.
     */
    public String getEnctype();
    public void setEnctype(String enctype);

    /**
     *  HTTP method used to submit form. See the  method attribute definition
     * in HTML 4.0.
     */
    public String getMethod();
    public void setMethod(String method);

    /**
     *  Frame to render the resource in. See the  target attribute definition
     * in HTML 4.0.
     */
    public String getTarget();
    public void setTarget(String target);

    /**
     *  Submits the form. It performs the same action as a  submit button.
     */
    public void submit();

    /**
     *  Restores a form element's default values. It performs  the same action
     * as a reset button.
     */
    public void reset();

}
