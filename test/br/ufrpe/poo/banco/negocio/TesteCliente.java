package br.ufrpe.poo.banco.negocio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import br.ufrpe.poo.banco.exceptions.ClienteJaPossuiContaException;
import br.ufrpe.poo.banco.exceptions.ClienteNaoPossuiContaException;

/**
 * Classe de teste respons�vel por testar as condi��es dos m�todos
 * adicionarConta e removerConta da classe Cliente.
 * 
 * @author Aluno
 * 
 */
public class TesteCliente {

	private Cliente clienteTest;

	/**
	 * Testa a inser��o de uma nova conta vinculada ao cliente
	 * 
	 * @throws ClienteJaPossuiContaException
	 */
	@Test
	public void adicionaContasSucesso() throws ClienteJaPossuiContaException {
		Cliente c1 = new Cliente("nome", "123");
		c1.adicionarConta("2");
		c1.adicionarConta("1");
		assertEquals(c1.getContas().size(), 2, 0);
		assertEquals(c1.procurarConta("2"), 0);
		assertEquals(c1.procurarConta("1"), 1);
	}

	/**
	 * Testa a condi��o da tentativa de adicionar uma conta j� existente � lista de
	 * contas do cliente
	 * 
	 * @throws ClienteJaPossuiContaException
	 */
	@Test(expected = ClienteJaPossuiContaException.class)
	public void adicionarContaJaExistente() throws ClienteJaPossuiContaException {
		Cliente c1 = new Cliente("nome", "123");
		c1.adicionarConta("1");
		c1.adicionarConta("2");
		c1.adicionarConta("3");
		c1.adicionarConta("4");
		c1.adicionarConta("3"); // tentativa de adicionar a mesma conta
	}

	/**
	 * Teste a remo��o de uma conta da lista de contas do cliente
	 * 
	 * @throws ClienteJaPossuiContaException
	 * @throws ClienteNaoPossuiContaException
	 */
	@Test
	public void removerContaCliente() throws ClienteJaPossuiContaException, ClienteNaoPossuiContaException {
		Cliente c1 = new Cliente("nome", "123");
		c1.adicionarConta("1");
		c1.adicionarConta("2");
		c1.removerConta("2");
		c1.removerConta("1");
		assertEquals(c1.procurarConta("1"), -1);
		assertEquals(c1.procurarConta("2"), -1);
	}

	/**
	 * Testa a remo��o de uma determinada conta que n�o est� vinculada ao cliente
	 * 
	 * @throws ClienteNaoPossuiContaException
	 */
	@Test(expected = ClienteNaoPossuiContaException.class)
	public void removerContaClienteSemContaTest() throws ClienteNaoPossuiContaException {
		Cliente c1 = new Cliente("nome", "123");
		c1.removerConta("1");
	}

	////////// MEUS TESTES /////////////////
	@Test
	public void ToString() {
		String name = "Christian";
		String acc = "12345678911";
		Cliente cliente = new Cliente(name, acc);
		String expected = "Nome: " + name + "\nCPF:" + acc + "\nContas: []";
		assertEquals(expected, cliente.toString());
	}

	@Test
	public void EqualsFalso() {
		Conta conta = new Conta("123", 100);
		Cliente cliente = new Cliente("Maria", "12345678911");
		assertFalse(cliente.equals((Object) conta));
	}

	@Test
	public void EqualsVerdadeiro() {
		Cliente clientA = new Cliente("Maria", "12345678911");
		Cliente clienteB = new Cliente("Maria", "12345678911");
		assertTrue(clientA.equals((Object) clienteB));
	}

	@Before
	public void Config() {
		clienteTest = new Cliente("João", "12345678901");
		try {
			clienteTest.adicionarConta("1");
			clienteTest.adicionarConta("2");
			clienteTest.adicionarConta("3");
		} catch (ClienteJaPossuiContaException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void ProcurarConta() {
		int indice = clienteTest.procurarConta("1");
		assertEquals(1, indice);
	}

	@Test
	public void ProcurarContaInexistente() { // ISSUE = FALTA EXCEÇÃO CONTA INEXISTENTE - OU JA USA EXC. CONTA
												// INCORRETA?
		int indice = clienteTest.procurarConta("999");
		assertEquals(-1, indice);
	}

	@Test
	public void RemoverTodasAsContas() {
		clienteTest.removerTodasAsContas();
		assertNull(clienteTest.getContas());
	}

	@Test
	public void GetNome() {
		Cliente cliente = new Cliente("ABC", "123456789");
		assertEquals("ABC", cliente.getNome());
	}

	@Test
	public void SetNome() {
		Cliente cliente = new Cliente("ABC", "123456789");
		cliente.setNome("XYZ");
		assertEquals("XYZ", cliente.getNome());
	}

	@Test
	public void GetCpf() {
		Cliente cliente = new Cliente("ABC", "123456789");
		assertEquals("123456789", cliente.getCpf());
	}

	@Test
	public void SetCpf() {
		Cliente cliente = new Cliente("ABC", "123456789");
		cliente.setCpf("123456780");
		assertEquals("123456780", cliente.getCpf());
	}

	@Test
	public void ConsultarNumeroConta() {// issue - conta deve solta exceção se nao achar a posicação
		if (clienteTest.getContas().size() == 0)
			return;
		String conta = clienteTest.consultarNumeroConta(0); // primeira conta
		String contaAchada = clienteTest.getContas().get(0);
		assertEquals("Deve ser iguais", conta.equals(contaAchada));

	}

}
