package io.ei.jsontoxls.filter;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class CorsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Max-Age", "1");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");

        Enumeration requestHeaderNames = ((HttpServletRequest) request).getHeaderNames();
        List<String> allowedHeaders = new ArrayList<>();
        while (requestHeaderNames.hasMoreElements()) {
            String header = (String) requestHeaderNames.nextElement();
            allowedHeaders.add(header);
        }
        allowedHeaders.add("Content-Type");
        httpResponse.setHeader("Access-Control-Allow-Headers", StringUtils.join(allowedHeaders, ", "));

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
