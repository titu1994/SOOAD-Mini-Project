package source;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class NetBankUtils {

	/*public static final String HOST = "jdbc:mysql://sql3.freemysqlhosting.net/";
	public static final String DB_NAME = "sql353714";
	public static final String DB_USER = "sql353714";
	public static final String DB_PASS = "vW3!xL9*";

	private static final String TABLE_ACCOUNT =  "AccountDB";
	private static final String TABLE_TRANSACTIONS = "TransactionDB";*/
	
	public static final String HOST = "jdbc:mysql://localhost/";
	public static final String DB_NAME = "sooad";
	public static final String DB_USER = "root";
	public static final String DB_PASS = "";

	private static final String TABLE_ACCOUNT =  "accountdb";
	private static final String TABLE_TRANSACTIONS = "transactiondb";
	public static final int PORT = 3306;

	private static final String COL_ACCID = "accountID";
	private static final String COL_ACCSECUREPASSWORD = "securePassword";
	private static final String COL_ACCCREDITMAX = "creditMaxLimit";
	private static final String COL_ACCCREDITCONSUMED = "creditConsumed";

	private static final String COL_TRANID = "transactionID";
	private static final String COL_TRANUSERID = "userID";
	private static final String COL_TRANTONAME = "transactionToName";
	private static final String COL_TRANSAMMOUNT = "transactionAmount";

	private static Connection conn;

	public static String getSecurePassword(String insecurePassword) {
		//String generatedPassword = null;
		/*try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(insecurePassword.getBytes());
			byte[] bytes = md.digest();
			StringBuilder sb = new StringBuilder();
			for(int i=0; i< bytes.length ;i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		} 
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}*/
		
		return insecurePassword;
	}

	public static Connection connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			if(conn == null || conn.isClosed()) {
				conn = DriverManager.getConnection(HOST + DB_NAME, DB_USER, DB_PASS);
			}
				
			else {
				return conn;
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return conn;
	}

	public static boolean insertData(NetBankAccountData data) {
		conn = connect();
		try {
			String sql = "insert into " + TABLE_ACCOUNT +" (" +COL_ACCID + "," + COL_ACCSECUREPASSWORD + "," + COL_ACCCREDITMAX + "," + COL_ACCCREDITCONSUMED + ")" +" values(?,?,?,?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setLong(1, data.getAccountID());
			stmt.setString(2, data.getSecurePassword());
			stmt.setDouble(3, data.getCreditMaxLimit());
			stmt.setDouble(4, data.getCreditConsumed());

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean insertData(NetBankTransactionData data) {
		conn = connect();
		try {	
			NetBankAccountData acc = queryAccount(data.getUserID());
			double max = acc.getCreditMaxLimit();
			double amt = acc.getCreditConsumed();

			if(max > (amt + data.getTransactionAmount())) {
				String sql = "insert into " + TABLE_TRANSACTIONS + " (" + COL_TRANID  + "," + COL_TRANUSERID + "," + COL_TRANTONAME + "," + COL_TRANSAMMOUNT + ") "+ " values(?,?,?,?)";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setLong(1, data.getTransactionID());
				stmt.setLong(2, data.getUserID());
				stmt.setString(3, data.getTransactionToName());
				stmt.setDouble(4, data.getTransactionAmount());

				stmt.executeUpdate();

				acc.setCreditConsumed(acc.getCreditConsumed() + data.getTransactionAmount());
				updateData(acc);
			}
			else {
				return false;
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean updateData(NetBankAccountData data) {
		conn = connect();
		try {
			String sql = "update " + TABLE_ACCOUNT + " set " + COL_ACCID + " = ? , " + COL_ACCSECUREPASSWORD + " = ? , " + COL_ACCCREDITMAX + " = ? , " + COL_ACCCREDITCONSUMED + " = ? where " + COL_ACCID + " = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setLong(1, data.getAccountID());
			stmt.setString(2, data.getSecurePassword());
			stmt.setDouble(3, data.getCreditMaxLimit());
			stmt.setDouble(4, data.getCreditConsumed());
			stmt.setLong(5, data.getAccountID());

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean updateData(NetBankTransactionData data) {
		conn = connect();
		try {
			String sql = "update " + TABLE_TRANSACTIONS + " set " + COL_TRANID + " = ? , " + COL_TRANUSERID + " = ?, " + COL_TRANTONAME + " = ? , " + COL_TRANSAMMOUNT + " = ? where " + COL_TRANID + " = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setLong(1, data.getTransactionID());
			stmt.setLong(2, data.getUserID());
			stmt.setString(3, data.getTransactionToName());
			stmt.setDouble(4, data.getTransactionAmount());
			stmt.setLong(5, data.getTransactionID());

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteAccount(long id) {
		conn = connect();
		try{
			String sql = "delete from " + TABLE_ACCOUNT + " where " + COL_ACCID + " = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setLong(1, id);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteTransaction(long id) {
		conn = connect();
		try{
			String sql = "delete from " + TABLE_TRANSACTIONS + " where " + COL_TRANID + " = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setLong(1, id);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static NetBankAccountData queryAccount(long accountID) {
		conn = connect();
		try{
			String sql = "select * from " + TABLE_ACCOUNT + " where " + COL_ACCID + " = ? order by " + COL_ACCID + " asc";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setLong(1, accountID);

			ResultSet result = stmt.executeQuery();
			System.out.println("Utils : Account Results obtained");
			if(result.first()) {
				System.out.println("Utils : Account found. Retrieving first one");
				String accPass = result.getString(COL_ACCSECUREPASSWORD);
				double accMax = result.getDouble(COL_ACCCREDITMAX);
				double accCon = result.getDouble(COL_ACCCREDITCONSUMED);

				NetBankAccountData data = new NetBankAccountData(accountID, accPass, accMax, accCon);
				return data;
			}
			else
				return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static NetBankAccountData[] queryAccount() {
		conn = connect();
		try{
			String sql = "select * from " + TABLE_ACCOUNT + " order by " + COL_TRANID + " asc";
			PreparedStatement stmt = conn.prepareStatement(sql);

			ResultSet result = stmt.executeQuery();
			long accID;
			String accPass;
			double accMax;
			double accCon;
			NetBankAccountData data;

			ArrayList<NetBankAccountData> list = new ArrayList<NetBankAccountData>();
			while(result.next()) {
				accID = result.getLong(COL_ACCID);
				accPass = result.getString(COL_ACCSECUREPASSWORD);
				accMax = result.getDouble(COL_ACCCREDITMAX);
				accCon = result.getDouble(COL_ACCCREDITCONSUMED);
				data = new NetBankAccountData(accID, accPass, accMax, accCon);

				list.add(data);
			}


			return list.toArray(new NetBankAccountData[list.size()]);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static NetBankTransactionData queryTransaction(long accountID, long transactionID) {
		conn = connect();
		try{
			String sql = "select * from " + TABLE_TRANSACTIONS + " where " + COL_TRANID + " = ? and " + COL_TRANUSERID + " = ?" + " order by " + COL_TRANID;
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setLong(1, transactionID);
			stmt.setLong(2, accountID);

			ResultSet result = stmt.executeQuery();
			result.first();
			String tranTo = result.getString(COL_TRANTONAME);
			double tranAmount = result.getDouble(COL_TRANSAMMOUNT);

			NetBankTransactionData data = new NetBankTransactionData(transactionID, accountID, tranTo, tranAmount);
			return data;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static NetBankTransactionData[] queryTransaction(long accountID) {
		conn = connect();
		try{
			String sql = "select * from " + TABLE_TRANSACTIONS + " where " + COL_TRANUSERID + " = ?" + " order by " + COL_TRANID;
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setLong(1, accountID);

			ResultSet result = stmt.executeQuery();
			long transID;
			String transTo;
			double transAmt;
			NetBankTransactionData data;

			ArrayList<NetBankTransactionData> list = new ArrayList<NetBankTransactionData>();
			while(result.next()) {
				transID = result.getLong(COL_TRANID);
				transTo = result.getString(COL_TRANTONAME);
				transAmt = result.getDouble(COL_TRANSAMMOUNT);
				data = new NetBankTransactionData(transID, accountID, transTo, transAmt);

				list.add(data);
			}


			return list.toArray(new NetBankTransactionData[list.size()]);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void closeConnection() {
		try {
			if(conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
