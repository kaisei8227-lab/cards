package com.example.cardgame.model;

public class Card {
    // CSV / Excel の列に対応する固定ステータス
    private Long id;              // No (カードID)
    private String cardClass;     // クラス (貴族, 怪物, 幽霊, 魔女, 人形, 狩人, 海賊, 無所属)
    private String name;          // カード名
    private String type;          // 種別 (クリーチャー, 魔法)
    private String rarity;        // レアリティ (一般, エース, 一般（準エース級）)
    private int attack;           // 攻撃力
    private int maxHp;            // 体力（最大値）
    private String description;   // 効果テキスト
    private String evolveEffect;  // 進化時効果

    // ゲーム進行時の変動ステータス
    private int currentHp;        // 現在のHP
    private int cost = 1;         // コスト（初期値1）
    private boolean canAttack = false; // 攻撃可能フラグ（召喚酔い管理）
    private boolean isEvolved = false; // 進化済みフラグ

    // デフォルトコンストラクタ
    public Card() {}

    // CSV読み込み用コンストラクタ
    public Card(Long id, String cardClass, String name, String type, String rarity, 
                int attack, int maxHp, String description, String evolveEffect) {
        this.id = id;
        this.cardClass = cardClass;
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.attack = attack;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.description = description;
        this.evolveEffect = evolveEffect;
        this.canAttack = false;
    }

    // ディープコピー用コンストラクタ（山札から生成する際に使用）
    public Card(Card other) {
        this.id = other.id;
        this.cardClass = other.cardClass;
        this.name = other.name;
        this.type = other.type;
        this.rarity = other.rarity;
        this.attack = other.attack;
        this.maxHp = other.maxHp;
        this.currentHp = other.currentHp;
        this.description = other.description;
        this.evolveEffect = other.evolveEffect;
        this.cost = other.cost;
        this.canAttack = other.canAttack;
        this.isEvolved = other.isEvolved;
    }

    // --- ゲーム用便利メソッド ---
    public void takeDamage(int amount) {
        this.currentHp = Math.max(0, this.currentHp - amount);
    }

    public void heal(int amount) {
        this.currentHp = Math.min(this.maxHp, this.currentHp + amount);
    }

    public boolean isAlive() {
        return this.currentHp > 0;
    }

    // --- ゲッター & セッター ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCardClass() { return cardClass; }
    public void setCardClass(String cardClass) { this.cardClass = cardClass; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }

    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }

    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }

    public int getCurrentHp() { return currentHp; }
    public void setCurrentHp(int currentHp) { this.currentHp = currentHp; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEvolveEffect() { return evolveEffect; }
    public void setEvolveEffect(String evolveEffect) { this.evolveEffect = evolveEffect; }

    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }

    public boolean isCanAttack() { return canAttack; }
    public void setCanAttack(boolean canAttack) { this.canAttack = canAttack; }

    public boolean isEvolved() { return isEvolved; }
    public void setEvolved(boolean evolved) { isEvolved = evolved; }

    // エラー防止用のエイリアス
    public int getHp() { return currentHp; }
    public void setHp(int hp) { this.currentHp = hp; }
}