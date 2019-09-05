package com.jq.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
@Component
public class RouteFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        Object name = request.getParameter("name");
        if(name == null) {
            //是否返回路由的响应
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            try {
                ctx.getResponse().getWriter().write("name is empty");
            }catch (Exception e){}

            return null;
        }
        if (!"jq".equals(name)) {
            try {
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(402);
                ctx.getResponse().getWriter().write("name isn't  jq");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        System.out.println("ok");
        return null;

    }
}
