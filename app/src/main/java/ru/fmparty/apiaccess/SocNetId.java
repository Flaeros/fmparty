package ru.fmparty.apiaccess;

public enum SocNetId {
    VKONTAKTE(1), FACEBOOK(2);

    private int socNetId;

    SocNetId(int socNetId){
        this.socNetId = socNetId;
    }

    public int get() {
        return socNetId;
    }
}
