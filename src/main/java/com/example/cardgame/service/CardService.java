package com.example.cardgame.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.example.cardgame.model.Card;

@Service
public class CardService {

    private final List<Card> masterCardList = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadCardsFromCsv();
    }

    private void loadCardsFromCsv() {
        try {
            ClassPathResource resource = new ClassPathResource("data/cards.csv");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    // CSVカンマ区切りのパース（カンマを含む効果文対策）
                    String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                    if (data.length >= 8) {
                        Long id = Long.parseLong(clean(data[0]));
                        String cardClass = clean(data[1]);
                        String name = clean(data[2]);
                        String type = clean(data[3]);
                        String rarity = clean(data[4]);
                        int attack = parseStat(data[5]);
                        int maxHp = parseStat(data[6]);
                        String description = clean(data[7]);
                        String evolveEffect = data.length > 9 ? clean(data[9]) : "";

                        Card card = new Card(id, cardClass, name, type, rarity, attack, maxHp, description, evolveEffect);
                        masterCardList.add(card);
                    }
                }
                System.out.println("カードデータ全 " + masterCardList.size() + " 枚のロードに成功しました。");
            }
        } catch (Exception e) {
            System.err.println("cards.csv の読み込みに失敗しました。");
            e.printStackTrace();
        }
    }

    private String clean(String str) {
        if (str == null) return "";
        return str.replace("\"", "").trim();
    }

    private int parseStat(String str) {
        String cleaned = clean(str);
        if (cleaned.isEmpty() || cleaned.equals("-")) return 0;
        try {
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // --- 各種取得メソッド ---
    public List<Card> getAllCards() {
        return masterCardList;
    }

    public List<Card> getCardsByClass(String cardClass) {
        return masterCardList.stream()
                .filter(c -> c.getCardClass().equals(cardClass))
                .collect(Collectors.toList());
    }

    /**
     * 指定したクラスからランダムな40枚のデッキを構築して返す
     */
    public List<Card> generateRandomDeck(String cardClass) {
        List<Card> pool = getCardsByClass(cardClass);
        if (pool.isEmpty()) {
            pool = masterCardList; // フォールバック
        }
        
        List<Card> deck = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            Card randomCard = pool.get((int) (Math.random() * pool.size()));
            deck.add(new Card(randomCard)); // 独立したオブジェクトとしてコピー
        }
        Collections.shuffle(deck);
        return deck;
    }
}