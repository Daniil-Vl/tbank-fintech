package ru.tbank.springapp.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
@ConditionalOnExpression("${aspect.enabled:true}")
public class TimedAspect {

    @Pointcut("@annotation(ru.tbank.springapp.aspect.Timed)")
    void hasAnnotation() {
    }

    @Around("hasAnnotation()")
    Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        String methodName = signature.getMethod().getName();
        String targetClassName = joinPoint.getTarget().getClass().getName();

        log.info("Catch {}.{} execution in TimedAspect", targetClassName, methodName);

        try {
            long startTime = System.nanoTime();
            Object result = joinPoint.proceed();
            long finishTime = System.nanoTime();

            log.info("Method {}.{} takes {} nanoseconds to complete",
                    targetClassName, methodName, finishTime - startTime);

            return result;
        } catch (Exception exception) {
            log.error("Failed to execute method {} on class {}", methodName, targetClassName);
            log.error(exception.getMessage());
            throw exception;
        }
    }
}
