package ru.fmparty.apiaccess;

public enum Consts {
    ApiPHP("http://dtigran.ru/fmapi/"),
    SQLiteDB("fmparty.db"),
    DbVersion("20");

    Consts(String value) { this.value = value; }

    public String get() {return value; }
    private String value;
}
