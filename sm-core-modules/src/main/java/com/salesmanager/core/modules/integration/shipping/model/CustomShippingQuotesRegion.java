package com.salesmanager.core.modules.integration.shipping.model;

import java.util.List;

import org.json.simple.JSONAware;

public class CustomShippingQuotesRegion implements JSONAware {
	
	private String customRegionName;//a name given by the merchant for this custom region
	private List<String> countries;//a list of country code for this region
	
	private List<CustomShippingQuoteWeightItem> quoteItems;//price max weight

	public void setQuoteItems(List<CustomShippingQuoteWeightItem> quoteItems) {
		this.quoteItems = quoteItems;
	}

	public List<CustomShippingQuoteWeightItem> getQuoteItems() {
		System.out.println("$#4899#"); return quoteItems;
	}

	public void setCountries(List<String> countries) {
		this.countries = countries;
	}

	public List<String> getCountries() {
		System.out.println("$#4900#"); return countries;
	}

	public void setCustomRegionName(String customRegionName) {
		this.customRegionName = customRegionName;
	}

	public String getCustomRegionName() {
		System.out.println("$#4901#"); return customRegionName;
	}
	

	public String toJSONString() {
		

		StringBuilder returnString = new StringBuilder();
		returnString.append("{");
		returnString.append("\"customRegionName\"").append(":\"").append(this.getCustomRegionName()).append("\"");
		
		
		
		System.out.println("$#4902#"); if(countries!=null) {
			returnString.append(",");
			StringBuilder coutriesList = new StringBuilder();
			int countCountry = 0;
			coutriesList.append("[");
			for(String country : countries) {
				coutriesList.append("\"").append(country).append("\"");
				System.out.println("$#4903#"); countCountry ++;
				System.out.println("$#4905#"); System.out.println("$#4904#"); if(countCountry<countries.size()) {
					coutriesList.append(",");
				}
			}
			
			coutriesList.append("]");
			returnString.append("\"countries\"").append(":").append(coutriesList.toString());
		}
		
		System.out.println("$#4906#"); if(quoteItems!=null) {
			returnString.append(",");
			StringBuilder quotesList = new StringBuilder();
			int countQuotes = 0;
			quotesList.append("[");
			for(CustomShippingQuoteWeightItem quote : quoteItems) {
				quotesList.append(quote.toJSONString());
				System.out.println("$#4907#"); countQuotes ++;
				System.out.println("$#4909#"); System.out.println("$#4908#"); if(countQuotes<quoteItems.size()) {
					quotesList.append(",");
				}
			}
			quotesList.append("]");

			returnString.append("\"quoteItems\"").append(":").append(quotesList.toString());
		}
		returnString.append("}");
		System.out.println("$#4910#"); return returnString.toString();
		
		
	}


}
