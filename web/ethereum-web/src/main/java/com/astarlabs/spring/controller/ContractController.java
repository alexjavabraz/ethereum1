package com.astarlabs.spring.controller;

import java.io.File;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.hsqldb.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.parity.Parity;

import com.astarlabs.spring.ethereum.TokenERC20;
import com.astarlabs.spring.model.Employee;
import com.astarlabs.spring.model.Saldo;
import com.astarlabs.spring.model.StatusLogin;
import com.astarlabs.spring.model.Transferencia;
import com.astarlabs.spring.model.Usuario;
import com.astarlabs.spring.service.TesteService;

/**
 * Handles requests for the Contract service.
 */
@Controller
public class ContractController {

	Web3j parity = Parity.build(new HttpService("https://kovan.infura.io/eZuf9su1N2ySD0Wj3Gn6")); //http://127.0.0.1:8545

	Credentials credentials = null;
	BigInteger gasPrice = BigInteger.valueOf(20_000_000_000L);
	BigInteger gasLimit = BigInteger.valueOf(4_300_000);

	private static final String CONTRACT_ADDRESS = "0x8c1cEbe144B2bdeBA2C11F1b42412b11d9579795";
	private static final Address ACCOUNT_TRANSFER_TO = new Address("0x00978cE44CA8E690d0Ca796efeFc81dE6Cf243Ae");

	TokenERC20 tokenContract = null;
	Connection conn;

	@Autowired
	private TesteService service;

	private static final Logger logger = LoggerFactory.getLogger(ContractController.class);

	@PostConstruct
	public void setup() {
		
		try{
			Server server = new Server();
			server.setDatabaseName(0, "xdb");
			server.setDatabasePath(0, "mem:xdb");
			server.setPort(9001); // this is the default port
			server.start();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (Exception e) {
			System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
			e.printStackTrace();
		}

		try {
			ClassLoader classLoader = getClass().getClassLoader();

			credentials = WalletUtils.loadCredentials("deltasp5k","/home/UTC--2017-08-02T22-13-37Z--560d903d-14d5-a015-048f-754bfd5b8788");
			tokenContract = TokenERC20.load(CONTRACT_ADDRESS, parity, credentials, gasPrice, gasLimit);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/xdb", "SA", "");
			update("CREATE TABLE usuarios ( id INTEGER IDENTITY, login VARCHAR(256), senha VARCHAR(256), conta VARCHAR(256),nome VARCHAR(256),email VARCHAR(256) )");

			update("insert into usuarios (  login , senha , conta ,nome ,email  ) values ('alex', 'alex', '0x00Faa15694456FEbAA43f0d932Fd0898946CCeC4', 'alex' , 'alex@astarlabs.com')");
			update("insert into usuarios (  login , senha , conta ,nome ,email  ) values ('jc', 'jc', '0x0719D260682B4ef5941E93ACbAAC1E1b14CeaE60', 'jc' , 'jc@astarlabs.com')");
			update("insert into usuarios (  login , senha , conta ,nome ,email  ) values ('guilherme', 'guilherme', '0x8F72e127FDEc49ddEd6d6FB5455435933821Fd4F', 'guilherme' , 'guilherme@astarlabs.com')");
			update("insert into usuarios (  login , senha , conta ,nome ,email  ) values ('teste', 'teste', '0xBcC7aC9639B8597095446E45f72894628063c674', 'teste' , 'teste@astarlabs.com')");

			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void shutdown() throws SQLException {

		Statement st = conn.createStatement();

		// db writes out to files and performs clean shuts down
		// otherwise there will be an unclean shutdown
		// when program ends
		st.execute("SHUTDOWN");
		conn.close(); // if there are no other open connection
	}

	// use for SQL command SELECT
	public synchronized StatusLogin obterUsuario(StatusLogin sl) throws SQLException {
		StatusLogin retorno = new StatusLogin();
		Statement st = conn.createStatement(); // statement objects can be
												// reused with

		try {
			ResultSet rs = null;

			// repeated calls to execute but we
			// choose to make a new one each time
			rs = st.executeQuery("SELECT * FROM usuarios WHERE login = '" + sl.getUsuario().getLogin() + "' and senha = '"
					+ sl.getUsuario().getSenha()+"' "); // run the query

			/**
			 * 
			 */
			if (rs.next()) {
				retorno.setEmail(rs.getString("email"));
				retorno.setNome(rs.getString("nome"));
				retorno.setDataLogin(new Date());
				retorno.setMensagem("OK");
				retorno.setUsuario(new Usuario());
				retorno.getUsuario().setConta(rs.getString("conta"));
				retorno.getUsuario().setEmail(rs.getString("email"));
				retorno.getUsuario().setId(String.valueOf(rs.getInt("id")));
				retorno.getUsuario().setLogin(rs.getString("login"));
				retorno.getUsuario().setNome(rs.getString("nome"));
				retorno.getUsuario().setSaldo(new Saldo());

				/**
				 * 
				 */
				if (retorno.getUsuario().getConta() != null) {
					try {
						Uint256 balance = tokenContract.balanceOf(new Address(retorno.getUsuario().getConta())).get();
						retorno.getUsuario().getSaldo().setValor(balance.getValue().doubleValue());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} else {
				throw new NullPointerException("Usuario inexistente.");
			}

		} catch (Exception e) {
			retorno.setMensagem("Ocorreu um erro " + e.getMessage());
		} finally {
			st.close(); // NOTE!! if you close a statement the associated
						// ResultSet
			// is
		}

		return retorno;
	}

	public synchronized void update(String expression) throws SQLException {

		Statement st = null;

		st = conn.createStatement(); // statements

		int i = st.executeUpdate(expression); // run the query

		if (i == -1) {
			System.out.println("db error : " + expression);
		}

		st.close();
	} // void update()

	// Map to store employees, ideally we should use database
	Map<Integer, Employee> empData = new HashMap<Integer, Employee>();

	@RequestMapping(value = ContractRestURIConstants.LOGIN, method = RequestMethod.POST)
	public @ResponseBody StatusLogin login(@RequestBody StatusLogin sl) {
		logger.info("Start login.");

		try {
			sl = obterUsuario(sl);
			sl.setMensagem("OK");
		} catch (Exception e) {
			sl.setMensagem("Ocorreu um erro");
		}

		return sl;
	}

	@RequestMapping(value = ContractRestURIConstants.SALDO, method = RequestMethod.GET)
	public @ResponseBody String saldo(@PathVariable("id") String conta) {
		logger.info("Start saldo do usuario.");

		Saldo sl = new Saldo();

		try {

			Uint256 balance = tokenContract.balanceOf(new Address(conta)).get();
			sl.setValor(balance.getValue().doubleValue());
			return sl.getValor().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	@RequestMapping(value = ContractRestURIConstants.TRANSFERE, method = RequestMethod.POST)
	public @ResponseBody String transfere(@RequestBody Transferencia transferencia) {
		logger.info("Start transferencia entre valores.");
		
		try{
			Address from  = new Address(transferencia.getOrigem());
			Address to    = new Address(transferencia.getDestino());
			
//			transferencia.setValor(1222d);
			
			
			Uint256 value = new Uint256(transferencia.getValor().intValue());
			
			transferencia.setMensagem("OK");
//			tokenContract.transferFrom(from, to, value); //(transferencia.getOrigem(), value);
		}catch(Exception e){
			e.printStackTrace();
			transferencia.setMensagem("Erro: " + e.getMessage());
		}
		

//		
		try{
			
			Transferencia atransferencia = new Transferencia();
			atransferencia.setDestino(transferencia.getDestino());
			atransferencia.setOrigem(transferencia.getOrigem());
			atransferencia.setValor(transferencia.getValor());
			
			Address from  = new Address(atransferencia.getOrigem());
			Address to    = new Address(atransferencia.getDestino());
			Uint256 value = new Uint256(atransferencia.getValor().intValue());
			tokenContract.transferFrom(from, to, value); //(transferencia.getOrigem(), value);
			return "OK";
		}catch(Exception e){
			e.printStackTrace();
		}		
		
		return "ERRO";
	}

	@RequestMapping(value = ContractRestURIConstants.TESTE_TRANSFERE, method = RequestMethod.GET)
	public @ResponseBody Transferencia transfereTeste() {
		logger.info("Start TESTE_TRANSFERE entre valores.");
		Transferencia transferencia = new Transferencia();
		transferencia.setDestino("0x0719D260682B4ef5941E93ACbAAC1E1b14CeaE60");
		transferencia.setOrigem("0x00Faa15694456FEbAA43f0d932Fd0898946CCeC4");
		transferencia.setValor(1222d);
		
		try{
			Address from  = new Address(transferencia.getOrigem());
			Address to    = new Address(transferencia.getDestino());
			Uint256 value = new Uint256(transferencia.getValor().intValue());
			transferencia.setMensagem("OK");
			tokenContract.transferFrom(from, to, value); //(transferencia.getOrigem(), value);
		}catch(Exception e){
			e.printStackTrace();
			transferencia.setMensagem("Erro: " + e.getMessage());
		}

		return transferencia;
	}

	@RequestMapping(value = ContractRestURIConstants.TESTE_LOGIN, method = RequestMethod.GET)
	public @ResponseBody StatusLogin loginTeste() {
		logger.info("Start TESTE_LOGIN entre valores.");
		StatusLogin sl = new StatusLogin();
		sl.setDataLogin(new Date());
		sl.setEmail("alex@astarlabs.com");
		sl.setMensagem("Login Sucesso");
		sl.setNome("Nome");
		Usuario user = new Usuario();
		user.setSaldo(new Saldo());
		user.getSaldo().setValor(1000d);
		user.setId("1");
		user.setConta("0x00Faa15694456FEbAA43f0d932Fd0898946CCeC4");
		user.setEmail("alex@astarlabs.com");
		user.setLogin("asimas");
		user.setNome("Alex Braz");
		user.setSenha("passwd");
		sl.setUsuario(user);
		return sl;
	}

	/*
	 * 
	 * 
	 * 
	 * Métodos de exemplo
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	@RequestMapping(value = ContractRestURIConstants.DUMMY_EMP, method = RequestMethod.GET)
	public @ResponseBody Employee getDummyEmployee() {
		logger.info("Start getDummyEmployee");
		service.teste();
		Employee emp = new Employee();
		emp.setId(9999);
		emp.setName("Dummy");
		emp.setCreatedDate(new Date());
		empData.put(9999, emp);
		return emp;
	}

	@RequestMapping(value = ContractRestURIConstants.GET_EMP, method = RequestMethod.GET)
	public @ResponseBody Employee getEmployee(@PathVariable("id") int empId) {
		logger.info("Start getEmployee. ID=" + empId);

		return empData.get(empId);
	}

	@RequestMapping(value = ContractRestURIConstants.GET_ALL_EMP, method = RequestMethod.GET)
	public @ResponseBody List<Employee> getAllEmployees() {
		logger.info("Start getAllEmployees.");
		List<Employee> emps = new ArrayList<Employee>();
		Set<Integer> empIdKeys = empData.keySet();
		for (Integer i : empIdKeys) {
			emps.add(empData.get(i));
		}
		return emps;
	}

	@RequestMapping(value = ContractRestURIConstants.CREATE_EMP, method = RequestMethod.POST)
	public @ResponseBody Employee createEmployee(@RequestBody Employee emp) {
		logger.info("Start createEmployee.");
		emp.setCreatedDate(new Date());
		empData.put(emp.getId(), emp);
		return emp;
	}

	@RequestMapping(value = ContractRestURIConstants.DELETE_EMP, method = RequestMethod.PUT)
	public @ResponseBody Employee deleteEmployee(@PathVariable("id") int empId) {
		logger.info("Start deleteEmployee.");
		Employee emp = empData.get(empId);
		empData.remove(empId);
		return emp;
	}

}