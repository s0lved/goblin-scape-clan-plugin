package com.goblinscape;
import lombok.Data;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;
@Data
public class GoblinScapePlayerData {
    @Getter
    private final String name;
    @Getter
    private final String title;
    @Getter
    private final WorldPoint waypoint;
    @Getter
    private final int world;

    public GoblinScapePlayerData(String name, int x, int y, int plane, String title, int world) {
        this.name = name;
        this.waypoint = new WorldPoint(x, y, plane);
        this.title = title;
        this.world = world;
    }
}
