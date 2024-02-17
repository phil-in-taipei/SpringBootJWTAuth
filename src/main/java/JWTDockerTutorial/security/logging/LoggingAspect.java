package JWTDockerTutorial.security.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("executeLogging()")
    public void logMethodCall(JoinPoint joinPoint) {
        StringBuilder message = new StringBuilder("Method Name : ");
        message.append(message.append(joinPoint.getSignature().getName()));
        final Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            message.append("method args=|");
            Arrays.asList(args).forEach(arg -> {
                message.append("arg=").append(arg).append("|");
            });
        }
        LOGGER.info(message.toString());
    }

    @Pointcut("@annotation(Loggable)")
    public void executeLogging() { }
}
