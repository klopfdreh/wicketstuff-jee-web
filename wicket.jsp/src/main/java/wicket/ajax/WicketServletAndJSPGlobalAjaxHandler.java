package wicket.ajax;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.WicketAjaxJQueryResourceReference;
import org.apache.wicket.ajax.WicketEventJQueryResourceReference;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.cycle.PageRequestHandlerTracker;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.JQueryResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to configure a global ajax event hook for the embedded
 * tags (servlet,jsp,jsf). The tag libs can be used to generate a callback
 * function to send an ajax request to the current rendered page.<br>
 * <br>
 * 
 * <b>WATCH OUT - The WebPage has to be configured with setStatelassHint(false);
 * !!!!
 * 
 * @see wicket.jsp.el.WicketELURL.ajaxCallbackWithQuery(String)
 * @see wicket.jsp.el.WicketELURL.ajaxCallback()
 * 
 * @author Tobias Soloschenko
 *
 */
public class WicketServletAndJSPGlobalAjaxHandler extends ResourceReference {

    private static final Logger LOGGER = LoggerFactory
	    .getLogger(WicketServletAndJSPGlobalAjaxHandler.class);

    private static final long serialVersionUID = 4348780269907263872L;

    private static final String NAME = "WicketServletAndJSPGlobalAjaxHandler";

    public WicketServletAndJSPGlobalAjaxHandler() {
	super(NAME);
    }

    /**
     * Receives ajax events and delegates them to the page which where rendered
     * previously.
     */
    @Override
    public IResource getResource() {
	return new IResource() {
	    private static final long serialVersionUID = 3070290312369930992L;

	    @Override
	    public void respond(Attributes attributes) {
		try {
		    int pageId = attributes.getParameters().get("pageId")
			    .toInt();
		    Page page = (Page) WebSession.get().getPageManager()
			    .getPage(pageId);
		    AjaxRequestTarget newAjaxRequestTarget = ((WebApplication) Application
			    .get()).newAjaxRequestTarget(page);
		    RequestCycle.get().scheduleRequestHandlerAfterCurrent(
			    newAjaxRequestTarget);
		    page.send(
			    page,
			    Broadcast.BREADTH,
			    new WicketServletAndJSPGlobalAjaxEvent(
				    newAjaxRequestTarget, attributes
					    .getParameters()));
		} catch (Exception e) {
		    LOGGER.error("Error while processing the ajax request", e);
		}
	    }
	};
    }

    /**
     * Configures the handler to the given application.
     * 
     * @param application
     *            the application to configure the handler to
     */
    public static void configure(WebApplication application) {
	application.getRequestCycleListeners().add(
		new PageRequestHandlerTracker());
	application.mountResource("/"
		+ WicketServletAndJSPGlobalAjaxHandler.NAME,
		new WicketServletAndJSPGlobalAjaxHandler());
	application.getHeaderContributorListenerCollection().add(
		new IHeaderContributor() {

		    private static final long serialVersionUID = 1644041155625458328L;

		    @Override
		    public void renderHead(IHeaderResponse response) {
			JavaScriptResourceReference forReference = new JavaScriptResourceReference(
				WicketServletAndJSPGlobalAjaxHandler.class,
				"WicketServletAndJSPGlobalAjaxHandler.js") {
			    
			    private static final long serialVersionUID = -3649384632770480975L;

			    @Override
			    public Iterable<? extends HeaderItem> getDependencies() {
				List<HeaderItem> dependencies = new ArrayList<HeaderItem>();
				dependencies.add(JavaScriptHeaderItem
					.forReference(JQueryResourceReference
						.get()));
				dependencies.add(JavaScriptHeaderItem
					.forReference(WicketEventJQueryResourceReference
						.get()));
				dependencies.add(JavaScriptHeaderItem
					.forReference(WicketAjaxJQueryResourceReference
						.get()));
				return dependencies;
			    }
			};
			response.render(JavaScriptHeaderItem
				.forReference(forReference));
		    }
		});
    }
}
