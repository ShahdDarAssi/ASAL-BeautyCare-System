class ProductQuantity {
	
    private int productId;
    private int totalQuantity;

    public ProductQuantity(int productId, int totalQuantity) {
        this.productId = productId;
        this.totalQuantity = totalQuantity;
    }

    public int getProductId() {
        return productId;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }
}