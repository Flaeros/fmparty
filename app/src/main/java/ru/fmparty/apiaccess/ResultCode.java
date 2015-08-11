package ru.fmparty.apiaccess;

public enum ResultCode {
    SUCCESS(1), ERROR(0);

    private int resultCode;

    ResultCode(int resultCode){
        this.resultCode = resultCode;
    }

    public int get() {
        return resultCode;
    }
}
