package base;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class BaseTomcatTesting {
    public void TomcatRun(TemporaryFolder temporaryFolder, ISetupServletsFunc setupServlets, ITestFunc testFunc) throws IOException, LifecycleException {
        final File newFolder = temporaryFolder.newFolder("MyTomcatTest");
        final String baseDir = newFolder.getAbsolutePath();

        // creating a Tomcat instance
        final Tomcat tomcat = new Tomcat();

        // configure tomcat
        {
            // random unassigned HTTP port
            tomcat.setPort(0);
            tomcat.setBaseDir(baseDir);
            tomcat.getConnector();
        }

        // set up servlets
        {
            setupServlets.func(tomcat);
        }

        // start tomcat
        {
            tomcat.start();
        }

        // test web app
        {
            testFunc.func(tomcat);
        }

        // stop/destroy tomcat
        {
            tomcat.stop();
            tomcat.destroy();
        }
    }
}
