
package org.efalk.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.ContentProducer;

/**
 * Utility class that lets you post multipart/form-data using
 * EntityTemplate.
 * See http://www.ietf.org/rfc/rfc2388.txt and
 * http://www.ietf.org/rfc/rfc2046.txt
 */
public class MultiPartProducer implements ContentProducer {
    //private final String name, filename;
    //private final File file;
    private final byte[] sep;
    private final List<MultiPartPair> parts;
    private static final byte[] crlf = {0xd, 0xa};
    private static final byte[] dashes = "--".getBytes();
    private static final byte[] dispos =
	"Content-Disposition: form-data; name=\"".getBytes();
    private static final byte[] dispFile = "\"; filename=\"".getBytes();
    private static final byte[] disposEnd = "\"\r\n".getBytes();
    private static final byte[] octetStream =
	"Content-Type: application/octet-stream\r\n".getBytes();

    public static class MultiPartPair {
	public final String name;	// form field name
	public final Object value;	// Either String or File
	public final String filename;	// for file only
	public MultiPartPair(String name, Object value) {
	    this.name = name;
	    this.value = value;
	    this.filename = null;
	}
	public MultiPartPair(String name, Object value, String filename) {
	    this.name = name;
	    this.value = value;
	    this.filename = filename;
	}
    }

    MultiPartProducer(String sep, List<MultiPartPair> parts) {
	this.sep = sep.getBytes();
	this.parts = parts;
    }

    MultiPartProducer(String sep, String name, File file) {
	this.sep = sep.getBytes();
	parts = new ArrayList<MultiPartPair>(1);
	parts.add(new MultiPartPair(name, file));
    }

    public void writeTo(OutputStream output) throws IOException {
	BufferedOutputStream os = new BufferedOutputStream(output);
	for (MultiPartPair pair : parts) {
	    writeSep(os, false);
	    if (pair.value instanceof File) {
		// File
		File file = (File) pair.value;
		os.write(dispos);
		os.write(pair.name.getBytes());
		os.write(dispFile);
		if (pair.filename != null)
		    os.write(pair.filename.getBytes());
		else
		    os.write(file.getName().getBytes());
		os.write(disposEnd);
		os.write(octetStream);
		os.write(crlf);
		BufferedInputStream is = new BufferedInputStream(
					    new FileInputStream(file));
		byte[] buffer = new byte[2048];
		int i;
		while ((i = is.read(buffer)) > 0)
		    os.write(buffer, 0, i);
		is.close();
		os.write(crlf);
	    } else {
		// Ordinary string
		os.write(dispos);
		os.write(pair.name.getBytes());
		os.write(disposEnd);
		os.write(crlf);
		os.write(pair.value.toString().getBytes());
		os.write(crlf);
	    }
	}
	writeSep(os, true);
	/*
	os.write("Content-Disposition: form-data; name=\"".getBytes());
	os.write(name.getBytes());
	os.write("\"; filename=\"".getBytes());
	os.write(filename.getBytes());
	os.write("\"\r\n".getBytes());
	os.write("Content-Type: application/octet-stream\r\n".getBytes());
	os.write(crlf);
	while ((i = is.read(buffer)) > 0)
	    os.write(buffer, 0, i);
	os.write(crlf);
	os.write(dashes);
	os.write(sep);
	os.write(dashes);
	os.write(crlf);
	is.close();
	*/
	os.close();
    }
    private void writeSep(BufferedOutputStream os, boolean last)
      throws IOException
    {
	os.write(dashes);
	os.write(sep);
	if (last) os.write(dashes);
	os.write(crlf);
    }
}
