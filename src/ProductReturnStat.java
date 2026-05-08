	public class ProductReturnStat {
	
		private int productId;
		private String productName;
		private int returnCount;
		private int totalQuantity;
	
		public ProductReturnStat(int productId, String productName, int returnCount, int totalQuantity) {
			this.productId = productId;
			this.productName = productName;
			this.returnCount = returnCount;
			this.totalQuantity = totalQuantity;
		}
	
		public int getProductId() {
			return productId;
		}
	
		public String getProductName() {
			return productName;
		}
	
		public int getReturnCount() {
			return returnCount;
		}
	
		public int getTotalQuantity() {
			return totalQuantity;
		}
	}