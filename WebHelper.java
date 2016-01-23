// $Id: WebHelper.java,v 1.3 2012-11-27 04:24:16 falk Exp $

/**
 * This module contains basic web communication utilities.
 *
 * TODO: examine this code for ways that bad actors could abuse it.
 */

package com.android.recovery;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.NameValuePair;

import android.util.Log;

/**
 * This class represents an open connection to a web site.
 * Key methods:
 *
 *  get(String url)		GET a url, return HttpResponse
 *  get(HttpRequestBase req)	GET a url, return HttpResponse
 *  getStream(String url)	GET a url, return InputStream
 *  getStream(HttpRequestBase)	GET a url, return InputStream
 *  getStream(HttpResponse)	Exctract the stream from a response
 *  getString(String url)	GET a url, return String
 *  getString(HttpRequestBase)	GET a url, return String
 *  getString(HttpResponse)	Extract a string from a response
 *  getArgs(String url, args)	Append args to url and get()
 *  getArgs(String args)	Same, use default url
 *  head(String url)		HEAD request, return HttpResponse
 *  post(url, String data)	POST request with data, return HttpResponse
 *  post(url, List data)	POST request with list of name/value pairs
 *  post(url, Map data)		POST request with map of name/value pairs
 *  post(url, name, value, ...)	POST request with name/value pairs in line
 *  post(url, from,file,name)	POST multipart, one file
 *  post(url, HttpEntity)	POST arbitrary request, arbitrary data entity
 *  post(HttpPost, HttpEntity)	POST arbitrary request, arbitrary data entity
 *
 * Utilities:
 *
 *  entity(HttpResponse resp)		Convert HttpResponse to HttpEntity
 *  inputStream(HttpEntity entity)	Convert HttpEntity to InputStream
 *  getString(InputStream)		Convert InputStream to String
 */
class WebHelper {
    static private final String TAG = "WebHelper";
    private String mDefUrl = "http://www.example.com/cgi-bin/api";
    private HttpClient mClient = null;
    private Random mRand = null;

    public WebHelper() {
	mClient = new DefaultHttpClient();
    }

    public WebHelper(final String url) {
	mDefUrl = url;
	mClient = new DefaultHttpClient();
    }

    // Any http return other than SC_OK will throw this exception.
    public static class WebError extends Exception {
	private static final long serialVersionUID = 1L;
	int status;
	WebError(final int status, final String reason) {
	    super(reason);
	    this.status = status;
	}
	WebError(final HttpResponse resp) {
	    super(resp.getStatusLine().getReasonPhrase());
	    this.status = resp.getStatusLine().getStatusCode();
	}
	public String toString() {
	    return ""+status+": "+getMessage();
	}
    }

    /**
     * GET the given url, return an HttpResponse
     */
    public HttpResponse get(String url) throws WebError {
	HttpGet req = new HttpGet(url);
	req.setHeader("Cache-Control", "no-cache");
	return get(req);
    }

    /**
     * Fetch the given request, return an HttpResponse
     */
    public HttpResponse get(HttpRequestBase req) throws WebError {
	HttpResponse resp;
	try {
	    resp = mClient.execute(req);
	    int status = resp.getStatusLine().getStatusCode();
	    if (status != HttpStatus.SC_OK)
		throw new WebError(resp);
	    return resp;
	} catch (ClientProtocolException e) {
	    Log.e(TAG, "Protocol exception: " + e);
	    throw new WebError(503, "Exception: " + e);
	} catch (UnknownHostException e) {
	    throw new WebError(HttpStatus.SC_SERVICE_UNAVAILABLE,
	    			"Exception: " + e);
	} catch (IOException e) {
	    Log.e(TAG, "IO exception: " + e);
	    throw new WebError(HttpStatus.SC_SERVICE_UNAVAILABLE,
	    			"Exception: " + e);
	}
    }

    /**
     * GET the given url, return an InputStream
     */
    public InputStream getStream(String url) throws WebError {
	return getStream(get(url));
    }

    /**
     * GET the given url, return an InputStream
     */
    public InputStream getStream(HttpRequestBase req) throws WebError {
	return getStream(get(req));
    }

    /**
     * Utility: convert an HttpResponse to a stream
     * @throws WebError on failure.
     */
    public static InputStream getStream(HttpResponse resp) throws WebError {
	return inputStream(entity(resp));
    }

    /**
     * GET the given url, return as a string
     */
    public String getString(final String url) throws WebError {
	return getString(getStream(url));
    }

    /**
     * GET the given url, return as a string
     */
    public String getString(final HttpRequestBase req) throws WebError {
	return getString(getStream(req));
    }

    /**
     * Utility: convert an HttpResponse to a string.
     * @throws WebError on failure.
     */
    public static String getString(final HttpResponse resp) throws WebError {
	return getString(inputStream(entity(resp)));
    }

    /**
     * Utility: get the string contained in the given input stream
     */
    public static String getString(final InputStream is) throws WebError {
	if (is == null) return null;
	try {
	    return Utils.inputStreamAsString(is);
	} catch (IOException e) {
	    Log.e(TAG, "IO Exception in getString: " + e);
	    Utils.where(e);
	    throw new WebError(503, "Exception: " + e);
	}
    }

    /**
     * Append the given arguments to the given URL and return the results
     * as an InputStream
     */
    public InputStream getArgs(String url, String args) throws WebError {
	return getStream(url + args);
    }

    /**
     * Append the given arguments to the default URL and return the results
     * as an InputStream
     */
    public InputStream getArgs(String args) throws WebError {
	return getArgs(mDefUrl, args);
    }

    /**
     * Fetch the headers for the given url, return as an HttpResponse
     */
    public HttpResponse head(String url) throws WebError {
	return get(new HttpHead(url));
    }

    /**
     * POST the given url, providing the given input string as data.
     * @param url   destination url
     * @param data  post data, as a single string
     * Caller is responsible for creating an encoded form within the string.
     */
    public HttpResponse post(String url, String data) throws WebError {
	HttpPost req = new HttpPost(url);
	req.setHeader("Content-Type", "application/x-www-form-urlencoded");
	StringEntity e;
	try {
	    e = new StringEntity(data, "UTF-8");
	} catch (UnsupportedEncodingException e1) {
	    Log.e(TAG, "Unsupported encoding: " + e1);
	    Utils.where(e1);
	    throw new WebError(503, "Exception: " + e1);
	}
	return post(req, e);
    }

    /**
     * POST the given url, providing the given as a list of NameValuePairs
     * @param url   destination url
     * @param data  post data, as a list of name/value pairs
     */
    public HttpResponse post(String url, List<NameValuePair> data)
	throws WebError
    {
	HttpPost req = new HttpPost(url);
	UrlEncodedFormEntity e;
	try {
	    e = new UrlEncodedFormEntity(data, "UTF-8");
	} catch (UnsupportedEncodingException e1) {
	    Log.e(TAG, "Unsupported encoding: " + e1);
	    Utils.where(e1);
	    throw new WebError(503, "Exception: " + e1);
	}
	return post(req, e);
    }

    /**
     * POST the given url, providing a Map of Name,Value
     * @param url   destination url
     * @param data  post data as a map.  All values must support toString()
     */
    public HttpResponse post(String url, Map<String,Object> data)
	throws WebError
    {
	List<NameValuePair> pairs = new ArrayList<NameValuePair>(data.size());
	NameValuePair pair;
	for (Map.Entry<String,Object> e : data.entrySet()) {
	    pair = new BasicNameValuePair(e.getKey(), e.getValue().toString());
	    pairs.add(pair);
	}
	return post(url, pairs);
    }

    /**
     * POST the given url, providing names and values.
     * @param url   destination url
     * @param name,value, ...   All must be strings.
     */
    public HttpResponse post(String url, String... args) throws WebError {
	List<NameValuePair> pairs = new ArrayList<NameValuePair>(args.length/2);
	NameValuePair pair;
	for (int i = 0; i < args.length; i += 2) {
	    pair = new BasicNameValuePair(args[i], args[i+1]);
	    pairs.add(pair);
	}
	return post(url, pairs);
    }

    /**
     * Post a file using multipart/form-data.
     * @param url   Url for the POST
     * @param from  Value for "From" field of form
     * @param file  File to upload in the "file" field.
     * @param name  Content-disposition value for file; may be null
     * @return HttpResponse from server
     * @throws WebError
     */
    public HttpResponse post(String url, String from, File file, String name)
	throws WebError
    {
	HttpPost req = new HttpPost(url);
	String sep = genSep();
	req.setHeader("Content-Type", "multipart/form-data; boundary=" + sep);
	List<MultiPartProducer.MultiPartPair> parts =
	    new ArrayList<MultiPartProducer.MultiPartPair>(2);
	parts.add(new MultiPartProducer.MultiPartPair("from", from));
	parts.add(new MultiPartProducer.MultiPartPair("file", file, name));
	EntityTemplate e =
	    new EntityTemplate(new MultiPartProducer(sep, parts));
	return post(req, e);
    }

    /**
     * Post an arbitrary entity.
     * @param url   Url for the POST
     * @param data  Any HttpEntity subclass
     * @return HttpResponse from server
     * @throws WebError
     */
    public HttpResponse post(String url, HttpEntity data) throws WebError {
	HttpPost req = new HttpPost(url);
	String sep = genSep();
	req.setHeader("Content-Type", "multipart/form-data; boundary=" + sep);
	return post(req, data);
    }

    /**
     * Post an arbitrary entity.
     * @param req   HttpPost
     * @param data  Any HttpEntity subclass
     * @return HttpResponse from server
     * @throws WebError
     */
    public HttpResponse post(HttpPost req, HttpEntity data) throws WebError {
	try {
	    req.setEntity(data);
	    HttpResponse resp = mClient.execute(req);
	    int status = resp.getStatusLine().getStatusCode();
	    if (status != HttpStatus.SC_OK) {
		//Log.w(TAG,
		//  "http error: " + resp.getStatusLine().getReasonPhrase());
		throw new WebError(resp);
	    }
	    return resp;
	} catch (ClientProtocolException e) {
	    Log.e(TAG, "Protocol exception: " + e);
	    throw new WebError(HttpStatus.SC_SERVICE_UNAVAILABLE,
	    			"Exception: " + e);
	} catch (UnknownHostException e) {
	    throw new WebError(HttpStatus.SC_SERVICE_UNAVAILABLE,
	    			"Exception: " + e);
	} catch (IOException e) {
	    Log.e(TAG, "IO exception: " + e);
	    throw new WebError(HttpStatus.SC_SERVICE_UNAVAILABLE,
	    			"Exception: " + e);
	} catch (WebError e) {
	    throw e;
	} catch (Exception e) {
	    // Catch-all
	    Log.e(TAG, "Unknown exception: " + e);
	    throw new WebError(503, "Exception: " + e);
	}
    }

    /**
     * Utility: convert HttpResponse to HttpEntity
     * @throws WebError
     */
    public static HttpEntity entity(HttpResponse resp) throws WebError {
	HttpEntity entity;
	if ((entity = resp.getEntity()) == null ) {
	    Log.w(TAG, "http error: empty response");
	    throw new WebError(204, "http error: empty response");
	}
	return entity;
    }

    /**
     * Utility: convert HttpEntity to InputStream
     * @throws WebError
     * Tip: use Utils.inputStreamAsString() to convert InputStream to String
     */
    public static InputStream inputStream(HttpEntity entity) throws WebError {
	InputStream input;
	try {
	    input = entity.getContent();
	} catch (IOException e) {
	    Log.e(TAG, "IO exception: " + e);
	    throw new WebError(503, "Exception: " + e);
	}
	if( input == null ) {
	    Log.w(TAG, "http error: empty response");
	    throw new WebError(204, "http error: empty response");
	}
	return input;
    }


    // Free resources.
    public void close() {
      mClient = null;
    }

    /**
     * Utility: generate multipart seperator string.
     */
    public String genSep() {
	final String ochars =
	  "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	StringBuilder sb = new StringBuilder("___WebHelper");
	if (mRand == null) mRand = new Random();
	for (int i=0; i<30; ++i)
	    sb.append(ochars.charAt(mRand.nextInt(64)));
	return sb.toString();
    }
}
