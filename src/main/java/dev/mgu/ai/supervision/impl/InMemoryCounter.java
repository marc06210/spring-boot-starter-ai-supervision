package dev.mgu.ai.supervision.impl;

import dev.mgu.ai.supervision.TokenCounter;
import dev.mgu.ai.supervision.TokenCounterService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCounter implements TokenCounterService {

    private final Map<String, TokenCounter> counters = new ConcurrentHashMap<>();

    @Override
    public void incrementTokenCounters(final TokenCounter tokenCounter) {
        counters.merge(tokenCounter.model(),
                tokenCounter,
                (existingValues, newValues) -> {
                    int prompt = existingValues.promptTokens() + newValues.promptTokens();
                    int generation = existingValues.generationTokens() + newValues.generationTokens();
                    return new TokenCounter(existingValues.model(), prompt, generation, prompt + generation);
                });
    }

    @Override
    public List<TokenCounter> getTokenCounters() {
        return new ArrayList<>(this.counters.values());
    }

    @Override
    public void reset() {
        counters.clear();
    }
}
