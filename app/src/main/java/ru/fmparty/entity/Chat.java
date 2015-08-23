package ru.fmparty.entity;

public class Chat {
    private int id;
    private int admin_id;
    private String name;

    public Chat(int id, int admin_id, String name) {
        this.id = id;
        this.admin_id = admin_id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString(){
        return "Chat: id: " + id + "; admin_id: " + admin_id + "; name: " + name;
    }
}
