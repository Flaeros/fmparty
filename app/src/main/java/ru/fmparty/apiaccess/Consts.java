package ru.fmparty.apiaccess;

public enum Consts {
    ApiPHP("http://dtigran.ru/fmapi/"),
    SQLiteDB("fmparty.db"),
    DbVersion("18");

    private String value;
    Consts(String value) { this.value = value; }
    public String get() {return value; }
}
