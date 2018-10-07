package com.kquallis.vehicle;

public class Vehicle {
	private double fuelTankCapacity;
	private String make;
	private String model;	
	private String webPageURL;
	private String year;	

	public Vehicle(String make, String model, String year){
		setMake(make);
		setModel(model);
		setYear(year);
		setWebPageURL();
	}

	public int calcCostToLevel(double currentLevel, double requestedLevel, double fuelPricePerGallon){
		fuelPricePerGallon+=0.009;//example: $2.89/gallon is $2.899/gallon

		double galsToLevel = calcGalsToLevel(currentLevel, requestedLevel); 

		return (int) Math.ceil(galsToLevel * fuelPricePerGallon);
	}

	public double calcGalsToLevel(double currentTankLevel, double requestedTankLevel) {//
		if(currentTankLevel == 0){			
			return requestedTankLevel * this.fuelTankCapacity;						
		}
		double currGalls = currentTankLevel * this.fuelTankCapacity;
		double reqGalls = requestedTankLevel * this.fuelTankCapacity;	

		return reqGalls - currGalls;
	}

	public double getFuelTankCapacity() {
		return fuelTankCapacity;
	}

	public String getMake() {
		return make;
	}

	public String getModel() {
		return model;
	}

	public String getWebPageURL() {
		return webPageURL;
	}

	public String getYear() {
		return year;
	}

	public void setFuelTankCapacity(String fuelTankCap) {
		this.fuelTankCapacity = Double.parseDouble( fuelTankCap );
	}

	public void setMake(String mke) {
		this.make = mke;
	}

	public void setModel(String mdl) {
		this.model = mdl;
	}

	public void setWebPageURL(){
		if(this.model.contains(" ")){
			String tmpModel = this.model.replaceAll(" ", "-");
			this.webPageURL = String.format("https://www.edmunds.com/%s/%s/%s/features-specs/", this.make.toLowerCase(),
					tmpModel.toLowerCase(), this.year);
		}
		else{
			this.webPageURL = String.format("https://www.edmunds.com/%s/%s/%s/features-specs/", this.make.toLowerCase(),
					this.model.toLowerCase(), this.year);
		}
	}

	public void setWebPageURL(String make, String model, String year) {
		if(model.contains(" ")){
			String tmpModel = model.replaceAll(" ", "-");
			this.webPageURL = String.format("https://www.edmunds.com/%s/%s/%s/features-specs/", make.toLowerCase(), 
					tmpModel.toLowerCase(), year);
		}
		else{
			this.webPageURL = String.format("https://www.edmunds.com/%s/%s/%s/features-specs/", make.toLowerCase(),
					model.toLowerCase(), year);
		}
	}

	public void setYear(String yr) {
		this.year = yr;
	}

}
