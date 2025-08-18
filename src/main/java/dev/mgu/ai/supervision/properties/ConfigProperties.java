package dev.mgu.ai.supervision.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mgu.ai-supervision")
public class ConfigProperties {

    /**
     * Model of the storage.
     */
    private SupervisionMode mode = SupervisionMode.MEMORY;
    /**
     * URL used to expose/reset counters
     */
    private String controller = "/supervision/tokens";

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    private Schema schema = new Schema();

    public SupervisionMode getMode() {
        return mode;
    }

    public void setMode(SupervisionMode mode) {
        this.mode = mode;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }
}
