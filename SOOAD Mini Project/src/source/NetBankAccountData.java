package source;



public class NetBankAccountData {

	private final long accountID;
	private String securePassword;
	private double creditMaxLimit;
	private double creditConsumed;
	private boolean isAdmin;

	public NetBankAccountData(long accountID, String securePassword) {
		this(accountID, securePassword, 10000, 0, false);
	}
	
	public NetBankAccountData(long accountID, String securePassword, boolean isAdmin) {
		this(accountID, securePassword, 10000, 0, isAdmin);
	}
	
	public NetBankAccountData(long accountID, String securePassword, double creditMax, double creditConsumed, boolean isAdmin) {
		this.accountID = accountID;
		this.securePassword = securePassword;
		this.creditMaxLimit = creditMax;
		this.creditConsumed = creditConsumed;
		this.isAdmin = isAdmin;
	}
	
	public long getAccountID() {
		return accountID;
	}

	public String getSecurePassword() {
		return securePassword;
	}
	
	public void setSecurePassword(String securePassword) {
		this.securePassword = securePassword;
	}

	public double getCreditMaxLimit() {
		return creditMaxLimit;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setCreditMaxLimit(double creditMaxLimit) {
		this.creditMaxLimit = creditMaxLimit;
	}

	public double getCreditConsumed() {
		return creditConsumed;
	}
	
	public void setCreditConsumed(double creditConsumed) {
		this.creditConsumed = creditConsumed;
	}

	public static class DataBase {
		
		public static NetBankAccountData[] getDataStore() {
			return NetBankUtils.queryAccount();
		}
		
		public static NetBankAccountData getDataStore(long id) {
			return NetBankUtils.queryAccount(id);
		}

		public static boolean insertData(NetBankAccountData data) {
			return NetBankUtils.insertData(data);
		}
		
		public static boolean updateData(NetBankAccountData data) {
			return NetBankUtils.updateData(data);
		}
		
		public static boolean removeData(long id) {
			return NetBankUtils.deleteAccount(id);
		}

	}
}
