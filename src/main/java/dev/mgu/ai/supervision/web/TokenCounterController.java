package dev.mgu.ai.supervision.web;

import dev.mgu.ai.supervision.TokenCounter;
import dev.mgu.ai.supervision.TokenCounterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.DeleteExchange;

import java.util.List;

@RestController
public class TokenCounterController {

    private final TokenCounterService tokenCounterService;

    public TokenCounterController(TokenCounterService tokenCounterService) {
        this.tokenCounterService = tokenCounterService;
    }

    @GetMapping("${mgu.ai-supervision.controller:/supervision/tokens}")
    public List<TokenCounter> getTokenCounters() {
        return this.tokenCounterService.getTokenCounters();
    }



    @DeleteExchange ("${mgu.ai-supervision.controller:/supervision/tokens}")
    public String resetCounters() {
        this.tokenCounterService.reset();
        return "Spring-ai-supervision counter reset";
    }
}
