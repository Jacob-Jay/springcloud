package com.jq.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class RouteFilter extends ZuulFilter {

    /**
     * 定义过滤器的执行时机
     * 1、pre    路由前执行
     * 2、route  路由时
     * 3、post    路由之后  错误之前
     * 4、error  发生错误
     * @return
     */
    @Override
    public String filterType() {
        return "post";
    }

    /**
     * 存在多个过滤器时，定义当前路由器的执行顺序
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 定义是否需要进行过滤，可以根据请求进行判断
     * @return
     */
    @Override
    public boolean shouldFilter() {
        //获取当前请求
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        //获取当前请求
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        Object name = request.getParameter("name");
            if(name == null) {
                //是否返回路由的响应
                ctx.setSendZuulResponse(false);
                //设置响应状态码
                ctx.setResponseStatusCode(401);
                ctx.setResponseBody("name is empty");

                return null;
            }
            if (!"jq".equals(name)) {
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(402);
                ctx.setResponseBody("name isn't  jq");
                return null;
            }
            System.out.println("ok");

        return null;

    }
}
