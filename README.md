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
```java
	public void setWebPageURL(){
		if(this.model.contains(" ")){
		String tmpModel = this.model.replaceAll(" ", "-");
		this.webPageURL = String.format("https://www.edmunds.com/%s/%s/%s/features-specs/", 			this.make.toLowerCase(), tmpModel.toLowerCase(), this.year);
	}
  ```
  ```
