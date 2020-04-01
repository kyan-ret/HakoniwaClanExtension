package net.th_hakoniwa.HakoniwaClanExtension.Data;

public enum ClanCreateResult {
	SUCCESS,				//成功
	NULL_LEADER,			//リーダーが指定されていない
	DUPLICATE_NAME,			//クラン名重複
	DUPLICATE_TAG,			//タグ重複
	LONG_NAME,				//クラン名文字数オーバー
	LONG_TAG,				//クランタグ文字数オーバー
	ILLEGAL_CHARACTERS,		//使用出来ない文字が含まれている
	;
}
