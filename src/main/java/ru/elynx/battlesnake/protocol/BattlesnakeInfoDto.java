package ru.elynx.battlesnake.protocol;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class BattlesnakeInfoDto {
    @NotNull
    @NotEmpty
    private String apiversion;
    private String author;

    @Pattern(regexp = "#[\\p{XDigit}]{6}")
    private String color;

    private String head;
    private String tail;
    private String version;

    public BattlesnakeInfoDto() {
    }

    public BattlesnakeInfoDto(String author, String color, String head, String tail, String version) {
        this.apiversion = "1";

        this.author = author;
        this.color = color;
        this.head = head;
        this.tail = tail;
        this.version = version;
    }

    public BattlesnakeInfoDto(BattlesnakeInfo battlesnakeInfo) {
        this.apiversion = "1";

        this.author = battlesnakeInfo.getAuthor();
        this.color = battlesnakeInfo.getColor();
        this.head = battlesnakeInfo.getHead();
        this.tail = battlesnakeInfo.getTail();
        this.version = battlesnakeInfo.getVersion();
    }

    public String getApiversion() {
        return apiversion;
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
