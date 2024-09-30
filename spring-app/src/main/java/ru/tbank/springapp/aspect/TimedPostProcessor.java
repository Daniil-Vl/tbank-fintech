package ru.tbank.springapp.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TimedPostProcessor implements BeanPostProcessor {

    private final Map<String, Class<?>> registeredBeans = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        var javaClass = bean.getClass();

        if (javaClass.isAnnotationPresent(Timed.class))
            registeredBeans.put(beanName, javaClass);

        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        var cls = registeredBeans.get(beanName);

        if (cls == null)
            return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);

        ProxyFactory proxyFactory = new ProxyFactory(bean);
        proxyFactory.addAdvice(new TimedInterceptor());

        return proxyFactory.getProxy();
    }


    private static class TimedInterceptor implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String methodName = invocation.getMethod().getName();
            String className = invocation.getMethod().getDeclaringClass().getName();

            log.info("Method {} started...", methodName);

            try {
                long startTime = System.nanoTime();
                Object result = invocation.proceed();
                long finishTime = System.nanoTime();

                log.info("Method {}.{} took {} nanoseconds",
                        className, methodName, finishTime - startTime);

                return result;
            } catch (Exception exception) {
                log.error("Failed to execute method {} on class {}", methodName, className);
                log.error(exception.getMessage());
                throw exception;
            }
        }

    }
}
