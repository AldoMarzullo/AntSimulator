package com.antSimulator.gui;

import java.awt.Point;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.antSimulator.logic.Ant;
import com.antSimulator.logic.Cell;
import com.antSimulator.logic.GroundState;
import com.antSimulator.logic.Manager;
import com.antSimulator.logic.World;

public class AntPanel extends Application {

	public static final int CELLSIZE = 3;
	public static final int PANEL_SIZE = 600;

	private World world;
	private Group root;
	private Stage stage;
	private Canvas canvas;
	private GraphicsContext gc;

	public AntPanel() {
		world = Manager.getInstance().world;

	}

	public static void main(String[] args) {

		Manager.getInstance().start();
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		this.stage = stage;

		this.stage.setTitle("AntSimulator!");
		this.root = new Group();
		this.canvas = new Canvas(PANEL_SIZE, PANEL_SIZE);
		this.gc = canvas.getGraphicsContext2D();

		// inits the repainter thread
		initThread();

		root.getChildren().add(canvas);
		stage.setScene(new Scene(root));
		stage.show();

		// closes the stage and stops the active threads
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {
				Manager.ISACTIVE = false;
				Platform.exit();

			}
		});

	}

	private void initThread() {

		new AnimationTimer() {

			@Override
			public void handle(long arg0) {

				repaint();
				sleepQuietly(Manager.SLEEP_TIME);

			}
		}.start();
	}

	private void sleepQuietly(int time) {

		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void repaint() {

		gc.clearRect(0, 0, PANEL_SIZE, PANEL_SIZE);

		Manager.getInstance().lock.lock();
		for (int i = 0; i < World.WIDTH; i++) {
			for (int j = 0; j < World.HEIGHT; j++) {
				Cell c = world.getCell(i, j);
				GroundState g = c.getG();
				gc.setGlobalAlpha(1.0 - g.getPhLevel() / 100);
				gc.setFill(Color.GRAY);
				gc.fillRoundRect(i * CELLSIZE, j * CELLSIZE, CELLSIZE,
						CELLSIZE, 10, 10);
				if (g.getLevel() == GroundState.MAXLEVEL) {

					gc.setFill(Color.BLACK);
					gc.fillRoundRect(i * CELLSIZE, j * CELLSIZE, CELLSIZE,
							CELLSIZE, 10, 10);

				}
				if (c.getA() != null) {
					// System.out.println("Ho spostato " + c.getA().getName() +
					// " " +
					// c.getA().getXPos()+" "+c.getA().getYPos()+" con livello "+c.getA().getLevel());
					gc.setGlobalAlpha(1.0);
					if (c.getA().getAntState() == Ant.SEARCH)
						gc.setFill(Color.RED);
					else
						gc.setFill(Color.BLUE);

					gc.fillRoundRect(i * CELLSIZE, j * CELLSIZE, CELLSIZE,
							CELLSIZE, 10, 10);

				}// if

			}// for

		}// for
		gc.setGlobalAlpha(1.0);
		for (Point food : world.getFood()) {
			gc.setFill(Color.GREEN);
			gc.fillRoundRect(food.getX() * CELLSIZE, food.getY() * CELLSIZE,
					CELLSIZE * World.FOOD_WIDTH, CELLSIZE * World.FOOD_HEIGHT, 10, 10);
		}
		gc.setFill(Color.BROWN);
		gc.fillRoundRect(world.getNest().getX() * CELLSIZE, world.getNest()
				.getY() * CELLSIZE, CELLSIZE * 2, CELLSIZE * 2, 10, 10);
		Manager.getInstance().lock.unlock();
	}// repaint

}// class