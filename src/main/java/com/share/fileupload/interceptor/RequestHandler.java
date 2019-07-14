package com.share.fileupload.interceptor;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequestHandler {

    Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    @Before("execution(* *(..)) && "
            + "( within(com.share.fileupload..controller..*) || within(com.share.fileupload..service..*) || within(com.share.fileupload..repository..*))")
    public void before(JoinPoint joinPoint) {
        logger.info("Allowed : execution for {} {}", joinPoint, joinPoint.getArgs());
    }

    @AfterReturning(value = "execution(* *(..)) && "
            + "( within(com.share.fileupload..controller..*) || within(com.share.fileupload..service..*) || within(com.share.fileupload..repository..*))", 
            returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Return : {} returned with value {}", joinPoint, result);
    }

}
