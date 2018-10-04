## Welcome!

My name is Kadeem Quallis and I enjoy making thoughts a reality through code.

### Ga$timate
This application estimates the cost of filling a gas tank. It gives you the ability to select the make,
model and year of a vehicle, enter a gas price, and estimate the cost of filling the gas tank to a selected level. I developed this using Java, the jsoup HTML Parser and a MySQL database.

```java
	public Gastimate(String make, String model, String year){
		setMake(make);
		setModel(model);
		setYear(year);
		setWebPageURL();
	}
  ```
  
  ```java
	public void setWebPageURL(){
		if(this.model.contains(" ")){
		String tmpModel = this.model.replaceAll(" ", "-");
		this.webPageURL = String.format("https://www.edmunds.com/%s/%s/%s/features-specs/",this.make.toLowerCase(),tmpModel.toLowerCase(), this.year);
	}
```
  
```java
  public static String getTankCap(String url) throws IOException{
	String tankCap = "";

	Document edmundsPage = Jsoup.connect(url).get();

	Elements tds = edmundsPage.select("td");
	for(Element e: tds){
		if( e.html().contains("gal.") ){
			if(e.html().substring(0, 4).contains(".")){
				tankCap = e.html().substring(0,4);
			}
			else{
				tankCap = e.html().substring(0,2);																				
			}
		}
	}	
	return tankCap;
}
  ```
  
  ```java
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
	```
  
