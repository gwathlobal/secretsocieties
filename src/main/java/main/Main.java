package main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import servlets.IndexServlet;
import utils.DeployGuiceServletConfig;

import java.io.File;

public class Main {
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        tomcat.setPort(8081);
        tomcat.getConnector();

        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);
        //context.addApplicationListener(Listener.class.getName());

        Injector injector = Guice.createInjector(new DeployGuiceServletConfig.DeployModule());

        IndexServlet indexServlet = injector.getInstance(IndexServlet.class);

        Tomcat.addServlet(context, IndexServlet.Name, indexServlet);
        context.addServletMappingDecoded("/*", IndexServlet.Name);

        tomcat.start();
        tomcat.getServer().await();

    }
}
