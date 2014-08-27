wicket.jsp
==========
The WicketJSPResolver is used to embed JSP content into wicked HTML pages, by a custom Wicket-Tag. It is tested with Wicket 6.16.0. Because include is used to apply the content, every restrictions of include is applied to the jsp. (No header modifications and so on). To use it you should registered it to the page settings in the init-Method of the Wicket-Application:

Setup
-----
@Override
protected void init() {
	super.init();
	getPageSettings().addComponentResolver(new WicketJSPResolver());
}

A tag specifies the location which JSP to load. (The argument is given to the getRequestDispatcher method of the ServletContext):

Usage
-----

<wicket:jsp file="/de/test/jspwicket/TestPage.jsp"></wicket:jsp>
 
!!! This tag must not be defined as empty tag - it has to be opened / closed!!!

Links
------
https://cwiki.apache.org/confluence/display/WICKET/Including+JSP+files+in+HTML+templates
http://apache-wicket.1842946.n4.nabble.com/Wicket-1-5-and-JSP-servlet-wrapping-td4407174.html
