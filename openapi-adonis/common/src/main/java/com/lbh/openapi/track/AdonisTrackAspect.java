package com.lbh.openapi.track;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.woozooha.adonistrack.aspect.ProfileAspect;

@Aspect
@Component
public class AdonisTrackAspect extends ProfileAspect {

	@Override
	@Pointcut("execution(* *(..)) && (within(com.lbh.openapi..*) || within(com.lbh.common..*+))")
	public void executionPointcut() {
		// TODO Auto-generated method stub
		
	}
}
