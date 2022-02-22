package ru.gosuslugi.pgu.smevconverter.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.atc.carcass.security.exception.EsiaOAuthTokenSessionException;
import ru.atc.carcass.security.service.impl.OAuthTokenUtil;
import ru.gosuslugi.pgu.common.core.exception.PguException;
import ru.gosuslugi.pgu.common.core.exception.dto.error.ErrorMessageWithoutModal;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.common.logging.service.SpanService;
import ru.gosuslugi.pgu.smevconverter.model.UserSession;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static ru.gosuslugi.pgu.common.logging.service.SpanService.USER_ID_TAG;


/**
 * Фильтр для проверки авторизационных данных на основе токена ЕСИА
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EsiaFilter extends OncePerRequestFilter {

    private static final String ACC_T_COOKIE = "acc_t";
    @Value("${esia.auth.exclude-urls:#{null}}")
    private List<String> excludeUrls;
    @Value("${esia.auth.disabled:#{false}}")
    private boolean authDisabled;
    private final UserSession userSession;
    private final SpanService spanService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (authDisabled) {
            spanService.addTagToSpan(USER_ID_TAG, "Unauthorized");
            return true;
        }
        Optional<String> excludeUrl = excludeUrls.stream()
                .filter(url -> request.getServletPath().startsWith(url))
                .findFirst();
        if (excludeUrl.isPresent()) {
            spanService.addTagToSpan(USER_ID_TAG, "Unauthorized");
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (tokenIsValid(request)) {
            filterChain.doFilter(request, response);
        } else {
            setCookieErrorResponse(response);
        }
    }

    private boolean tokenIsValid(HttpServletRequest request) {
        final var esiaErrorMessage = "Ошибка Cookie " + ACC_T_COOKIE + " %s, ESIA authorization failed";

        var cookieOptional = Optional.ofNullable(request.getCookies())
                .map(List::of)
                .orElse(Collections.emptyList())
                .stream()
                .filter(cookie -> ACC_T_COOKIE.equals(cookie.getName()))
                .findFirst();
        if (cookieOptional.isEmpty()) {
            log.error(String.format(esiaErrorMessage, "is not found"));
            return false;
        }

        try {
            var tokenInfo = OAuthTokenUtil.getTokenInfo(cookieOptional.get().getValue());
            // не просрочен ли токен
            if (Instant.now().isAfter(Instant.ofEpochSecond(tokenInfo.getExp()))) {
                log.error(String.format(esiaErrorMessage, "date expired"));
                return false;
            }
            userSession.setUserId(Long.parseLong(tokenInfo.getUserId()));
        } catch (EsiaOAuthTokenSessionException | ArrayIndexOutOfBoundsException e) {
            log.error(String.format(esiaErrorMessage, "has errors"));
            return false;
        }

        return true;
    }

    private void setCookieErrorResponse(HttpServletResponse response) {
        response.setStatus(UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        try {
            var err = new ErrorMessageWithoutModal();
            err.setStatus(UNAUTHORIZED.name());
            err.setMessage("Ошибка авторизации ESIA");
            err.setDescription("Воспользуйтесь своими логином и паролем для аутентификации на портале");
            err.setTraceId(spanService.getCurrentTraceId());
            response.getWriter().write(JsonProcessingUtil.toJson(err));
        } catch (IOException e) {
            throw new PguException(e.getMessage(), e);
        }
    }
}
