package com.salesmanager.core.model.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object used to contain the integration information with an external gateway Uses simple JSON to
 * encode the object in JSON by implementing JSONAware and uses jackson JSON decode to parse JSON
 * String to an Object
 * 
 * @author csamson
 *
 */
public class IntegrationConfiguration implements JSONAware {


  public final static String TEST_ENVIRONMENT = "TEST";
  public final static String PRODUCTION_ENVIRONMENT = "PRODUCTION";

  private String moduleCode;
  private boolean active;
  private boolean defaultSelected;
  private Map<String, String> integrationKeys = new HashMap<String, String>();
  private Map<String, List<String>> integrationOptions = new HashMap<String, List<String>>();
  private String environment;


  public String getModuleCode() {
				System.out.println("$#4701#"); return moduleCode;
  }

  @JsonProperty("moduleCode")
  public void setModuleCode(String moduleCode) {
    this.moduleCode = moduleCode;
  }

  public boolean isActive() {
				System.out.println("$#4703#"); System.out.println("$#4702#"); return active;
  }

  @JsonProperty("active")
  public void setActive(boolean active) {
    this.active = active;
  }

  public Map<String, String> getIntegrationKeys() {
				System.out.println("$#4704#"); return integrationKeys;
  }

  @JsonProperty("integrationKeys")
  public void setIntegrationKeys(Map<String, String> integrationKeys) {
    this.integrationKeys = integrationKeys;
  }


  protected String getJsonInfo() {

    StringBuilder returnString = new StringBuilder();
    returnString.append("{");
    returnString.append("\"moduleCode\"").append(":\"").append(this.getModuleCode()).append("\"");
    returnString.append(",");
    returnString.append("\"active\"").append(":").append(this.isActive());
    returnString.append(",");
    returnString.append("\"defaultSelected\"").append(":").append(this.isDefaultSelected());
    returnString.append(",");
    returnString.append("\"environment\"").append(":\"").append(this.getEnvironment()).append("\"");
				System.out.println("$#4705#"); return returnString.toString();

  }


  @SuppressWarnings("unchecked")
  @Override
  public String toJSONString() {


    StringBuilder returnString = new StringBuilder();
    returnString.append(getJsonInfo());

				System.out.println("$#4707#"); System.out.println("$#4706#"); if (this.getIntegrationKeys().size() > 0) {

      JSONObject data = new JSONObject();
      Set<String> keys = this.getIntegrationKeys().keySet();
      for (String key : keys) {
        data.put(key, this.getIntegrationKeys().get(key));
      }
      String dataField = data.toJSONString();

      returnString.append(",").append("\"integrationKeys\"").append(":");
      returnString.append(dataField.toString());


    }


				System.out.println("$#4709#"); System.out.println("$#4708#"); if (this.getIntegrationOptions() != null && this.getIntegrationOptions().size() > 0) {

      // JSONObject data = new JSONObject();
      StringBuilder optionDataEntries = new StringBuilder();
      Set<String> keys = this.getIntegrationOptions().keySet();
      int countOptions = 0;
      int keySize = 0;

      for (String key : keys) {
        List<String> values = this.getIntegrationOptions().get(key);
								System.out.println("$#4711#"); if (values != null) {
										System.out.println("$#4712#"); keySize++;
        }
      }

      for (String key : keys) {

        List<String> values = this.getIntegrationOptions().get(key);
								System.out.println("$#4713#"); if (values == null) {
          continue;
        }
        StringBuilder optionsEntries = new StringBuilder();
        StringBuilder dataEntries = new StringBuilder();

        int count = 0;
        for (String value : values) {

          dataEntries.append("\"").append(value).append("\"");
										System.out.println("$#4716#"); System.out.println("$#4715#"); System.out.println("$#4714#"); if (count < values.size() - 1) {
            dataEntries.append(",");
          }
										System.out.println("$#4717#"); count++;
        }

        optionsEntries.append("[").append(dataEntries.toString()).append("]");

        optionDataEntries.append("\"").append(key).append("\":").append(optionsEntries.toString());

								System.out.println("$#4720#"); System.out.println("$#4719#"); System.out.println("$#4718#"); if (countOptions < keySize - 1) {
          optionDataEntries.append(",");
        }
								System.out.println("$#4721#"); countOptions++;

      }
      String dataField = optionDataEntries.toString();

      returnString.append(",").append("\"integrationOptions\"").append(":{");
      returnString.append(dataField.toString());
      returnString.append("}");

    }


    returnString.append("}");


				System.out.println("$#4722#"); return returnString.toString();

  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  public String getEnvironment() {
				System.out.println("$#4723#"); return environment;
  }

  public Map<String, List<String>> getIntegrationOptions() {
				System.out.println("$#4724#"); return integrationOptions;
  }

  public void setIntegrationOptions(Map<String, List<String>> integrationOptions) {
    this.integrationOptions = integrationOptions;
  }

  public boolean isDefaultSelected() {
				System.out.println("$#4726#"); System.out.println("$#4725#"); return defaultSelected;
  }

  public void setDefaultSelected(boolean defaultSelected) {
    this.defaultSelected = defaultSelected;
  }



}
