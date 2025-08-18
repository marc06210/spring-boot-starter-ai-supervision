package dev.mgu.ai.supervision.impl;

import dev.mgu.ai.supervision.TokenCounter;
import dev.mgu.ai.supervision.TokenCounterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;

/**
 * CallAdvisor that count token for each ChatClientRequest in success
 */
public class TokenCountingAdvisor implements CallAdvisor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TokenCounterService tokenCounterService;

    public TokenCountingAdvisor(TokenCounterService tokenCounterService) {
        logger.info("Creating TokenCountingAdvisor");
        logger.debug("TokenCounterService is of type: {}", tokenCounterService.getClass().getSimpleName());
        this.tokenCounterService = tokenCounterService;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        logger.debug("Calling TokenCountingAdvisor");
        ChatClientResponse response = chain.nextCall(request);

        if (response.chatResponse() != null) {
            ChatResponseMetadata metadata = response.chatResponse().getMetadata();
            // Access the metadata after the call is complete
            Usage usage = metadata.getUsage();
            if (usage != null) {
                try {
                    TokenCounter tokenCounter = new TokenCounter(
                            metadata.getModel(),
                            usage.getPromptTokens(),
                            usage.getCompletionTokens(),
                            usage.getTotalTokens());
                    logger.debug("Adding counter data: {}", tokenCounter);
                    tokenCounterService.incrementTokenCounters(tokenCounter);
                } catch (Exception e) {
                    logger.error("Error while token counting", e);
                }
            }
        }
        return response;
    }

    @Override
    public String getName() {
        return "Mgu_" + this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}