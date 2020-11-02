package servlets;

import base.BaseTomcatTesting;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import utils.DeployGuiceServletConfig;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class IndexServletTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testDoGet() {
        var tomcatTesting = new BaseTomcatTesting();
        try {
            tomcatTesting.TomcatRun(
                    temporaryFolder,
                    (tomcat) ->
                    {
                        String contextPath = "";
                        String docBase = new File("src/main/resources").getAbsolutePath();

                        Context context = tomcat.addContext(contextPath, docBase);
                        Injector injector = Guice.createInjector(new DeployGuiceServletConfig.DeployModule());

                        IndexServlet indexServlet = injector.getInstance(IndexServlet.class);

                        Tomcat.addServlet(context, IndexServlet.Name, indexServlet);
                        context.addServletMappingDecoded("/*", IndexServlet.Name);
                    },
                    (tomcat) ->
                    {
                        final HttpGet request = new HttpGet("http://localhost:" + tomcat.getConnector().getLocalPort() + '/');

                        try (final CloseableHttpClient client = HttpClientBuilder.create().build();
                             final CloseableHttpResponse response = client.execute(request)) {
                            final HttpEntity entity = response.getEntity();

                            assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK);

                            //String body = EntityUtils.toString(entity);
                            //assertTrue(body.equals("Hello, world #4!"));

                            // close the content stream.
                            EntityUtils.consumeQuietly(entity);
                        } catch (ClientProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}