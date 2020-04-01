package net.th_hakoniwa.HakoniwaClanExtension.Command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.th_hakoniwa.HakoniwaClanExtension.HakoniwaClanExtension;
import net.th_hakoniwa.HakoniwaClanExtension.Clan.ClanManager;
import net.th_hakoniwa.HakoniwaClanExtension.Data.Clan;
import net.th_hakoniwa.HakoniwaClanExtension.Data.ClanAction;
import net.th_hakoniwa.HakoniwaCore.Core.Command.Sub.Base.HCSubCommandBase;

public class ClanCommand implements HCSubCommandBase {
	//承認コマンド待ちアクション
	private Map<UUID, ClanAction> approvalPending = new HashMap<>();
	//承認コマンドの実行対象
	private Map<UUID, UUID> approvalTarget = new HashMap<>();

	@Override
	public boolean onSubCommand(CommandSender sender, String subCmd, String label, String[] args) {
		//不正呼び出し防止
		if(!subCmd.equalsIgnoreCase("clan")){
			return false;
		}

		if(sender instanceof Player){
			Player p = (Player) sender;
			if(args.length > 0){
				//引数有り
				if(args[0].equalsIgnoreCase("create")){
					//クラン作成コマンド
					if(args.length > 2){
						if(ClanManager.getInstance().getClanFromPlayerUUID(p.getUniqueId()) != null){
							//既に所属している
							p.sendMessage("[§bHCL§f] §cCannot create a clan because you are already in a clan.");
							return false;
						}
						switch(ClanManager.getInstance().createClan(args[1], args[2], p.getUniqueId())){
						case SUCCESS:
							HakoniwaClanExtension.getHCPlugin().getServer().broadcastMessage("[§bHCL§f] クラン \"" + args[1] + "\" が作成されました");
							p.sendMessage("[§bHCL§f] クランの作成が完了しました");
							return true;
						case DUPLICATE_NAME:
							p.sendMessage("[§bHCL§f] §c指定されたクラン名は既に使用されています");
							break;
						case DUPLICATE_TAG:
							p.sendMessage("[§bHCL§f] §c指定されたクランタグは既に使用されています");
							break;
						case LONG_NAME:
							p.sendMessage("[§bHCL§f] §cクラン名が長すぎます §7(最大32文字)");
							break;
						case LONG_TAG:
							p.sendMessage("[§bHCL§f] §cタグ名が長すぎます §7(最大5文字)");
							break;
						case ILLEGAL_CHARACTERS:
							p.sendMessage("[§bHCL§f] §cクラン名に使用できない文字が含まれています §7(半角英数字のみ)");
							break;
						default:
							break;
						}
						p.sendMessage("[§bHCL§f] §cクランの作成に失敗しました");
						return false;
					}
				}else if(args[0].equalsIgnoreCase("disband")){
					//クラン解散コマンド
					if(args.length > 1){
						//管理者用コマンド 権限チェック
						if(p.hasPermission("hakoniwa.admin.clan")){
							//クラン取得
							Clan clan = ClanManager.getInstance().getClanFromName(args[1]);
							if(clan == null){
								//クランが見つからなかった
								p.sendMessage("[§bHCL§f] §c指定されたクランが見つかりません");
								return false;
							}
							//指定クラン解散
							approvalPending.put(p.getUniqueId(), ClanAction.DISBAND);
							approvalTarget.put(p.getUniqueId(), clan.getUniqueId());

							p.sendMessage("[§bHCL§f] クラン名 \"" + clan.getClanName() + "\" を解散しようとしています");
							p.sendMessage("[§bHCL§f] 解散を実行するには以下のコマンドを入力してください");
							p.sendMessage("[§bHCL§f] \"/" + label + " clan accept\"");

							return true;
						}
					}
					//クラン名指定無し or 権限無し 自クラン解散
					Clan clan = ClanManager.getInstance().getClanFromPlayerUUID(p.getUniqueId());

					if(clan == null) p.sendMessage("Clan is null.");

					if(clan == null || !clan.getLeader().equals(p.getUniqueId())){
						//クランに所属していない、またはリーダーではない
						//TODO メッセージマネージャー
						p.sendMessage("[§bHCL§f] §cこのコマンドはクランリーダーのみ実行可能です");
						return false;
					}
					//指定クラン解散
					approvalPending.put(p.getUniqueId(), ClanAction.DISBAND);
					approvalTarget.put(p.getUniqueId(), clan.getUniqueId());

					//メッセージ
					p.sendMessage("[§bHCL§f] クラン名 \"" + clan.getClanName() + "\" を解散しようとしています");
					p.sendMessage("[§bHCL§f] 解散を実行するには以下のコマンドを入力してください");
					p.sendMessage("[§bHCL§f] \"/" + label + " clan accept\"");
					return true;
				}else if(args[0].equalsIgnoreCase("invite")){

				}else if(args[0].equalsIgnoreCase("info")){

				}else if(args[0].equalsIgnoreCase("accept")){
					//承認待ちアクション確認
					if(approvalPending.containsKey(p.getUniqueId()) && approvalTarget.containsKey(p.getUniqueId())){
						//アクション別で処理を実装
						Clan clan = null;
						switch(approvalPending.get(p.getUniqueId())){
						case INVITE:
							//招待承認
							clan = ClanManager.getInstance().getClanFromUUID(approvalTarget.get(p.getUniqueId()));
							clan.addMember(p.getUniqueId());
							clan.save();
							//メッセージ
							p.sendMessage("[§bHCL§f] クラン \"" + clan.getClanName() + "\" へ参加しました");
							//TODO オンラインのクランメンバーへメッセージ送信

							break;
						case CHANGE_LEADER:
							//リーダー変更
							clan = ClanManager.getInstance().getClanFromPlayerUUID(p.getUniqueId());
							clan.setLeader(approvalTarget.get(p.getUniqueId()));
							clan.save();
							//TODO メッセージ
							p.sendMessage("[§bHCL§f] クランリーダーが変更されました");
							break;
						case DISBAND:
							//解散
							ClanManager.getInstance().disbandClan(approvalTarget.get(p.getUniqueId()));
							p.sendMessage("[§bHCL§f] クランを解散しました");
							break;
						default:
							p.sendMessage("[§bHCL§f] §c不明な操作です");
							break;
						}
						//承認待ち情報のmapから処理したデータを削除
						approvalPending.remove(p.getUniqueId());
						approvalTarget.remove(p.getUniqueId());
						//正常終了
						return true;
					}else {
						//承認待ちアクションが無い
						//TODO メッセージマネージャーから取得するように変更
						p.sendMessage("[§bHCL§f] §c承認待ちのアクションがありません");
						return true;
					}
				}else if(args[0].equalsIgnoreCase("deny")){
					//承認待ちアクション確認
					if(approvalPending.containsKey(p.getUniqueId()) && approvalTarget.containsKey(p.getUniqueId())){
						//アクション別で処理を実装
						switch(approvalPending.get(p.getUniqueId())){
						case INVITE:
							//招待拒否
							//TODO 招待した側のクランに拒否通知

							//プレイヤーに拒否通知
							p.sendMessage("[§bHCL§f] クランの招待を拒否しました");
							break;
						case CHANGE_LEADER:
							//リーダー変更キャンセル
							p.sendMessage("[§bHCL§f] クランリーダーの変更をキャンセルしました");
							break;
						case DISBAND:
							//解散キャンセル
							p.sendMessage("[§bHCL§f] クランの解散をキャンセルしました");
							break;
						default:
							p.sendMessage("[§bHCL§f] §c不明な操作です");
							break;
						}
						//承認待ち情報のmapから処理したデータを削除
						approvalPending.remove(p.getUniqueId());
						approvalTarget.remove(p.getUniqueId());
						//正常終了
						return true;
					}else {
						//承認待ちアクションが無い
						//TODO メッセージマネージャーから取得するように変更
						p.sendMessage("[§bHCL§f] §c承認待ちのアクションがありません");
						return true;
					}
				}
			}

			//コマンドヘルプ
			p.sendMessage("[§bHCL§f] ====== §eHakoniwa clan command help§f ======");
			p.sendMessage("[§bHCL§f] /" + label + " clan create [ClanName] [ClanTag] : Create a new clan");						//クラン作成(未所属)
			p.sendMessage("[§bHCL§f] /" + label + " clan disband : Disband your clan (leader only)");							//クラン解散(リーダー)
			if(p.hasPermission("hakoniwa.admin.clan")){
				//管理者向けヘルプ
				p.sendMessage("[§bHCL§f] /" + label + " clan disband [ClanName] : Disband target clan (admin only)");			//クラン解散(管理者)
			}
			p.sendMessage("[§bHCL§f] /" + label + " clan invite [Player] : Send the clan invitation to the target player");		//クラン招待(リーダー)
			p.sendMessage("[§bHCL§f] /" + label + " clan info [ClanName] : Display clan information");							//クラン情報
			p.sendMessage("[§bHCL§f] /" + label + " clan accept : Execute action pending approval");							//承認待ちアクション実行
			p.sendMessage("[§bHCL§f] /" + label + " clan deny : Reject actions awaiting approval");								//承認待ちアクション拒否
		}else {
			sender.sendMessage("[HCL] This command can only be executed from the player.");
			return false;
		}
		return false;
	}
}
