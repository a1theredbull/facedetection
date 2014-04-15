import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;

import org.opencv.core.Point;
import org.opencv.core.Rect;

/*
 * Authors: Alexander Chau, Cameron Ohrt
 * CIS 365 Artificial Intelligence
 * Project 3 - OpenCV
 * 
 * This class encapsulates a heat map Swing panel.
 */

public class HeatMapPanel extends CVPanel {
	private static final long serialVersionUID = 1L;
	
	private Color[][] grid;
	//number of cells
	private int gridWidth, gridHeight;
	//pixel dimensions
	private int dimensionWidth, dimensionHeight;
	//pixel dimensions of cells
	private int cellWidth, cellHeight;
	//speed in which cells darken
	private int heatFactor;
	
	private ArrayList<Rect> detectedFaces;
	
	public HeatMapPanel(int gridWidth, int gridHeight, 
			int dimensionWidth, int dimensionHeight,
			int heatFactor) {
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		this.dimensionWidth = dimensionWidth;
		this.dimensionHeight = dimensionHeight;
		this.cellWidth = (int) Math.floor(dimensionWidth / gridWidth);
		this.cellHeight = (int) Math.floor(dimensionHeight / gridHeight);
		this.heatFactor = heatFactor;
		
		//default to white cells
		grid = new Color[gridWidth][gridHeight];
		for(int i = 0; i < gridWidth; i++) {
			for(int j = 0; j < gridHeight; j++) {
				grid[i][j] = new Color(255, 255, 255);
			}
		}
	}
	
	public int getGridWidth() {
		return this.gridWidth;
	}
	
	public int getGridHeight() {
		return this.gridHeight;
	}
	
	public int getDimensionWidth() {
		return this.dimensionWidth;
	}
	
	public int getDimensionHeight() {
		return this.dimensionHeight;
	}
	
	/*
	 * set the grid back to white
	 */
	public void resetGrid() {
		for(int i = 0; i < gridWidth; i++) {
			for(int j = 0; j < gridHeight; j++) {
				grid[i][j] = new Color(255, 255, 255);
			}
		}
		
		Graphics g = super.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
	}
	
	/*
	 * set the detected faces
	 */
	public void setDetectedFaces(Rect[] detectedFaces) {
		this.detectedFaces = new ArrayList<Rect>(Arrays.asList(detectedFaces));
	}
	
	//find the closest cell to a point
	private HeatEntry getClosestCellColor(int x, int y) {
		int cellX = x / cellWidth;
		int cellY = y / cellHeight;

		return new HeatEntry(grid[cellX][cellY], cellX, cellY);
	}
	
	public void paintComponent(Graphics g) {
		if(detectedFaces == null) return;
		
		//paint cells with detected faces
		for(int i = 0, size = detectedFaces.size(); i < size; i++) {
			Rect face = detectedFaces.get(i);
			Point center = new Point(face.x + face.width * 0.5, face.y
					+ face.height * 0.5);
			
			HeatEntry entry = getClosestCellColor((int)Math.floor(center.x),
					(int)Math.floor(center.y));
			if(entry == null) return;
			
			//empty? make white
			if(entry.color == null) {
				grid[entry.cellX][entry.cellY] = new Color(255, 255, 255);
			} else { //not empty? make it a darker shade of the current color
				grid[entry.cellX][entry.cellY] = getDarkerShade(entry.color);
			}
			
			g.setColor(grid[entry.cellX][entry.cellY]);
			g.fillRect(entry.cellX * cellWidth, entry.cellY * cellHeight,
					cellWidth, cellHeight);
		}
	}
	
	/*
	 * return a darker shade of the color
	 */
	public Color getDarkerShade(Color color) {
		int darkR = color.getRed() - heatFactor;
		int darkG = color.getGreen() - heatFactor;
		int darkB = color.getBlue() - heatFactor;
		darkR = darkR >= 0 ? darkR : 0;
		darkG = darkG >= 0 ? darkG : 0;
		darkB = darkB >= 0 ? darkB : 0;
		
		return new Color(darkR, darkG, darkB);
	}

	// represents a cell keeping track of color and x, y coordinate
	public class HeatEntry {
		public Color color;
		public int cellX;
		public int cellY;
		
		public HeatEntry(Color color, int cellX, int cellY) {
			this.color = color;
			this.cellX = cellX;
			this.cellY = cellY;
		}
	}
}
