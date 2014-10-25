package source;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import source.NetBankClient.ClientListener;

public class ClientUI extends JFrame {
	private static final long serialVersionUID = 12321L;
	
	private static LogIn login;
	private static TransactionUI trans;
	
	private static long id;
	private static String password;

	private static NetBankServer server;
	public static NetBankClient client;
	private static ClientEventListener listener;
	
	public static JDialog waitDialog = new JDialog();

	public static void main(String[] args) {
		listener = new ClientEventListener();
		
		server = new NetBankServer();
		JLabel label = new JLabel("Please wait...");
		waitDialog.setLocationRelativeTo(null);
		waitDialog.setTitle("Please Wait...");
		waitDialog.add(label);
		waitDialog.pack();
	
		login = new LogIn();
		login.setVisible(true);
	}
	
	public static void startClient(long id, String password) {
		ClientUI.id = id; 
		ClientUI.password = password;
		
		waitDialog.setVisible(true);
		
		server.startCommunication();
		client = new NetBankClient(id, password, listener);
		client.startCommunication();
	}
	
	public static void TransactionScreen() {
		login.setVisible(false);
		trans = new TransactionUI();
		trans.setVisible(true);
	}

	public static class ClientEventListener implements ClientListener {

		@Override
		public void clientLogInFailed() {
			waitDialog.setVisible(false);
			login.logInFailed();
		}

		@Override
		public void clientLogInSuccess(double limit, double consumed) {
			waitDialog.setVisible(false);
			TransactionScreen();
		}

		@Override
		public void clientTransactionAdded(boolean succesful) {
			
		}

		@Override
		public void clientAllTransactionData(NetBankTransactionData[] datas) {
			trans.clientAllTransactionData(datas);
		}

		@Override
		public void clientOldPasswordMatchesNewPassword() {
			trans.clientOldPasswordMatchesNewPassword();
		}

		@Override
		public void clientOldPasswordNotEnteredCorrectly() {
			trans.clientOldPasswordNotEnteredCorrectly();
		}

		@Override
		public void clientNewPasswordEmpty() {
			trans.clientNewPasswordEmpty();
		}

		@Override
		public void clientOldPasswordNotFound() {
			trans.clientOldPasswordNotFound();
		}

		@Override
		public void clientPasswordChanged() {
			trans.clientPasswordChanged();
		}
		
	}
}
