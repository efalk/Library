// $Id: Utils.java,v 1.1 2010-01-04 14:29:59 falk Exp $

/**
 * This module contains basic utilities.
 */

package org.efalk.util

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Closeable;

import android.util.Log;

/**
 * Various utility functions.
 */
public final class Util {
    static private final String TAG = "Utils";

    private Util() { }

    /**
     * Print the top of a stack trace
     */
    public static void where(Throwable t) {
	StackTraceElement[] stack = t.getStackTrace();
	for( int i=0; i < stack.length; ++i)
	    Log.e(TAG, stack[i].toString());
    }

    /**
     * Return a specific entry from the stack trace.
     */
    public static String where(Throwable t, String method) {
	StackTraceElement[] stack = t.getStackTrace();
	for (StackTraceElement el : stack)
	    if (el.getMethodName().equals(method))
		return el.toString();
	return stack[0].toString();
    }

    /**
     * Read a string from an InputStream.
     */
    public static String inputStreamAsString(InputStream s) throws IOException {
	BufferedReader br = new BufferedReader( new InputStreamReader(s));
	StringBuilder sb = new StringBuilder();
	String line;

	while ((line = br.readLine()) != null) {
	  sb.append(line).append("\n");
	}
	br.close();
	return sb.toString();
    }

    /**
     * Closes 'closeable', ignoring any checked exceptions.
     * Does nothing if 'closeable' is null.
     */
    public static void closeQuietly(Closeable closeable) {
	if (closeable == null) return;
	try {
	    closeable.close();
	} catch (Exception ignored) {
	}
    }

    /**
     * Sleep, return true if interrupted.
     */
    public static boolean sleep(int ms) {
	try {
	    Thread.sleep(ms);
	} catch (InterruptedException e) {
	    return true;
	}
	return false;
    }
}
