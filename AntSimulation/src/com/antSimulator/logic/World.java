package com.antSimulator.logic;

import java.util.Random;

public class World {

	public static int NUM_OF_ANTS = 100;
	public static final short MAX_NUM_OF_ANT = 500;
	
	public static final int WIDTH = 100;
	public static final int HEIGHT = 100;
	public static int FOOD_WIDTH = 1;
	public static int FOOD_HEIGHT = 1;
	public static final int NEST_WIDTH = 6;
	public static final int NEST_HEIGHT = 6;
	public static int NESTX = 10;
	public static int NESTY = 15;
	
	public static final short MAX_PH_LEVEL = 1000;

	private Cell[][] matrix;
	private Cell[][] updatingMatrix;
	private boolean updated;
	public Nest nest;

	// private ArrayList<Point> food;

	public World() {

		loadWorld();
		spawnAnts();

	}

	private void spawnAnts() {

		for (int i = 0; i < NUM_OF_ANTS; i++)
			nest.addAnt(i);

	}

	public void respawnAnts() {

		Manager.getInstance().lock.lock();

		if (nest.getAnts().size() < NUM_OF_ANTS) {
			for (int i = nest.getAnts().size(); i < NUM_OF_ANTS; i++)
				nest.addAnt(i);

		} else {

			while (nest.getAnts().size() != NUM_OF_ANTS) {

				Ant a = nest.removeLastAnt();
				this.matrix[a.getXPos()][a.getYPos()].removeAntfromArray();
				this.updatingMatrix[a.getXPos()][a.getYPos()].removeAntfromArray();
				
			}

		}

		Manager.getInstance().lock.unlock();

	}

	private void loadWorld() {

		matrix = new Cell[WIDTH][HEIGHT];
		updatingMatrix = new Cell[WIDTH][HEIGHT];
		updated = false;
		initWorld();

		nest = new Nest(NESTX, NESTY, matrix[10][15].getGroundState());
		initFood();

	}

	private void initFood() {
		for (int i = 0; i < FOOD_WIDTH; i++)
			for (int j = 0; j < FOOD_HEIGHT; j++){
				matrix[45 + i][20 + j].insertFood();
				updatingMatrix[45+i][20+j].insertFood();
			}

	}

	private void initWorld() {
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				int newVal = 0;

				// int random = new Random().nextInt(2);
				// if (i - 1 >= 0) {
				// if (random == 0)
				// newVal = matrix[i - 1][j].getGroundState().getLevel() - 1;
				// else
				// newVal = matrix[i - 1][j].getGroundState().getLevel() + 1;
				//
				// if (newVal < 0)
				// newVal = 0;
				// if (newVal > GroundState.MAXLEVEL)
				// newVal--;
				// }

				matrix[i][j] = new Cell(i, j, newVal);
				updatingMatrix[i][j] = new Cell(i, j, newVal);
			}
		}

		for (int k = 0; k < 5; k++) {

			int i = new Random().nextInt(HEIGHT);
			int j = new Random().nextInt(WIDTH);
			matrix[i][j].getGroundState().setLevel(GroundState.MAXLEVEL);
			updatingMatrix[i][j].getGroundState().setLevel(GroundState.MAXLEVEL);
		}

	}

	public Cell[][] getWorld() {
		return matrix;
	}

	public void setWorld(Cell[][] world) {
		this.matrix = world;
	}

	public Cell getAvailableCell(int xPos, int yPos) {

		if (xPos < 0 || xPos >= WIDTH || yPos < 0 || yPos >= HEIGHT)
			return null;
		else if (matrix[xPos][yPos].getGroundState().getLevel() == GroundState.MAXLEVEL)
			return null;

		else {
			if (!updated)
				return matrix[xPos][yPos];
			else
				return updatingMatrix[xPos][yPos];
		}
	}

	public Cell getCellToDraw(int xPos,int yPos){
		if (xPos < 0 || xPos >= WIDTH || yPos < 0 || yPos >= HEIGHT)
			return null;

		else {
			if (updated)
				return matrix[xPos][yPos];
			else
				return updatingMatrix[xPos][yPos];
		}
	}
	public Cell getCell(int xPos, int yPos) {

		if (xPos < 0 || xPos >= WIDTH || yPos < 0 || yPos >= HEIGHT)
			return null;
		else {
			if (!updated)
				return matrix[xPos][yPos];
			else
				return updatingMatrix[xPos][yPos];
		}
	}

	/*
	 * public void unlockCell(int x, int y, int nx, int ny) {
	 * Manager.getInstance().lock.lock(); try {
	 * 
	 * lockedCell[x][y] = false; lockedCell[nx][ny] = false;
	 * 
	 * Manager.getInstance().condition.signalAll();
	 * 
	 * } finally { Manager.getInstance().lock.unlock(); } }
	 * 
	 * public void lockCell(int x, int y, int nx, int ny) {
	 * 
	 * Manager.getInstance().lock.lock(); try {
	 * 
	 * while (lockedCell[x][y] || lockedCell[nx][ny]) try {
	 * 
	 * Manager.getInstance().condition.signalAll();
	 * Manager.getInstance().condition.await();
	 * 
	 * } catch (InterruptedException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * lockedCell[x][y] = true; lockedCell[nx][ny] = true;
	 * 
	 * } finally { Manager.getInstance().lock.unlock(); }
	 * 
	 * }
	 */

	public void setCell(Cell cell) {
		this.matrix[cell.getX()][cell.getY()] = cell;
	}

	public Nest getNest() {
		return nest;
	}

	public void setNest(Nest nest) {
		this.nest = nest;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

}
