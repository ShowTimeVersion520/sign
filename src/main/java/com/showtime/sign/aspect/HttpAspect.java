package com.showtime.sign.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class HttpAspect {

    private final static Logger logger = LoggerFactory.getLogger(HttpAspect.class);

    //@Before("execution(public * com.showtime.sign.controller.GrilController.*(..))")
    @Pointcut("execution(public * com.showtime.sign.service.*.*(..))")
    public void log(){
    }
    @Before("log()")
    public void doBefore(JoinPoint joinPoint){
        ServletRequestAttributes attributes =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        //url
        logger.info("url = {}", request.getRequestURI());
        //method
        logger.info("method = {}", request.getMethod());
        //ip
        logger.info("ip = {}", request.getRemoteAddr());
        //类方法
        logger.info("class_method = {}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        //参数
        logger.info("args = {}", joinPoint.getArgs());
    }

    @After("log()")
    public void doAfter(){
        logger.info("end method");
    }

    @AfterReturning(returning = "object", pointcut = "log()")
    public void doAfterReturning(Object object){
        if(object == null){
            logger.info("response = {}", "null");
        }else{
            logger.info("response = {}", object.toString());
        }
    }
}
