class UsuarioService{
	constructor(){
		this._http = new HttpService();
	}
	
	atualizarUsuario(){
		return new Promise((resolve, reject)=>{
			
			console.log('UsuarioService getting rest/eth/saldo/1 :) ');
			
            this._http.get('rest/eth/saldo/1')
            .then(objeto =>{
            	
            	console.log(`Recuperei o objeto ${objeto}`);
            	console.log(objeto);
            	console.log(objeto.valor);
            	let saldo = new Saldo(objeto.valor);
            	
                resolve(saldo)
            })
            .catch(erro=>{
                console.log(erro);
                reject(`UsuarioService ocorreu um erro ${erro} `);
            });
		});
	}
	
}