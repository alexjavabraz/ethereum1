class UsuarioView{
	
	constructor(nome, senha, conta, saldo){
		this._nome = nome;
		this._senha = senha;
		this._conta = conta;
		this._saldo = saldo;
	}
	
	update(novoNome, novaSenha, novaConta, novoSaldo){
		this._nome.value  = novoNome;
		this._senha.value = novaSenha;
		this._conta.value = novaConta;
		this._saldo.value = novoSaldo;	
	}
	
}