package net.th_hakoniwa.HakoniwaClanExtension.Event;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClanDisbandEvent extends Event {
	//定型文 ここから
	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
    //定型文 ここまで

	private UUID uid = null;
	private String n = null;
	private String t = null;

	public ClanDisbandEvent(UUID uuid, String name, String tag){
		uid = uuid;
		n = name;
		t = tag;
	}

	public UUID getClanUUID(){
		return uid;
	}

	public String getClanName(){
		return n;
	}

	public String getClanTag(){
		return t;
	}
}
