package za.co.digitalcowboy.global.services.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
@Log4j2
public class IdempotentInterceptor implements HandlerInterceptor {

    private static final String IDEM_KEY = "idempotency-key";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String idemKey = request.getHeader(IDEM_KEY);

        return true;
    }
}
