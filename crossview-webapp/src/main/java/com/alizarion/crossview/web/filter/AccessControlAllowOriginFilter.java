package com.alizarion.crossview.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author selim@openlinux.fr.
 */
public class AccessControlAllowOriginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        ((HttpServletResponse)resp).addHeader(
                "Access-Control-Allow-Origin", "*"
        );

        ((HttpServletResponse)resp).addHeader(
                "Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE"
        );

        ((HttpServletResponse)resp).addHeader(
                "Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization"
        );

        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {

    }


}