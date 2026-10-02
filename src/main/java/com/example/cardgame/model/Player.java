package com.example.cardgame.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String id;
    private int hp = 20;            // プレイヤーの初期生命力
    private int maxPp = 0;          // 最大PP（プレイポイント）
    private int currentPp = 0;      // 現在のPP

    // カード管理用リスト
    private List<Card> deck = new ArrayList<>();   // 山札
    private List<Card> hand = new ArrayList<>();   // 手札
    private List<Card> board = new ArrayList<>();  // 盤面（場）

    // 引数なしコンストラクタ（フレームワーク用）
    public Player() {}

    // ID指定コンストラクタ
    public Player(String id) {
        this.id = id;
    }

    // --- ゲーム進行用便利メソッド ---

    /**
     * 山札の先頭から1枚ドローして手札に加える
     * （手札上限は9枚とし、上限を超える分は破棄）
     */
    public void drawCard() {
        if (!deck.isEmpty()) {
            Card drawnCard = deck.remove(0);
            if (hand.size() < 9) {
                hand.add(drawnCard);
            } else {
                System.out.println("手札が上限（9枚）のため、カード「" + drawnCard.getName() + "」は破棄されました。");
            }
        }
    }

    /**
     * ダメージ処理
     */
    public void takeDamage(int amount) {
        this.hp = Math.max(0, this.hp - amount);
    }

    /**
     * 回復処理
     */
    public void heal(int amount) {
        this.hp += amount;
    }

    /**
     * 生存確認
     */
    public boolean isAlive() {
        return this.hp > 0;
    }

    // --- ゲッター & セッター ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }

    public int getMaxPp() { return maxPp; }
    public void setMaxPp(int maxPp) { this.maxPp = maxPp; }

    public int getCurrentPp() { return currentPp; }
    public void setCurrentPp(int currentPp) { this.currentPp = currentPp; }

    public List<Card> getDeck() { return deck; }
    public void setDeck(List<Card> deck) { this.deck = deck; }

    public List<Card> getHand() { return hand; }
    public void setHand(List<Card> hand) { this.hand = hand; }

    public List<Card> getBoard() { return board; }
    public void setBoard(List<Card> board) { this.board = board; }
}