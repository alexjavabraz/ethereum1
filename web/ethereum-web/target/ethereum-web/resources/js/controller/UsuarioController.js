class UsuarioController{
	constructor(){
		let $ = document.querySelector.bind(document);
		this._inputUsuario = $('#usuario');
		this._inputSenha = $('#senha');
		this._inputConta = $('#conta');
		this._inputSaldo = $('#saldo');
		this._usuarioView = new UsuarioView(this._inputUsuario, this._inputSenha, this._inputConta, this._inputSaldo);
		
		
	}
	
	atualiza(event){
		event.preventDefault();
		console.log('UsuarioController atualizando saldo do usuario');
		
		let service = new UsuarioService();
		
		console.log(`UsuarioController atualizando saldo do usuario ${service}`);
		console.log(`UsuarioController Input Value ${this._inputSaldo.value}`);

		
		service.atualizarUsuario()
			.then(saldo => {
				console.log(`UsuarioController saldo ${saldo}`);
				console.log(`UsuarioController saldo ${saldo.valor} :) `);
				
				this._inputSaldo.value = saldo.valor;
				
			})
			.catch(error => console.log(`${error} `));
		console.log('UsuarioController Fim atualizando saldo do usuario');
	}
}