//9/3/2018
package com.kquallis.dbinteraction;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;

import com.kquallis.webscraper.*;

public class DBInteraction {
	static Connection conn;
	private static String userName = "...";
	private static String dbms = "...";
	private static String password = "...";
	private static int portNumber = ...;
	private static String serverName = "...";
	static Statement stmnt;

	//9/22/2018
	public static void addMakeModYearData(String make){
		String model;
		String tmpMake = make.replace(" ", "-").toLowerCase();
		try {
			HashMap<String, ArrayList<String>> makeModelMap;

			makeModelMap = WebScraper.mapModels(tmpMake);
			ArrayList<String> modelList = makeModelMap.get(tmpMake);
			int listSize = modelList.size();

			for(int i = 0; i < listSize; i++){
				model = modelList.get(i);
				populateModelYearTable(tmpMake, model);
			}
		} 
		catch (IOException | SQLException e) {
			e.printStackTrace();
		}

	}
	public static void closeConnection(){
		try {
			conn.close();
			if(conn.isClosed()){
				System.out.println("The db connection has been closed");
			}

		} catch (SQLException sqle) {
			sqle.getMessage();
		}
	}

	//6/25/2018 - overload
	public static void closeConnection(Connection conn){
		try {
			conn.close();
			if(conn.isClosed()){
				System.out.println("The db connection has been closed");
			}

		} catch (SQLException sqle) {
			sqle.getMessage();
		}
	}

	public static void createMakeModelsTable() throws SQLException{
		String sql_stmt = "CREATE TABLE IF NOT EXISTS makemodels.`makemodelyear` (\n"        
				+ "    `make` VARCHAR(25) NOT NULL,\n"
				+ "    `model` VARCHAR(25) NOT NULL,\n"
				+ "    PRIMARY KEY (`make`, `model`)\n"
				+ ");";

		stmnt = conn.createStatement();

		stmnt.executeUpdate(sql_stmt);

		System.out.println("MakeModelYear table has been created");
	}

	public static Connection getConnection() throws SQLException {

		Properties connectionProps = new Properties();
		connectionProps.put("user", userName);
		connectionProps.put("password", password);

		if (dbms.equals("mysql")) {
			//	1-26-18:added ?useSSL=false to get rid of the SSL warning message
			conn = DriverManager.getConnection(
					"jdbc:" + dbms + "://" +
							serverName +
							":" + portNumber + "/"+"?useSSL=false",
							connectionProps);
		}
		System.out.println("Connected to database");
		return conn;
	}

	//9-12-2018
	public static void populateModelYearTable(String tmpMake) throws IOException, SQLException{
		String make, tmpModel, model, year;
		PreparedStatement insertStatement;

		if (tmpMake.contains(" ")){//if-else added on 8/15/2018
			make = tmpMake.replaceAll(" ", "-").toLowerCase();	
		}
		else{
			make = tmpMake.toLowerCase();
		}

		HashMap<String,ArrayList<String>> makeModelMap = WebScraper.mapModels(tmpMake);
		HashMap<String, ArrayList<String>> modYearMap = WebScraper.mapModelYears(makeModelMap,tmpMake);
		for(Entry<String, ArrayList<String>> entry: modYearMap.entrySet()) {

			int hyphen;//tells the position in the String that is a hyphen

			tmpModel = entry.getKey();
			if(tmpModel.contains("-")){
				hyphen = tmpModel.indexOf("-");
				model = tmpModel.substring(0, hyphen) + "-" + 
						tmpModel.substring(hyphen+1, tmpModel.length());
			}
			else{
				model = entry.getKey().replace("-", " "); 
			}

			ArrayList<String> yearList = entry.getValue();
			for(int i = 0; i < yearList.size(); i++ ){
				if(make.contains("-")){
					make = tmpMake;//if the make name has spaces in it, it should be saved in the DB that way
				}
				year = yearList.get(i);
				insertStatement = conn.prepareStatement("INSERT INTO makemodels.modelyear (make, model, modyear) VALUES(?,?,?)");
				insertStatement.setString(1,make);
				insertStatement.setString(2,model);
				insertStatement.setString(3,year);
				insertStatement.execute();
			}
		}			
	}

	//9/22/2018
	public static void populateModelYearTable(String make, String model) throws IOException, SQLException{
		String year;
		PreparedStatement insertStatement;

		HashMap<String,ArrayList<String>> modYearMap = WebScraper.mapModelYears(make, model);
		for(Entry<String, ArrayList<String>> entry: modYearMap.entrySet()) {
			ArrayList<String> yearList = entry.getValue();

			for(int i = 0; i < yearList.size(); i++ ){
				year = yearList.get(i);
				insertStatement = conn.prepareStatement("INSERT INTO makemodels.modelyear (make, model, modyear) VALUES(?,?,?)");
				insertStatement.setString(1,make);
				insertStatement.setString(2,model);
				insertStatement.setString(3,year);
				insertStatement.execute();
			}
		}			
	}
	
	//one time
	public static void populateTable() throws IOException, SQLException{
		String key;
		String value;
		PreparedStatement insertStatement;

		ArrayList<String> makes = WebScraper.retrieveMakes();	
		HashMap<String,ArrayList<String>> makeModelMap = WebScraper.mapModels(makes);	

		conn = getConnection();

		for(Entry<String, ArrayList<String>> entry: makeModelMap.entrySet()) {
			key = entry.getKey();
			ArrayList<String> list = entry.getValue();
			for(int i = 0; i < list.size(); i++ ){
				value = list.get(i);
				insertStatement = conn.prepareStatement("INSERT INTO makemodels.makemodel (make, model) VALUES(?,?)");
				insertStatement.setString(1,key);
				insertStatement.setString(2,value);
				insertStatement.execute();
			}
		}			
	}
	
	//	
	public static void setConnectionProps(){
		Scanner scan = new Scanner(System.in);
		Properties connectionProps = new Properties();

		System.out.println("Enter user name: ");
		userName = scan.next();

		System.out.println("Enter password: ");
		password = scan.next();

		System.out.println("Enter dbms: ");
		dbms = scan.next();

		System.out.println("Enter server name: ");
		serverName = scan.next();

		System.out.println("Enter port number: ");
		portNumber = scan.nextInt();

		connectionProps.put("user", userName);
		connectionProps.put("password", password);
		connectionProps.put("dbms", dbms);
		connectionProps.put("serverName", serverName);
		connectionProps.put("portNumber", portNumber);
	}

}