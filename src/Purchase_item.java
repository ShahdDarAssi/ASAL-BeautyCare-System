import java.time.LocalDate;

public class Purchase_item {
    private int item_id;
    private int quantity;
    private int cost_per_unit;
    private Purchase purchase;
    private Product product;

    public Purchase_item(int item_id, int quantity, int cost_per_unit, Purchase purchase,Product product) {
        this.item_id = item_id;
        this.quantity = quantity;
        this.cost_per_unit = cost_per_unit;
        this.purchase = purchase;
        this.product=product;
    }


    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }



    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCost_per_unit() {
        return cost_per_unit;
    }

    public void setCost_per_unit(int cost_per_unit) {
        this.cost_per_unit = cost_per_unit;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    @Override
    public String toString() {
        return "Purchase_item{" +
                "item_id=" + item_id +
                ", quantity=" + quantity +
                ", cost_per_unit=" + cost_per_unit +
                ", purchase=" + purchase +
                ", product=" + product +
                '}';
    }
}
