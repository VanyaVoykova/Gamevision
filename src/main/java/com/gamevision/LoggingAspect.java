package com.gamevision.aop;

import com.gamevision.model.servicemodels.GameAddServiceModel;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

//slf4j and its implementation log4j both come with Spring Boot Starter
import org.slf4j.Logger; //only API, a facade; implementation is log4j and it enables log files
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


//Interceptor
@Aspect
@Component
public class LoggingAspect {
    static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);


    //  @AfterReturning(pointcut = "execution(* com.gamevision.service.impl.GameServiceImpl.addGame(..))") //SPACE after *

    //Using @Around i/o @AfterReturning here so we can get acces to ProceedingJoinPoint which we need to get the Object (its fields are used in the logged messages)
    @Around("execution(* com.gamevision.service.impl.GameServiceImpl.addGame(..))") //SPACE after *
    public void afterAddGame(ProceedingJoinPoint pjp) throws Throwable { //JP is the addGameMethod
        // Object[] arguments = pjp.getArgs();
        GameAddServiceModel gameAdded = (GameAddServiceModel) pjp.proceed(); //should return GameAddServiceModel if no exception is thrown

        LocalDateTime timeCreated = LocalDateTime.now();
        String gameTitle = gameAdded.getTitle();
        String addedByUser = gameAdded.getAddedBy();
        LOGGER.info(String.format("%s: game with title \"%s\" added by user %s", timeCreated, gameTitle, addedByUser));

    }
}
