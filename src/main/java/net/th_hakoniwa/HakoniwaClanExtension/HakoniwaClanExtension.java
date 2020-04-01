package net.th_hakoniwa.HakoniwaClanExtension;

import java.io.File;

import org.bukkit.plugin.Plugin;

import net.th_hakoniwa.HakoniwaClanExtension.Clan.ClanManager;
import net.th_hakoniwa.HakoniwaClanExtension.Command.ClanCommand;
import net.th_hakoniwa.HakoniwaCore.Extension.HakoniwaExtension;

public class HakoniwaClanExtension extends HakoniwaExtension {
	private static File dataFolder = null;
	private static Plugin plugin = null;

	@Override
	public void onEnable(){
		dataFolder = getDataFolder();
		plugin = getPlugin();

		//クラン初期化
		ClanManager.getInstance().initialize();
		//サブコマンド登録
		this.registerSubCommand("clan", new ClanCommand(), "Hakoniwa clan manage command");
	}

	@Override
	public void onDisable(){
		//クラン終了
		ClanManager.getInstance().finalize();
	}

	public static File getExDataFolder(){
		return dataFolder;
	}

	public static Plugin getHCPlugin(){
		return plugin;
	}
}
