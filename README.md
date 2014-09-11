wicketstuff-jee-web (previously wicket.jsp)
==========
The JEEWebResolver is used to embed Servlet, JSP abd JSF content into wicked HTML pages, by a custom Wicket-Tag. It is tested with Wicket 6.x / 7.x. Because include is used to apply the content, every restrictions of include is applied to the embed content. (No header modifications and so on). To use it you should registered it to the page settings in the init-Method of the Wicket-Application:

Setup
-----
<pre>
WebApplication:
@Override
protected void init() {
	super.init();
	getPageSettings().addComponentResolver(new JEEWebResolver());
}
</pre>

A tag specifies the location which embed content to load. (The argument is given to the getRequestDispatcher method of the ServletContext):

Usage
-----

<pre>
&lt;wicket:jsp file="/de/test/jsp/TestPage.jsp"/&gt;

or 

&lt;wicket:servlet path="/de/test/servlet/Servlet/"&gt;

or

&lt;wicket:jsf file="/TestPage.xhtml"/&gt;

</pre>

Tags for JSP / JSF
------------------
<pre>
JSP: &lt;%@ taglib prefix="wicket" uri="http://wicketstuff-jee-web.org/functions/jsp" %&gt;
JSF: &lt;div xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html" xmlns:wicket="http://wicketstuff-jee-web.org/functions/jsf"&gt;

Tag: url // Parameters: page(required), query(optional) // Example:
JSP Example: &lt;a href="&lt;wicket:url page="mypage.MyTestPage" query="param1=value1&param2=value2"/&gt;"&gt;LINK&lt;/a&gt;
JSF Example: Tag is the same but should not be used within a href, please refer to the EL-Functions
</pre>

EL-Functions for JSP / JSF
--------------------------
<pre>
JSP: &lt;%@ taglib prefix="wicket" uri="http://wicketstuff-jee-web.org/functions/jsp" %&gt;
JSF: &lt;div xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html" xmlns:wicket="http://wicketstuff-jee-web.org/functions/jsf"&gt;

EL-Function:
${wicket:url('mypackage.MyPage')}
${wicket:urlWithQuery('mypackage.MyPage','param1=value1')}
</pre>

Forms (GET / POST)
------------------
<pre>
POST
JSP-Fragment:
&lt;form action="&lt;wicket:url page="mypackage.MyPage2"/&gt;" method="POST"&gt;
	&lt;input type="hidden" name="hiddenparam" value="testvalue"&gt;
	&lt;input type="submit" value="Submit"&gt;
&lt;/form&gt;

mypackage.MyPage2:
public TestPage2(PageParameters parameters){
	String hiddenparam = RequestCycle.get().getRequest()
	.getPostParameters().getParameterValue("hiddenparam");
}

GET
JSP-Fragment:
&lt;form action="&lt;wicket:url page="mypackage.MyPage2"/&gt;" method="GET"&gt;
	&lt;input type="hidden" name="hiddenparam" value="testvalue"&gt;
	&lt;input type="submit" value="Submit"&gt;
&lt;/form&gt;

mypackage.MyPage2:
public TestPage2(PageParameters parameters){
	String hiddenparam = parameters.get("hiddenparam").toString()
}
</pre>

Ajax-Support
------------
<pre>
WebApplication:
    @Override
    protected void init() {
	super.init();
		getPageSettings().addComponentResolver(new JEEWebGlobalAjaxHandler());
		JEEWebGlobalAjaxHandler.configure(this);
    }

Page (IMPORTANT: In constructor use setStatelessHint(false); !!!):
    @Override
    public void onEvent(IEvent<?> event) {
		JEEWebGlobalAjaxEvent castedEvent = 
		JEEWebGlobalAjaxEvent.getCastedEvent(event);
		if (castedEvent!= null) {
			AjaxRequestTarget ajaxRequestTarget = castedEvent.getAjaxRequestTarget();
			
			// Get-Request
			castedEvent.getPageParameters().get("param");
			
			// Post-Request
			castedEvent.getPostParameters().getParameterValue("param")
		}
    }

In JSP:
&lt;a href="#" onClick="${wicket:ajaxGetWithQuery('param=value')}"&gt;Update&lt;/a&gt;
&lt;a href="#" onClick="${wicket:ajaxGet()}"&gt;Update&lt;/a&gt;

In JSP with Javascript:

&lt;script type="text/javascript"&gt;
	function processCallBack(){
		var url = '${wicket:ajaxCallbackUrl()}';
		var urlWithPreRenderedArgs = 
		'${wicket:ajaxCallbackUrlWithQuery("param=value")}';
		
		// Get-Request
		var url = Wicket.Ajax.applyGetParameters(url,{"param":"value"})
		Wicket.Ajax.wrapget(url);
		
		// Post-Request
		Wicket.Ajax.wrappost(url,{"param":"value"});
	}
	processCallBack();
&lt;/script&gt;

</pre>

Links
------
https://cwiki.apache.org/confluence/display/WICKET/Including+JSP+files+in+HTML+templates
http://apache-wicket.1842946.n4.nabble.com/Wicket-1-5-and-JSP-servlet-wrapping-td4407174.html


IMPORTANT
---------
- I attached an example project which shows that it is possible to include JSF into the wicket page.
- As of Wicketstuff 6.17.0 / 7.0.0-M3 it is part of Wicketstuff-Minis with only basic tag support for JSP. (https://github.com/wicketstuff/core/pull/338)
<pre>
&lt;dependency&gt;
	&lt;groupId&gt;org.wicketstuff&lt;/groupId&gt;
	&lt;artifactId&gt;wicketstuff-minis&lt;/artifactId&gt;
	&lt;version&gt;/version/&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
	&lt;groupId&gt;org.wicketstuff&lt;/groupId&gt;
	&lt;artifactId&gt;wicketstuff-minis-examples&lt;/artifactId&gt;
	&lt;version&gt;/version/&lt;/version&gt;
&lt;/dependency&gt;
</pre>

