
public class Warehouse {

	private int warehouseId;
	private OnlineStore onlineStore;
	private String location;
	private int capacity;
	private String stockManager;

	public Warehouse() {
	}

	public Warehouse(int warehouseId, OnlineStore onlineStore, String location, int capacity, String stockManager) {
		super();
		this.warehouseId = warehouseId;
		this.onlineStore = onlineStore;
		this.location = location;
		this.capacity = capacity;
		this.stockManager = stockManager;
	}

	public int getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}

	public OnlineStore getOnlineStore() {
		return onlineStore;
	}

	public void setOnlineStore(OnlineStore onlineStore) {
		this.onlineStore = onlineStore;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getStockManager() {
		return stockManager;
	}

	public void setStockManager(String stockManager) {
		this.stockManager = stockManager;
	}
	public String getWarehouseName() {
        return location; 
    }
    
    public void setWarehouseName(String warehouseName) {
        this.location = warehouseName;
    }
    
}
