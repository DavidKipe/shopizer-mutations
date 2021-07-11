package com.salesmanager.core.model.search;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class IndexProduct implements JSONAware {
	
	private String name;
	private Double price;
	private List<String> categories;//category code
	private String manufacturer;//id of the manufacturer
	private boolean available;
	private String description;
	private List<String> tags;//keywords ?
	private String highlight;
	private String store;
	private String lang;
	private String id;//required by the search framework

	@SuppressWarnings("unchecked")
	@Override
	public String toJSONString() {
		
		
		
		
		JSONObject obj = new JSONObject();
		obj.put("name", this.getName());
		obj.put("price", this.getPrice());
		obj.put("description", this.getDescription());
		obj.put("highlight", this.getHighlight());
		obj.put("store", this.getStore());
		obj.put("manufacturer", this.getManufacturer());
		obj.put("lang", this.getLang());
		obj.put("id", this.getId());
		System.out.println("$#4499#"); if(categories!=null) {
			JSONArray categoriesArray = new JSONArray();
			for(String category : categories) {
				categoriesArray.add(category);
			}
			obj.put("categories", categoriesArray);
		}
		
		System.out.println("$#4500#"); if(tags!=null) {
			JSONArray tagsArray = new JSONArray();
			for(String tag : tags) {
				tagsArray.add(tag);
			}
			obj.put("tags", tagsArray);
		}
		
		System.out.println("$#4501#"); return obj.toJSONString();

	}

	public String getName() {
		System.out.println("$#4502#"); return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public List<String> getCategories() {
		System.out.println("$#4503#"); return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public boolean isAvailable() {
		System.out.println("$#4505#"); System.out.println("$#4504#"); return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getDescription() {
		System.out.println("$#4506#"); return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getTags() {
		System.out.println("$#4507#"); return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getHighlight() {
		System.out.println("$#4508#"); return highlight;
	}

	public void setHighlight(String highlight) {
		this.highlight = highlight;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getPrice() {
		System.out.println("$#4509#"); return price;
	}

	public void setStore(String store) {
		this.store = store;
	}

	public String getStore() {
		System.out.println("$#4510#"); return store;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getLang() {
		System.out.println("$#4511#"); return lang;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		System.out.println("$#4512#"); return id;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getManufacturer() {
		System.out.println("$#4513#"); return manufacturer;
	}

}
