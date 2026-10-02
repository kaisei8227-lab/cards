package com.example.cardgame.model;

public class GameRoom {
    private Player player1;
    private Player player2;
    private String currentTurnPlayerId;
    private boolean started = false;
    private String winnerId = null; // 勝者ID

    public void startGame() {
        this.started = true;
        this.currentTurnPlayerId = player1.getId();
        
        // 初期手札を3枚ずつドロー
        for (int i = 0; i < 3; i++) {
            player1.drawCard();
            player2.drawCard();
        }
        startTurn(player1);
    }

    public void startTurn(Player player) {
        if (player.getMaxPp() < 10) {
            player.setMaxPp(player.getMaxPp() + 1);
        }
        player.setCurrentPp(player.getMaxPp());
        player.drawCard(); // ターン開始時ドロー
        
        // 自分の場のフォロワーを攻撃可能にする
        for (Card card : player.getBoard()) {
            card.setCanAttack(true);
        }
    }

    public void endTurn() {
        if (winnerId != null) return;

        if (currentTurnPlayerId.equals(player1.getId())) {
            currentTurnPlayerId = player2.getId();
            startTurn(player2);
        } else {
            currentTurnPlayerId = player1.getId();
            startTurn(player1);
        }
    }

    // 勝敗チェック
    public void checkWinCondition() {
        if (player1.getHp() <= 0) {
            winnerId = player2.getId();
        } else if (player2.getHp() <= 0) {
            winnerId = player1.getId();
        }
    }

    // ゲッター & セッター
    public Player getPlayer1() { return player1; }
    public void setPlayer1(Player player1) { this.player1 = player1; }
    public Player getPlayer2() { return player2; }
    public void setPlayer2(Player player2) { this.player2 = player2; }
    public String getCurrentTurnPlayerId() { return currentTurnPlayerId; }
    public boolean isStarted() { return started; }
    public String getWinnerId() { return winnerId; }
}