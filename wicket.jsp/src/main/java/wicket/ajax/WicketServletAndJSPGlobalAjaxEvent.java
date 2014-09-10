package wicket.ajax;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Wraps the created ajax request target and the page parameters received by the
 * ajax request, to provide them via event to the current rendered page.
 * 
 * @author Tobias Soloschenko
 *
 */
public class WicketServletAndJSPGlobalAjaxEvent {

    private AjaxRequestTarget ajaxRequestTarget;

    private PageParameters pageParameters;

    public WicketServletAndJSPGlobalAjaxEvent(
	    AjaxRequestTarget ajaxRequestTarget, PageParameters pageParameters) {
	this.ajaxRequestTarget = ajaxRequestTarget;
	this.pageParameters = pageParameters;
    }

    /**
     * Gets the ajax request target created during the ajax call
     * 
     * @return the ajax request target
     */
    public AjaxRequestTarget getAjaxRequestTarget() {
	return ajaxRequestTarget;
    }

    /**
     * Sets the ajax request target during the ajax call
     * 
     * @param ajaxRequestTarget
     *            the ajax request target
     */
    public void setAjaxRequestTarget(AjaxRequestTarget ajaxRequestTarget) {
	this.ajaxRequestTarget = ajaxRequestTarget;
    }

    /**
     * Gets the page parameters received by the ajax call
     * 
     * @return the page parameters
     */
    public PageParameters getPageParameters() {
	return pageParameters;
    }

    /**
     * Sets the parameter received by the ajax call
     * 
     * @param pageParameters
     *            the page parameters
     */
    public void setPageParameters(PageParameters pageParameters) {
	this.pageParameters = pageParameters;
    }

    /**
     * Gets the event object from the original event. Only used to make the code
     * a little bit smarter.
     * 
     * @param event
     *            the event received by the page
     * @return the event to get the ajax request target and the parameters from
     */
    public static WicketServletAndJSPGlobalAjaxEvent getCastedEvent(
	    IEvent<?> event) {
	if (event.getPayload() instanceof WicketServletAndJSPGlobalAjaxEvent) {
	    return ((WicketServletAndJSPGlobalAjaxEvent) event.getPayload());
	} else {
	    return null;
	}
    }
}
