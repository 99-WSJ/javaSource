/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
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
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 * The original version of this source code and documentation
 * is copyrighted and owned by Taligent, Inc., a wholly-owned
 * subsidiary of IBM. These materials are provided under terms
 * of a License Agreement between Taligent and Sun. This technology
 * is protected by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java8.java.text;

import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;

import java.lang.ref.SoftReference;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.text.spi.BreakIteratorProvider;
import java.util.Locale;


/**
 * The <code>BreakIterator</code> class implements methods for finding
 * the location of boundaries in text. Instances of <code>BreakIterator</code>
 * maintain a current position and scan over text
 * returning the index of characters where boundaries occur.
 * Internally, <code>BreakIterator</code> scans text using a
 * <code>CharacterIterator</code>, and is thus able to scan text held
 * by any object implementing that protocol. A <code>StringCharacterIterator</code>
 * is used to scan <code>String</code> objects passed to <code>setText</code>.
 *
 * <p>
 * You use the factory methods provided by this class to create
 * instances of various types of break iterators. In particular,
 * use <code>getWordInstance</code>, <code>getLineInstance</code>,
 * <code>getSentenceInstance</code>, and <code>getCharacterInstance</code>
 * to create <code>BreakIterator</code>s that perform
 * word, line, sentence, and character boundary analysis respectively.
 * A single <code>BreakIterator</code> can work only on one unit
 * (word, line, sentence, and so on). You must use a different iterator
 * for each unit boundary analysis you wish to perform.
 *
 * <p><a name="line"></a>
 * Line boundary analysis determines where a text string can be
 * broken when line-wrapping. The mechanism correctly handles
 * punctuation and hyphenated words. Actual line breaking needs
 * to also consider the available line width and is handled by
 * higher-level software.
 *
 * <p><a name="sentence"></a>
 * Sentence boundary analysis allows selection with correct interpretation
 * of periods within numbers and abbreviations, and trailing punctuation
 * marks such as quotation marks and parentheses.
 *
 * <p><a name="word"></a>
 * Word boundary analysis is used by search and replace functions, as
 * well as within text editing applications that allow the user to
 * select words with a double click. Word selection provides correct
 * interpretation of punctuation marks within and following
 * words. Characters that are not part of a word, such as symbols
 * or punctuation marks, have word-breaks on both sides.
 *
 * <p><a name="character"></a>
 * Character boundary analysis allows users to interact with characters
 * as they expect to, for example, when moving the cursor through a text
 * string. Character boundary analysis provides correct navigation
 * through character strings, regardless of how the character is stored.
 * The boundaries returned may be those of supplementary characters,
 * combining character sequences, or ligature clusters.
 * For example, an accented character might be stored as a base character
 * and a diacritical mark. What users consider to be a character can
 * differ between languages.
 *
 * <p>
 * The <code>BreakIterator</code> instances returned by the factory methods
 * of this class are intended for use with natural languages only, not for
 * programming language text. It is however possible to define subclasses
 * that tokenize a programming language.
 *
 * <P>
 * <strong>Examples</strong>:<P>
 * Creating and using text boundaries:
 * <blockquote>
 * <pre>
 * public static void main(String args[]) {
 *      if (args.length == 1) {
 *          String stringToExamine = args[0];
 *          //print each word in order
 *          BreakIterator boundary = BreakIterator.getWordInstance();
 *          boundary.setText(stringToExamine);
 *          printEachForward(boundary, stringToExamine);
 *          //print each sentence in reverse order
 *          boundary = BreakIterator.getSentenceInstance(Locale.US);
 *          boundary.setText(stringToExamine);
 *          printEachBackward(boundary, stringToExamine);
 *          printFirst(boundary, stringToExamine);
 *          printLast(boundary, stringToExamine);
 *      }
 * }
 * </pre>
 * </blockquote>
 *
 * Print each element in order:
 * <blockquote>
 * <pre>
 * public static void printEachForward(BreakIterator boundary, String source) {
 *     int start = boundary.first();
 *     for (int end = boundary.next();
 *          end != BreakIterator.DONE;
 *          start = end, end = boundary.next()) {
 *          System.out.println(source.substring(start,end));
 *     }
 * }
 * </pre>
 * </blockquote>
 *
 * Print each element in reverse order:
 * <blockquote>
 * <pre>
 * public static void printEachBackward(BreakIterator boundary, String source) {
 *     int end = boundary.last();
 *     for (int start = boundary.previous();
 *          start != BreakIterator.DONE;
 *          end = start, start = boundary.previous()) {
 *         System.out.println(source.substring(start,end));
 *     }
 * }
 * </pre>
 * </blockquote>
 *
 * Print first element:
 * <blockquote>
 * <pre>
 * public static void printFirst(BreakIterator boundary, String source) {
 *     int start = boundary.first();
 *     int end = boundary.next();
 *     System.out.println(source.substring(start,end));
 * }
 * </pre>
 * </blockquote>
 *
 * Print last element:
 * <blockquote>
 * <pre>
 * public static void printLast(BreakIterator boundary, String source) {
 *     int end = boundary.last();
 *     int start = boundary.previous();
 *     System.out.println(source.substring(start,end));
 * }
 * </pre>
 * </blockquote>
 *
 * Print the element at a specified position:
 * <blockquote>
 * <pre>
 * public static void printAt(BreakIterator boundary, int pos, String source) {
 *     int end = boundary.following(pos);
 *     int start = boundary.previous();
 *     System.out.println(source.substring(start,end));
 * }
 * </pre>
 * </blockquote>
 *
 * Find the next word:
 * <blockquote>
 * <pre>{@code
 * public static int nextWordStartAfter(int pos, String text) {
 *     BreakIterator wb = BreakIterator.getWordInstance();
 *     wb.setText(text);
 *     int last = wb.following(pos);
 *     int current = wb.next();
 *     while (current != BreakIterator.DONE) {
 *         for (int p = last; p < current; p++) {
 *             if (Character.isLetter(text.codePointAt(p)))
 *                 return last;
 *         }
 *         last = current;
 *         current = wb.next();
 *     }
 *     return BreakIterator.DONE;
 * }
 * }</pre>
 * (The iterator returned by BreakIterator.getWordInstance() is unique in that
 * the break positions it returns don't represent both the start and end of the
 * thing being iterated over.  That is, a sentence-break iterator returns breaks
 * that each represent the end of one sentence and the beginning of the next.
 * With the word-break iterator, the characters between two boundaries might be a
 * word, or they might be the punctuation or whitespace between two words.  The
 * above code uses a simple heuristic to determine which boundary is the beginning
 * of a word: If the characters between this boundary and the next boundary
 * include at least one letter (this can be an alphabetical letter, a CJK ideograph,
 * a Hangul syllable, a Kana character, etc.), then the text between this boundary
 * and the next is a word; otherwise, it's the material between words.)
 * </blockquote>
 *
 * @see CharacterIterator
 *
 */

public abstract class BreakIterator implements Cloneable
{
    /**
     * Constructor. BreakIterator is stateless and has no default behavior.
     */
    protected BreakIterator()
    {
    }

    /**
     * Create a copy of this iterator
     * @return A copy of this
     */
    @Override
    public Object clone()
    {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    /**
     * DONE is returned by previous(), next(), next(int), preceding(int)
     * and following(int) when either the first or last text boundary has been
     * reached.
     */
    public static final int DONE = -1;

    /**
     * Returns the first boundary. The iterator's current position is set
     * to the first text boundary.
     * @return The character index of the first text boundary.
     */
    public abstract int first();

    /**
     * Returns the last boundary. The iterator's current position is set
     * to the last text boundary.
     * @return The character index of the last text boundary.
     */
    public abstract int last();

    /**
     * Returns the nth boundary from the current boundary. If either
     * the first or last text boundary has been reached, it returns
     * <code>BreakIterator.DONE</code> and the current position is set to either
     * the first or last text boundary depending on which one is reached. Otherwise,
     * the iterator's current position is set to the new boundary.
     * For example, if the iterator's current position is the mth text boundary
     * and three more boundaries exist from the current boundary to the last text
     * boundary, the next(2) call will return m + 2. The new text position is set
     * to the (m + 2)th text boundary. A next(4) call would return
     * <code>BreakIterator.DONE</code> and the last text boundary would become the
     * new text position.
     * @param n which boundary to return.  A value of 0
     * does nothing.  Negative values move to previous boundaries
     * and positive values move to later boundaries.
     * @return The character index of the nth boundary from the current position
     * or <code>BreakIterator.DONE</code> if either first or last text boundary
     * has been reached.
     */
    public abstract int next(int n);

    /**
     * Returns the boundary following the current boundary. If the current boundary
     * is the last text boundary, it returns <code>BreakIterator.DONE</code> and
     * the iterator's current position is unchanged. Otherwise, the iterator's
     * current position is set to the boundary following the current boundary.
     * @return The character index of the next text boundary or
     * <code>BreakIterator.DONE</code> if the current boundary is the last text
     * boundary.
     * Equivalent to next(1).
     * @see #next(int)
     */
    public abstract int next();

    /**
     * Returns the boundary preceding the current boundary. If the current boundary
     * is the first text boundary, it returns <code>BreakIterator.DONE</code> and
     * the iterator's current position is unchanged. Otherwise, the iterator's
     * current position is set to the boundary preceding the current boundary.
     * @return The character index of the previous text boundary or
     * <code>BreakIterator.DONE</code> if the current boundary is the first text
     * boundary.
     */
    public abstract int previous();

    /**
     * Returns the first boundary following the specified character offset. If the
     * specified offset equals to the last text boundary, it returns
     * <code>BreakIterator.DONE</code> and the iterator's current position is unchanged.
     * Otherwise, the iterator's current position is set to the returned boundary.
     * The value returned is always greater than the offset or the value
     * <code>BreakIterator.DONE</code>.
     * @param offset the character offset to begin scanning.
     * @return The first boundary after the specified offset or
     * <code>BreakIterator.DONE</code> if the last text boundary is passed in
     * as the offset.
     * @exception  IllegalArgumentException if the specified offset is less than
     * the first text boundary or greater than the last text boundary.
     */
    public abstract int following(int offset);

    /**
     * Returns the last boundary preceding the specified character offset. If the
     * specified offset equals to the first text boundary, it returns
     * <code>BreakIterator.DONE</code> and the iterator's current position is unchanged.
     * Otherwise, the iterator's current position is set to the returned boundary.
     * The value returned is always less than the offset or the value
     * <code>BreakIterator.DONE</code>.
     * @param offset the character offset to begin scanning.
     * @return The last boundary before the specified offset or
     * <code>BreakIterator.DONE</code> if the first text boundary is passed in
     * as the offset.
     * @exception   IllegalArgumentException if the specified offset is less than
     * the first text boundary or greater than the last text boundary.
     * @since 1.2
     */
    public int preceding(int offset) {
        // NOTE:  This implementation is here solely because we can't add new
        // abstract methods to an existing class.  There is almost ALWAYS a
        // better, faster way to do this.
        int pos = following(offset);
        while (pos >= offset && pos != DONE) {
            pos = previous();
        }
        return pos;
    }

    /**
     * Returns true if the specified character offset is a text boundary.
     * @param offset the character offset to check.
     * @return <code>true</code> if "offset" is a boundary position,
     * <code>false</code> otherwise.
     * @exception   IllegalArgumentException if the specified offset is less than
     * the first text boundary or greater than the last text boundary.
     * @since 1.2
     */
    public boolean isBoundary(int offset) {
        // NOTE: This implementation probably is wrong for most situations
        // because it fails to take into account the possibility that a
        // CharacterIterator passed to setText() may not have a begin offset
        // of 0.  But since the abstract BreakIterator doesn't have that
        // knowledge, it assumes the begin offset is 0.  If you subclass
        // BreakIterator, copy the SimpleTextBoundary implementation of this
        // function into your subclass.  [This should have been abstract at
        // this level, but it's too late to fix that now.]
        if (offset == 0) {
            return true;
        }
        int boundary = following(offset - 1);
        if (boundary == DONE) {
            throw new IllegalArgumentException();
        }
        return boundary == offset;
    }

    /**
     * Returns character index of the text boundary that was most
     * recently returned by next(), next(int), previous(), first(), last(),
     * following(int) or preceding(int). If any of these methods returns
     * <code>BreakIterator.DONE</code> because either first or last text boundary
     * has been reached, it returns the first or last text boundary depending on
     * which one is reached.
     * @return The text boundary returned from the above methods, first or last
     * text boundary.
     * @see #next()
     * @see #next(int)
     * @see #previous()
     * @see #first()
     * @see #last()
     * @see #following(int)
     * @see #preceding(int)
     */
    public abstract int current();

    /**
     * Get the text being scanned
     * @return the text being scanned
     */
    public abstract CharacterIterator getText();

    /**
     * Set a new text string to be scanned.  The current scan
     * position is reset to first().
     * @param newText new text to scan.
     */
    public void setText(String newText)
    {
        setText(new StringCharacterIterator(newText));
    }

    /**
     * Set a new text for scanning.  The current scan
     * position is reset to first().
     * @param newText new text to scan.
     */
    public abstract void setText(CharacterIterator newText);

    private static final int CHARACTER_INDEX = 0;
    private static final int WORD_INDEX = 1;
    private static final int LINE_INDEX = 2;
    private static final int SENTENCE_INDEX = 3;

    @SuppressWarnings("unchecked")
    private static final SoftReference<BreakIteratorCache>[] iterCache = (SoftReference<BreakIteratorCache>[]) new SoftReference<?>[4];

    /**
     * Returns a new <code>BreakIterator</code> instance
     * for <a href="BreakIterator.html#word">word breaks</a>
     * for the {@linkplain Locale#getDefault() default locale}.
     * @return A break iterator for word breaks
     */
    public static java.text.BreakIterator getWordInstance()
    {
        return getWordInstance(Locale.getDefault());
    }

    /**
     * Returns a new <code>BreakIterator</code> instance
     * for <a href="BreakIterator.html#word">word breaks</a>
     * for the given locale.
     * @param locale the desired locale
     * @return A break iterator for word breaks
     * @exception NullPointerException if <code>locale</code> is null
     */
    public static java.text.BreakIterator getWordInstance(Locale locale)
    {
        return getBreakInstance(locale, WORD_INDEX);
    }

    /**
     * Returns a new <code>BreakIterator</code> instance
     * for <a href="BreakIterator.html#line">line breaks</a>
     * for the {@linkplain Locale#getDefault() default locale}.
     * @return A break iterator for line breaks
     */
    public static java.text.BreakIterator getLineInstance()
    {
        return getLineInstance(Locale.getDefault());
    }

    /**
     * Returns a new <code>BreakIterator</code> instance
     * for <a href="BreakIterator.html#line">line breaks</a>
     * for the given locale.
     * @param locale the desired locale
     * @return A break iterator for line breaks
     * @exception NullPointerException if <code>locale</code> is null
     */
    public static java.text.BreakIterator getLineInstance(Locale locale)
    {
        return getBreakInstance(locale, LINE_INDEX);
    }

    /**
     * Returns a new <code>BreakIterator</code> instance
     * for <a href="BreakIterator.html#character">character breaks</a>
     * for the {@linkplain Locale#getDefault() default locale}.
     * @return A break iterator for character breaks
     */
    public static java.text.BreakIterator getCharacterInstance()
    {
        return getCharacterInstance(Locale.getDefault());
    }

    /**
     * Returns a new <code>BreakIterator</code> instance
     * for <a href="BreakIterator.html#character">character breaks</a>
     * for the given locale.
     * @param locale the desired locale
     * @return A break iterator for character breaks
     * @exception NullPointerException if <code>locale</code> is null
     */
    public static java.text.BreakIterator getCharacterInstance(Locale locale)
    {
        return getBreakInstance(locale, CHARACTER_INDEX);
    }

    /**
     * Returns a new <code>BreakIterator</code> instance
     * for <a href="BreakIterator.html#sentence">sentence breaks</a>
     * for the {@linkplain Locale#getDefault() default locale}.
     * @return A break iterator for sentence breaks
     */
    public static java.text.BreakIterator getSentenceInstance()
    {
        return getSentenceInstance(Locale.getDefault());
    }

    /**
     * Returns a new <code>BreakIterator</code> instance
     * for <a href="BreakIterator.html#sentence">sentence breaks</a>
     * for the given locale.
     * @param locale the desired locale
     * @return A break iterator for sentence breaks
     * @exception NullPointerException if <code>locale</code> is null
     */
    public static java.text.BreakIterator getSentenceInstance(Locale locale)
    {
        return getBreakInstance(locale, SENTENCE_INDEX);
    }

    private static java.text.BreakIterator getBreakInstance(Locale locale, int type) {
        if (iterCache[type] != null) {
            BreakIteratorCache cache = iterCache[type].get();
            if (cache != null) {
                if (cache.getLocale().equals(locale)) {
                    return cache.createBreakInstance();
                }
            }
        }

        java.text.BreakIterator result = createBreakInstance(locale, type);
        BreakIteratorCache cache = new BreakIteratorCache(locale, result);
        iterCache[type] = new SoftReference<>(cache);
        return result;
    }

    private static java.text.BreakIterator createBreakInstance(Locale locale,
                                                               int type) {
        LocaleProviderAdapter adapter = LocaleProviderAdapter.getAdapter(BreakIteratorProvider.class, locale);
        java.text.BreakIterator iterator = createBreakInstance(adapter, locale, type);
        if (iterator == null) {
            iterator = createBreakInstance(LocaleProviderAdapter.forJRE(), locale, type);
        }
        return iterator;
    }

    private static java.text.BreakIterator createBreakInstance(LocaleProviderAdapter adapter, Locale locale, int type) {
        BreakIteratorProvider breakIteratorProvider = adapter.getBreakIteratorProvider();
        java.text.BreakIterator iterator = null;
        switch (type) {
        case CHARACTER_INDEX:
            iterator = breakIteratorProvider.getCharacterInstance(locale);
            break;
        case WORD_INDEX:
            iterator = breakIteratorProvider.getWordInstance(locale);
            break;
        case LINE_INDEX:
            iterator = breakIteratorProvider.getLineInstance(locale);
            break;
        case SENTENCE_INDEX:
            iterator = breakIteratorProvider.getSentenceInstance(locale);
            break;
        }
        return iterator;
    }

    /**
     * Returns an array of all locales for which the
     * <code>get*Instance</code> methods of this class can return
     * localized instances.
     * The returned array represents the union of locales supported by the Java
     * runtime and by installed
     * {@link BreakIteratorProvider BreakIteratorProvider} implementations.
     * It must contain at least a <code>Locale</code>
     * instance equal to {@link Locale#US Locale.US}.
     *
     * @return An array of locales for which localized
     *         <code>BreakIterator</code> instances are available.
     */
    public static synchronized Locale[] getAvailableLocales()
    {
        LocaleServiceProviderPool pool =
            LocaleServiceProviderPool.getPool(BreakIteratorProvider.class);
        return pool.getAvailableLocales();
    }

    private static final class BreakIteratorCache {

        private java.text.BreakIterator iter;
        private Locale locale;

        BreakIteratorCache(Locale locale, java.text.BreakIterator iter) {
            this.locale = locale;
            this.iter = (java.text.BreakIterator) iter.clone();
        }

        Locale getLocale() {
            return locale;
        }

        java.text.BreakIterator createBreakInstance() {
            return (java.text.BreakIterator) iter.clone();
        }
    }
}
