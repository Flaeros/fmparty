package ru.fmparty.entity;

public class Message implements Comparable {
    private long id;
    private int chatId;
    private int userId;
    private String userName;
    private String text;

    public Message(long id, int chatId, int userId, String userName, String text) {
        this.id = id;
        this.chatId = chatId;
        this.userId = userId;
        this.userName = userName;
        this.text = text;
    }

    public String getText() { return text; }
    public int getUserId() { return userId; }
    public int getChatId() { return chatId; }
    public String getUserName() { return userName; }
    public long getId() { return id; }

    @Override
    public String toString(){ return "{Message: id: " + id + "; chatId: " + chatId + "; userId: " + userId + "; text: " + text + "}"; }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Long.valueOf(id).hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Message other = (Message) obj;
        if (id != other.getId())
            return false;
        if (chatId != other.getChatId())
            return false;
        if (userId != other.getUserId())
            return false;
        if (!userName.equals(other.getUserName()))
            return false;
        if (!text.equals(other.getText()))
            return false;
        return true;
    }

    @Override
    public int compareTo(Object another) {
        Message msg = (Message) another;
        if(this.getId() == msg.getId())
            return 0;
        if(this.getId() > msg.getId())
            return 1;
        else
            return -1;
    }
}
