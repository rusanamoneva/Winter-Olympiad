package com.inf.winter_olympiad.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.web.util.matcher.RegexRequestMatcher.regexMatcher;

class SecurityConfigRulesTest {

    private final RequestMatcher publicMatcher = regexMatcher("^/api/.+/public/.*$");
    private final RequestMatcher adminMatcher = regexMatcher("^/api/.+/admin/.*$");
    private final RequestMatcher athleteMatcher = regexMatcher("^/api/.+/athlete/.*$");
    private final RequestMatcher meMatcher = regexMatcher("^/api/.+/me$");

    @Test
    void publicPatternMatchesExpectedPaths() {
        assertTrue(publicMatcher.matches(request("/api/competitions/public/list")));
        assertTrue(publicMatcher.matches(request("/api/slalom/public/1/ranking")));
        assertFalse(publicMatcher.matches(request("/api/slalom/admin/1/ranking")));
    }

    @Test
    void adminPatternMatchesExpectedPaths() {
        assertTrue(adminMatcher.matches(request("/api/biathlon/admin/42/results")));
        assertTrue(adminMatcher.matches(request("/api/competitions/admin/slalom")));
        assertFalse(adminMatcher.matches(request("/api/biathlon/public/42/ranking")));
    }

    @Test
    void athletePatternMatchesExpectedPaths() {
        assertTrue(athleteMatcher.matches(request("/api/registrations/athlete/15")));
        assertTrue(athleteMatcher.matches(request("/api/athletes/athlete/history")));
        assertFalse(athleteMatcher.matches(request("/api/registrations/admin/15")));
    }

    @Test
    void mePatternMatchesExpectedPaths() {
        assertTrue(meMatcher.matches(request("/api/auth/me")));
        assertTrue(meMatcher.matches(request("/api/athletes/me")));
        assertTrue(meMatcher.matches(request("/api/auth/public/me")));
        assertFalse(meMatcher.matches(request("/api/auth/me/extra")));
    }

    private MockHttpServletRequest request(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        request.setServletPath(uri);
        return request;
    }
}



