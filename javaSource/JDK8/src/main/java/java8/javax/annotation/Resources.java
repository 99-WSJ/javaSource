/*
 * Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.annotation;
import javax.annotation.Resource;
import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * This class is used to allow multiple resources declarations.
 *
 * @see Resource
 * @since Common Annotations 1.0
 */

@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Resources {
   /**
    * Array used for multiple resource declarations.
    */
   Resource[] value();
}
