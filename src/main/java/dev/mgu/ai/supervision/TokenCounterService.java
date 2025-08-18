package dev.mgu.ai.supervision;

import java.util.List;

public interface TokenCounterService {
    void incrementTokenCounters(TokenCounter tokenCounter);

    List<TokenCounter> getTokenCounters();

    void reset();
}
