package org.keyboardplaying.cron;

import static org.junit.Assert.fail;

import java.io.IOException;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;

import org.junit.Before;
import org.junit.Test;

/**
 * Ensures that there is no package dependency cycle.
 *
 * @see http://blog.mafr.de/2010/10/02/java-finding-package-cycles/
 *
 * @author Cyrille Chopelet (http://keyboardplaying.org)
 */
public class CyclicDependenciesTest {

    /** The {@link JDepend} instance used to run tests. */
    private JDepend jdepend;

    /**
     * Initialises {@link #jdepend}.
     *
     * @throws IOException
     *             when initialization fails
     */
    @Before
    public void initJdepend() throws IOException {
        jdepend = new JDepend();
        jdepend.addDirectory("target/classes");

        jdepend.analyze();
    }

    /** Ensure there is no package cycle. */
    @Test
    public void testCycles() {
        if (jdepend.containsCycles()) {
            StringBuilder sb = new StringBuilder(
                    "The following packages contain cycles which should be removed.");

            for (Object element : jdepend.getPackages()) {
                JavaPackage pack = (JavaPackage) element;
                if (pack.containsCycle()) {
                    /*
                     * Append chars to avoid instantiating strings.
                     *
                     * Micro-optimisation in a test class _is_ ridiculous, don't you think? You are
                     * allowed to laugh at me on this one.
                     */
                    sb.append('\n').append('\t').append(pack.getName());
                }
            }
            fail(sb.toString());
        }
    }
}
