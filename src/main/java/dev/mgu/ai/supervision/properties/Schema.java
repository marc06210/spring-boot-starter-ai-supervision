package dev.mgu.ai.supervision.properties;

public class Schema {
    /**
     * Defines it the table schema is automatically created if needed
     */
    private boolean create = false;

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }
}
