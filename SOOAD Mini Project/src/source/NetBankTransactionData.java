package source;

public class NetBankTransactionData {


	public final long transactionID;
	public final long userID;
	public final String transactionToName;
	public final double transactionAmount;
	
	public NetBankTransactionData(long transactionID, long userID, String transactionToName, double transactionAmount) {
		this.transactionID = transactionID;
		this.userID = userID;
		this.transactionToName = transactionToName;
		this.transactionAmount = transactionAmount;
	}

	public NetBankTransactionData(String transactionID, String userID, String transactionToName, String transactionAmount) {
		this.transactionID = Long.parseLong(transactionID);
		this.userID = Long.parseLong(userID);
		this.transactionToName = transactionToName;
		this.transactionAmount = Double.parseDouble(transactionAmount);
	}
	
	public long getUserID() {
		return userID;
	}

	public long getTransactionID() {
		return transactionID;
	}

	public String getTransactionToName() {
		return transactionToName;
	}

	public double getTransactionAmount() {
		return transactionAmount;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(transactionID + "\n");
		sb.append(userID + "\n");
		sb.append(transactionToName + "\n");
		sb.append(transactionAmount + ",");
		return sb.toString();
	}

	public static class Database {
		
		public static NetBankTransactionData[] getDataStore(long accid) {
			return NetBankUtils.queryTransaction(accid);
		}
		
		public static NetBankTransactionData getDataStore(long accid, long id){
			return NetBankUtils.queryTransaction(accid, id);
		}
		
		public static boolean insertData(NetBankTransactionData data) {
			return NetBankUtils.insertData(data);
		}
		
		public static boolean updateData(NetBankTransactionData data) {
			return NetBankUtils.updateData(data);
		}
		
		public static boolean removeData(long id) {
			return NetBankUtils.deleteTransaction(id);
		}
	}
	
}
