package com.artsemrogovenko.diplom.specification.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class myAspect {
    private static final Logger logger = LoggerFactory.getLogger(myAspect.class);

    @Before("@annotation(com.artsemrogovenko.diplom.specification.aspect.LogMethod)")
    public void trackAction(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toString();
        Object[] args = joinPoint.getArgs();
        logger.info("Пользователь  вызвал  " + methodName + " с аргументами: " + Arrays.toString(args));
    }

    @AfterReturning(pointcut = "@annotation(com.artsemrogovenko.diplom.specification.aspect.LogMethod)", returning = "returnValue")
    public void methodReturn(JoinPoint joinPoint, Object returnValue) {
        String methodName = String.format("%s,%s",joinPoint.getSignature().getClass(),   joinPoint.getSignature().getDeclaringTypeName());
        logger.info("" + methodName + " вернул: " + returnValue);
    }

}