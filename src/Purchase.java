
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class Purchase {

    private int purchase_id;
    private Date Purchase_date;
    private Double total_cost;

    public void setArchivedDate(Date archivedDate) {
        this.archivedDate = archivedDate;
    }

    private Supplier supplier;
    private Date archivedDate;

    public Purchase(int purchase_id, Double total_cost, Supplier supplier) {
        this.purchase_id = purchase_id;
        this.total_cost = total_cost;
        this.supplier = supplier;
    }
    public Purchase(int purchase_id, Date  Purchase_date,Double total_cost, Supplier supplier) {
        this.purchase_id = purchase_id;
        this.Purchase_date = Purchase_date;
        this.total_cost = total_cost;
        this.supplier = supplier;
    }
    public Purchase(int purchase_id, Date  Purchase_date, Double total_cost, Supplier supplier , Date archivedDate) {
        this.purchase_id = purchase_id;
        this.Purchase_date = Purchase_date;
        this.total_cost = total_cost;
        this.supplier = supplier;
        this.archivedDate=archivedDate;
    }

    public Date getArchivedDate() {
        return archivedDate;
    }

    public int getPurchase_id() {
        return purchase_id;
    }

    public void setPurchase_id(int purchase_id) {
        this.purchase_id = purchase_id;
    }

    public Date getPurchase_date() {
        return Purchase_date;
    }

    public void setPurchase_date(Date purchase_date) {
        Purchase_date = purchase_date;
    }

    public Double getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(Double total_cost) {
        this.total_cost = total_cost;
    }


    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public String toString() {
        return "Purches{" +
                "purchase_id=" + purchase_id +
                ", Purchase_date=" + Purchase_date +
                ", total_cost=" + total_cost +
                ", supplier=" + supplier +
                '}';
    }

}

