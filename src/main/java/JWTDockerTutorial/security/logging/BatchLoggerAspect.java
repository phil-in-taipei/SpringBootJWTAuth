package JWTDockerTutorial.security.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// This will not log the arguments as string
// values -- only logs the number of args.
// This is for better performance and/or security
// when logging some select service methods
@Aspect
@Component
public class BatchLoggerAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchLoggerAspect.class);

    @Before("executeLogging()")
    public void logMethodCall(JoinPoint joinPoint) {
        StringBuilder message = new StringBuilder("Batch method Name : ");
        message.append(message.append(joinPoint.getSignature().getName()));
        final Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            message.append("method args=|");
            message.append("length=").append(args.length).append("|");
        }
        LOGGER.info(message.toString());
    }

    @Pointcut("@annotation(BatchLogger)")
    public void executeLogging() { }
}