# Library of useful functions, by Ed Falk

For the most part, you want to copy these modules into your own source code.

Name | What it is
---- | ----
Matrix3d.java | 3-d matrices, with an emphasis on 3d graphics
RandColors.java | Assign random colors to backgrounds of all the widgets; used to debug layouts.
SetDialogSize.java | Code fragment; sets the size of a dialog to 90% of the screen width
Timer.java | Schedule and receive periodic alarms
Utils.java | Small utilities
WebHelper.java | HTTP utilities, based on apache.http
MultiPartProducer.java | Post multipart/form-data; used with Apache HTTP

## WebHelper.java

This class contains enough functionality that it's worth describing
in detail.

For the most part, this class consists of convenience wrappers around
the Apache HTTP library.

### Constructor

Method | Brief description
---- | ----
WebHelper() | Create WebHelper object
WebHelper(String url) | Create WebHelper object (not commonly used)

The second form can be used with getArgs(String args). Typically this
is used when all requests are to the same cgi script and only the
arguments change.

### Methods:

Return | Method | Brief description
----: | :---- | ----
HttpResponse | get(String url) | GET a url, return HttpResponse
HttpResponse | get(HttpRequestBase req) | GET a url, return HttpResponse
InputStream | getStream(String url) | GET a url, return InputStream
InputStream | getStream(HttpRequestBase) | GET a url, return InputStream
InputStream | getStream(HttpResponse) | Exctract the stream from a response
String | getString(String url) | GET a url, return String
String | getString(HttpRequestBase) | GET a url, return String
String | getString(HttpResponse) | Extract a string from a response
InputStream | getArgs(String url, String args) | Append args to url and get()
InputStream | getArgs(String args) | Same, use default url
HttpResponse | head(String url) | HEAD request, return HttpResponse
HttpResponse | post(url, String data) | POST request with data, return HttpResponse
HttpResponse | post(url, List data) | POST request with list of name/value pairs
HttpResponse | post(url, Map data) | POST request with map of name/value pairs
HttpResponse | post(url, name, value, ...) | POST request with name/value pairs in line
HttpResponse | post(url, from,file,name) | POST multipart, one file
HttpResponse | post(url, HttpEntity) | POST arbitrary request, arbitrary data entity
HttpResponse | post(HttpPost, HttpEntity) | POST arbitrary request, arbitrary data entity
HttpEntity | entity(HttpResponse resp) | Convert HttpResponse to HttpEntity
InputStream | inputStream(HttpEntity entity) | Convert HttpEntity to InputStream

### Static Utility Methods:

Return | Method | Brief description
----: | :---- | ----
InputStream | getStream(HttpResponse) | Convert HttpResponse to InputStream
String | getString(InputStream) | Convert InputStream to String
String | getString(HttpResponse) | Convert HttpResponse to String
String | getString(InputStream) | Convert InputStream to String
HttpEntity | entity(HttpResponse) | Convert HttpResponse to HttpEntity
InputStream | inputStream(HttpEntity) | convert HttpEntity to InputStream

