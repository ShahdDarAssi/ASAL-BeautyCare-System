
public class Inventory {

	private int quantity;
	private Warehouse warehouse;
	private Product product;

	public Inventory() {
	}

	public Inventory(int quantity, Warehouse warehouse, Product product) {
	
		this.quantity = quantity;
		this.warehouse = warehouse;
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

}
