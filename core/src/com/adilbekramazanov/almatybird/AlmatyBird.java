package com.adilbekramazanov.almatybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class AlmatyBird extends ApplicationAdapter {

	Texture gameover;
	SpriteBatch batch;
	Texture background;
	ShapeRenderer shapeRenderer;

	Texture[] birds;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	Circle birdCircle;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;

	int score = 0;
	int scoringTube = 0;

	int gameState = 0;
	float gravity = (float) 1.7;

	Texture topTube;
	Texture bottomTube;

	float maxTubeOffset;
	Random randomGenerator;

	float tubeVelocity = 4;
	int numberOfTubes = 4;

	float[] tubeOffset = new float[numberOfTubes];
	float[] tubeX = new float[numberOfTubes];


	float distanceBetweenTubes;
	float gap;
	BitmapFont font;



	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("almatybg.png");
		gameover = new Texture("gameover.png");


		font = new BitmapFont();
		font.setColor(Color.MAROON);
		font.getData().setScale(10);

		shapeRenderer = new ShapeRenderer();

		birdCircle = new Circle();
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];


		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");

		gap = birds[0].getHeight()*4.3f;

		topTube = new Texture("toptubeNew.png");
		bottomTube = new Texture("bottomtubeNew.png");
		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;

		startGame();

	}

	public void startGame(){
		birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

		for (int i = 0; i < numberOfTubes; i++) {

			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();

		}
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {

			if (tubeX[scoringTube] < Gdx.graphics.getWidth()/2){

				score++;

				Gdx.app.log("Score", String.valueOf(score));

				if (scoringTube <  numberOfTubes - 1){
					scoringTube++;
				}else{
					scoringTube =0;
				}

			}

			if (Gdx.input.justTouched()) {

				velocity = -25;

			}

			for (int i = 0; i < numberOfTubes; i++) {

				if (tubeX[i] < - topTube.getWidth()) {

					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

					tubeX[i] += numberOfTubes * distanceBetweenTubes;



				} else {

					tubeX[i] = tubeX[i] - tubeVelocity;



				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

			}



			if (birdY > 0) {

				velocity = velocity + gravity;
				birdY -= velocity;

			}else {
				gameState = 2;

			}

		} else if(gameState == 0){

			if (Gdx.input.justTouched()) {


				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;

			}

		}else if (gameState ==2){
			batch.draw(gameover, Gdx.graphics.getWidth()/2 - gameover.getWidth()/2, Gdx.graphics.getHeight()/2 + 300);
			if (Gdx.input.justTouched()) {
				gravity =0;
				startGame();
				velocity =0;
				score = 0;
				scoringTube = 0;
				gameState = 1;
				if (Gdx.input.justTouched()) {
					gravity = 1.7f;
				}

			}
		}


		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}

		font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth()/2 - 50, Gdx.graphics.getHeight()/2 + 300);
		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
		batch.end();

		birdCircle.set(Gdx.graphics.getWidth()/2,birdY + birds[flapState].getHeight()/2 , birds[flapState].getWidth()/2);


//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.BLUE);
//        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		for (int i = 0; i < numberOfTubes; i++) {
//            shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
//            shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

			if(Intersector.overlaps(birdCircle, topTubeRectangles[i]) || (Intersector.overlaps(birdCircle, bottomTubeRectangles[i]))){
				gameState = 2;

			}
		}


//        shapeRenderer.end();

	}
}

