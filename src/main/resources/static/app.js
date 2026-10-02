document.addEventListener('DOMContentLoaded', () => {
  // JavaのREST APIを呼び出す
  fetch('/api/cards')
    .then(response => response.json())
    .then(cards => {
      renderCards(cards);
    })
    .catch(error => console.error('エラー発生:', error));
});

function renderCards(cards) {
  const container = document.getElementById('card-container');
  container.innerHTML = '';

  cards.forEach(card => {
    const cardEl = document.createElement('div');
    cardEl.className = 'card';
    cardEl.innerHTML = `
      <div class="card-header">
        <span class="card-cost">コスト: ${card.cost}</span>
        <span class="card-name">${card.name}</span>
      </div>
      <div class="card-type">${card.type}</div>
      <div class="card-body">
        <p>${card.description}</p>
      </div>
      <div class="card-footer">
        <span>ATK: ${card.attack}</span>
        <span>DEF: ${card.defense}</span>
      </div>
    `;
    container.appendChild(cardEl);
  });
}