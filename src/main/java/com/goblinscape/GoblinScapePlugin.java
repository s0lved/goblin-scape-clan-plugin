package com.goblinscape;

import com.google.inject.Provides;
import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.clan.ClanMember;
import net.runelite.api.clan.ClanSettings;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.worldmap.WorldMap;
import net.runelite.api.worldmap.WorldMapData;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.ChatMessageType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

import java.util.ArrayList;


@PluginDescriptor(
	name = "Goblin Scape Clan Plugin"
)
@Slf4j
public class GoblinScapePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	@Getter
	private ClientThread clientThread;

	@Inject
	private GoblinScapeAPI api;

	@Inject
	private GoblinScapeConfig config;

	@Getter
	@Setter
	private String playerName;

	@Getter
	@Setter
	private int playerWorld;

	@Getter
	@Setter
	private String playerTitle;

	@Getter
	@Setter
	private ArrayList<GoblinScapePlayerData> PlayerData = new ArrayList<>();



	@Getter
	@Setter
	private boolean postError = false;

	@Setter
	private boolean msgError = false;

	@Getter
	@Setter
	private boolean getError = false;

	@Provides
	GoblinScapeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GoblinScapeConfig.class);
	}


	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.CLAN_MESSAGE)
		{
			String player = extractUsername(chatMessage.getMessage());
			String type = getType(chatMessage.getMessage());
			String message = chatMessage.getMessage();
			int timestamp = chatMessage.getTimestamp();
			GoblinScapeMessage m = new GoblinScapeMessage(player, message, type, timestamp);
			api.makeMessageRequest(m);
		}
	}

	private String extractUsername(String message) {
		String[] patterns = {
				"^(.+?) has reached ",
				"^(.+?) received a new collection log item:",
				"^(.+?) received a drop:",
				"^(.+?) received special loot from a raid:",
				"^(.+?) received a clue item:",
				"^(.+?) has a funny feeling like",
				"^(.+?) feels something weird sneaking",
				"^(.+?) feels like",
				"^(.+?) has achieved a new ",
				"^(.+?) has completed a quest:",
				"^(.+?) has completed the",
				"^(.+?) has unlocked the",
				"^(.+?) has deposited",
				"^(.+?) has withdrawn",
				"^(.+?) has defeated ",
				"^(.+?) has been defeated ",
				"^(.+?) has left the clan",
				"^(.+?) has been invited into the clan"
		};

		for (String regex : patterns) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(message);
			if (matcher.find()) {
				return matcher.group(1).trim();
			}
		}

		return null;
	}

	private String getType(String message) {
		if (message.contains("has reached ") && (message.contains("level") || message.contains("XP")))
		{
			return "Level";
		}
		else if (message.contains("received a new collection log item"))
		{
			return "Collection_Log";
		}
		else if (message.contains("received a drop:"))
		{
			return "Loot";
		}
		else if (message.contains("received special loot from a raid:"))
		{
			return "Loot";
		}
		else if (message.contains("received a clue item:"))
		{
			return "Clue";
		}
		else if (message.contains("has a funny feeling like") || message.contains("feels something weird sneaking") || message.contains("acquired something special:"))
		{
			return "Pet";
		}
		else if (message.contains("has completed a quest:"))
		{
			return "Quest";
		}
		else if (message.contains("has completed the") && message.contains("diary"))
		{
			return "Diary";
		}
		else if (message.contains("tier of rewards from Combat Achievements!") || (message.contains("has completed") && message.contains("combat task")))
		{
			return "Combat_Task";
		}
		else if (message.contains("personal best:"))
		{
			return "Personal_Best";
		}
		else if (message.contains("has defeated") || message.contains("has been defeated"))
		{
			return "PK";
		}
		else if (message.contains("into the coffer") || message.contains("from the coffer") || message.contains("has left the clan") || message.contains("has been invited into the clan"))
		{
			return "Clan";
		}
		else {
			return "Other";
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick) {
		if (isValidURL(config.getEndpoint())) {
			if (wildernessChecker()) {
			playerTitle = getTitle();
				Player player = client.getLocalPlayer();
				LocalPoint localPoint = player.getLocalLocation();
				WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, localPoint);
			GoblinScapePlayerData p = new GoblinScapePlayerData(playerName, worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane(), playerTitle, playerWorld);
			api.makePostRequest(p);
		       }
			}


		}

public boolean isValidURL(String url)
{
	String regex = "((http|https)://)(www.)?"
			+ "[a-zA-Z0-9@:%._\\-\\+~#?&//=]"
			+ "{2,256}(\\.|\\:)[a-z0-9]"
			+ "{2,6}\\b([-a-zA-Z0-9@:%"
			+ "._\\+~#?&//=]*)";

	Pattern p = Pattern.compile(regex);

	if (url == null) {
		return false;
	}

	Matcher m = p.matcher(url);

	//log.info(url + " matches regex: " + String.valueOf(m.matches()));
	return m.matches();
}
	public String getGetEndpoint()
	{
		return config.getEndpoint();
	}

	public String getPostEndpoint()
	{
		String url = config.getEndpoint();
		//redirect for members that have the old URL in their config
		if (url.contains("goblin-scape-2cb7a2ad634e"))
		{
			url = "https://app.goblinscape.net/api/v1/";
			return url + "post";
		}
		if (url.substring(url.length() - 1).equals("/"))
		{
			return url + "post";
		}
		return url + "/post";
	}

	public String getMessageEndpoint()
	{
		String url = config.getEndpoint();
		//redirect for members that have the old URL in their config
		if (url.contains("goblin-scape-2cb7a2ad634e"))
		{
			url = "https://app.goblinscape.net/api/v1/";
			return url + "message";
		}
		if (url.substring(url.length() - 1).equals("/"))
		{
			return url + "message";
		}
		return url + "/message";
	}

	public String getSharedKey()
	{
		return config.sharedKey();
	}
private String getTitle()
{
	ClanSettings clanSettings = client.getClanSettings();
	if (clanSettings == null) {
		return "";
	}
	ClanMember member = clanSettings.findMember(playerName);
	if (member == null) {
		return "";
	}

	return clanSettings.titleForRank(member.getRank()).getName();
}

	public boolean wildernessChecker()
	{
		if (config.filterWilderness())
		{
			return true;
		}
		else return client.getVarbitValue(Varbits.IN_WILDERNESS) == 0;
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
		clientThread.invokeLater(() ->
		{
			playerName = client.getLocalPlayer().getName();
			if (playerName == null)
			{
				return false;
			}
			playerWorld = client.getWorld();

			return true;
		});
	}

	}


}
