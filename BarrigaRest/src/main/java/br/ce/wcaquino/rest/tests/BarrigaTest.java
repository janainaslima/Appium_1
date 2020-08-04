package br.ce.wcaquino.rest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;

public class BarrigaTest extends BaseTest {
	
	private String TOKEN;
	
	@Before
	
	public void login() {
		
		Map<String, String> login = new HashMap<>();
		login.put("email", "janainaslima@gmail.com");
		login.put("senha", "123456");
		
		TOKEN = given()
				.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token");
	}

	
	@Test
	
	public void naoAcessarAPISemToken() {
			given()
			.when()
			.get("/contas")
		.then()
			.statusCode(401);
	}
	
	@Test
	
	public void incluirConta() {
		
		given()
			.header("Authorization", "JWT " + TOKEN) //usar JWT ou bearer
			.body("{\"nome\": \"conta JanaTest2\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.log().all()
		;
		
	}

	
	@Test
	
	public void alterarConta() {
		
		given()
			.header("Authorization", "JWT " + TOKEN) //usar JWT ou bearer
			.body("{\"nome\": \"conta alterada\"}")
		.when()
			.put("/contas/96727")
		.then()
			.log().all()	
			.statusCode(200)
			.body("nome", Matchers.is("conta alterada"));
			}

	@Test
	
	public void naoIncluirContaMesmoNome() {
		
		given()
			.header("Authorization", "JWT " + TOKEN) //usar JWT ou bearer
			.body("{\"nome\": \"conta alterada\"}")
		.when()
			.post("/contas")
		.then()
			.log().all()	
			.statusCode(400)
			.body("error", Matchers.is("Já existe uma conta com esse nome!"));
			
			}
	
	@Test
	
	public void inserirMovimentacao() {
		
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(96727);
		//mov.setUsuario_id(usuario_id);
		mov.setDescricao("Descricao da Movimentacao");
		mov.setEnvolvido("Envolvido na mov");
		mov.setTipo("REC");
		mov.setData_transacao("01/01/2010");
		mov.setData_pagamento("01/05/2020");
		mov.setValor(100f);
		mov.setStatus(true);
		
		given()
			.header("Authorization", "JWT " + TOKEN) //usar JWT ou bearer
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			;
			
			}

	@Test
	
	public void validarCamposObrigatorios() {
		
		   given()
			.header("Authorization", "JWT " + TOKEN) //usar JWT ou bearer
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			;
			
			}

	@Test
	
	public void naoRemoverContaComMovimentacao() {
		given()
			.header("Authorization", "JWT " + TOKEN) //usar JWT ou bearer
		.when()
			.delete("/contas/96727") // para transações coloca /transacoes/numero transacao
		.then()
			.statusCode(500)//transacao cod 204
			;
			
			}

	@Test
	
	public void calcularSaldoContas() {
		given()
			.header("Authorization", "JWT " + TOKEN) //usar JWT ou bearer
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == 96727}.saldo", Matchers.is("200.00"))
			;
			
			}

	
}