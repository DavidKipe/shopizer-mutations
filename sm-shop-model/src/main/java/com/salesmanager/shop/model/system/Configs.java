package com.salesmanager.shop.model.system;

public class Configs {

	private String facebook;
	private String pinterest;
	private String ga;
	private String instagram;

	private boolean allowOnlinePurchase;
	private boolean displaySearchBox;
	private boolean displayContactUs;
	private boolean displayShipping;

	private boolean displayCustomerSection =false;
	private boolean displayAddToCartOnFeaturedItems = false;
	private boolean displayCustomerAgreement = false;
	private boolean displayPagesMenu = true;

	public String getFacebook() {
		System.out.println("$#9379#"); return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getPinterest() {
		System.out.println("$#9380#"); return pinterest;
	}

	public void setPinterest(String pinterest) {
		this.pinterest = pinterest;
	}

	public String getGa() {
		System.out.println("$#9381#"); return ga;
	}

	public void setGa(String ga) {
		this.ga = ga;
	}

	public String getInstagram() {
		System.out.println("$#9382#"); return instagram;
	}

	public void setInstagram(String instagram) {
		this.instagram = instagram;
	}

	public boolean isAllowOnlinePurchase() {
		System.out.println("$#9384#"); System.out.println("$#9383#"); return allowOnlinePurchase;
	}

	public void setAllowOnlinePurchase(boolean allowOnlinePurchase) {
		this.allowOnlinePurchase = allowOnlinePurchase;
	}

	public boolean isDisplaySearchBox() {
		System.out.println("$#9386#"); System.out.println("$#9385#"); return displaySearchBox;
	}

	public void setDisplaySearchBox(boolean displaySearchBox) {
		this.displaySearchBox = displaySearchBox;
	}

	public boolean isDisplayContactUs() {
		System.out.println("$#9388#"); System.out.println("$#9387#"); return displayContactUs;
	}

	public void setDisplayContactUs(boolean displayContactUs) {
		this.displayContactUs = displayContactUs;
	}

	public boolean isDisplayShipping() {
		System.out.println("$#9390#"); System.out.println("$#9389#"); return displayShipping;
	}

	public void setDisplayShipping(boolean displayShipping) {
		this.displayShipping = displayShipping;
	}

	public boolean isDisplayCustomerSection() {
		System.out.println("$#9392#"); System.out.println("$#9391#"); return displayCustomerSection;
	}

	public void setDisplayCustomerSection(boolean displayCustomerSection) {
		this.displayCustomerSection = displayCustomerSection;
	}

	public boolean isDisplayAddToCartOnFeaturedItems() {
		System.out.println("$#9394#"); System.out.println("$#9393#"); return displayAddToCartOnFeaturedItems;
	}

	public void setDisplayAddToCartOnFeaturedItems(boolean displayAddToCartOnFeaturedItems) {
		this.displayAddToCartOnFeaturedItems = displayAddToCartOnFeaturedItems;
	}

	public boolean isDisplayCustomerAgreement() {
		System.out.println("$#9396#"); System.out.println("$#9395#"); return displayCustomerAgreement;
	}

	public void setDisplayCustomerAgreement(boolean displayCustomerAgreement) {
		this.displayCustomerAgreement = displayCustomerAgreement;
	}

	public boolean isDisplayPagesMenu() {
		System.out.println("$#9398#"); System.out.println("$#9397#"); return displayPagesMenu;
	}

	public void setDisplayPagesMenu(boolean displayPagesMenu) {
		this.displayPagesMenu = displayPagesMenu;
	}
}
