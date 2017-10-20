<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Detalhes do Usuário</title>
<link rel="stylesheet"
	href="<spring:url value='resources/css/bootstrap.css'/>">
    <link rel="stylesheet" href="<spring:url value='resources/css/bootstrap-theme.css'/>">
    
    
</head>
<body class="container">
    
    <h1 class="text-center">Detalhes Usuário</h1>
    
    <form class="form" onsubmit="usuarioController.atualiza(event)">
        
        <div class="form-group">
            <label for="usuario">Usuário</label>
            <input type="text" id="usuario" class="form-control" required autofocus/>        
        </div> 
        
           <div class="form-group">
            <label for="senha">Senha</label>
            <input type="text" id="senha" class="form-control" required/>        
        </div>   
        
        <div class="form-group">
            <label for="conta">Conta</label>
            <input type="number"   disabled="disabled"  id="conta" class="form-control" value="1" required/>
        </div>
        
        <div class="form-group">
            <label for="saldo">Saldo</label>
            <input id="saldo" type="number" disabled="disabled" class="form-control"  min="0.01" step="0.01" value="0.0" required />
        </div>
        
        <button class="btn btn-primary" type="submit">Atualizar usuário</button>
    </form>
    
    <div class="text-center">
        <button class="btn btn-primary text-center" type="button" onclick="negociacaoController.importaNegociacoes();">
            Consultar Transações
        </button>
        <button class="btn btn-primary text-center" type="button" onclick="usuarioController.atualiza()">
            Apagar
        </button>
    </div> 
    <br>
    <br>
    <script src="<spring:url value='resources/js/service/HttpService.js'/>"></script>
    <script src="<spring:url value='resources/js/model/Saldo.js'/>"></script>
	<script src="<spring:url value='resources/js/helper/Bind.js'/>"></script>
	<script src="<spring:url value='resources/js/helper/DateHelper.js'/>"></script>
	<script src="<spring:url value='resources/js/view/UsuarioView.js'/>"></script>
	<script src="<spring:url value='resources/js/service/UsuarioService.js'/>"></script>
	<script	src="<spring:url value='resources/js/controller/UsuarioController.js'/>"></script>

    <script>
        let usuarioController = new UsuarioController();
    </script>
</body>
</html>