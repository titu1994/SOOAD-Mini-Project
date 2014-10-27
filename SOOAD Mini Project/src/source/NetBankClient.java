package source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import source.NetBankServer.NetBankServerProtocols;
import source.NetBankServer.ServerListener;

public class NetBankClient implements ServerListener{
	private static ExecutorService executor;
	private Socket client = null;
	private String protocol;

	private String password;
	private long accountID;
	private boolean isAdmin;

	private double creditLimit, creditConsumed; 
	private PrintWriter pr;
	private BufferedReader bb;

	private ClientListener listener;

	public NetBankClient(long accountID, String password, ClientListener listener) {
		if(!isExecutorAvailable())
			executor = Executors.newCachedThreadPool();

		this.listener = listener;

		this.password = password;
		this.accountID = accountID;

		System.out.println("Client : Starting");
		try {
			client = new Socket(InetAddress.getLocalHost(), NetBankServerProtocols.PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Client : Started");
	}

	public void startCommunication() {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				NetBankServer.setServerListener(NetBankClient.this);
				initiateCommunication();
			}
		};

		if(isExecutorAvailable()) {
			executor.submit(r);
		}
		else {
			restartExecutor();
			executor.submit(r);
		}
	}

	private NetBankAccountData initiateCommunication() {
		synchronized (client) {
			try {
				pr = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
				bb = new BufferedReader(new InputStreamReader(client.getInputStream()));

				//Send User credentials to Server to authenticate
				if((protocol = bb.readLine()).equals(NetBankServerProtocols.serverReadyToReceive)) {
					sendCredentialsToUser(client);
				}

				//Recieve full data from server 
				if((protocol = bb.readLine()).equals(NetBankServerProtocols.serverReadyToSend)) {
					String data = bb.readLine();
					getSecureCredentials(client, data);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}

	private void sendCredentialsToUser(Socket client) {
		synchronized (client) {
			pr.println(accountID);
			pr.println(password);
			pr.println(NetBankServerProtocols.clientReadyToRecieve);
		}
	}

	private void getSecureCredentials(Socket client, String data) {
		synchronized (client) {
			String arr[] = data.split(",");
			if(NetBankServerProtocols.serverError.equals(arr[0])) {
				if(NetBankServerProtocols.errorIdDoesNotExist.equals(arr[1])) {
					listener.clientLogInFailed();
				}
			}
			else {
				creditLimit = Double.parseDouble(arr[0]);
				creditConsumed = Double.parseDouble(arr[1]);
				isAdmin = Boolean.parseBoolean(arr[2]);
				NetBankAccountData d = new NetBankAccountData(accountID, password, creditLimit, creditConsumed, isAdmin);
				listener.clientLogInSuccess(d);
			}
		}
	}

	/**
	 * Note: Syntax about how to call the Server functions.
	 * TO implement a new function :
	 * 1) Create a new string in the ClientProtocols interface with appropriate name
	 * 2) Create a function with similar name in this class
	 * 3) First statement must be : pr.println(NetBankClientProtocols.NEW_STRING_PROTOCOL);
	 * 4) Send as many inputs you want in string format using pr.println() in this class. Recieve in same order as String in Server using bb.readLine() in Server;
	 * 5) Recieve as many inputs you want in String format using bb.readLine() in this class. Send in same order as String from Server using pr.println() in Server.
	 * 
	 * Note : Try not to recieve output results here. Instead, only send protocol and inputs and exit this function. 
	 * 		  To get the results, create a new function prepended with "server" in ServerListener and override in this class.
	 * 		  To send data from the Server to Client, use the Server's "listener" object
	 * 		  To send data from the Client to ClientUI, use the Client's "listener" object
	 * 
	 * Also, create new corresponding functions in ClientListener prepended with "client" to distinguish.
	 * 
	 * Note : Flow of data :
	 * 				pr.println()		 Client.function()  	ClientUI.function()	 <-------
	 * 		  Server    <=>      Client      <=>      ClientUI      <=>     JFrames
	 * 	------>		ServerListener		 ClientListener			frame.function()
	 */
	
	public void createNewAccount(long accountID, String password, boolean isAdmin) {
		System.out.println("Client : Sending data to create new account.");
		pr.println(NetBankClientProtocols.clientAddAccount);
		pr.println(accountID);
		pr.println(password);
		pr.println(isAdmin);
	}

	public void cancelConnection() {
		System.out.println("Client : Closing connection as account doesn't exist");
		try {
			NetBankServer.cancelConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			if(client != null)
				client.close();

			System.out.println("Client : Closed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addTransaction(long id, String to, double amt) {
		System.out.println("Client : Adding transaction");
		StringBuilder sb = new StringBuilder();
		pr.println(NetBankClientProtocols.clientAddTransaction);
		sb.append(id + ",");
		sb.append(accountID + ",");
		sb.append(to + ",");
		sb.append(amt);
		pr.println(sb.toString());
	}

	public void viewAllTransactions() {
		pr.println(NetBankClientProtocols.clientViewAllTransactions);
		pr.println(accountID);
	}

	public void alterPassword(String oldPass, String newPass) {
		pr.println(NetBankClientProtocols.clientAlterCredentials);

		if(oldPass != null && oldPass.length() != 0) {
			pr.println(oldPass);
		}
		else if(oldPass.equals(newPass)){
			//Error Handling Measures
			pr.println("");
			try {
				bb.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			listener.clientOldPasswordMatchesNewPassword();
			return;
		}
		else {	
			//Error Handling Measures
			pr.println("");
			try {
				bb.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			listener.clientOldPasswordNotEnteredCorrectly();
			return;
		}


		try {
			if((protocol = bb.readLine()).equals(NetBankServerProtocols.serverReadyToReceive)) {
				if(newPass != null && newPass.length() != 0) {
					pr.println(newPass);
					listener.clientPasswordChanged();
				}
				else {
					pr.println(oldPass);
					listener.clientNewPasswordEmpty();
				}
			}
			else {
				listener.clientOldPasswordNotFound();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void viewAccountDetails() {
		pr.println(NetBankClientProtocols.clientViewAccount);
	}
	
	public void isAccountAdmin() {
		pr.println(NetBankClientProtocols.clientIsAccountAdmin);
	}
	
	public void updateAccount(long id, long credit, long max) {
		pr.println(NetBankClientProtocols.clientUpdateAccount);
		pr.println(id);
		pr.println(credit);
		pr.println(max);
	}


	public interface NetBankClientProtocols {
		String clientAddAccount = "clientAddAccount";
		String clientCloseConnection = "clientCloseConnection";

		String clientAlterCredentials = "clientAlterCredentials";
		String clientAddTransaction = "clientAddTransaction";
		String clientViewAllTransactions = "clientViewAllTransactions";
		String clientViewAccount = "clientViewAccount";
		String clientIsAccountAdmin = "clientIsAccountAdmin";
		String clientUpdateAccount = "clientUpdateAccount";
	}

	public interface ClientListener {
		/**
		 * This interface acts as a guard between the 2 thredds : Server and Client.
		 * Add new functions and override them in ClientUI class which acts as the mediator between all Views.
		 */
		void clientLogInFailed();
		void clientLogInSuccess(NetBankAccountData data);

		void clientTransactionAdded(boolean succesful);

		void clientAllTransactionData(NetBankTransactionData datas[]);

		void clientOldPasswordMatchesNewPassword();
		void clientOldPasswordNotEnteredCorrectly();
		void clientNewPasswordEmpty();
		void clientOldPasswordNotFound();
		void clientPasswordChanged();
		
		void clientAccountData(NetBankAccountData data);
		void clientAccountIsAdmin(boolean isAdmin);
		void clientAccountUpdatedSuccesfully(boolean isSuccesful);
	}


	public static boolean isExecutorAvailable() {
		return executor != null && !executor.isShutdown();
	}

	public boolean restartExecutor() {
		if(!isExecutorAvailable()) {
			executor = Executors.newCachedThreadPool();
			return true;
		}
		else
			return false;
	}

	public boolean stopExecutor() {
		if(isExecutorAvailable()) {
			executor.shutdown();
			executor = null;
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Here, these fucntions are obtained from ServerListener in the NetBackServer class. They deliver information in class format or true data
	 * format instead of string parsing, and act as Async operations between the Server and Client.
	 * 
	 * Override these functions from the Server interface ServerListener
	 */

	@Override
	public void serverSendIsTransactionStored(boolean isStored) {
		listener.clientTransactionAdded(isStored);
	}

	@Override
	public void serverSendTransactionData(NetBankTransactionData[] datas) {
		listener.clientAllTransactionData(datas);
	}

	@Override
	public void serverAccountAdded(NetBankAccountData acc) {
		listener.clientLogInSuccess(acc);
	}

	@Override
	public void serverPasswordChanged() {
		listener.clientPasswordChanged();
	}

	@Override
	public void serverAccountData(NetBankAccountData data) {
		listener.clientAccountData(data);
	}

	@Override
	public void serverIsAccountAdmin(boolean isAdmin) {
		listener.clientAccountIsAdmin(isAdmin);
	}

	@Override
	public void serverAccountUpdatedSuccesfully(boolean isSuccesful) {
		listener.clientAccountUpdatedSuccesfully(isSuccesful);
	}

}
