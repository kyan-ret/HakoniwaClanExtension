package net.th_hakoniwa.HakoniwaClanExtension.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.th_hakoniwa.HakoniwaClanExtension.Data.Clan;

public class ClanCreateEvent extends Event {
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

	public ClanCreateEvent(Clan clan){
		c = clan;
	}

	public Clan getClan(){
		return c;
	}
}
