/*
 * Copyright (c) 2007-2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java8.sun.org.apache.xerces.internal.jaxp.validation;

import org.xml.sax.SAXException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.IOException;

/**
 * <p>Instances of ValidatorHelper are able to validate
 * specific source and result types.</p>
 *
 * @author Michael Glavassevich, IBM
 */
interface ValidatorHelper {

    public void validate(Source source, Result result)
        throws SAXException, IOException;
}
