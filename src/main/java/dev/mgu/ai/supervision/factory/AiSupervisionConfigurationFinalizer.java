package dev.mgu.ai.supervision.factory;

import dev.mgu.ai.supervision.impl.DataSourceCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AiSupervisionConfigurationFinalizer implements ApplicationListener<ApplicationReadyEvent> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ApplicationContext context;

    public AiSupervisionConfigurationFinalizer(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            this.checkDataSourceConfiguration(this.context.getBean(DataSourceCounter.class));
        } catch (NoSuchBeanDefinitionException e) {
            logger.debug("DataSourceCounter bean is not present. Skipping database initialization.");
        }
    }

    private void checkDataSourceConfiguration(DataSourceCounter dataSourceCounterBean) {
        if (this.context.getEnvironment().containsProperty("mgu.ai-supervision.schema.create")) {
            String createSchema = this.context.getEnvironment().getProperty("mgu.ai-supervision.schema.create");
            if ( "true".equals(createSchema)) {
                dataSourceCounterBean.createSchema();
            } else {
                logger.warn("mgu.ai-supervision.schema.create=false");
            }
        } else {
            logger.warn("mgu.ai-supervision.schema.create=false");
        }
    }
}
