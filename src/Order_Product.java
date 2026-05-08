public class Order_Product {

    private int orderId;
    private int productId;
    private int quantity;
    private double priceAtOrder;
    private double subtotal;

    public Order_Product(int orderId, int productId, int quantity, double priceAtOrder, double subtotal) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
        this.subtotal = subtotal;
    }

    public Order_Product(int orderId, int productId, int quantity, double priceAtOrder) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
        this.subtotal = quantity * priceAtOrder;
    }


    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = this.quantity * this.priceAtOrder;
    }

    public double getPriceAtOrder() {
        return priceAtOrder;
    }

    public void setPriceAtOrder(double priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
        this.subtotal = this.quantity * this.priceAtOrder;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "Order_Product{" +
                "orderId=" + orderId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", priceAtOrder=" + priceAtOrder +
                ", subtotal=" + subtotal +
                '}';
    }
}