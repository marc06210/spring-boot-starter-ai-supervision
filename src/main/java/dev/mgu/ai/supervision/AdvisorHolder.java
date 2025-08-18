package dev.mgu.ai.supervision;

import org.springframework.ai.chat.client.advisor.api.Advisor;

public interface AdvisorHolder {
    Advisor getAdvisor();
}
