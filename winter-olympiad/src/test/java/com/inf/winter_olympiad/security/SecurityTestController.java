package com.inf.winter_olympiad.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SecurityTestController {

    @GetMapping("/api/demo/public/ping")
    String apiPublic() {
        return "ok";
    }

    @GetMapping("/api/demo/admin/ping")
    String apiAdmin() {
        return "ok";
    }

    @GetMapping("/api/demo/athlete/ping")
    String apiAthlete() {
        return "ok";
    }

    @GetMapping("/api/demo/me")
    String apiMe() {
        return "ok";
    }

    @GetMapping("/admin/ping")
    String adminPage() {
        return "ok";
    }

    @GetMapping("/athlete/ping")
    String athletePage() {
        return "ok";
    }
}

