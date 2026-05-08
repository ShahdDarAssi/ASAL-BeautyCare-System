public class Returns_Product {
    private int return_id;
    private int quantity;
    private Returns returns;
    private Product product;

    public int getReturn_id() {
        return return_id;
    }

    public Returns_Product(int return_id, int quantity, Returns returns, Product product) {
        this.return_id = return_id;
        this.quantity = quantity;
        this.returns = returns;
        this.product = product;
    }

    public void setReturn_id(int return_id) {
        this.return_id = return_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Returns getReturns() {
        return returns;
    }

    public void setReturns(Returns returns) {
        this.returns = returns;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "Returns_Product{" +
                "return_product_id=" + return_id +
                ", quantity=" + quantity +
                ", returns=" + returns +
                ", product=" + product +
                '}';
    }
}
