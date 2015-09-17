package ru.fmparty.entity;

public class Chat {
    private int id;
    private int admin_id;
    private String name;
    private String image;

    public Chat(int id, int admin_id, String name, String image) {
        this.id = id;
        this.admin_id = admin_id;
        this.name = name;
        this.image = image;
    }

    public int getId() { return id; }
    public int getAdmin_id() { return admin_id; }
    public String getName() { return name; }
    public String getImage() { return image; }

    @Override
    public String toString(){
        return "Chat: id: " + id + "; admin_id: " + admin_id + "; name: " + name;
    }

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
        Chat other = (Chat) obj;
        if (id != other.getId())
            return false;
        if (admin_id != other.getAdmin_id())
            return false;
        if (name != other.getName())
            return false;
        return true;
    }
}
