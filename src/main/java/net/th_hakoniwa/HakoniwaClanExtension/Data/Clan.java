package net.th_hakoniwa.HakoniwaClanExtension.Data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import net.th_hakoniwa.HakoniwaClanExtension.Clan.ClanManager;
import net.th_hakoniwa.HakoniwaClanExtension.Event.ClanAddMemberEvent;
import net.th_hakoniwa.HakoniwaClanExtension.Event.ClanLeaderChangeEvent;
import net.th_hakoniwa.HakoniwaClanExtension.Event.ClanNameChangeEvent;
import net.th_hakoniwa.HakoniwaClanExtension.Event.ClanRemoveMemberEvent;
import net.th_hakoniwa.HakoniwaClanExtension.Event.ClanTagChangeEvent;
import net.th_hakoniwa.HakoniwaCore.API.HakoniwaAPI;
import net.th_hakoniwa.HakoniwaCore.Core.Player.HCPlayer;

public class Clan {
	//クランデータ 各種パス
	private final String CLAN_UUID_PATH = "Clan.Info.UUID";
	private final String CLAN_NAME_PATH = "Clan.Info.Name";
	private final String CLAN_TAG_PATH = "Clan.Info.Tag";
	private final String CLAN_LEADER_PATH = "Clan.Info.Leader";
	private final String CLAN_CREATED_PATH = "Clan.Info.CreatedDate";
	private final String CLAN_FF_PATH = "Clan.Info.FF";
	private final String CLAN_COMMENT_PATH = "Clan.Info.Comments";
	private final String CLAN_MEMBER_PATH = "Clan.Members.List";
	//クランデータファイル
	private File clanFile = null;
	private YamlConfiguration clanConf = null;

	private UUID uid, l;									//クランUUID	/ クランリーダーUUID
	private String n, t;									//クラン名		/ クランタグ

	private List<String> cmt = new ArrayList<String>();		//クランコメントリスト
	private List<UUID> m = new ArrayList<UUID>();			//クランメンバーリスト

	private long cd;										//クラン作成日時
	private boolean ff = false;								//FF有効フラグ

	/* コンストラクタ */
	//新規作成
	public Clan(String name, String tag, UUID leader){
		//UUID生成
		uid = UUID.randomUUID();

		//データファイル作成
		clanFile = new File(ClanManager.getInstance().getDataFolder(), uid.toString() + ".yml");
		try {
			clanFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//ファイルロード
		clanConf = YamlConfiguration.loadConfiguration(clanFile);

		//データ設定
		n = name;
		t = tag;
		l = leader;
		cd = System.currentTimeMillis();
		ff = false;
		addMember(leader);

		//データ保存
		save();
	}

	//ロード
	public Clan(File file){
		//ファイル設定
		clanFile = file;
		//ファイルロード
		clanConf = YamlConfiguration.loadConfiguration(clanFile);

		//ロード
		uid = UUID.fromString(clanConf.getString(CLAN_UUID_PATH));
		n = clanConf.getString(CLAN_NAME_PATH);
		t = clanConf.getString(CLAN_TAG_PATH);
		l = UUID.fromString(clanConf.getString(CLAN_LEADER_PATH));
		cd = clanConf.getLong(CLAN_CREATED_PATH);
		ff = clanConf.getBoolean(CLAN_FF_PATH);
		cmt = clanConf.getStringList(CLAN_COMMENT_PATH);
		for(String muid : clanConf.getStringList(CLAN_MEMBER_PATH)){
			m.add(UUID.fromString(muid));
		}
	}

	//クランUUID
	public UUID getUniqueId(){
		return uid;
	}

	//クラン名
	public boolean setClanName(String name){
		//クラン名重複チェック
		if(ClanManager.getInstance().getClanFromName(name.toLowerCase()) != null) return false;
		String old = n;
		n = name;
		//クラン名変更イベント呼び出し
		Bukkit.getPluginManager().callEvent(new ClanNameChangeEvent(this, old, n));
		//正常終了
		return true;
	}
	public String getClanName(){
		return n;
	}

	//クランタグ
	public boolean setClanTag(String tag){
		//クランタグ重複チェック
		if(ClanManager.getInstance().getClanFromTag(tag) != null) return false;
		String old = t;
		t = tag;
		//クランタグ変更イベント呼び出し
		Bukkit.getPluginManager().callEvent(new ClanTagChangeEvent(this, old, t));
		//正常終了
		return true;
	}
	public String getClanTag(){
		return t;
	}

	//コメント
	public void addComment(String comment){
		//追加
		cmt.add(comment);
	}
	public boolean setComment(String comment, int line){
		//置き換え
		if(cmt.size() > line) {
			cmt.set(line, comment);
			return true;
		}else {
			//コメント行指定がおかしい
			return false;
		}
	}
	public boolean insertComment(String comment, int line){
		//位置指定で追加
		if(cmt.size() > line) {
			cmt.add(line, comment);
			return true;
		}else {
			//コメント行指定がおかしい
			return false;
		}
	}
	public List<String> getComments(){
		return new ArrayList<>(cmt);
	}

	//リーダー
	public void setLeader(UUID leader){
		UUID old = l;
		l = leader;
		//クランリーダー変更イベント呼び出し
		Bukkit.getPluginManager().callEvent(new ClanLeaderChangeEvent(this, old, l));
	}
	public UUID getLeader(){
		return l;
	}

	//作成日時
	public long getCreatedDate(){
		return cd;
	}

	//フレンドリファイア設定
	public void setFF(boolean enable){
		ff = enable;
	}
	public boolean getFF(){
		return ff;
	}

	//メンバー
	public boolean addMember(UUID muid){
		//既にこのクランのメンバー
		if(m.contains(muid)) return false;
		//プレイヤー存在チェック
		OfflinePlayer ofp = Bukkit.getOfflinePlayer(muid);
		//プレイヤーが存在しない(取得失敗)
		if(ofp == null) return false;
		//プレイヤーのオンラインフラグ
		boolean online = ofp.isOnline();
		/* クラン所属チェック */
		//オフラインだった場合一時的にデータをロード
		if(!online) HakoniwaAPI.loadPlayer(muid);
		//プレイヤー情報取得
		HCPlayer hcp = HakoniwaAPI.getPlayer(muid);
		//TODO 前のプラグインでなぜか文字列でNULLを入れるようにしていたのでそれのチェック。正式公開時には文字列のNULLにならないように改変すること。
		if(hcp.getDataString(ClanManager.PLAYER_CLAN_PATH) != null && !hcp.getDataString(ClanManager.PLAYER_CLAN_PATH).equalsIgnoreCase("NULL")){
			//既にクランに所属している
			//追加でプレイヤーデータをロードした場合アンロード
			if(!online) HakoniwaAPI.unloadPlayer(muid);
			return false;
		}
		//プレイヤーデータ更新
		hcp.setData(ClanManager.PLAYER_CLAN_PATH, uid.toString());
		//プレイヤーデータ保存
		hcp.save();
		//追加でプレイヤーデータをロードした場合アンロード
		if(!online) HakoniwaAPI.unloadPlayer(muid);
		//リスト更新
		m.add(muid);
		//クラン情報をファイルに保存
		save();
		//クランメンバー追加イベント呼び出し
		Bukkit.getPluginManager().callEvent(new ClanAddMemberEvent(this, muid));
		//正常終了
		return true;
	}
	public boolean removeMember(UUID muid, boolean force){
		//このクランに所属していない、または複数人所属している場合でリーダーを指定した場合
		if(!m.contains(muid) || (muid == l && !force)) return false;
		//プレイヤー存在チェック
		OfflinePlayer ofp = Bukkit.getOfflinePlayer(muid);
		//プレイヤーが存在しない(取得失敗)
		if(ofp == null) return false;
		//プレイヤーのオンラインフラグ
		boolean online = ofp.isOnline();
		/* クラン所属チェック */
		//オフラインだった場合一時的にデータをロード
		if(!online) HakoniwaAPI.loadPlayer(muid);
		//プレイヤー情報取得
		HCPlayer hcp = HakoniwaAPI.getPlayer(muid);
		//プレイヤーデータ更新
		hcp.setData(ClanManager.PLAYER_CLAN_PATH, null);
		//プレイヤーデータ保存
		hcp.save();
		//追加でプレイヤーデータをロードした場合アンロード
		if(!online) HakoniwaAPI.unloadPlayer(muid);
		//リスト更新
		m.remove(muid);
		//クラン情報をファイルに保存
		save();
		//クランメンバー追加イベント呼び出し
		Bukkit.getPluginManager().callEvent(new ClanRemoveMemberEvent(this, muid));
		return true;
	}
	public List<UUID> getMembers(){
		return new ArrayList<>(m);
	}


	public boolean save(){
		//コンフィグへ値を設定
		clanConf.set(CLAN_UUID_PATH, uid.toString());
		clanConf.set(CLAN_NAME_PATH, n);
		clanConf.set(CLAN_TAG_PATH, t);
		clanConf.set(CLAN_LEADER_PATH, l.toString());
		clanConf.set(CLAN_CREATED_PATH, cd);
		clanConf.set(CLAN_FF_PATH, ff);
		clanConf.set(CLAN_COMMENT_PATH, cmt);
		List<String> mlist = new ArrayList<>();
		for(UUID muid : m){
			mlist.add(muid.toString());
		}
		clanConf.set(CLAN_MEMBER_PATH, mlist);

		//ファイルへ出力
		try {
			clanConf.save(clanFile);
		} catch (IOException e) {
			e.printStackTrace();

			Bukkit.getLogger().warning("[HCL] Failed to save the clan data.");
			Bukkit.getLogger().warning("[HCL] During clan data dump...");

			Bukkit.getLogger().info("[HCL]");
			Bukkit.getLogger().info("[HCL] ==== UUID " + uid.toString() + " clan data dump ====");
			Bukkit.getLogger().info(clanConf.toString());
			Bukkit.getLogger().info("[HCL] ==== UUID " + uid.toString() + " clan data dump ====");
			Bukkit.getLogger().info("[HCL]");

			Bukkit.getLogger().warning("[HCL] Output complete.");;
			return false;
		}
		return true;
	}
}
