import java.util.Date;

public class Orders {

    private int orderId;
    private int customerId;
    private Date orderDate;
    private String status;
    private double totalAmount;
    private double discount;
    private double finalAmount;
    private String paymentStatus;
    private double cost;
    private String notes;
    private String createdAt;
    private String lastUpdated;

    public Orders() {
    }


    public Orders(int orderId, int customerId, Date orderDate, String status,
                  double totalAmount, double discount, double finalAmount,
                  String paymentStatus, double cost, String notes,
                  String createdAt, String lastUpdated) {

        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.discount = discount;
        this.finalAmount = finalAmount;
        this.paymentStatus = paymentStatus;
        this.cost = cost;
        this.notes = notes;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
    }



    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", orderDate='" + orderDate + '\'' +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", discount=" + discount +
                ", finalAmount=" + finalAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", cost=" + cost +
                ", notes='" + notes + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                '}';
    }
}