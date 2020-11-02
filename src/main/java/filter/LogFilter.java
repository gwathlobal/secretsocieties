package filter;

import com.google.inject.Singleton;

import java.io.*;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

@Singleton
public class LogFilter implements Filter
{
    private FilterConfig config = null;
    private boolean active = false;

    @Override
    public void init (FilterConfig config) throws ServletException
    {
        this.config = config;
        String act = config.getInitParameter("active");
        if (act != null)
            active = (act.toUpperCase().equals("TRUE"));
        System.out.println("LogFilter init!");
    }

    @Override
    public void doFilter (ServletRequest request, ServletResponse response,
                          FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;

        String servletPath = req.getServletPath();

        System.out.println("#INFO " + new Date() + " - ServletPath :" + servletPath //
                + ", URL =" + req.getRequestURL());

        chain.doFilter(request, response);
    }

    @Override
    public void destroy()
    {
        config = null;
        System.out.println("LogFilter destroy!");
    }
}
