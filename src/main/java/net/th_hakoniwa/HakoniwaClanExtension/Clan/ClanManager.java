package net.th_hakoniwa.HakoniwaClanExtension.Clan;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.th_hakoniwa.HakoniwaClanExtension.HakoniwaClanExtension;
import net.th_hakoniwa.HakoniwaClanExtension.Data.Clan;
import net.th_hakoniwa.HakoniwaClanExtension.Data.ClanCreateResult;
import net.th_hakoniwa.HakoniwaClanExtension.Event.ClanCreateEvent;
import net.th_hakoniwa.HakoniwaClanExtension.Event.ClanDisbandEvent;
import net.th_hakoniwa.HakoniwaClanExtension.Event.ClanNameChangeEvent;
import net.th_hakoniwa.HakoniwaClanExtension.Event.ClanTagChangeEvent;
import net.th_hakoniwa.HakoniwaCore.API.HakoniwaAPI;
import net.th_hakoniwa.HakoniwaCore.Core.Player.HCPlayer;

public class ClanManager implements Listener {
	//定数 プレイヤーデータ クランパス
	private final String PLAYER_CLAN_PATH = "Data.Info.Clan";

	//変数
	private static ClanManager instance = null;
	private boolean init = false;
	//ディレクトリ
	private File dataDir = null;

	//クラン一覧
	private Map<UUID, Clan> clanList = new HashMap<>();
	private Map<String, UUID> clanNameList = new HashMap<>();
	private Map<String, UUID> clanTagList = new HashMap<>();


	private ClanManager(){
		//イベントリスナー登録(クラン名、クランタグの更新検知用)
		Bukkit.getPluginManager().registerEvents(this, HakoniwaClanExtension.getHCPlugin());
	}

	public static ClanManager getInstance(){
		if(instance == null){
			instance = new ClanManager();
		}
		return instance;
	}

	public File getDataFolder(){
		return dataDir;
	}

	public void initialize(){
		//初期化されていたら実行しない
		if(init) return;
		//開始ログ
		Bukkit.getLogger().info("[HCL] Initializing clan manager...");
		//処理時間計測用
		long start = System.currentTimeMillis();
		//
		Bukkit.getLogger().info("[HCL] DataFolder: " + HakoniwaClanExtension.getExDataFolder().getPath());
		//ディレクトリチェック
		dataDir = new File(HakoniwaClanExtension.getExDataFolder(), "ClanData");
		if(!dataDir.exists()){
			dataDir.mkdir();
		}
		//クランロード
		Bukkit.getLogger().info("[HCL] Loading clan files...");
		//ロード数カウント
		int count = 0;
		//クランファイルロード
		for(File file : dataDir.listFiles()){
			try{
				//ロード処理
				Clan clan = new Clan(file);
				//各種mapに配置
				clanList.put(clan.getUniqueId(), clan);
				clanNameList.put(clan.getClanName().toLowerCase(), clan.getUniqueId());
				clanTagList.put(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', clan.getClanTag().toLowerCase())), clan.getUniqueId());
				//カウント加算
				count++;
				//ロード完了ログ
				Bukkit.getLogger().info("[HCL] Clan loaded: " + clan.getUniqueId().toString() + " (Name: " + clan.getClanName() + ")");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		//クランロード完了
		Bukkit.getLogger().info("[HCL] Clan files has been loaded.");
		Bukkit.getLogger().info("[HCL] " + count + " clans loaded.");

		//処理時間計測用
		long end = System.currentTimeMillis();
		//完了ログ
		Bukkit.getLogger().info("[HCL] Clan manager initialized. (" + (end - start) + "ms)");

		init = true;
	}

	public void finalize(){
		//開始ログ
		Bukkit.getLogger().info("[HCL] Unloading clan data...");
		//処理時間計測用
		long start = System.currentTimeMillis();
		//アンロード
		for(UUID cuid : clanList.keySet()){
			//クラン取得
			Clan clan = clanList.get(cuid);
			//保存
			clan.save();
			//各種mapから削除
			clanList.remove(cuid);
			clanNameList.remove(cuid);
			clanTagList.remove(cuid);
			//Clanにnullをセット
			clan = null;
			//アンロード完了ログ
			Bukkit.getLogger().info("[HCL] Clan unloaded: " + cuid.toString());
		}
		//処理時間計測用
		long end = System.currentTimeMillis();
		//完了ログ
		Bukkit.getLogger().info("[HCL] Clan data unloaded. (" + (end - start) + "ms)");
	}


	//クラン作成
	public ClanCreateResult createClan(String name, String tag, UUID leader){
		//リーダーnullチェック
		if(leader == null) return ClanCreateResult.NULL_LEADER;
		//クラン名重複チェック
		if(getClanFromName(name) != null) return ClanCreateResult.DUPLICATE_NAME;
		//クランタグ重複チェック
		if(getClanFromTag(tag) != null) return ClanCreateResult.DUPLICATE_TAG;

		//クラン名制限チェック
		if(name.length() > 32){
			//32文字まで 文字数オーバー
			return ClanCreateResult.LONG_NAME;
		}
		//クランタグ制限チェック
		String rTag = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', tag.toLowerCase()));
		//文字数
		if(rTag.length() > 5){
			//5文字まで 文字数オーバー
			return ClanCreateResult.LONG_TAG;
		}
		//文字種
		if(!Pattern.matches("^[0-9a-zA-Z]+$", rTag)){
			//英数字のみ 使えない文字
			return ClanCreateResult.ILLEGAL_CHARACTERS;
		}

		//クランインスタンス生成
		Clan clan = new Clan(name, tag, leader);
		//各種mapへ配置
		clanList.put(clan.getUniqueId(), clan);
		clanNameList.put(clan.getClanName().toLowerCase(), clan.getUniqueId());
		clanTagList.put(rTag, clan.getUniqueId());
		//クラン作成イベント呼び出し
		Bukkit.getServer().getPluginManager().callEvent(new ClanCreateEvent(clan));
		//正常終了
		return ClanCreateResult.SUCCESS;
	}

	//クラン解散
	public boolean disbandClan(UUID uid){
		//TODO 実装
		//クラン存在チェック
		Clan clan = getClanFromUUID(uid);
		if(clan == null) return false;
		//全プレイヤーをクランから脱退させる
		for(UUID puid : clan.getMembers()){
			clan.removeMember(puid, true);
		}
		//登録解除用にカラコ無しのタグを取得
		String rTag = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', clan.getClanTag().toLowerCase()));
		//登録削除
		clanList.remove(clan.getUniqueId());
		clanNameList.remove(clan.getClanName().toLowerCase());
		clanTagList.remove(rTag);

		//クラン解散イベント呼び出し
		Bukkit.getServer().getPluginManager().callEvent(new ClanDisbandEvent(clan.getUniqueId(), clan.getClanName(), clan.getClanTag()));
		//クランをnullに
		clan = null;
		//正常終了
		return true;
	}

	//クランデータをUUIDから取得
	public Clan getClanFromUUID(UUID uid){
		return clanList.containsKey(uid) ? clanList.get(uid) : null;
	}
	//クランデータをクラン名から取得
	public Clan getClanFromName(String name){
		return clanNameList.containsKey(name.toLowerCase()) ? getClanFromUUID(clanNameList.get(name.toLowerCase())) : null;
	}
	//クランデータをクランタグから取得
	public Clan getClanFromTag(String tag){
		String rTag = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', tag.toLowerCase()));
		return clanTagList.containsKey(rTag) ? getClanFromUUID(clanNameList.get(rTag)) : null;
	}
	//クランデータをプレイヤーUUIDから取得
	public Clan getClanFromPlayerUUID(UUID uid){
		//プレイヤー取得
		OfflinePlayer ofp = Bukkit.getOfflinePlayer(uid);
		//nullチェック
		if(ofp == null) return null;
		//オンラインフラグ
		boolean online = ofp.isOnline();
		//オフラインなら追加ロード
		if(!online) HakoniwaAPI.loadPlayer(uid);
		//プレイヤー情報取得
		HCPlayer hcp = HakoniwaAPI.getPlayer(uid);
		if(hcp.getDataString(PLAYER_CLAN_PATH) == null){
			//クランに所属していない
			//オフラインならアンロード
			if(!online) HakoniwaAPI.unloadPlayer(uid);
			return null;
		}
		//クラン取得
		Clan clan = null;
		UUID cuid = null;
		try{
			cuid = UUID.fromString(hcp.getDataString(PLAYER_CLAN_PATH));
		}catch (Exception e){
			//UUIDじゃなかった
			return null;
		}
		//nullチェック
		if(cuid != null){
			clan = getClanFromUUID(cuid);
		}
		//オフラインならアンロード
		if(!online) HakoniwaAPI.unloadPlayer(uid);
		//取得した情報を返す
		return clan;
	}


	/* イベントリスナー ここから */
	@EventHandler
	public void onClanNameChange(ClanNameChangeEvent e){
		//クラン名リスト更新
		clanNameList.put(e.getNewName(), e.getClan().getUniqueId());
	}

	@EventHandler
	public void onClanTagChange(ClanTagChangeEvent e){
		//クランタグリスト更新
		clanTagList.put(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', e.getNewTag().toLowerCase())), e.getClan().getUniqueId());
	}
}
