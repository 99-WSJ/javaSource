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
 * Copyright (c) 2009 by Oracle Corporation. All Rights Reserved.
 */

package java8.javax.xml.stream.events;

import javax.xml.stream.events.XMLEvent;

/**
 * An interface that describes the data found in processing instructions
 *
 * @version 1.0
 * @author Copyright (c) 2009 by Oracle Corporation. All Rights Reserved.
 * @since 1.6
 */
public interface ProcessingInstruction extends XMLEvent {

  /**
   * The target section of the processing instruction
   *
   * @return the String value of the PI or null
   */
  public String getTarget();

  /**
   * The data section of the processing instruction
   *
   * @return the String value of the PI's data or null
   */
  public String getData();
}
