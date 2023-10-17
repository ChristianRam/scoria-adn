package com.ceiba.library.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {

        final RequestAttributes requestAttributes =
                RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            final HttpServletRequest httpServletRequest =
                    ((ServletRequestAttributes)
                            requestAttributes).getRequest();
            requestTemplate.header(HttpHeaders.AUTHORIZATION,
                    httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION));
        }
    }
}
