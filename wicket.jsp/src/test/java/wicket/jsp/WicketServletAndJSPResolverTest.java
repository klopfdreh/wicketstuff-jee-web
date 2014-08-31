package wicket.jsp;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WicketServletAndJSPResolverTest {

    private static final Logger LOGGER = LoggerFactory
	    .getLogger(WicketServletAndJSPResolverTest.class);
    
    private static final int PORT = 8089;
    private static final String HOST = "127.0.0.1";

    @Test
    public void testServletsAreResolvedRight() throws Exception {
	final Server server = new Server(PORT);
	final WebAppContext context = new WebAppContext();
	context.setDescriptor(new File(".", "src/main/webapp/WEB-INF/web.xml")
		.getCanonicalPath());
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
		InputStream contentIn = null;
		try {
		    CloseableHttpClient httpclient = HttpClients
			    .createDefault();
		    HttpGet httpGet = new HttpGet("http://"+HOST+":"+PORT+"/");
		    CloseableHttpResponse response1 = httpclient
			    .execute(httpGet);
		    contentIn = response1.getEntity().getContent();
		    String content = IOUtils.toString(contentIn);
		    
		    // This both assert has to be true - that shows wicket.jsp is working
		    Assert.assertTrue(content.contains("This is a servlet"));
		    Assert.assertTrue(content.contains("This is a JSP"));
		} catch (Exception e) {
		    LOGGER.error("Error while making the get request to check the jsp / servlet integration.");
		}finally{
		    IOUtils.closeQuietly(contentIn);
		}
		try {
		    server.stop();
		} catch (Exception e) {
		    LOGGER.error("Error while shutting down the server to check the jsp / servlet integration.");
		}
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
	server.start();
	server.join();
    }
}
