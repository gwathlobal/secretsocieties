package templater;

import javax.servlet.ServletContext;
import java.util.Map;

public interface IPageGenerator {
    String getPage(ServletContext servletContext, String filename, Map<String, Object> data);
}
