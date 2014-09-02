wicket.jsp
==========
The WicketServletAndJSPResolver is used to embed Servlet and JSP content into wicked HTML pages, by a custom Wicket-Tag. It is tested with Wicket 6.16.0. Because include is used to apply the content, every restrictions of include is applied to the jsp. (No header modifications and so on). To use it you should registered it to the page settings in the init-Method of the Wicket-Application:

Setup
-----
<pre>
@Override
protected void init() {
	super.init();
	getPageSettings().addComponentResolver(new WicketServletAndJSPResolver());
}
</pre>

A tag specifies the location which JSP to load. (The argument is given to the getRequestDispatcher method of the ServletContext):

Usage
-----

<pre>
&lt;wicket:jsp file="/de/test/jspwicket/TestPage.jsp"&gt;&lt;/wicket:jsp&gt;

or 

&lt;wicket:servlet path="/de/test/jspwicket/Servlet"&gt;&lt;/wicket:servlet&gt;
</pre>

Taglib
------
<pre>
&lt;%@ taglib prefix="wicket" uri="http://wicket.jsp/functions" %&gt;

Tag: url // Parameters: page(required), query(optional) // Example:
&lt;a href="&lt;wicket:url page="mypage.MyTestPage" query="param1=value1&param2=value2"/&gt;"&gt;LINK&lt;/a&gt;
</pre>

Links
------
https://cwiki.apache.org/confluence/display/WICKET/Including+JSP+files+in+HTML+templates
http://apache-wicket.1842946.n4.nabble.com/Wicket-1-5-and-JSP-servlet-wrapping-td4407174.html

IMPORTANT
---------
This project is now part of Wicketstuff Minis: https://github.com/wicketstuff/core/pull/338 - but I'm going to apply the changes in here, too.
