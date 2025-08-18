package dev.mgu.ai.supervision.factory;

import dev.mgu.ai.supervision.AdvisorHolder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.List;

/**
 * This BeanPostProcessor implementation will inject all Advisor implementation exposed through
 * an AdvisorHolder bean into the ChatClient.Builder beans.
 */
public class ChatClientAdvisorInjector implements BeanPostProcessor {

    private final List<Advisor> advisors;

    public ChatClientAdvisorInjector(List<AdvisorHolder> advisorHolders) {
        this.advisors = advisorHolders.stream().map(AdvisorHolder::getAdvisor).toList();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ChatClient.Builder chatClientBuilder) {
            return chatClientBuilder
                    .defaultAdvisors(advisors.toArray(new Advisor[0]));
        } else if (bean instanceof ChatClient chatClient) {
            return chatClient.mutate().defaultAdvisors(advisors.toArray(new Advisor[0])).build();
        }
        return bean;
    }
}
