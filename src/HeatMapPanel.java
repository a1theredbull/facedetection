import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.opencv.core.Point;
import org.opencv.core.Rect;


public class HeatMapPanel extends CVPanel {
	private static final long serialVersionUID = 1L;
	
	private Color[][] grid;
	//number of cells
	private int gridWidth, gridHeight;
	//pixel dimensions
	private int dimensionWidth, dimensionHeight;
	//pixel dimensions of cells
	private int cellWidth, cellHeight;
	
	private ArrayList<Rect> detectedFaces;
	
	public HeatMapPanel(int gridWidth, int gridHeight, 
			int dimensionWidth, int dimensionHeight) {
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		this.dimensionWidth = dimensionWidth;
		this.dimensionHeight = dimensionHeight;
		this.cellWidth = (int) Math.floor(dimensionWidth / gridWidth);
		this.cellHeight = (int) Math.floor(dimensionHeight / gridHeight);
		
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
	
	public void setDetectedFaces(Rect[] detectedFaces) {
		this.detectedFaces = new ArrayList<Rect>(Arrays.asList(detectedFaces));
	}
	
	private HeatEntry getClosestCellColor(int x, int y) {
		int cellX = x / cellWidth;
		int cellY = y / cellHeight;

		HeatEntry entry = new HeatEntry(grid[cellX][cellY], cellX, cellY);
		return entry;
	}
	
	public void paintComponent(Graphics g) {
		if(detectedFaces == null) return;
		
		for(int i = 0, size = detectedFaces.size(); i < size; i++) {
			Rect face = detectedFaces.get(i);
			Point center = new Point(face.x + face.width * 0.5, face.y
					+ face.height * 0.5);
			
			HeatEntry entry = getClosestCellColor((int)Math.floor(center.x),
					(int)Math.floor(center.y));
			if(entry == null) return;
			
			if(entry.color == null) {
				grid[entry.cellX][entry.cellY] = new Color(255, 255, 255);
			} else {
				grid[entry.cellX][entry.cellY] = getDarkerShade(entry.color);
			}
			
			g.setColor(grid[entry.cellX][entry.cellY]);
			g.fillRect(entry.cellX * cellWidth, entry.cellY * cellHeight,
					cellWidth, cellHeight);
		}
	}
	
	public Color getDarkerShade(Color color) {
		int darkR = color.getRed() - 3;
		int darkG = color.getGreen() - 3;
		int darkB = color.getBlue() - 3;
		darkR = darkR >= 0 ? darkR : 0;
		darkG = darkG >= 0 ? darkG : 0;
		darkB = darkB >= 0 ? darkB : 0;
		
		return new Color(darkR, darkG, darkB);
	}
	
	public Color getRandomLightColor() {
		return new Color(Color.HSBtoRGB((float) Math.random(), 
				(float) Math.random(), 0.5F + ((float) Math.random())/2F));
	}
	
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
