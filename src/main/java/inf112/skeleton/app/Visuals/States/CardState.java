package inf112.skeleton.app.Visuals.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import inf112.skeleton.app.GameMechanics.Board.Board;
import inf112.skeleton.app.GameMechanics.Cards.Card;
import inf112.skeleton.app.GameMechanics.Cards.CardManager;
import inf112.skeleton.app.Netcode.Client;
import inf112.skeleton.app.Netcode.Host;
import inf112.skeleton.app.Netcode.INetCode;
import inf112.skeleton.app.Visuals.*;
import inf112.skeleton.app.GameMechanics.Player;

import java.util.HashMap;

public class CardState extends State {

	private Board board;
	private BoardGUI boardGUI;
	private SpriteBatch batch;
	private Player[] players;

	private CardHandGUI cardHandGUI;

	private PlayerInfoGUI playerInfoGUI;

	private CardManager cardManager;

	private INetCode net;
	private boolean shouldSend;

	public CardState(GameStateManager gsm, Board board, CardManager cardManager, INetCode net) {
		super(gsm);
		this.board = board;
		this.batch = new SpriteBatch();
		this.boardGUI = new BoardGUI(board, this.camera, this.stage, this.gsm, super.assetHandler);
		this.boardGUI.create();

		this.players = board.getAllPlayers();

		this.playerInfoGUI = new PlayerInfoGUI(board, batch, stage, super.assetHandler);
		this.cardManager = cardManager;

		this.cardHandGUI = new CardHandGUI(cardManager, stage, super.assetHandler);

		this.net = net;
		this.shouldSend = true;
	}

	private void isHostHandling(){
		Host host = (Host)this.net;

		if(host.getHostHandler().getCards() != null && this.shouldSend){
			host.send(host.getHostHandler().getCards().toString());
			this.shouldSend = false;
		}

	}

	private void isClientHandling(){
		Client client = (Client)this.net;
		if(players[client.getIndex()].isReady() && this.shouldSend){
			String sendString = "CARDS!";
			for(Card card : players[client.getIndex()].getCardSequence()){
				sendString += card.getCardType() + "&" + card.getPriority() + ",";
			}
			client.send(sendString);
			this.shouldSend = false;
		}

		HashMap<Integer, Card[]> cards = client.getClientHandler().getCards();
		if(cards != null){
			for (Player player : players) {
				cardManager.setCardSeq(player, cards.get(player.getIndex()));
			}


		}



	}


	@Override
	public void update(float dt) {
		super.update(dt);
		playerInfoGUI.update();
		Gdx.input.setInputProcessor(stage);

		if(this.net instanceof Client){
			//client
			isClientHandling();
		}else if(this.net instanceof Host){
			isHostHandling();
			//host
		}else{
			//local
		}


		for (Player player : players) {
			if (!player.isReady()) {
				return;
			}
		}
		board.initRound();
		gsm.set(new ActionState(gsm, board, cardManager, this.net));
	}

	@Override
	public void render() {
		if (Gdx.input.isKeyPressed(Input.Keys.P)) {
			System.out.println("PAUSE!");
			this.gsm.push(new PauseState(this.gsm));
		}
		super.render();
	}

	@Override
	public void dispose() {
		cardHandGUI.dispose();
		playerInfoGUI.dispose();
		batch.dispose();
	}
}
