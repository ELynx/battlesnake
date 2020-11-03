package ru.elynx.battlesnake.protocol;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class BattlesnakeInfoDto {
    @NotNull
    @NotEmpty
    private String apiVersion;
    private String author;
    @Pattern(regexp = "#[[:xdigit:]]{6}")
    private String color;
    private String head;
    private String tail;
    private String version;

    public BattlesnakeInfoDto() {
    }

    public BattlesnakeInfoDto(BattlesnakeInfo battlesnakeInfo) {
        this.apiVersion = "1";

        this.author = battlesnakeInfo.getAuthor();
        this.color = battlesnakeInfo.getColor();
        this.head = battlesnakeInfo.getHead();
        this.tail = battlesnakeInfo.getTail();
        this.version = battlesnakeInfo.getVersion();
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
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
