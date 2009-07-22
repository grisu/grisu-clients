package org.vpac.grisu.client.view.console;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import jline.CandidateListCompletionHandler;
import jline.CompletionHandler;
import jline.Completor;
import jline.ConsoleReader;
import jline.CursorBuffer;

import org.apache.log4j.Logger;

/**
 *  <p>
 *  A {@link CompletionHandler} that deals with multiple distinct completions
 *  by outputting the complete list of possibilities to the console. This
 *  mimics the behavior of the
 *  <a href="http://www.gnu.org/directory/readline.html">readline</a>
 *  library.
 *  </p>
 *
 *  <strong>TODO:</strong>
 *  <ul>
 *        <li>handle quotes and escaped quotes</li>
 *        <li>enable automatic escaping of whitespace</li>
 *  </ul>
 *
 *  @author  <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class GrisuCompletionHandler implements CompletionHandler {
	
	static final Logger myLogger = Logger.getLogger(GrisuCompletionHandler.class
			.getName());
	
    private static ResourceBundle loc = ResourceBundle.
        getBundle(CandidateListCompletionHandler.class.getName());

    private boolean eagerNewlines = true;

    public void setAlwaysIncludeNewline(boolean eagerNewlines) {
        this.eagerNewlines = eagerNewlines;
    }

    public boolean complete(final ConsoleReader reader, final List candidates,
                            final int pos) throws IOException {
        CursorBuffer buf = reader.getCursorBuffer();

        // if there is only one completion, then fill in the buffer
        if (candidates.size() == 1) {
            String value = candidates.get(0).toString();

            // fail if the only candidate is the same as the current buffer
            if (value.equals(buf.toString())) {
                return false;
            }

            setBuffer(reader, value, pos);

            return true;
        } else if (candidates.size() > 1) {
            String value = getUnambiguousCompletions(candidates);
            String bufString = buf.toString();
            setBuffer(reader, value, pos);
        }

        if (eagerNewlines)
            reader.printNewline();
        printCandidates(reader, candidates);

        // redraw the current console buffer
        reader.drawLine();

        return true;
    }

    private static void setBuffer(ConsoleReader reader, String value, int offset)
                           throws IOException {
        while ((reader.getCursorBuffer().cursor > offset)
                   && reader.backspace()) {
            ;
        }

        reader.putString(value);
        reader.setCursorPosition(offset + value.length());
    }

    /**
     *  Print out the candidates. If the size of the candidates
     *  is greated than the {@link getAutoprintThreshhold},
     *  they prompt with aq warning.
     *
     *  @param  candidates  the list of candidates to print
     */
    private final void printCandidates(ConsoleReader reader,
                                       Collection candidates)
                                throws IOException {
        Set distinct = new HashSet(candidates);

//        for (Object cand : candidates) {
//        	myLogger.debug("Printing: "+(String)cand);
//        }
        
        if (distinct.size() > reader.getAutoprintThreshhold()) {
            if (!eagerNewlines)
                reader.printNewline();
            reader.printString(MessageFormat.format
                (loc.getString("display-candidates"), new Object[] {
                    new Integer(candidates .size())
                    }) + " ");

            reader.flushConsole();

            int c;

            String noOpt = loc.getString("display-candidates-no");
            String yesOpt = loc.getString("display-candidates-yes");

            while ((c = reader.readCharacter(new char[] {
                yesOpt.charAt(0), noOpt.charAt(0) })) != -1) {
                if (noOpt.startsWith
                    (new String(new char[] { (char) c }))) {
                    reader.printNewline();
                    return;
                } else if (yesOpt.startsWith
                    (new String(new char[] { (char) c }))) {
                    break;
                } else {
                    reader.beep();
                }
            }
        }

        // copy the values and make them distinct, without otherwise
        // affecting the ordering. Only do it if the sizes differ.
        if (distinct.size() != candidates.size()) {
            Collection copy = new ArrayList();

            for (Iterator i = candidates.iterator(); i.hasNext();) {
                Object next = i.next();
                myLogger.debug("Copying: "+(String)next);
                if (!(copy.contains(next))) {
                	myLogger.debug("Adding: "+(String)next);
                    copy.add(next);
                }
            }

            candidates = copy;
        }
        
        Set friendly = new TreeSet();
        
        for ( Object friendlyOutput : candidates ) {
//        	myLogger.debug("Friendly: "+(String)friendlyOutput+"xxx");
        	String newString = null;
//        	if ( ((String)friendlyOutput).startsWith(GrisuCompletor.LOCAL_FILE_INIDICATOR) ) {
//        		// local file
//        		newString = (String)friendlyOutput;
//        	} else {
        		// argument
        		newString = ((String)friendlyOutput).substring(((String)friendlyOutput).lastIndexOf(" ")+1);
//        	}
//        	myLogger.debug("Adding: "+newString);
        	friendly.add(newString);
        }
        reader.printNewline();
//        reader.printColumns(candidates);
        reader.printColumns(friendly);
    }

    /**
     *  Returns a root that matches all the {@link String} elements
     *  of the specified {@link List}, or null if there are
     *  no commalities. For example, if the list contains
     *  <i>foobar</i>, <i>foobaz</i>, <i>foobuz</i>, the
     *  method will return <i>foob</i>.
     */
    private final String getUnambiguousCompletions(final List candidates) {
        if ((candidates == null) || (candidates.size() == 0)) {
            return null;
        }

        // convert to an array for speed
        String[] strings =
            (String[]) candidates.toArray(new String[candidates.size()]);

        String first = strings[0];
        StringBuffer candidate = new StringBuffer();

        for (int i = 0; i < first.length(); i++) {
            if (startsWith(first.substring(0, i + 1), strings)) {
                candidate.append(first.charAt(i));
            } else {
                break;
            }
        }

        return candidate.toString();
    }

    /**
     *  @return  true is all the elements of <i>candidates</i>
     *                          start with <i>starts</i>
     */
    private final boolean startsWith(final String starts,
                                     final String[] candidates) {
        for (int i = 0; i < candidates.length; i++) {
            if (!candidates[i].startsWith(starts)) {
                return false;
            }
        }

        return true;
    }
}
