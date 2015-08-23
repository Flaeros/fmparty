package ru.fmparty.entity;

public class Message {
    private long id;
    private int chat_id;
    private long user_id;
    private String text;

    public Message(long id, int chat_id, long user_id, String text) {
        this.id = id;
        this.chat_id = chat_id;
        this.user_id = user_id;
        this.text = text;
    }

    public String getText() { return text; }

    public long getUser_id() { return user_id; }

    public int getChat_id() { return chat_id; }

    public long getId() { return id; }

    @Override
    public String toString(){
        return "Message: id: " + id + "; chat_id: " + chat_id + "; user_id: " + user_id + "; text: " + text;
    }
}
