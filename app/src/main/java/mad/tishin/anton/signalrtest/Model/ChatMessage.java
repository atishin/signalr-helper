package mad.tishin.anton.signalrtest.Model;

/**
 * Created by atish on 20.05.2016.
 */
public class ChatMessage {
    public String message;
    public String author;
    public ChatMessage(String message, String author) {
        this.message = message;
        this.author = author;
    }
    public ChatMessage() {
        this("", "");
    }

    @Override
    public String toString() {
        return author + ": " + message;
    }
}
