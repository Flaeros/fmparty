package ru.fmparty.apiaccess;

public enum ResultCode {
    SUCCESS(1), ERROR(0), CHAT_LEFT(2);

    private int resultCode;

    ResultCode(int resultCode){
        this.resultCode = resultCode;
    }

    public int get() {
        return resultCode;
    }
}
