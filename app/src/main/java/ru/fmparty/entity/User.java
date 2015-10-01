package ru.fmparty.entity;

public class User {
    private final int id;
    private final int socNetId;
    private final long socUserId;
    private final String name;
    private String image;

    public User(int id, int socNetId, long socUserId, String name, String image) {
        this.id = id;
        this.socNetId = socNetId;
        this.socUserId = socUserId;
        this.name = name;
        this.image = image;
    }

    public int getId() { return id; }
    public int getSocNetId() { return socNetId; }
    public long getSocUserId() { return socUserId; }
    public String getName() { return name; }
    public String getImage() { return image; }

    public String setImage() { return image; }

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
        User other = (User) obj;
        if (id != other.getId())
            return false;
        if (socNetId != other.getSocNetId())
            return false;
        if (name != other.getName())
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "{User: id: " + id + "; socNetId: " + socNetId + "; socUserId: " + socUserId + "; name: " + name + "; image: " + image + "}";
    }
}
