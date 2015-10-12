package me.molchanoff.microblog.model;

/**
 * Alert types used by bootstrap alert class
 */
public enum AlertType {
    SUCCESS("alert-success"), INFO("alert-info"), WARNING("alert-warning"), DANGER("alert-danger");

    private final String type;

    private AlertType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
