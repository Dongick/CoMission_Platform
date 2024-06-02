package mission.aspect;

import mission.exception.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass()); // 로거 인스턴스 생성

    @Pointcut("within(mission..*)")
    public void pointCutAll() {
    }
    @Pointcut("within(mission..*service..*)")
    public void pointCutService() {
    }

    @Pointcut("within(mission..*controller..*)")
    public void pointCutController() {
    }

    @Pointcut("within(mission..*exception..*)")
    public void pointCutException() {
    }

    @Around("pointCutService() || pointCutController()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 메서드 진입 로그 출력
        logger.debug("진입: {}.{}() 인수 = {}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        try {
            // 실제 메서드 실행
            Object result = joinPoint.proceed();
            // 메서드 종료 로그 출력
            logger.debug("종료: {}.{}() 결과 = {}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), result);
            return result;
        } catch (BadRequestException | MissionAuthenticationException | NotFoundException | ConflictException |
                ForbiddenException | MethodArgumentNotValidException | MethodArgumentTypeMismatchException e) {
            logger.error("잘못된 인수: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e; // 예외를 다시 던져서 처리를 위임
        }
    }
}
