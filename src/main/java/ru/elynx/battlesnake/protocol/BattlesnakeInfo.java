package ru.elynx.battlesnake.protocol;

import javax.validation.constraints.Pattern;

public class BattlesnakeInfo {
    private String author;
    @Pattern(regexp = "#[[:xdigit:]]{6}")
    private String color;
    private String head;
    private String tail;
    private String version;

    public BattlesnakeInfo() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getTail() {
        return tail;
    }

    public void setTail(String tail) {
        this.tail = tail;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
