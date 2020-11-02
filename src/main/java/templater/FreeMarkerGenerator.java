package templater;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class FreeMarkerGenerator implements IPageGenerator {
    //private static final String HTML_DIR = String.join(File.separator, "WEB-INF", "views");
    private static final String HTML_DIR = "WEB-INF/views";

    private final Configuration cfg = new Configuration(Configuration.VERSION_2_3_0);

    @Override
    public String getPage(ServletContext servletContext, String filename, Map<String, Object> data) {
        Writer stream = new StringWriter();
        try {
            cfg.setServletContextForTemplateLoading(servletContext, HTML_DIR);
            Template template = cfg.getTemplate(filename);
            template.process(data, stream);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        return stream.toString();
    }
}
