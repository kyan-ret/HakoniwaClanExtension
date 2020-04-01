package net.th_hakoniwa.HakoniwaClanExtension.Event;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.th_hakoniwa.HakoniwaClanExtension.Data.Clan;

public class ClanLeaderChangeEvent extends Event {
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
	private UUID ol = null, nl = null;

	public ClanLeaderChangeEvent(Clan clan, UUID old_leader, UUID new_leader){
		c = clan;
		ol = old_leader;
		nl = new_leader;
	}

	public Clan getClan(){
		return c;
	}

	public UUID getOldLeader(){
		return ol;
	}

	public UUID getNewLeader(){
		return nl;
	}
}
