package ru.gosuslugi.pgu.identification.core.filter;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.atc.carcass.security.model.EsiaOAuthTokenSession;
import ru.atc.carcass.security.service.impl.OAuthTokenUtil;
import ru.gosuslugi.pgu.common.core.exception.PguException;
import ru.gosuslugi.pgu.common.core.exception.dto.error.ErrorMessage;
import ru.gosuslugi.pgu.common.core.exception.dto.error.ErrorMessageWithoutModal;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.common.logging.service.SpanService;
import ru.gosuslugi.pgu.identification.core.model.UserSession;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static ru.gosuslugi.pgu.common.logging.service.SpanService.USER_ID_TAG;


/**
 * Фильтр для проверки авторизационных данных на основе токена ЕСИА. Также запрашивает персональные данные и помещает их
 * в userPersonalData (bean со скоупом request).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EsiaFilter extends OncePerRequestFilter {

    private static final String ACC_T_COOKIE = "acc_t";
    @Value("${esia.auth.exclude-urls:#{null}}")
    private List<String> excludeUrls;

    private final UserSession userSession;
    private final SpanService spanService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
            for (String excludeUrl : excludeUrls) {
                if (request.getRequestURI().startsWith(excludeUrl)) {
                    spanService.addTagToSpan(USER_ID_TAG, "Unauthorized");
                    return true;
                }
            }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<Cookie> cookieOptional = Optional.ofNullable(request.getCookies())
                .map(List::of)
                .orElse(Collections.emptyList())
                .stream()
                .filter(cookie -> ACC_T_COOKIE.equals(cookie.getName()))
                .findFirst();
        if (cookieOptional.isEmpty()) {
            setCookieErrorResponse(response);
            return;
        }

        String token = cookieOptional.get().getValue();
        EsiaOAuthTokenSession tokenInfo = OAuthTokenUtil.getTokenInfo(token);
        userSession.setUserId(Long.parseLong(tokenInfo.getUserId()));
        userSession.setCookie(token);

        filterChain.doFilter(request, response);
    }

    private void setCookieErrorResponse(HttpServletResponse response) {
        boolean isLogError = true;
        Throwable ex = null;

        String message = "Cookie '" + ACC_T_COOKIE + "' is not found. ESIA authorization failed.";
        if (isLogError) {
            log.error(message, ex);
        } else if (log.isInfoEnabled()) {
            log.info(message);
        }
        response.setStatus(UNAUTHORIZED.value());
        response.setContentType("application/json");
        try {
            ErrorMessage err = new ErrorMessageWithoutModal();
            err.setStatus(UNAUTHORIZED.name());
            err.setMessage(message);
            err.setDescription(ex != null ? ex.getMessage() : null);
            err.setTraceId(spanService.getCurrentTraceId());

            String json = JsonProcessingUtil.toJson(err);
            response.getWriter().write(json);
        } catch (IOException e) {
            throw new PguException(e.getMessage(), e);
        }
    }
}
