import java.sql.Date;

public class Staff {

	private int staffId;
	private String staffName;
	private String position;
	private double salary;
	private Date archivedDate;

	public Staff(int staffId, String staffName, String position, double salary, Date archivedDate) {
		this.staffId = staffId;
		this.staffName = staffName;
		this.position = position;
		this.salary = salary;
		this.archivedDate = archivedDate;
	}
	public Staff(int staffId, String staffName, String position, double salary) {
		super();
		this.staffId = staffId;
		this.staffName = staffName;
		this.position = position;
		this.salary = salary;
	}
	public Staff(int staffId, String staffName) {
		super();
		this.staffId = staffId;
		this.staffName = staffName;

	}


	public int getStaffId() {
		return staffId;
	}
	public Date getArchivedDate() {
		return archivedDate;
	}

	public void setArchivedDate(Date archivedDate) {
		this.archivedDate = archivedDate;
	}
	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

}
