package net.th_hakoniwa.HakoniwaClanExtension.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.th_hakoniwa.HakoniwaClanExtension.Data.Clan;

public class ClanNameChangeEvent extends Event {
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
	private String on = null, nn = null;

	public ClanNameChangeEvent(Clan clan, String old_name, String new_name){
		c = clan;
		on = old_name;
		nn = new_name;
	}

	public Clan getClan(){
		return c;
	}

	public String getOldName(){
		return on;
	}

	public String getNewName(){
		return nn;
	}
}
