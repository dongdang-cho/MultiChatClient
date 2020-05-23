package dongdang.homework.MultiChatClient.model.dto;


public class ChatBubbleDTO implements Cloneable {
    private String sender;
    private String content;
    private int align;
    private String type;
    private int backgroundColor;

    public ChatBubbleDTO() {
    }

    public ChatBubbleDTO(int align, String type, int backgroundColor) {
        this.align = align;
        this.type = type;
        this.backgroundColor = backgroundColor;
    }

    public ChatBubbleDTO(String sender, String content, int align, String type, int backgroundColor) {
        this.sender = sender;
        this.content = content;
        this.align = align;
        this.type = type;
        this.backgroundColor = backgroundColor;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public String toString() {
        return "ChatBubbleDTO{" +
                "sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", align=" + align +
                ", type='" + type + '\'' +
                ", backgroundColor=" + backgroundColor +
                '}';
    }


    public ChatBubbleDTO clone() throws CloneNotSupportedException {
        return (ChatBubbleDTO)super.clone();
    }
}
