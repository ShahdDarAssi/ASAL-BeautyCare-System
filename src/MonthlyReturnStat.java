class MonthlyReturnStat {
	private int year;
	private int month;
	private int count;
	private double totalRefund;

	public MonthlyReturnStat(int year, int month, int count, double totalRefund) {
		this.year = year;
		this.month = month;
		this.count = count;
		this.totalRefund = totalRefund;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getCount() {
		return count;
	}

	public double getTotalRefund() {
		return totalRefund;
	}
}