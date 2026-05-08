import java.sql.Date;

public class Returns {
	private int returnId;
	private int orderId;
	private Date returnDate;
	private String reason;
	private String status;
	private double refundAmount;
	private Date archivedDate;

	public Returns(int returnId, int orderId, Date returnDate, String reason, String status, double refundAmount,
			Date archivedDate) {
		this.returnId = returnId;
		this.orderId = orderId;
		this.returnDate = returnDate;
		this.reason = reason;
		this.status = status;
		this.refundAmount = refundAmount;
		this.archivedDate = archivedDate;
	}

	public Returns(int returnId, int orderId, Date returnDate, String reason, String status, double refundAmount) {
		this.returnId = returnId;
		this.orderId = orderId;
		this.returnDate = returnDate;
		this.reason = reason;
		this.status = status;
		this.refundAmount = refundAmount;
	}

	public Date getArchivedDate() {
		return archivedDate;
	}

	// Getters and Setters
	public int getReturnId() {
		return returnId;
	}

	public void setReturnId(int returnId) {
		this.returnId = returnId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(double refundAmount) {
		this.refundAmount = refundAmount;
	}
}