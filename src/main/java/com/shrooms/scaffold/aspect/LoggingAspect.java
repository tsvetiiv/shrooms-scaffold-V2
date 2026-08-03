package com.shrooms.scaffold.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @AfterThrowing(
            pointcut = "execution(* com.shrooms.scaffold.service..*(..))",
            throwing = "exception"
    )
    public void logAfterServiceMethodThrowsException(JoinPoint joinPoint, Exception exception) {
        LOGGER.warn("Service method failed: {} with exception: {}",
                joinPoint.getSignature().toShortString(),
                exception.getMessage());
    }
}