package tn.esprit.tahwissa.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatMessage {
    public enum Sender { USER, ASSISTANT }

    private final Sender sender;
    private final String content;
    private final LocalDateTime timestamp;

    public ChatMessage(Sender sender, String content) {
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public Sender getSender() { return sender; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public String getFormattedTime() {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
