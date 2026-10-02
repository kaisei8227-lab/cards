package com.example.cardgame.controller;

import java.util.List;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.cardgame.model.Card;
import com.example.cardgame.model.GameRoom;
import com.example.cardgame.model.Player;
import com.example.cardgame.service.CardService;

@RestController
public class CardController {

    private final CardService cardService;
    private GameRoom gameRoom = new GameRoom();

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    // --- REST API （一覧確認・カード図鑑用） ---
    @GetMapping("/api/cards")
    public List<Card> getAllCards() {
        return cardService.getAllCards();
    }

    @GetMapping("/api/cards/class/{className}")
    public List<Card> getCardsByClass(@PathVariable String className) {
        return cardService.getCardsByClass(className);
    }

    // --- WebSocket （対戦中の操作用） ---
    @MessageMapping("/join")
    @SendTo("/topic/game")
    public GameRoom joinGame(Map<String, String> payload) {
        String playerId = payload.get("playerId");
        String selectedClass = payload.getOrDefault("cardClass", "貴族");

        if (gameRoom.getPlayer1() == null) {
            Player p1 = new Player(playerId);
            p1.setDeck(cardService.generateRandomDeck(selectedClass));
            gameRoom.setPlayer1(p1);
        } else if (gameRoom.getPlayer2() == null && !playerId.equals(gameRoom.getPlayer1().getId())) {
            Player p2 = new Player(playerId);
            p2.setDeck(cardService.generateRandomDeck(selectedClass));
            gameRoom.setPlayer2(p2);
            gameRoom.startGame();
        }
        return gameRoom;
    }

    @MessageMapping("/action")
    @SendTo("/topic/game")
    public GameRoom handleAction(Map<String, Object> action) {
        String type = (String) action.get("type");
        String playerId = (String) action.get("playerId");

        if (!gameRoom.isStarted() || !playerId.equals(gameRoom.getCurrentTurnPlayerId())) {
            return gameRoom;
        }

        Player me = playerId.equals(gameRoom.getPlayer1().getId()) ? gameRoom.getPlayer1() : gameRoom.getPlayer2();
        Player enemy = playerId.equals(gameRoom.getPlayer1().getId()) ? gameRoom.getPlayer2() : gameRoom.getPlayer1();

        switch (type) {
            case "PLAY_CARD": {
                String cardId = String.valueOf(action.get("cardId"));
                Card cardToPlay = me.getHand().stream()
                        .filter(c -> String.valueOf(c.getId()).equals(cardId))
                        .findFirst()
                        .orElse(null);

                if (cardToPlay != null && me.getCurrentPp() >= cardToPlay.getCost() && me.getBoard().size() < 5) {
                    me.setCurrentPp(me.getCurrentPp() - cardToPlay.getCost());
                    me.getHand().remove(cardToPlay);
                    cardToPlay.setCanAttack(false); // 召喚酔い
                    me.getBoard().add(cardToPlay);
                }
                break;
            }

            case "ATTACK_PLAYER": {
                String attackerId = String.valueOf(action.get("attackerId"));
                Card attacker = me.getBoard().stream()
                        .filter(c -> String.valueOf(c.getId()).equals(attackerId))
                        .findFirst()
                        .orElse(null);

                if (attacker != null && attacker.isCanAttack()) {
                    enemy.setHp(enemy.getHp() - attacker.getAttack());
                    attacker.setCanAttack(false);
                }
                break;
            }

            case "ATTACK_CARD": {
                String myAttackerId = String.valueOf(action.get("attackerId"));
                String targetCardId = String.valueOf(action.get("targetId"));

                Card myCard = me.getBoard().stream()
                        .filter(c -> String.valueOf(c.getId()).equals(myAttackerId))
                        .findFirst()
                        .orElse(null);

                Card targetCard = enemy.getBoard().stream()
                        .filter(c -> String.valueOf(c.getId()).equals(targetCardId))
                        .findFirst()
                        .orElse(null);

                if (myCard != null && targetCard != null && myCard.isCanAttack()) {
                    targetCard.takeDamage(myCard.getAttack());
                    myCard.takeDamage(targetCard.getAttack());
                    myCard.setCanAttack(false);

                    if (!targetCard.isAlive()) {
                        enemy.getBoard().remove(targetCard);
                    }
                    if (!myCard.isAlive()) {
                        me.getBoard().remove(myCard);
                    }
                }
                break;
            }

            case "END_TURN": {
                gameRoom.endTurn();
                break;
            }
        }

        gameRoom.checkWinCondition();
        return gameRoom;
    }
}