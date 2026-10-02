let stompClient = null;
let myPlayerId = "player_" + Math.floor(Math.random() * 10000);
let currentGameState = null;
let selectedAttackerId = null; // 攻撃選択中の自分カード

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function () {
        document.getElementById('connection-status').textContent = "対戦相手を探しています...";
        stompClient.subscribe('/topic/game', function (message) {
            currentGameState = JSON.parse(message.body);
            renderGame(currentGameState);
        });

        stompClient.send("/app/join", {}, JSON.stringify({ playerId: myPlayerId }));
    });
}

function renderGame(room) {
    if (!room.started) return;

    document.getElementById('lobby-overlay').style.display = "none";
    document.getElementById('battlefield').style.display = "flex";

    const isPlayer1 = room.player1.id === myPlayerId;
    const me = isPlayer1 ? room.player1 : room.player2;
    const enemy = isPlayer1 ? room.player2 : room.player1;

    // ステータス & PP更新
    document.getElementById('my-hp').textContent = me.hp;
    document.getElementById('enemy-hp').textContent = enemy.hp;
    document.getElementById('my-pp-text').textContent = `PP ${me.currentPp}/${me.maxPp}`;
    document.getElementById('enemy-pp-text').textContent = `PP ${enemy.currentPp}/${enemy.maxPp}`;

    const isMyTurn = room.currentTurnPlayerId === myPlayerId;
    const endTurnBtn = document.getElementById('end-turn-btn');
    endTurnBtn.style.opacity = isMyTurn ? "1" : "0.4";
    endTurnBtn.style.pointerEvents = isMyTurn ? "auto" : "none";

    // 手札描画
    const handContainer = document.getElementById('my-hand');
    handContainer.innerHTML = "";
    me.hand.forEach(card => {
        const cardElem = createCardElem(card);
        if (isMyTurn && me.currentPp >= card.cost) {
            cardElem.onclick = () => playCard(card.id);
        }
        handContainer.appendChild(cardElem);
    });

    // 自分の場
    const myBoardContainer = document.getElementById('my-board');
    myBoardContainer.innerHTML = "";
    me.board.forEach(card => {
        const cardElem = createCardElem(card);
        if (isMyTurn && card.canAttack) {
            cardElem.classList.add("can-attack");
            if (selectedAttackerId === card.id) {
                cardElem.classList.add("selected");
            }
            cardElem.onclick = () => selectAttacker(card.id);
        }
        myBoardContainer.appendChild(cardElem);
    });

    // 相手の場
    const enemyBoardContainer = document.getElementById('enemy-board');
    enemyBoardContainer.innerHTML = "";
    enemy.board.forEach(card => {
        const cardElem = createCardElem(card);
        if (selectedAttackerId !== null) {
            cardElem.onclick = () => attackTargetCard(card.id); // フォロワー同士の攻撃
        }
        enemyBoardContainer.appendChild(cardElem);
    });

    // 勝敗判定表示
    if (room.winnerId !== null) {
        showResultModal(room.winnerId === myPlayerId);
    }
}

function createCardElem(card) {
    const div = document.createElement('div');
    div.className = 'card';
    div.innerHTML = `
        <div class="card-cost">${card.cost}</div>
        <div class="card-name">${card.name}</div>
        <div class="card-stats">
            <span class="stat-atk">${card.attack}</span>
            <span class="stat-hp">${card.hp}</span>
        </div>
    `;
    return div;
}

function playCard(cardId) {
    selectedAttackerId = null;
    stompClient.send("/app/action", {}, JSON.stringify({
        type: 'PLAY_CARD',
        playerId: myPlayerId,
        cardId: cardId
    }));
}

function selectAttacker(cardId) {
    selectedAttackerId = (selectedAttackerId === cardId) ? null : cardId;
    renderGame(currentGameState);
}

function attackTargetCard(targetCardId) {
    if (!selectedAttackerId) return;
    stompClient.send("/app/action", {}, JSON.stringify({
        type: 'ATTACK_CARD',
        playerId: myPlayerId,
        attackerId: selectedAttackerId,
        targetId: targetCardId
    }));
    selectedAttackerId = null;
}

function attackPlayerDirect() {
    if (!selectedAttackerId) return;
    stompClient.send("/app/action", {}, JSON.stringify({
        type: 'ATTACK_PLAYER',
        playerId: myPlayerId,
        attackerId: selectedAttackerId
    }));
    selectedAttackerId = null;
}

function endTurn() {
    selectedAttackerId = null;
    stompClient.send("/app/action", {}, JSON.stringify({
        type: 'END_TURN',
        playerId: myPlayerId
    }));
}

function showResultModal(isWin) {
    const modal = document.getElementById('result-modal');
    const title = document.getElementById('result-title');
    title.textContent = isWin ? "VICTORY" : "DEFEAT";
    title.style.color = isWin ? "#ffd700" : "#ff3333";
    modal.style.display = "flex";
}