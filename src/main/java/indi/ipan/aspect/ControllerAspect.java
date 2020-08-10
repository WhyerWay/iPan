package indi.ipan.aspect;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class ControllerAspect {
    private final static Logger logger = LoggerFactory.getLogger(ControllerAspect.class);
    
    @Pointcut("execution(public * indi.ipan.controller.*.*(..))")
    public void log() {}
    
    @Before("log()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
         logger.info("sessionid={" + attributes.getSessionId());
        // url
        logger.info("url={}", request.getRequestURL());
        // method GET/POST
        logger.info("method={}", request.getMethod());
        // ip
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (!ip.trim().isEmpty()) {
            ip = ip.split(",")[0];
        }
        logger.info("ip={}", ip);
        // class.method
        logger.info("class_method={}"
        , joinPoint.getSignature().getDeclaringTypeName() 
        + "." + joinPoint.getSignature().getName());
        // args
        logger.info("args={}", Arrays.toString(joinPoint.getArgs()));
    }
    
    @AfterThrowing("log()")
    public void doAfter() {
         logger.info("Operation fail");
    }

    @AfterReturning(returning = "object", pointcut = "log()")
    public void doAfterReturning(Object object) {
        if (object != null) {
            logger.info("response={}", object.toString());
        }
    }
}
