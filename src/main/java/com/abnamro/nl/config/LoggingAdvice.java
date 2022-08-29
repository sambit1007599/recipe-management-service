package com.abnamro.nl.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAdvice {

    @Around("within(@org.springframework.web.bind.annotation.RestController *)" +
            " || @annotation(org.springframework.web.bind.annotation.RequestMapping)" +
            " || @annotation(org.springframework.web.bind.annotation.GetMapping)" +
            " || @annotation(org.springframework.web.bind.annotation.PostMapping)" +
            " || @annotation(org.springframework.web.bind.annotation.PutMapping)" +
            " || @annotation(org.springframework.web.bind.annotation.DeleteMapping)"
    )
    Object logControllerCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
        Object responseObject = null;
        try {
            responseObject = joinPoint.proceed(joinPoint.getArgs());
        } finally {
            String methodName = sourceName(joinPoint);
            logger.info("client request : {} {}", methodName, joinPoint.getArgs());
            logger.info("client response: {}", responseObject);
        }
        return responseObject;
    }

    private String sourceName(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        return methodSignature.getMethod().getName();
    }
}
