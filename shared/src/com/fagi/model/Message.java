
import java.io.Serializable;

public abstract class Message<E> implements Serializable {
    /**
     * The message sender
     */
    private final String sender;
    /**
     * The conversation
     */
    private final long conversationID;
    /**
     * TODO: Deprecated?
     */
    private final boolean systemMessage;

    public Message(String sender, long conversationID) {
        this.sender = sender;
        this.conversationID = conversationID;
        systemMessage = false;
    }

    public String getSender() {
        return sender;
    }

    public long getConversationID() {
        return conversationID;
    }

    public boolean isSystemMessage() {
        return systemMessage;
    }

    public abstract E getData();
}