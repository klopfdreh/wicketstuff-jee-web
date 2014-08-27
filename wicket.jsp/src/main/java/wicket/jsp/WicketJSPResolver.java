package wicket.jsp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupException;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.WicketTag;
import org.apache.wicket.markup.parser.filter.WicketTagIdentifier;
import org.apache.wicket.markup.resolver.IComponentResolver;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.cycle.RequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The WicketJSPResolver is used to embed JSP content into wicked HTML pages, by
 * a custom Wicket-Tag. It is tested with Wicket 6.16.0. Because include is used
 * to apply the content, every restrictions of include is applied to the jsp.
 * (No header modifications and so on). To use it you should registered it to the
 * page settings in the init-Method of the Wicket-Application: <code><pre>
 * 	{@literal @}Override
 * 	protected void init() {
 * 		super.init();
 * 		getPageSettings().addComponentResolver(new WicketJSPResolver());
 * 	}
 * </pre></code> A tag specifies the location which JSP to load. (The argument
 * is given to the getRequestDispatcher method of the ServletContext):
 * <code><pre>
 * 	&lt;wicket:jsp file="/de/test/jspwicket/TestPage.jsp"&gt;&lt;/wicket:jsp&gt;
 * </pre></code> <b>!!! This tag must not be defined as empty tag - it has to be
 * opened / closed!!!</b><br>
 * <br>
 *
 * <b>Links:</b><br>
 * https://cwiki.apache.org/confluence/display/WICKET/Including+JSP+files+in+
 * HTML+templates<br>
 * http://apache-wicket.1842946.n4.nabble.com/Wicket-1-5-and-JSP-servlet-
 * wrapping-td4407174.html<br>
 * <br>
 * 
 * @see org.apache.wicket.protocol.http.WebApplication.init()
 * 
 * @author Tobias Soloschenko
 */
public class WicketJSPResolver implements IComponentResolver {

    private static final long serialVersionUID = -5545617085402658472L;
    
    private static final String JSP_ENCODING = "UTF-8";
    
    private static final Logger LOGGER = LoggerFactory
	    .getLogger(WicketJSPResolver.class);

    // Registration of the tag identifier
    static {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Registering" + WicketJSPResolver.class.getName());
	}
	WicketTagIdentifier.registerWellKnownTagName("jsp");
    }

    @Override
    public Component resolve(MarkupContainer container,
	    MarkupStream markupStream, ComponentTag tag) {
	if (tag instanceof WicketTag) {
	    WicketTag wtag = (WicketTag) tag;
	    if ("jsp".equalsIgnoreCase(wtag.getName())) {
		String file = wtag.getAttributes().getString("file");
		if (file == null || file.trim().length() == 0) {
		    throw new MarkupException(
			    "Wrong format of <wicket:jsp file='/foo.jsp'>: attribute 'file' is missing");
		}
		return new JspFileContainer(file);
	    }
	}
	return null;
    }

    /**
     * The JSP container which contains the JSP output and renders it to the
     * Wicket HTML page
     */
    private static class JspFileContainer extends MarkupContainer {

	private static final long serialVersionUID = -4296125929087527034L;
	
	private String file;

	public JspFileContainer(String file) {
	    super(file);
	    this.file = file;
	}

	/**
	 * Renders the component tag body with the content of the JSP output
	 */
	@Override
	public void onComponentTagBody(MarkupStream markupStream,
		ComponentTag openTag) {

	    // Get the everything required to include the jsp file
	    RequestCycle cycle = (RequestCycle) RequestCycle.get();
	    ServletRequest request = (HttpServletRequest) cycle.getRequest()
		    .getContainerRequest();
	    JSPIncludeHttpServletResponseWrapper response = new JSPIncludeHttpServletResponseWrapper(
		    (HttpServletResponse) cycle.getResponse()
			    .getContainerResponse());
	    ServletContext context = ((WebApplication) Application.get())
		    .getServletContext();

	    // Handle a missing jsp file
	    handleMissingFile(context);

	    try {
		// include the JSP file by the given request / response
		context.getRequestDispatcher(file).include(request, response);

		// replace the component tag body with the result of the JSP
		// output
		replaceComponentTagBody(markupStream, openTag,
			response.getOutput());
	    } catch (ServletException e) {
		throw new WicketRuntimeException(e);
	    } catch (IOException e) {
		throw new WicketRuntimeException(e);
	    }

	}

	/**
	 * Handles missing files.
	 * 
	 * @param context
	 *            the servlet context
	 * @throws WicketRuntimeException
	 *             if the resource file couldn't be resolved or an exception
	 *             should be thrown if it is missing
	 */
	private void handleMissingFile(ServletContext context)
		throws WicketRuntimeException {
	    try {
		if (context.getResource(file) == null) {
		    if (shouldThrowExceptionForMissingFile()) {
			throw new WicketRuntimeException(
				String.format(
					"Cannot locate resource %s within current context: %s",
					file, context.getContextPath()));
		    } else {
			LOGGER.warn(
				"File will not be processed. Cannot locate resource {} within current context: {}",
				file, context.getContextPath());
		    }
		}
	    } catch (MalformedURLException e) {
		throw new WicketRuntimeException(e);
	    }
	}

	/**
	 * Checks if an exception should be thrown, if a resource file is
	 * missing.
	 * 
	 * @return if an exception should be thrown
	 */
	private boolean shouldThrowExceptionForMissingFile() {
	    return Application.get().getResourceSettings()
		    .getThrowExceptionOnMissingResource();
	}
    }

    /**
     * The ByteArrayServletOutputStream writes bytes to the given
     * ByteArrayOutputStream
     */
    private static class ByteArrayServletOutputStream extends
	    ServletOutputStream {
	private ByteArrayOutputStream baos;

	public ByteArrayServletOutputStream(ByteArrayOutputStream baos) {
	    this.baos = baos;
	}

	@Override
	public void write(int param) throws IOException {
	    baos.write(param);
	}
    }

    /**
     * This HttpServletResponseWrapper is used to get the JSP's output by
     * including it with a request dispatcher.
     */
    private static class JSPIncludeHttpServletResponseWrapper extends
	    HttpServletResponseWrapper {

	private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	private ServletOutputStream byteArrayServletOutputStream = new ByteArrayServletOutputStream(
		byteArrayOutputStream);
	private PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);

	public JSPIncludeHttpServletResponseWrapper(HttpServletResponse response) {
	    super(response);
	}

	@Override
	public ServletOutputStream getOutputStream() {
	    return byteArrayServletOutputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
	    return printWriter;
	}

	public String getOutput() throws UnsupportedEncodingException {
	    // if something has been written with the writer - we need to flush
	    // it!
	    printWriter.flush();
	    return byteArrayOutputStream.toString(JSP_ENCODING);
	}
    }

}
