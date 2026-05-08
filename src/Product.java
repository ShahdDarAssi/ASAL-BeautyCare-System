
public class Product {

	private int product_id;
	private String product_name;
	private double price;
	private String brand;
	private String product_description;
	private Category category;

	public Product() {
	}

	public Product(int product_id, String product_name, double price, String brand,
			String product_description,Category category) {
		super();
		this.product_id = product_id;
		this.category = category;
		this.product_name = product_name;
		this.price = price;
		this.brand = brand;
		this.product_description = product_description;
	}

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getProduct_description() {
		return product_description;
	}

	public void setProduct_description(String product_description) {
		this.product_description = product_description;
	}



}
