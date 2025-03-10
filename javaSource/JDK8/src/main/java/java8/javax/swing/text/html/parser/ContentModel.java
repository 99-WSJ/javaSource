/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.swing.text.html.parser;

import javax.swing.text.html.parser.Element;
import java.util.Vector;
import java.util.Enumeration;
import java.io.*;


/**
 * A representation of a content model. A content model is
 * basically a restricted BNF expression. It is restricted in
 * the sense that it must be deterministic. This means that you
 * don't have to represent it as a finite state automaton.<p>
 * See Annex H on page 556 of the SGML handbook for more information.
 *
 * @author   Arthur van Hoff
 *
 */
public final class ContentModel implements Serializable {
    /**
     * Type. Either '*', '?', '+', ',', '|', '&amp;'.
     */
    public int type;

    /**
     * The content. Either an Element or a ContentModel.
     */
    public Object content;

    /**
     * The next content model (in a ',', '|' or '&amp;' expression).
     */
    public javax.swing.text.html.parser.ContentModel next;

    public ContentModel() {
    }

    /**
     * Create a content model for an element.
     */
    public ContentModel(Element content) {
        this(0, content, null);
    }

    /**
     * Create a content model of a particular type.
     */
    public ContentModel(int type, javax.swing.text.html.parser.ContentModel content) {
        this(type, content, null);
    }

    /**
     * Create a content model of a particular type.
     */
    public ContentModel(int type, Object content, javax.swing.text.html.parser.ContentModel next) {
        this.type = type;
        this.content = content;
        this.next = next;
    }

    /**
     * Return true if the content model could
     * match an empty input stream.
     */
    public boolean empty() {
        switch (type) {
          case '*':
          case '?':
            return true;

          case '+':
          case '|':
            for (javax.swing.text.html.parser.ContentModel m = (javax.swing.text.html.parser.ContentModel)content; m != null ; m = m.next) {
                if (m.empty()) {
                    return true;
                }
            }
            return false;

          case ',':
          case '&':
            for (javax.swing.text.html.parser.ContentModel m = (javax.swing.text.html.parser.ContentModel)content; m != null ; m = m.next) {
                if (!m.empty()) {
                    return false;
                }
            }
            return true;

          default:
            return false;
        }
    }

    /**
     * Update elemVec with the list of elements that are
     * part of the this contentModel.
     */
     public void getElements(Vector<Element> elemVec) {
         switch (type) {
         case '*':
         case '?':
         case '+':
             ((javax.swing.text.html.parser.ContentModel)content).getElements(elemVec);
             break;
         case ',':
         case '|':
         case '&':
             for (javax.swing.text.html.parser.ContentModel m = (javax.swing.text.html.parser.ContentModel)content; m != null; m=m.next){
                 m.getElements(elemVec);
             }
             break;
         default:
             elemVec.addElement((Element)content);
         }
     }

     private boolean valSet[];
     private boolean val[];
     // A cache used by first().  This cache was found to speed parsing
     // by about 10% (based on measurements of the 4-12 code base after
     // buffering was fixed).

    /**
     * Return true if the token could potentially be the
     * first token in the input stream.
     */
    public boolean first(Object token) {
        switch (type) {
          case '*':
          case '?':
          case '+':
            return ((javax.swing.text.html.parser.ContentModel)content).first(token);

          case ',':
            for (javax.swing.text.html.parser.ContentModel m = (javax.swing.text.html.parser.ContentModel)content; m != null ; m = m.next) {
                if (m.first(token)) {
                    return true;
                }
                if (!m.empty()) {
                    return false;
                }
            }
            return false;

          case '|':
          case '&': {
            Element e = (Element) token;
            if (valSet == null) {
                valSet = new boolean[Element.getMaxIndex() + 1];
                val = new boolean[valSet.length];
                // All Element instances are created before this ever executes
            }
            if (valSet[e.index]) {
                return val[e.index];
            }
            for (javax.swing.text.html.parser.ContentModel m = (javax.swing.text.html.parser.ContentModel)content; m != null ; m = m.next) {
                if (m.first(token)) {
                    val[e.index] = true;
                    break;
                }
            }
            valSet[e.index] = true;
            return val[e.index];
          }

          default:
            return (content == token);
            // PENDING: refer to comment in ContentModelState
/*
              if (content == token) {
                  return true;
              }
              Element e = (Element)content;
              if (e.omitStart() && e.content != null) {
                  return e.content.first(token);
              }
              return false;
*/
        }
    }

    /**
     * Return the element that must be next.
     */
    public Element first() {
        switch (type) {
          case '&':
          case '|':
          case '*':
          case '?':
            return null;

          case '+':
          case ',':
            return ((javax.swing.text.html.parser.ContentModel)content).first();

          default:
            return (Element)content;
        }
    }

    /**
     * Convert to a string.
     */
    public String toString() {
        switch (type) {
          case '*':
            return content + "*";
          case '?':
            return content + "?";
          case '+':
            return content + "+";

          case ',':
          case '|':
          case '&':
            char data[] = {' ', (char)type, ' '};
            String str = "";
            for (javax.swing.text.html.parser.ContentModel m = (javax.swing.text.html.parser.ContentModel)content; m != null ; m = m.next) {
                str = str + m;
                if (m.next != null) {
                    str += new String(data);
                }
            }
            return "(" + str + ")";

          default:
            return content.toString();
        }
    }
}
