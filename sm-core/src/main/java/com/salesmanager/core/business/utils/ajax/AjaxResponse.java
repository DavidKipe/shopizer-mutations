package com.salesmanager.core.business.utils.ajax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class AjaxResponse implements JSONAware {
	
	public final static int RESPONSE_STATUS_SUCCESS=0;
	public final static int RESPONSE_STATUS_FAIURE=-1;
	public final static int RESPONSE_STATUS_VALIDATION_FAILED=-2;
	public final static int RESPONSE_OPERATION_COMPLETED=9999;
	public final static int CODE_ALREADY_EXIST=9998;
	
	private int status;
	private List<Map<String,String>> data = new ArrayList<Map<String,String>>();
	private Map<String,String> dataMap = new HashMap<String,String>();
	private Map<String,String> validationMessages = new HashMap<String,String>();
	public Map<String, String> getValidationMessages() {
		System.out.println("$#3397#"); return validationMessages;
	}
	public void setValidationMessages(Map<String, String> validationMessages) {
		this.validationMessages = validationMessages;
	}
	public int getStatus() {
		System.out.println("$#3398#"); return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	protected List<Map<String,String>> getData() {
		System.out.println("$#3399#"); return data;
	}
	
	public void addDataEntry(Map<String,String> dataEntry) {
		this.data.add(dataEntry);
	}
	
	public void addEntry(String key, String value) {
		dataMap.put(key, value);
	}
	
	
	public void setErrorMessage(Throwable t) {
		System.out.println("$#3400#"); this.setStatusMessage(t.getMessage());
	}
	
	public void setErrorString(String t) {
		System.out.println("$#3401#"); this.setStatusMessage(t);
	}
	

	public void addValidationMessage(String fieldName, String message) {
		this.validationMessages.put(fieldName, message);
	}
	
	private String statusMessage = null;
	
	
	public String getStatusMessage() {
		System.out.println("$#3402#"); return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	
	protected String getJsonInfo() {
		
		StringBuilder returnString = new StringBuilder();
		returnString.append("{");
		returnString.append("\"response\"").append(":");
		returnString.append("{");
		returnString.append("\"status\"").append(":").append(this.getStatus());
		System.out.println("$#3403#"); if(this.getStatusMessage()!=null && this.getStatus()!=0) {
			returnString.append(",").append("\"statusMessage\"").append(":\"").append(JSONObject.escape(this.getStatusMessage())).append("\"");
		}
		System.out.println("$#3405#"); return returnString.toString();
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String toJSONString() {
		StringBuilder returnString = new StringBuilder();
		
		returnString.append(getJsonInfo());

		System.out.println("$#3407#"); System.out.println("$#3406#"); if(this.getData().size()>0) {
			StringBuilder dataEntries = null;
			int count = 0;
			for(Map keyValue : this.getData()) {
				System.out.println("$#3408#"); if(dataEntries == null) {
					dataEntries = new StringBuilder();
				}
				JSONObject data = new JSONObject();
				Set<String> keys = keyValue.keySet();
				for(String key : keys) {
					data.put(key, keyValue.get(key));
				}
				String dataField = data.toJSONString();
				dataEntries.append(dataField);
				System.out.println("$#3411#"); System.out.println("$#3410#"); System.out.println("$#3409#"); if(count<this.data.size()-1) {
					dataEntries.append(",");
				}
				System.out.println("$#3412#"); count ++;
			}
			
			returnString.append(",").append("\"data\"").append(":[");
			System.out.println("$#3413#"); if(dataEntries!=null) {
				returnString.append(dataEntries.toString());
			}
			returnString.append("]");
		}
		
		System.out.println("$#3415#"); System.out.println("$#3414#"); if(this.getDataMap().size()>0) {
			StringBuilder dataEntries = null;
			int count = 0;
			for(String key : this.getDataMap().keySet()) {
				System.out.println("$#3416#"); if(dataEntries == null) {
					dataEntries = new StringBuilder();
				}
				
				dataEntries.append("\"").append(key).append("\"");
				dataEntries.append(":");
				dataEntries.append("\"").append(this.getDataMap().get(key)).append("\"");

				System.out.println("$#3419#"); System.out.println("$#3418#"); System.out.println("$#3417#"); if(count<this.getDataMap().size()-1) {
					dataEntries.append(",");
				}
				System.out.println("$#3420#"); count ++;
			}

			System.out.println("$#3421#"); if(dataEntries!=null) {
				returnString.append(",").append(dataEntries.toString());
			}
		}
		
		System.out.println("$#3422#"); if(CollectionUtils.isNotEmpty(this.getValidationMessages().values())) {
			StringBuilder dataEntries = null;
			int count = 0;
			for(String key : this.getValidationMessages().keySet()) {
				System.out.println("$#3423#"); if(dataEntries == null) {
					dataEntries = new StringBuilder();
				}
				dataEntries.append("{");
				dataEntries.append("\"field\":\"").append(key).append("\"");
				dataEntries.append(",");
				dataEntries.append("\"message\":\"").append(this.getValidationMessages().get(key)).append("\"");
				dataEntries.append("}");

				System.out.println("$#3426#"); System.out.println("$#3425#"); System.out.println("$#3424#"); if(count<this.getValidationMessages().size()-1) {
					dataEntries.append(",");
				}
				System.out.println("$#3427#"); count ++;
			}
			
			returnString.append(",").append("\"validations\"").append(":[");
			System.out.println("$#3428#"); if(dataEntries!=null) {
				returnString.append(dataEntries.toString());
			}
			returnString.append("]");

		}
		
		returnString.append("}}");

		
		System.out.println("$#3429#"); return returnString.toString();

		
	}
	public Map<String,String> getDataMap() {
		System.out.println("$#3430#"); return dataMap;
	}
	public void setDataMap(Map<String,String> dataMap) {
		this.dataMap = dataMap;
	}

}
