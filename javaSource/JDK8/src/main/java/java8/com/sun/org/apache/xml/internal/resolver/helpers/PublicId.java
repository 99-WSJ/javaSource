/*
 * Copyright (c) 2007-2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
// PublicId.java - Information about public identifiers

/*
 * Copyright 2001-2004 The Apache Software Foundation or its licensors,
 * as applicable.
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

package java8.sun.org.apache.xml.internal.resolver.helpers;

/**
 * Static methods for dealing with public identifiers.
 *
 * <p>This class defines a set of static methods that can be called
 * to handle public identifiers.</p>
 *
 * @author Norman Walsh
 * <a href="mailto:Norman.Walsh@Sun.COM">Norman.Walsh@Sun.COM</a>
 *
 */
public abstract class PublicId {
  protected PublicId() { }

  /**
   * Normalize a public identifier.
   *
   * <p>Public identifiers must be normalized according to the following
   * rules before comparisons between them can be made:</p>
   *
   * <ul>
   * <li>Whitespace characters are normalized to spaces (e.g., line feeds,
   * tabs, etc. become spaces).</li>
   * <li>Leading and trailing whitespace is removed.</li>
   * <li>Multiple internal whitespaces are normalized to a single
   * space.</li>
   * </ul>
   *
   * <p>This method is declared static so that other classes
   * can use it directly.</p>
   *
   * @param publicId The unnormalized public identifier.
   *
   * @return The normalized identifier.
   */
  public static String normalize(String publicId) {
    String normal = publicId.replace('\t', ' ');
    normal = normal.replace('\r', ' ');
    normal = normal.replace('\n', ' ');
    normal = normal.trim();

    int pos;

    while ((pos = normal.indexOf("  ")) >= 0) {
      normal = normal.substring(0, pos) + normal.substring(pos+1);
    }

    return normal;
  }

  /**
   * Encode a public identifier as a "publicid" URN.
   *
   * <p>This method is declared static so that other classes
   * can use it directly.</p>
   *
   * @param publicId The unnormalized public identifier.
   *
   * @return The normalized identifier.
   */
  public static String encodeURN(String publicId) {
    String urn = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.normalize(publicId);

    urn = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(urn, "%", "%25");
    urn = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(urn, ";", "%3B");
    urn = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(urn, "'", "%27");
    urn = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(urn, "?", "%3F");
    urn = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(urn, "#", "%23");
    urn = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(urn, "+", "%2B");
    urn = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(urn, " ", "+");
    urn = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(urn, "::", ";");
    urn = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(urn, ":", "%3A");
    urn = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(urn, "//", ":");
    urn = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(urn, "/", "%2F");

    return "urn:publicid:" + urn;
  }

  /**
   * Decode a "publicid" URN into a public identifier.
   *
   * <p>This method is declared static so that other classes
   * can use it directly.</p>
   *
   * @param urn The urn:publicid: URN
   *
   * @return The normalized identifier.
   */
  public static String decodeURN(String urn) {
    String publicId = "";

    if (urn.startsWith("urn:publicid:")) {
      publicId = urn.substring(13);
    } else {
      return urn;
    }

    publicId = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(publicId, "%2F", "/");
    publicId = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(publicId, ":", "//");
    publicId = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(publicId, "%3A", ":");
    publicId = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(publicId, ";", "::");
    publicId = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(publicId, "+", " ");
    publicId = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(publicId, "%2B", "+");
    publicId = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(publicId, "%23", "#");
    publicId = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(publicId, "%3F", "?");
    publicId = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(publicId, "%27", "'");
    publicId = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(publicId, "%3B", ";");
    publicId = com.sun.org.apache.xml.internal.resolver.helpers.PublicId.stringReplace(publicId, "%25", "%");

    return publicId;
    }

  /**
   * Replace one string with another.
   *
   */
  private static String stringReplace(String str,
                                      String oldStr,
                                      String newStr) {

    String result = "";
    int pos = str.indexOf(oldStr);

    //    System.out.println(str + ": " + oldStr + " => " + newStr);

    while (pos >= 0) {
      //      System.out.println(str + " (" + pos + ")");
      result += str.substring(0, pos);
      result += newStr;
      str = str.substring(pos+1);

      pos = str.indexOf(oldStr);
    }

    return result + str;
  }
}
