package net.th_hakoniwa.HakoniwaClanExtension.Event;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.th_hakoniwa.HakoniwaClanExtension.Data.Clan;

public class ClanAddMemberEvent extends Event {
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
	private UUID nm = null;

	public ClanAddMemberEvent(Clan clan, UUID member){
		c = clan;
		nm = member;
	}

	public Clan getClan(){
		return c;
	}

	public UUID getMember(){
		return nm;
	}
}
