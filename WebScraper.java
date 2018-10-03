//9-2-2018

package com.kquallis.webscraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScraper {

	public static void getModels(String make) throws IOException{
		String url;
		url =  String.format("https://www.edmunds.com/inventory/srp.html?make=%s&radius=100", make);
		Document edmundsPage = Jsoup.connect(url).get();
		Elements modelListing = edmundsPage.select("[data-tracking-value=model] > option");

		for(Element e: modelListing){	
			if (e.text().contains("Any Model") || e.text().equals("Truck")
					|| e.text().equals("Van")){
				continue; //overlooks the first element of the dropdown list
			}
		}
	}

	//5/11/2018 The HTML structure of the page changed. So, I changed the code to accommodate
	public static String getTankCap(String url) throws IOException{
		String tankCap = "";

		Document edmundsPage = Jsoup.connect(url).get();

		Elements tds = edmundsPage.select("td");
		for(Element e: tds){
			if( e.html().contains("gal.") ){//added on 9-2-2018
				if(e.html().substring(0, 4).contains(".")){
					tankCap = e.html().substring(0,4);
				}
				else{//added on 9-2-2018
					tankCap = e.html().substring(0,2);																				
				}
			}
		}	
		return tankCap;
	}
	public static HashMap<String,ArrayList<String>> mapModels(ArrayList<String> makes) throws IOException{

		HashMap<String,ArrayList<String>> modelMap = new HashMap<>();
		Document makeModelPage;
		Elements modelListing;
		String make, url;

		for(int i= 0; i < makes.size(); i++){
			make = makes.get(i).replaceAll(" ", "-");

			modelMap.put(make, new ArrayList<String>());

			url =  String.format("https://www.edmunds.com/inventory/srp.html?make=%s&radius=100", make);

			makeModelPage = Jsoup.connect(url).get();
			modelListing = makeModelPage.select("[data-tracking-value=model] > option");
			for(Element m: modelListing){
				if (m.text().contains("Any Model") || m.text().equals("Truck")
						|| m.text().equals("Van")){
					continue; //overlooks the first element of the dropdown list
				}
				modelMap.get(make).add(m.text());
			}
		}
		return modelMap;
	}

	//OVERLOAD - 8/23/2018
	public static HashMap<String,ArrayList<String>> mapModels(String make) throws IOException{

		HashMap<String,ArrayList<String>> modelMap = new HashMap<>();
		Document makeModelPage;
		Elements modelListing;
		String tmpMake, url;
		modelMap.put(make, new ArrayList<String>());

		url =  String.format("https://www.edmunds.com/inventory/srp.html?make=%s&radius=100", make);

		makeModelPage = Jsoup.connect(url).get();
		modelListing = makeModelPage.select("[data-tracking-value=model] > option");
		int repeat = 0;//if an unwanted list entry occurs twice..will be used stop reading the list
		for(Element mod: modelListing){
			if (mod.text().equals("Any Model") || mod.text().equals("Truck")
					|| mod.text().equals("Van")){
				if(mod.text().equals("Any Model")){
					repeat++;//increment counter
					continue; //overlooks the first element of the dropdown list
				}
			}
			else{
				modelMap.get(make).add(mod.text());					
			}
			if(repeat == 2){
				break;//if "Any Model" qppears in the list twice
			}
		}
		ArrayList<String> models = modelMap.get(make);
		String first = models.get(0);
		String last = models.get(models.size()-1);

		if(last.equals(first)){//9-17-2018
			models.remove(models.size()-1);//removes duplicate model list item
			modelMap.clear();//
			modelMap.put(make, models);//
		}

		return modelMap;
	}

	//7-20-2018...
	public static HashMap<String,ArrayList<String>> mapModelYears(	HashMap<String,ArrayList<String>> modMap ) throws IOException{

		HashMap<String,ArrayList<String>> modelYearMap = new HashMap<>();
		Document modelYearPage;
		Elements modelYearListing;
		String url;

		Iterator<Entry<String, ArrayList<String>>> it = modMap.entrySet().iterator();

		while(it.hasNext()){
			Entry<String, ArrayList<String>> pair = it.next();
			String make = pair.getKey();
			ArrayList<String> models = pair.getValue();

			for(int j = 0; j < models.size(); j++){
				String modTmp = models.get(j); System.out.println(modTmp);
				String model = modTmp.replaceAll(" ", "-").toLowerCase();//in an url, a hyphen must be used in the place of spaces

				url =  String.format("https://www.edmunds.com/amp/%s/%s/", make.toLowerCase(), model.toLowerCase());

				modelYearPage = Jsoup.connect(url).get();
				modelYearListing = modelYearPage.select("ul.model-years_list > li.model-years-item > p.my-1 > a.model-years-link");
				ArrayList<String> years = new ArrayList<>();		

				for(Element year: modelYearListing){
					years.add(year.text());
				}
				modelYearMap.put(model, years);		
			}//for	
		}//while
		return modelYearMap;
	}//method

	//7-23-2018 - Overload
	public static HashMap<String,ArrayList<String>> mapModelYears(HashMap<String, ArrayList<String>> modelMap, String make ) throws IOException{

		HashMap<String,ArrayList<String>> newMakeModelMap = new HashMap<>();
		HashMap<String,ArrayList<String>> modelYearMap = new HashMap<>();

		for(Map.Entry<String, ArrayList<String>> entry : modelMap.entrySet()){
			newMakeModelMap.put(entry.getKey(), entry.getValue());
		}

		Document modelYearPage;
		Elements modelYearListing;
		String url;
		String model;

		Iterator<Entry<String, ArrayList<String>>> it = modelMap.entrySet().iterator();

		while(it.hasNext()){
			Entry<String, ArrayList<String>> pair = it.next();
			ArrayList<String> models = pair.getValue();
			for(int j = 0; j < models.size(); j++){
				if(models.get(j).equals("any model")){continue;}
				String modTmp = models.get(j).toLowerCase(); 
				modTmp = modTmp.replaceAll(" ", "-");//...

				model = modTmp.replace("/","");//...

				url =  String.format("https://www.edmunds.com/amp/%s/%s/", make.toLowerCase(), model);

				modelYearPage = Jsoup.connect(url).get();
				modelYearListing = modelYearPage.select("ul.model-years_list > li.model-years-item > p.my-1 > a.model-years-link");

				ArrayList<String> years = new ArrayList<>();//			
				for(Element year: modelYearListing){
					years.add(year.text());
				}
				modelYearMap.put(model, years);		
			}		
		}
		return modelYearMap;
	}

	//8-31-2018 - Overload	
	public static HashMap<String,ArrayList<String>> mapModelYears(String make, String model ) throws IOException{
		HashMap<String,ArrayList<String>> modelYearMap = new HashMap<>();

		Document modelYearPage;
		Elements modelYearListing;
		String url, tmpMod1, tmpMod2;

		tmpMod1 = model.replaceAll(" ", "-").toLowerCase();//...

		if(tmpMod1.contains("/")){
			tmpMod2 = tmpMod1.replace("/","");//Chevrolet
		}
		else{
			tmpMod2 = tmpMod1;
		}

		url =  String.format("https://www.edmunds.com/amp/%s/%s/", make.toLowerCase(), tmpMod2);

		modelYearPage = Jsoup.connect(url).get();
		modelYearListing = modelYearPage.select("ul.model-years_list > li.model-years-item > p.my-1 > a.model-years-link");
		ArrayList<String> years = new ArrayList<>();			
		for(Element year: modelYearListing){
			years.add(year.text());
		}
		modelYearMap.put(model, years);		

		return modelYearMap;
	}

	public static ArrayList<String> retrieveMakes() throws IOException{
		ArrayList<String> tempMakes = new ArrayList<>();

		Document edmundsHomeHomePage = Jsoup.connect("https://www.edmunds.com").get();
		Elements makeListing = edmundsHomeHomePage.select("select > option");

		for(Element e: makeListing){
			if (e.text().contains("Select")){
				continue; //overlooks the first element of the dropdown list
			}
			tempMakes.add( e.text() );//add car model name to makes list
		}
		return tempMakes;
	}

	//Overload
	public static ArrayList<String> retrieveMakes(String url) throws IOException{
		ArrayList<String> tempMakes = new ArrayList<>();

		Document edmundsHomeHomePage = Jsoup.connect(url).get();
		Elements makeListing = edmundsHomeHomePage.select("select > option");

		for(Element e: makeListing){
			if (e.text().contains("Select")){
				continue; //overlooks the first element of the dropdown list
			}
			tempMakes.add( e.text() );//add car model name to makes list
		}
		return tempMakes;
	}

}
