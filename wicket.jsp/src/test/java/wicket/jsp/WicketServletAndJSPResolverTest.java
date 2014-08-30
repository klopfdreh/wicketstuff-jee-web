package wicket.jsp;

import java.io.File;

import org.apache.wicket.util.tester.WicketTester;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class WicketServletAndJSPResolverTest {

    private WicketTester wicketTester;

    @Test
    public void testServletsAreResolvedRight() throws Exception {
	Server server = new Server(8080);
	WebAppContext context = new WebAppContext();
	context.setResourceBase(new File(".", "src/main/webapp")
		.getCanonicalPath());
	context.setContextPath("/");
	context.addServlet(TestServlet.class, "/TestServlet");
	context.setParentLoaderPriority(true);

	server.setHandler(context);
	server.addLifeCycleListener(new LifeCycle.Listener() {

	    @Override
	    public void lifeCycleFailure(LifeCycle arg0, Throwable arg1) {
	    }

	    @Override
	    public void lifeCycleStarted(LifeCycle arg0) {
		// This is currently not working - but it should - the
		// wicketTester is using the ServletContext of Jetty which
		// provides the TestServlet to the path /TestServlet - the
		// TestPage.html is accessing the Servlet via path="/TestServlet"
		wicketTester.startPage(TestPage.class);
		System.err.println(wicketTester.getLastResponseAsString());
	    }

	    @Override
	    public void lifeCycleStarting(LifeCycle arg0) {
	    }

	    @Override
	    public void lifeCycleStopped(LifeCycle arg0) {
	    }

	    @Override
	    public void lifeCycleStopping(LifeCycle arg0) {
	    }

	});
	wicketTester = new WicketTester(new TestApplication(),
		context.getServletContext());
	server.start();
	server.join();
    }
}
