package net.th_hakoniwa.HakoniwaClanExtension.API;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

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

	public static void sendMessageToClan(UUID uid, String message) {
		if(uid == null) {
			Bukkit.getServer().getLogger().warning("[HCL] UUID cannot be null. (sendMessageToClan)");
			return;
		}
		sendMessageToClan(getClanFromUUID(uid), message);
	}

	public static void sendMessageToClan(Clan clan, String message) {
		if(clan == null) {
			Bukkit.getServer().getLogger().warning("[HCL] Target clan cannot be null. (sendMessageToClan)");
			return;
		}
		if(message == null) {
			Bukkit.getServer().getLogger().warning("[HCL] Message cannot be null. (sendMessageToClan)");
			return;
		}

		for(UUID uid : clan.getMembers()) {
			//参加プレイヤーなら無視
			OfflinePlayer ofp = Bukkit.getOfflinePlayer(uid);
			if(ofp.isOnline()) {
				//オンラインだった メッセージ送信
				Bukkit.getPlayer(uid).sendMessage(message);
			}
		}
	}
}
