package dev.mgu.ai.supervision.impl;

import dev.mgu.ai.supervision.AdvisorHolder;
import dev.mgu.ai.supervision.TokenCounterService;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.stereotype.Component;

/**
 * Bean implementing the AdviceHolder interface
 */
@Component
public class TokenMonitoringAdvisorHolder implements AdvisorHolder {
    private final TokenCounterService tokenCounterService;

    public TokenMonitoringAdvisorHolder(TokenCounterService tokenCounterService) {
        this.tokenCounterService = tokenCounterService;
    }

    @Override
    public Advisor getAdvisor() {
        return new TokenCountingAdvisor(this.tokenCounterService);
    }
}
