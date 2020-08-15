package com.shop.ItemServiceRest.Aop;

import com.shop.ItemServiceRest.Controller.ItemController;
import com.shop.ItemServiceRest.Model.Item;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.NoSuchElementException;

@Component
@Aspect
public class ItemControllerAspect {
    private final static Logger logger = LoggerFactory.getLogger(ItemController.class);

    @Around(value = "@annotation(NoSuchElementPointcut)")
    public Object afterThrowNoElement(ProceedingJoinPoint joinPoint) throws Throwable {
        Long id = null;
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        Parameter[] params = methodSignature.getMethod().getParameters();

        for (int i = 0; i < method.getParameterCount(); ++i) {
            for (Annotation ann : params[i].getAnnotations()) {
                if (ann instanceof PathVariable && ((PathVariable)ann).value().equals("id")) {
                    id = (Long)args[i];
                    break;
                }
            }

            if (id != null) {
                break;
            }
        }

        try {
            return joinPoint.proceed();
        } catch (NoSuchElementException ex) {
            logger.warn("Item with id " + id + " not found");
            logger.error(ex.toString());

            Class<?> returnValue = methodSignature.getMethod().getReturnType();
            if (returnValue != Void.class) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            } else {
                return Void.class;
            }
        }
    }

    @Around(value = "@annotation(BadRequestPointcut)")
    public Object afterBadRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        BindingResult bindingResult = null;
        Item item = null;
        for (Object arg : joinPoint.getArgs()) {
            if (arg.getClass().getSimpleName().contains("BindingResult")) {
                bindingResult = (BindingResult)arg;
            }

            if (arg.getClass().getSimpleName().equals("Item")) {
                item = (Item)arg;
            }
        }

        if (bindingResult == null || bindingResult.hasErrors()) {
            logger.info("Bad request on item information");
            return new ResponseEntity<>(item, HttpStatus.BAD_REQUEST);
        } else {
            return joinPoint.proceed();
        }
    }
}
