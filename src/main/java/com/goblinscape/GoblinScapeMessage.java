package com.goblinscape;
import lombok.Data;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Data
public class GoblinScapeMessage {
    @Getter
    private final String player;
    @Getter
    private final String message;
    @Getter
    private final String type;
    @Getter
    private final Integer timestamp;

    public GoblinScapeMessage(String player, String message, String type, int timestamp) {
        this.player = player;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
    }
}
