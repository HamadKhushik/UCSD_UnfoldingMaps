package module3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

public class LifeExpectancy extends PApplet{
	
	UnfoldingMap map;
	Map<String, Float> lifeExpByCountry;
	
	List<Feature> countries; // list of features for countries
	List<Marker> countryMarkers; // list of markers for the countries

	
	public void setup() {
		size(950, 600, OPENGL);  // setup the canvas 950 x 600
		
		//map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());  // define map provider
		map = new UnfoldingMap(this, 200, 50, 700, 500, new Microsoft.RoadProvider());
		
		map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map); // make map interactive
	    
	    // build the map(HashMap) for life expectancy
	    lifeExpByCountry = loadLifeExpFromCSV("LifeExpectancyWorldBankModule3.csv");
	    
	    // load the features for countries from the json file
	    countries = GeoJSONReader.loadData(this, "countries.geo.json");
	    
	    // create markers from the features
	    countryMarkers = MapUtils.createSimpleMarkers(countries);
	    
	    // add markers to map(world map)
	    map.addMarkers(countryMarkers);
	    
	    // color the countries according to life expectancy
	    shadeCountries();
		
	}
	
	private void shadeCountries() {
		
		for (Marker marker : countryMarkers) {
			String countryID = marker.getId();
			//System.out.println(countryID);
			
			// check if country ID exists in the map(HashMap)
			if (lifeExpByCountry.containsKey(countryID)) {
				
				// get life expectancy of that country
				float lifeExp = lifeExpByCountry.get(countryID);
				
				// get and set color level according to life expectancy
				int colorLevel = (int) map(lifeExp, 40, 90, 10, 255); // map() translates range 40-90 to 10-255
				marker.setColor(color(255 - colorLevel, 100, colorLevel));
			}
			else {
				// if country ID is not in map, shade the country grey
				marker.setColor(color(150, 150, 150));
			}
		}
	}
	
	private Map<String, Float> loadLifeExpFromCSV(String filename){
		
		//constructor for hashmap
		Map<String, Float> lifeExp = new HashMap<String, Float>();
		
		// read each row of the file
		String [] rows = loadStrings(filename);
		
		// iterate through the rows
		for (String row : rows) {
			
			// split each row into columns
			String [] columns = row.split(",");
			
			// check if row has min 6 columns and column[5] has real value
			if ( columns.length == 6 && !columns[5].equals("..")  && !columns[5].isEmpty()){
				//System.out.println(columns[4] + columns[5]);
				
				String ageExp = columns[5];
				
				try {
				float value = Float.parseFloat(columns[5]);
				lifeExp.put(columns[4], value); // load the data to map(HashMap)
				//System.out.println(columns[4]);
				//System.out.println("..");
				}
				catch (Exception e) {
					System.err.println("Error with value " + ageExp + " for country " + columns[3] + " with Country ID " + columns[4]);
				}
			}
		}
		
		
		return lifeExp;
	}
	
	public void draw() {
		map.draw();
		
	}
}
