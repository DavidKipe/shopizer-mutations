package com.salesmanager.core.business.utils.ajax;

import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

public class AjaxPageableResponse extends AjaxResponse {
	
	
	private int startRow;
	public int getStartRow() {
		System.out.println("$#3384#"); return startRow;
	}



	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}



	private int endRow;
	private int totalRow;
	
	protected String getPageInfo() {
		
		StringBuilder returnString = new StringBuilder();
		returnString.append("\"startRow\"").append(":");
		returnString.append(this.startRow).append(",");
		returnString.append("\"endRow\"").append(":").append(this.endRow).append(",");
		returnString.append("\"totalRows\"").append(":").append(super.getData().size());
		System.out.println("$#3385#"); return returnString.toString();
		
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public String toJSONString() {
		
		StringBuilder returnString = new StringBuilder();
		
		returnString.append(getJsonInfo()).append(",");
		returnString.append(getPageInfo());

		System.out.println("$#3387#"); System.out.println("$#3386#"); if(this.getData().size()>0) {
			StringBuilder dataEntries = null;
			int count = 0;
			for(Map keyValue : this.getData()) {
				System.out.println("$#3388#"); if(dataEntries == null) {
					dataEntries = new StringBuilder();
				}
				JSONObject data = new JSONObject();
				Set<String> keys = keyValue.keySet();
				for(String key : keys) {
					data.put(key, keyValue.get(key));
				}
				String dataField = data.toJSONString();
				dataEntries.append(dataField);
				System.out.println("$#3391#"); System.out.println("$#3390#"); System.out.println("$#3389#"); if(count<super.getData().size()-1) {
					dataEntries.append(",");
				}
				System.out.println("$#3392#"); count ++;
			}
			
			returnString.append(",").append("\"data\"").append(":[");
			System.out.println("$#3393#"); if(dataEntries!=null) {
				returnString.append(dataEntries.toString());
			}
			returnString.append("]");
		}
		returnString.append("}}");

		
		System.out.println("$#3394#"); return returnString.toString();
		
		
		
	}



	public int getEndRow() {
		System.out.println("$#3395#"); return endRow;
	}



	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}



	public int getTotalRow() {
		System.out.println("$#3396#"); return totalRow;
	}



	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}

}
