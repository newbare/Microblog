package me.molchanoff.microblog.model;

/**
 * Alert object for storing various messages
 */
public class Alert {
    private final AlertType type;
    private final String title;
    private final String message;

    public Alert(AlertType type, String title, String message) {
        this.type = type;
        this.title = title;
        this.message = message;
    }

    public AlertType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
