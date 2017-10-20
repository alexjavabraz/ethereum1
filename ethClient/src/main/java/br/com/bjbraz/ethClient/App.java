package br.com.bjbraz.ethClient;

import java.math.BigInteger;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.parity.Parity;

/**
 * Hello world!
 *
 */
public class App {
	
	/**
	 * 
	 */
//	Web3j web3    = Web3j.build(new HttpService("http://127.0.0.1:8545"));
	Web3j parity = Parity.build(new HttpService("http://127.0.0.1:8545"));
	
	Credentials credentials = null; 
	BigInteger gasPrice     = BigInteger.valueOf(20_000_000_000L);
	BigInteger gasLimit     = BigInteger.valueOf(4_300_000);
	
	private static final String CONTRACT_ADDRESS = "0x2D36b70e1e14dD771A40A3b96e9571242344ddbB";
	private static final Address ACCOUNT_TRANSFER_TO = new Address("0x7c1D17e69010e4AC1f3bD1630A694004179Ec49e");
	
	TokenERC20 tokenContract = null;
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		App app = new App();
		app.loadCredentials();
		app.transferToToken();
		app.getTokenBalance();
//		app.listTransactions();
		
	}

	private void listTransactions() {
		try{
			EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST,
			        DefaultBlockParameterName.LATEST, "0x2D36b70e1e14dD771A40A3b96e9571242344ddbB")
					.addNullTopic();
			parity.ethLogObservable(filter).subscribe(log -> {
				System.out.println(log);
			});
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	/**
	 * Transfer token to address
	 */
	private void transferToken() {
		Uint256 value = new Uint256(new BigInteger("1000"));
		tokenContract.transfer(ACCOUNT_TRANSFER_TO, value);
	}
	
	/**
	 * Transfer token to address
	 */
	private void transferToToken() {
		Uint256 value = new Uint256(new BigInteger("1000"));
		Address from  = new Address("0x00Faa15694456FEbAA43f0d932Fd0898946CCeC4");
		Address to    = new Address("0x7c1D17e69010e4AC1f3bD1630A694004179Ec49e");
		
		tokenContract.transferFrom(from, to, value); 
	}
	
	/**
	 * Token Balance of andress
	 */
	private void getTokenBalance(){
		try{
			Uint256 balance = tokenContract.balanceOf(ACCOUNT_TRANSFER_TO).get();
			System.out.println(balance.getValue());
		}catch(Exception e){
			//TODO LOG
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void loadCredentials() {
		try{
			credentials   = WalletUtils.loadCredentials("deltasp5k", "/home/asimas/.local/share/io.parity.ethereum/keys/kovan/UTC--2017-08-02T22-13-37Z--560d903d-14d5-a015-048f-754bfd5b8788");
			tokenContract = TokenERC20.load(CONTRACT_ADDRESS, parity, credentials, gasPrice, gasLimit);
		}catch(Exception e){
			//TODO LOG
			e.printStackTrace();
		}
		
	}

}
