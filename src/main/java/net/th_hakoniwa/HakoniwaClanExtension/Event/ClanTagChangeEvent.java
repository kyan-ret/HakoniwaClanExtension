package net.th_hakoniwa.HakoniwaClanExtension.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.th_hakoniwa.HakoniwaClanExtension.Data.Clan;

public class ClanTagChangeEvent extends Event {
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

	private Clan c = null;
	private String ot = null, nt = null;

	public ClanTagChangeEvent(Clan clan, String old_tag, String new_tag){
		c = clan;
		ot = old_tag;
		nt = new_tag;
	}

	public Clan getClan(){
		return c;
	}

	public String getOldTag(){
		return ot;
	}

	public String getNewTag(){
		return nt;
	}
}
