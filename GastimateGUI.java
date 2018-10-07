//8/15/2018
package com.kquallis.gastimate;

import java.io.IOException;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.kquallis.dbinteraction.DBInteraction;
import com.kquallis.vehicle.Vehicle;
import com.kquallis.webscraper.WebScraper;

public class GastimateGUI {
	static Connection conn;
	static ResultSet makeList, modelList, yearList;
	static Statement stmnt;
	
	JLabel kquallis = new JLabel("K.Quallis 2018");
	//Gastimate vehicle;
	Vehicle vehicle;//added on 10/6/2018
	
	public GastimateGUI(){
		try{
			conn = DBInteraction.getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
	    final JFrame frame = new JFrame("Ga$timate");
		
	    JLabel makeLbl = new JLabel("Make:");
		JLabel modLbl = new JLabel("Model:");
		JLabel yearLbl = new JLabel("Year:");
		JLabel currLevlLbl = new JLabel("Current Level:");
		JLabel toLevLbl = new JLabel("Fill To Level:");
		
		final JComboBox<String> makes = new JComboBox<String>();
		makes.setPrototypeDisplayValue("Mercedes-Benz");//
		final JComboBox<String> models = new JComboBox<String>();
		models.setPrototypeDisplayValue("Mercedes-Benz");
		
		final JComboBox<String> years = new JComboBox<String>();
		
		JLabel gasPriceLbl = new JLabel("Price:");
		final JTextField gasPrice = new JTextField();
		gasPrice.setColumns(3);
		gasPrice.setEditable(false);//disabled by default
		
		final JComboBox<String> currLevel = new JComboBox<String>();
		currLevel.setPrototypeDisplayValue("3/4 Tank");
		currLevel.setEnabled(false);//disabled by default
		
		final JComboBox<String> toLevel = new JComboBox<String>();
		toLevel.setPrototypeDisplayValue("1/4 Tank");
		toLevel.setEnabled(false);//disabled by default
		
		final JButton getEstimate = new JButton("Get Estimate");
		getEstimate.setEnabled(false);//disabled by default
		
		makes.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String m = (String) makes.getSelectedItem();
						populateModels(m, models);
					}
		});
		
		models.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String make = (String) makes.getSelectedItem();
						String model = (String) models.getSelectedItem();
						populateModYears(make, model, years);
					}
		});
		
		JButton getCar = new JButton("Get Car");
		getCar.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						boolean hasException = false;
						String make = (String) makes.getSelectedItem();
						String model = (String) models.getSelectedItem();
						String year = (String) years.getSelectedItem();
						
						vehicle = new Vehicle(make, model, year);//changed to use the Vehicle class - 10/6/2018
						try{	
							vehicle.setFuelTankCapacity( WebScraper.getTankCap( vehicle.getWebPageURL() ) );
						}
						catch (IOException ioe) {
							JOptionPane.showMessageDialog(frame, 
									"There was an issue searching for the vehicle you selected. Please search again.", "",
									2);
							hasException = true;	ioe.printStackTrace();		 
					}
						if(!hasException){
							JOptionPane.showMessageDialog(frame, "Selected Vehicle: "+vehicle.getYear()
							+" "+vehicle.getMake()+" "+vehicle.getModel());
							currLevel.setEnabled(true);
							toLevel.setEnabled(true);
							gasPrice.setEditable(true);
							getEstimate.setEnabled(true);
						}
					}
		});
		
		currLevel.addActionListener(//
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String curr = (String) currLevel.getSelectedItem();
						popToLevels(curr, toLevel);
					}
		});
		
		getEstimate.setEnabled(false);//disable Cost button
		getEstimate.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {	
						String [] levels = {(String)currLevel.getSelectedItem(),(String)toLevel.getSelectedItem()}; 
						double price = Double.parseDouble(gasPrice.getText());
						
						double [] doubLevels = convertLevels(levels);
						double curr = doubLevels[0];
						double to = doubLevels[1];
					
						int estimate = vehicle.calcCostToLevel(curr, to, price); 
						JOptionPane.showMessageDialog(frame, "Estimated Cost: $"+estimate);
					}
		});
		
		final JButton exit = new JButton("Exit");
		exit.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						DBInteraction.closeConnection(conn);
						frame.dispose();
					}
				});
		
		frame.setLayout(new FlowLayout( FlowLayout.LEFT, 5, 20) );
		frame.add(makeLbl);
		frame.add(makes);
		frame.add(modLbl);
		frame.add(models);
		frame.add(yearLbl);
		frame.add(years);
		frame.add(getCar);
		
		frame.add(currLevlLbl);
		frame.add(currLevel);
		frame.add(toLevLbl);
		frame.add(toLevel);
		frame.add(gasPriceLbl);
		frame.add(gasPrice);
		frame.add(getEstimate);
		
		frame.add(exit);
		frame.add(kquallis);
		
		frame.setPreferredSize(new Dimension(674, 200));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		populateMakeList(makes);//populate the list of makes
		popCurrLevels(currLevel);//populate the list of tank levels
		
	}

	public void populateMakeList(JComboBox<String> makes){ 
		try{
			String sql_stmt = "SELECT DISTINCT MAKE FROM MAKEMODELS.MAKEMODEL ORDER BY MAKE;";
		
			stmnt = conn.createStatement();
			
			makeList = stmnt.executeQuery(sql_stmt);
				while(makeList.next()){
					String m = makeList.getString("make");
					makes.addItem(m);	
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void populateModels(String make, JComboBox<String> models){
		PreparedStatement ps;
		
		models.removeAllItems();//remove list items before executing query
		try {
			ps = conn.prepareStatement("SELECT MODEL FROM MAKEMODELS.MAKEMODEL WHERE MAKE= ?"
				+ " ORDER BY MODEL;");
			ps.setString(1,make);
			modelList = ps.executeQuery();
				while(modelList.next()){
					String mod = modelList.getString("model");
					models.addItem(mod);
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void populateModYears(String make, String model, JComboBox<String> years){
		PreparedStatement ps;
		
		years.removeAllItems();//remove list items before executing query
		try {
			ps = conn.prepareStatement("SELECT MODYEAR FROM MAKEMODELS.MODELYEAR WHERE MAKE= ? AND MODEL = ?"
				+ " ORDER BY MODYEAR;");
			ps.setString(1,make);
			ps.setString(2,model);
			yearList = ps.executeQuery();
				while(yearList.next()){
					String y = yearList.getString("ModYear");
					years.addItem(y);
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void popCurrLevels(JComboBox<String> level){
		level.addItem("Empty");
		level.addItem("1/4 Tank");
		level.addItem("1/2 Tank");
		level.addItem("3/4 Tank");
	}
	
	public void popToLevels(String currLevel, JComboBox<String> toLevel){
		toLevel.removeAllItems();//remove list items before executing query
		
		switch(currLevel){
			case "Empty":
				toLevel.addItem("1/4 Tank");
				toLevel.addItem("1/2 Tank");
				toLevel.addItem("3/4 Tank");
				toLevel.addItem("Full Tank");
				break;
			case "1/4 Tank":
				toLevel.addItem("1/2 Tank");
				toLevel.addItem("3/4 Tank");
				toLevel.addItem("Full Tank");
				break;
			case "1/2 Tank":
				toLevel.addItem("3/4 Tank");
				toLevel.addItem("Full Tank");
				break;
			case "3/4 Tank":
				toLevel.addItem("Full Tank");
				break;
		}
	}
	
	/*Convert text list value to its corresponding double value*/
	public double[] convertLevels(String [] levels){
		double [] doubLevels = new double[2];
		
		String curr = levels[0];
		String to = levels[1];
		
		switch(curr){//set double value for current level
		case "Empty":
			doubLevels[0] = 0;
			break;
		case "1/4 Tank":
			doubLevels[0] = 0.25;
			break;
		case "1/2 Tank":
			doubLevels[0] = 0.50;
			break;
		case "3/4 Tank":
			doubLevels[0] = 0.75;
			break;
		}
		
		switch(to){//set double value for fill to level
		case "1/4 Tank":
			doubLevels[1] = 0.25;
			break;
		case "1/2 Tank":
			doubLevels[1] = 0.50;
			break;
		case "3/4 Tank":
			doubLevels[1] = 0.75;
			break;
		case "Full Tank":
			doubLevels[1] = 1;
			break;
		}
		
		return doubLevels;
	}
}
