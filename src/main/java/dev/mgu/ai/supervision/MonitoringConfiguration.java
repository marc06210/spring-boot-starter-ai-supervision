package dev.mgu.ai.supervision;

import dev.mgu.ai.supervision.factory.AiSupervisionConfigurationFinalizer;
import dev.mgu.ai.supervision.factory.ChatClientAdvisorInjector;
import dev.mgu.ai.supervision.impl.DataSourceCounter;
import dev.mgu.ai.supervision.impl.InMemoryCounter;
import dev.mgu.ai.supervision.impl.TokenMonitoringAdvisorHolder;
import dev.mgu.ai.supervision.properties.ConfigProperties;
import dev.mgu.ai.supervision.web.TokenCounterController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

/**
 * <p>Entry point of this SpringBoot library.</p>
 * <p>It registers
 * <ul>
 *     <li>the ChatClientAdvisorInjector BeanPostProcessor class</li>
 *     <li>the TokenMonitoringAdvisorHolder bean implementing the AdvisorHolder interface</li>
 *     <li>the TokenCounterController bean</li>
 * </ul></p>
 */
@Configuration
@EnableConfigurationProperties(ConfigProperties.class)
@Import({TokenMonitoringAdvisorHolder.class, TokenCounterController.class, AiSupervisionConfigurationFinalizer.class})
public class MonitoringConfiguration {
    @Bean
    public ChatClientAdvisorInjector chatClientAdvisorInjector(
            org.springframework.beans.factory.ObjectProvider<java.util.List<AdvisorHolder>> advisorHolders
    ) {
        return new ChatClientAdvisorInjector(advisorHolders.getIfAvailable(java.util.List::of));
    }

    @Bean
    @ConditionalOnProperty(name = "mgu.ai-supervision.mode", havingValue = "jdbc")
    public TokenCounterService jdbcCounterService(DataSource dataSource) {
        return new DataSourceCounter(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean(TokenCounterService.class)
    public TokenCounterService tokenCounterService() {
        return new InMemoryCounter();
    }
}
