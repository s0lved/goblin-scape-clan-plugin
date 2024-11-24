package com.goblinscape;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("goblinscape")
public interface GoblinScapeConfig extends Config
{
	@ConfigItem(
		keyName = "APIUrl",
		name = "API URL",
		position = 1,
		secret = true,
		description = "Enter the API URL for the plugin. Message Solved or an Admin for this"
	)
	default String getEndpoint()
	{
		return "";
	}
	@ConfigItem(
			keyName = "sharedKey",
			name = "API Shared Key",
			position = 2,
			secret = true,
			description = "Enter the API Shared Key for the plugin. Message Solved or an Admin for this"
	)
	default String sharedKey()
	{
		return "";
	}

	@ConfigItem(
			keyName = "filterWilderness",
			name = "Send wilderness location",
			position = 3,
			description = "Sends your location if you are in the Wilderness"
	)
	default boolean filterWilderness() {return false; }

}
