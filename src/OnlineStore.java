
public class OnlineStore {

	private int storeId;
	private String storeName;
	private String website;
	private String contactEmail;
	private String contactPhone;
	private String managerName;

	public OnlineStore() {
	}

	public OnlineStore(int storeId, String storeName, String website, String contactEmail, String contactPhone,
			String managerName) {
		super();
		this.storeId = storeId;
		this.storeName = storeName;
		this.website = website;
		this.contactEmail = contactEmail;
		this.contactPhone = contactPhone;
		this.managerName = managerName;
	}

	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

}
