import java.time.LocalDate;

public class Payment {

    private int payment_id;
    private int amount;
    private LocalDate date;
    private String method;


    public Payment(int payment_id, int amount, LocalDate date, String method) {
        this.payment_id = payment_id;
        this.amount = amount;
        this.date = date;
        this.method = method;
    }

    public int getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(int payment_id) {
        this.payment_id = payment_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
    @Override
    public String toString() {
        return "Payment{" +
                "payment_id=" + payment_id +
                ", amount=" + amount +
                ", date=" + date +
                ", method='" + method + '\'' +
                '}';
    }

}
