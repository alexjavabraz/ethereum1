class HttpService{
	

    get(url){
        return new Promise((resolve, reject) => {
            let xhr = new XMLHttpRequest();
            console.log(`Requested URL ${url} `);
            xhr.open('GET', url);
            /*configuracoes */
            xhr.onreadystatechange = ()=>{
                /*
                0: requisição ainda não iniciada
                1: conexão com o servidor estabelecida
                2: requisicação recebida
                3: processando requisição
                4: requisição concluida e a resposta esta pronta
                */
                if(xhr.readyState == 4){
                    if(xhr.status == 200){
                    	console.log(xhr.responseText);
                        console.log('HttpService Fazendo request pro servidor.');
                        resolve(JSON.parse(xhr.responseText));
                    }else{
                        console.log('HttpService Nao foi possivel obter os dados do servidor.');
                        console.log(xhr.responseText);
                        reject(xhr.responseText);
                    }
                }
            }
            xhr.send();
        });
    }


    post(url, dado) {
        
        
                return new Promise((resolve, reject) => {
        
                    let xhr = new XMLHttpRequest();
                    xhr.open("POST", url, true);
                    xhr.setRequestHeader("Content-Type", "application/json");


                    xhr.onreadystatechange = () => {
                        
                                        if (xhr.readyState == 4) {
                        
                                            if (xhr.status == 200) {
                        
                                                resolve(JSON.parse(xhr.responseText));
                                            } else {
                        
                                                reject(xhr.responseText);
                                            }
                                        }
                                    };

                                    xhr.send(JSON.stringify(dado)); // usando JSON.stringify para converter objeto em uma string no formato JSON.
                                });
        
            }

}