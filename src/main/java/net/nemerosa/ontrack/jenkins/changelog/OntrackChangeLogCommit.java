package net.nemerosa.ontrack.jenkins.changelog;

public class OntrackChangeLogCommit {

    private final String id;
    private final String shortId;
    private final String author;
    private final String authorEmail;
    private final String timestamp;
    private final String message;
    private final String formattedMessage;
    private final String link;

    public OntrackChangeLogCommit(String id, String shortId, String author, String authorEmail, String timestamp, String message, String formattedMessage, String link) {
        this.id = id;
        this.shortId = shortId;
        this.author = author;
        this.authorEmail = authorEmail;
        this.timestamp = timestamp;
        this.message = message;
        this.formattedMessage = formattedMessage;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public String getShortId() {
        return shortId;
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getFormattedMessage() {
        return formattedMessage;
    }

    public String getLink() {
        return link;
    }
}
