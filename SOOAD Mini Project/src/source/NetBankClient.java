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

				//handleUserChoice(client, pr, bb);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

				/*try {
					if(client != null)
						client.close();

					System.out.println("Client : Closed");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
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
					//TODO: Handle client if Account does not exist
					//For now immediately create a new account:

					listener.clientLogInFailed();
				}
			}
			else {
				creditLimit = Double.parseDouble(arr[0]);
				creditConsumed = Double.parseDouble(arr[1]);

				listener.clientLogInSuccess(creditLimit, creditConsumed);;
			}
		}
	}

	public void createNewAccount(long accountID, String password) {
		System.out.println("Client : Sending data to create new account.");
		pr.println(NetBankClientProtocols.clientAddAccount);
		pr.println(accountID);
		pr.println(password);
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


	public interface NetBankClientProtocols {
		String clientAddAccount = "clientAddAccount";
		String clientCloseConnection = "clientCloseConnection";

		String clientAlterCredentials = "clientAlterCredentials";
		String clientAddTransaction = "clientAddTransaction";
		String clientViewAllTransactions = "clientViewAllTransactions";
		String clientViewAccount = "clientViewAccount";
	}

	public interface ClientListener {
		void clientLogInFailed();
		void clientLogInSuccess(double limit, double consumed);

		void clientTransactionAdded(boolean succesful);

		void clientAllTransactionData(NetBankTransactionData datas[]);

		void clientOldPasswordMatchesNewPassword();
		void clientOldPasswordNotEnteredCorrectly();
		void clientNewPasswordEmpty();
		void clientOldPasswordNotFound();
		void clientPasswordChanged();
		
		void clientAccountData(NetBankAccountData data);
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
		listener.clientLogInSuccess(acc.getCreditMaxLimit(), acc.getCreditConsumed());
	}

	@Override
	public void serverPasswordChanged() {
		listener.clientPasswordChanged();
	}

	@Override
	public void serverAccountData(NetBankAccountData data) {
		listener.clientAccountData(data);
	}

}
