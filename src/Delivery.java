import java.time.LocalDate;

public class Delivery {
    private int delivery_id;
    private String shipping_company;
    private LocalDate delivery_date;
    private String status;
    private int cost;
    private Orders orders;

    public Delivery(int delivery_id, String shipping_company, LocalDate delivery_date, String status, int cost, Orders orders) {
        this.delivery_id = delivery_id;
        this.shipping_company = shipping_company;
        this.delivery_date = delivery_date;
        this.status = status;
        this.cost = cost;
        this.orders = orders;
    }

    public int getDelivery_id() {
        return delivery_id;
    }

    public void setDelivery_id(int delivery_id) {
        this.delivery_id = delivery_id;
    }

    public String getShipping_company() {
        return shipping_company;
    }

    public void setShipping_company(String shipping_company) {
        this.shipping_company = shipping_company;
    }

    public LocalDate getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(LocalDate delivery_date) {
        this.delivery_date = delivery_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Orders getSales() {
        return orders;
    }

    public void setSales(Orders orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "delivery_id=" + delivery_id +
                ", shipping_company='" + shipping_company + '\'' +
                ", delivery_date=" + delivery_date +
                ", status='" + status + '\'' +
                ", cost=" + cost +
                ", orders=" +orders +
                '}';
    }

}
