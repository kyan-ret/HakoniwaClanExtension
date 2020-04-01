package net.th_hakoniwa.HakoniwaClanExtension.API;

import java.util.UUID;

import net.th_hakoniwa.HakoniwaClanExtension.Clan.ClanManager;
import net.th_hakoniwa.HakoniwaClanExtension.Data.Clan;

public class HakoniwaClanAPI {
	public static Clan getClanFromUUID(UUID uid){
		return ClanManager.getInstance().getClanFromUUID(uid);
	}

	public static Clan getClanFromName(String name){
		return ClanManager.getInstance().getClanFromName(name);
	}

	public static Clan getClanFromTag(String tag){
		return ClanManager.getInstance().getClanFromTag(tag);
	}

	public static Clan getClanFromPlayerUUID(UUID uid){
		return ClanManager.getInstance().getClanFromPlayerUUID(uid);
	}
}
