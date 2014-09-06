package antworld.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import antworld.ai.AntUtilities;
import antworld.constants.Constants;
import antworld.data.AntData;
import antworld.data.AntType;
import antworld.data.CommData;
import antworld.data.FoodData;
import antworld.data.NestNameEnum;
import antworld.data.TeamNameEnum;
import antworld.info.AntManager;
import antworld.info.FoodManager;

public class AntLive extends JPanel
{	
	static AntManager activeAnts;
	static FoodManager activeFood;
	
	private AntWorldImage image;
	private JScrollPane imageScroll;
	private JTable antTable;
	private AntTableModel model;
	private AntControl antControl;
	
	public AntLive(AntManager activeAnts, FoodManager activeFood)
	{
		this.setLayout(new BorderLayout());
		
		AntLive.activeAnts = activeAnts;
		AntLive.activeFood = activeFood;
		
		this.image = new AntWorldImage(Constants.WORLD_MAP_FILEPATH);
		this.imageScroll = new JScrollPane(image);				
		this.model = new AntTableModel(activeAnts.getAllMyAnts());		
		this.antTable = new JTable(model);
		this.antControl = new AntControl(activeAnts, null);

		//Add the AntImage
		this.imageScroll.setPreferredSize(new Dimension(700, 500));
    this.add(imageScroll, BorderLayout.LINE_START);
	
    //Add the AntTable
    this.antTable.setRowHeight(25);
		this.antTable.setGridColor(Color.BLACK);
		this.antTable.setEnabled(false);
		this.antTable.setPreferredSize(this.antTable.getPreferredScrollableViewportSize());
		this.antTable.setDefaultRenderer(Object.class, new AntCellRenderer());
		this.add(new JScrollPane(this.antTable), BorderLayout.LINE_END);
		
		//Add the AntControl
		this.add(antControl, BorderLayout.PAGE_END);
	}
	
	
	
	public void update(Set<FoodData> foodSet)
	{
	  //Update the world image.
	  this.image.clearAnts();
	  this.image.paintMyAnts(activeAnts.getAllMyAnts());
    this.image.paintEnemyAnts(activeAnts.getAllEnemyAnts());
		this.image.paintFood(foodSet);
		this.image.repaint();
		
		//Update the ant table.
		this.model.fireTableDataChanged();
	}
	
	public void centerViewPort(List<AntData> antData)
	{
		Point averageLocation = AntUtilities.getAverageLocation(antData);
		averageLocation.x = averageLocation.x - 300;
		averageLocation.y = averageLocation.y - 200;
		
	  this.imageScroll.getViewport().setViewPosition(averageLocation);
	}
	
	public static void main(String[] args)
	{
		ArrayList<AntData> antData = new ArrayList<AntData>();
		AntData a1 = new AntData(213, AntType.ATTACK, NestNameEnum.ACORN, TeamNameEnum.BlueGrama);
		a1.gridX = 450;
		a1.gridY = 346;
		antData.add(a1);
		
		AntData a2 = new AntData(214, AntType.BASIC, NestNameEnum.ACORN, TeamNameEnum.BlueGrama);
		a1.gridX = 454;
		a1.gridY = 349;
		antData.add(a2);
		
		CommData data = new CommData(NestNameEnum.ACORN, TeamNameEnum.BlueGrama);
		data.myAntList = antData;
		
		AntManager manager = new AntManager(data);
		
		AntLive live = new AntLive(manager, null);
		
		JFrame frame = new JFrame("Live");
		frame.add(live);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
