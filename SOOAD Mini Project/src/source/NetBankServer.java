package source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import source.NetBankAccountData.DataBase;
import source.NetBankClient.NetBankClientProtocols;

public class NetBankServer {
	private static ExecutorService executor;
	private ServerSocket server = null;
	private static Socket client;
	private String protocol;

	private long id;

	private static ServerListener listener;
	private static PrintWriter pr;
	private static BufferedReader bb;
	private NetBankAccountData acc;

	public NetBankServer() {
		if(!isExecutorAvailable())
			executor = Executors.newCachedThreadPool();

		System.out.println("Server : Starting");
		try {
			server = new ServerSocket(NetBankServerProtocols.PORT, 50, InetAddress.getLocalHost());
		} catch (IOException e) {

		}
		System.out.println("Server : Started");

	}
	
	public static void setServerListener(ServerListener listener) {
		NetBankServer.listener = listener;
	}

	public void startCommunication() {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				serveClient();
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

	boolean clientWaiting = true;
	private void serveClient() {
		try {

			System.out.println("Server : Accepting conections");
			client = server.accept();

			synchronized (client) {
				System.out.println("Server : Accepted connection");
				pr = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
				bb = new BufferedReader(new InputStreamReader(client.getInputStream()));

				StringBuilder sb = new StringBuilder();

				System.out.println("Server : Ready to recieve data");
				pr.println(NetBankServerProtocols.serverReadyToReceive);

				System.out.println("Server : Recieving until client is ready to recieve");
				while(!(protocol = bb.readLine()).equals(NetBankServerProtocols.clientReadyToRecieve)) {
					sb.append(protocol + "\n");
				}

				//TODO: Parse sb here
				System.out.println("Server : Parsing details");
				acc = parseClientUserPass(sb.toString());

				System.out.println("Server : Ready to send information");
				pr.println(NetBankServerProtocols.serverReadyToSend);

				//TODO: Send data here
				//Note : data may be null, meaning it doesn't exist in DB. Client must handle that
				sendClientAllData(acc);

				handleServerChoice();
				cancelConnection();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void cancelConnection()
			throws IOException {
		NetBankUtils.closeConnection();
		pr.close();
		bb.close();
		client.close();
	}

	private void sendClientAllData(NetBankAccountData data) {
		synchronized (client) {
			//If Data exists
			if(data != null) {
				System.out.println("Server : Sending the Account data");
				pr.println(data.getCreditMaxLimit()+ "," + data.getCreditConsumed());
			}
			else { //If Data does not exist
				System.out.println("Server : Account data does not exist.");
				pr.println(NetBankServerProtocols.serverError + "," + NetBankServerProtocols.errorIdDoesNotExist);

				String protocol = "";
				try {
					if((protocol = bb.readLine()).equals(NetBankClientProtocols.clientAddAccount)) {
						System.out.println("Server : Accepting id and password for new account.");
						long id = Long.parseLong(bb.readLine());
						String pass = bb.readLine();
						acc = new NetBankAccountData(id, pass);
						NetBankAccountData.DataBase.insertData(acc);
						listener.serverAccountAdded(acc);
						System.out.println("Server : New account created.");
					} else {
						cancelConnection();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	private NetBankAccountData parseClientUserPass(String input) {
		synchronized (client) {
			String arr[] = input.split("[\\r\n]+");

			long id = Long.parseLong(arr[0]);
			String password = arr[1];
			//System.out.println("ID : " + id + " Pass : " + password);
			String generatedPassword = NetBankUtils.getSecurePassword(password);
			NetBankAccountData data = null; 
			data = DataBase.getDataStore(id);
			if(data != null && data.getSecurePassword().equals(generatedPassword)) {
				return data;
			}
			else {
				System.out.println("Server : No data was found");
				return null;
			}
		}
	}

	private void handleServerChoice() throws IOException {
		synchronized (client) {
			String data = null;
			String segs[] = null;
			do {
				int choice = decodeProtocolAction(bb.readLine());
				System.out.println("Server : Processing Action");
				switch(choice) {
				case 1: {
					System.out.println("Server : Adding Transaction");
					data = bb.readLine();
					System.out.println("Server : Transaction data string fetched");
					segs = data.split(",");
					System.out.println(Arrays.toString(segs));
					NetBankTransactionData transaction = new NetBankTransactionData(Long.parseLong(segs[0]), Long.parseLong(segs[1]),
							segs[2]	, Double.parseDouble(segs[3]));
					System.out.println("segs : Inserting Data");
					boolean store = NetBankTransactionData.Database.insertData(transaction);

					listener.serverSendIsTransactionStored(store);
					break;
				}
				case 2: {
					System.out.println("Server : Gathering all data");
					long accid = Long.parseLong(bb.readLine());
					NetBankTransactionData datas[] = NetBankTransactionData.Database.getDataStore(accid);

					System.out.println("Server : Query Complete");
					//TODO: Send data to client to display
					listener.serverSendTransactionData(datas);
					break;
				}
				case 3: {
					String oldPass = bb.readLine();
					System.out.println("Server : Old Pass  " + oldPass);
					String generatedPassword = NetBankUtils.getSecurePassword(oldPass);
					try {
						if(acc.getSecurePassword().equals(generatedPassword)) {
							pr.println(NetBankServerProtocols.serverReadyToReceive);
							String newPass = bb.readLine();

							acc.setSecurePassword(NetBankUtils.getSecurePassword(newPass));
							DataBase.updateData(acc);
							
						}
						else {
							pr.println(NetBankServerProtocols.serverError);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				case 4: {
					listener.serverAccountData(acc);
					break;
				}
				default: {
					System.out.println("Server : Stopping");
					clientWaiting = false;
					break;
				}

				}

			} while(clientWaiting);
		}
	}

	private int decodeProtocolAction(String protocol) {
		System.out.println("Server : Protocol - " + protocol);
		if(protocol.equals(NetBankClientProtocols.clientAddTransaction))
			return 1;
		else if(protocol.equals(NetBankClientProtocols.clientViewAllTransactions))
			return 2;
		else if(protocol.equals(NetBankClientProtocols.clientAlterCredentials))
			return 3;
		else if(protocol.equals(NetBankClientProtocols.clientViewAccount))
			return 4;


		return -1;
	}


	public interface NetBankServerProtocols {
		int PORT = 8888;
		String localHost = "127.0.0.1";

		String serverReadyToSend = "ServerSend";
		String serverReadyToReceive = "ServerReceive";
		String serverFinishCommunication = "ServerFinished";

		String serverError = "ServerError";
		String errorIdDoesNotExist = "NoMatchesForID";

		String clientReadyToSend = "ClientSend";
		String clientReadyToRecieve = "ClientRecieve";
		String clientFinishCommunication = "ClientFinish";

		String allClientsServed = "AllClientsServed";
	}

	public interface ServerListener {
		void serverSendIsTransactionStored(boolean isStored);
		void serverSendTransactionData(NetBankTransactionData datas[]);
		void serverAccountAdded(NetBankAccountData acc);
		void serverPasswordChanged();
		void serverAccountData(NetBankAccountData data);
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

}
