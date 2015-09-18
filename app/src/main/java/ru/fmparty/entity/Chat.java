package ru.fmparty.entity;

public class Chat {
    private final int id;
    private final int admin_id;
    private final String name;
    private final String image;
    private final String descr;
    private final String date;
    private final String city;

    private Chat (Builder builder) {
        id = builder.id;
        admin_id = builder.admin_id;
        name = builder.name;
        image = builder.image;
        descr = builder.descr;
        date = builder.date;
        city = builder.city;
    }

    public static class Builder {

        //Mandatory
        private final int id;
        private final int admin_id;
        private final String name;

        //Auxiliary
        private String image;
        private String descr;
        private String date;
        private String city;

        public Builder(int id, int admin_id, String name) {
            this.id = id;
            this.admin_id = admin_id;
            this.name = name;
        }

        public Builder image(String val) { image = val; return this; }
        public Builder descr(String val)  { descr = val;  return this; }
        public Builder date(String val)  { date = val;  return this; }
        public Builder city(String val)  { city = val;  return this; }

        public Chat build() { return new Chat(this); }
    }

    public int getId() { return id; }
    public int getAdmin_id() { return admin_id; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public String getDescr() { return descr; }
    public String getDate() { return date; }
    public String getCity() { return city; }

    @Override
    public String toString(){
        return "Chat: id: " + id + "; admin_id: " + admin_id + "; name: " + name + "; descr: " + descr + "; date: " + date + "; city: " + city ;
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
