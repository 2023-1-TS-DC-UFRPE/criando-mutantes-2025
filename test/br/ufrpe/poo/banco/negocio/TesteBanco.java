package br.ufrpe.poo.banco.negocio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import br.ufrpe.poo.banco.dados.RepositorioClientesArquivoBin;
import br.ufrpe.poo.banco.dados.RepositorioContasArquivoBin;
import br.ufrpe.poo.banco.exceptions.AtualizacaoNaoRealizadaException;
import br.ufrpe.poo.banco.exceptions.ClienteJaCadastradoException;
import br.ufrpe.poo.banco.exceptions.ClienteJaPossuiContaException;
import br.ufrpe.poo.banco.exceptions.ClienteNaoCadastradoException;
import br.ufrpe.poo.banco.exceptions.ClienteNaoPossuiContaException;
import br.ufrpe.poo.banco.exceptions.ContaJaAssociadaException;
import br.ufrpe.poo.banco.exceptions.ContaJaCadastradaException;
import br.ufrpe.poo.banco.exceptions.ContaNaoEncontradaException;
import br.ufrpe.poo.banco.exceptions.InicializacaoSistemaException;
import br.ufrpe.poo.banco.exceptions.RenderBonusContaEspecialException;
import br.ufrpe.poo.banco.exceptions.RenderJurosPoupancaException;
import br.ufrpe.poo.banco.exceptions.RepositorioException;
import br.ufrpe.poo.banco.exceptions.SaldoInsuficienteException;
import br.ufrpe.poo.banco.exceptions.ValorInvalidoException;

public class TesteBanco {

	private static Banco banco;

	@Before
	public void apagarArquivos() throws IOException, RepositorioException, InicializacaoSistemaException {

		BufferedWriter bw = new BufferedWriter(new FileWriter("clientes.dat"));
		bw.close();
		bw = new BufferedWriter(new FileWriter("contas.dat"));
		bw.close();

		Banco.instance = null;
		banco = Banco.getInstance();
	}

	/**
	 * Verifica o cadastramento de uma nova conta.
	 * 
	 */
	@Test
	public void contaCadastradaPersistidaCorretamente() throws RepositorioException, ContaJaCadastradaException,
			ContaNaoEncontradaException, InicializacaoSistemaException {

		ContaAbstrata conta1 = new Conta("1", 100);
		banco.cadastrar(conta1);
		ContaAbstrata conta2 = banco.procurarConta("1");
		assertEquals(conta1.getNumero(), conta2.getNumero());
		assertEquals(conta1.getSaldo(), conta2.getSaldo(), 0);
	}

	/**
	 * Verifica que nao e permitido cadastrar duas contas com o mesmo numero.
	 * 
	 */
	@Test(expected = ContaJaCadastradaException.class)
	public void tentativaCadastrarContaJaExistente() throws RepositorioException, ContaJaCadastradaException,
			ContaNaoEncontradaException, InicializacaoSistemaException {

		Conta c1 = new Conta("1", 200);
		Conta c2 = new Conta("1", 300);
		banco.cadastrar(c1);
		banco.cadastrar(c2);
		fail("Excecao ContaJaCadastradaException nao levantada");
	}

	/**
	 * Verifica se o credito esta sendo executado corretamente em uma conta
	 * corrente.
	 * 
	 */
	@Test
	public void testeCreditarSaldoSuficiente() throws RepositorioException, ContaNaoEncontradaException,
			InicializacaoSistemaException, ContaJaCadastradaException, ValorInvalidoException {

		ContaAbstrata conta = new Conta("1", 100);
		banco.cadastrar(conta);
		banco.creditar(conta, 100);
		conta = banco.procurarConta("1");
		assertEquals(200, conta.getSaldo(), 0);
	}

	/**
	 * Verifica que a operacao de debito em conta corrente esta acontecendo
	 * corretamente.
	 * 
	 */
	@Test
	public void testeDebitarSaldoSuficiente()
			throws RepositorioException, ContaNaoEncontradaException, SaldoInsuficienteException,
			InicializacaoSistemaException, ContaJaCadastradaException, ValorInvalidoException {

		ContaAbstrata conta = new Conta("1", 50);
		banco.cadastrar(conta);
		banco.debitar(conta, 50);
		conta = banco.procurarConta("1");
		assertEquals(0, conta.getSaldo(), 0);
	}

	/**
	 * Verifica que a transferencia entre contas correntes e realizada com sucesso.
	 * 
	 */
	@Test
	public void transferirEntreContasCorrentesSaldoSuficiente()
			throws RepositorioException, ContaNaoEncontradaException, SaldoInsuficienteException,
			InicializacaoSistemaException, ContaJaCadastradaException, ValorInvalidoException {

		ContaAbstrata conta1 = new Conta("1", 100);
		ContaAbstrata conta2 = new Conta("2", 200);
		banco.cadastrar(conta1);
		banco.cadastrar(conta2);
		banco.transferir(conta1, conta2, 50);
		conta1 = banco.procurarConta("1");
		conta2 = banco.procurarConta("2");
		assertEquals(50, conta1.getSaldo(), 0);
		assertEquals(250, conta2.getSaldo(), 0);
	}

	/**
	 * Verifica que render juros de uma conta poupanca funciona corretamente
	 * 
	 */
	@Test
	public void renderJurosContaPoupanca() throws RepositorioException, ContaNaoEncontradaException,
			RenderJurosPoupancaException, InicializacaoSistemaException, ContaJaCadastradaException {

		Poupanca poupanca = new Poupanca("20", 100);
		banco.cadastrar(poupanca);
		double saldoSemJuros = poupanca.getSaldo();
		double saldoComJuros = saldoSemJuros + (saldoSemJuros * 0.008);
		poupanca.renderJuros(0.008);
		assertEquals(saldoComJuros, poupanca.getSaldo(), 0);
	}

	// ======================================== MEUS TESTES =======================
	@Test
	public void CadastarNovaConta() throws RepositorioException, ContaJaCadastradaException,
			ContaNaoEncontradaException, InicializacaoSistemaException {

		Banco banco = new Banco(null, new RepositorioContasArquivoBin());
		ContaAbstrata conta1 = new Conta("1", 100);
		banco.cadastrar(conta1);
		ContaAbstrata conta2 = banco.procurarConta("1");
		assertEquals(conta1.getNumero(), conta2.getNumero());
		assertEquals(conta1.getSaldo(), conta2.getSaldo(), 0);
	}

	@Test(expected = ContaJaCadastradaException.class)
	public void testeCadastrarContaExistente() throws RepositorioException, ContaJaCadastradaException,
			ContaNaoEncontradaException, InicializacaoSistemaException {

		Conta c1 = new Conta("1", 200);
		Conta c2 = new Conta("1", 300);
		banco.cadastrar(c1);
		banco.cadastrar(c2);
		fail("Excecao ContaJaCadastradaException nao levantada");
	}

	@Test(expected = ClienteNaoCadastradoException.class)
	public void testeProcurarClienteInexistente() throws ClienteNaoCadastradoException {

		banco.procurarCliente(null);
		fail("Excecao ClienteNaoCadastradoException nao levantada");

	}

	@Test(expected = ClienteJaCadastradoException.class)
	public void testeCadastrarClienteExistente()
			throws ClienteNaoCadastradoException, RepositorioException, ClienteJaCadastradoException {

		Cliente c1 = new Cliente("testt", "testtt");

		banco.cadastrarCliente(c1);
		banco.cadastrarCliente(c1);
		fail("Excecao ClienteJaCadastradoException nao levantada");

	}

	@Test(expected = ContaNaoEncontradaException.class)
	public void testeRemoverContaInexistente() throws RepositorioException, ContaJaCadastradaException,
			ContaNaoEncontradaException, InicializacaoSistemaException, ClienteNaoPossuiContaException,
			ClienteJaCadastradoException, ClienteNaoCadastradoException {

		Cliente c1 = new Cliente("teste", "teste");
		c1.contas.add("teste");
		banco.cadastrarCliente(c1);

		banco.removerConta(c1, "teste");

		fail("Excecao ContaNaoEncontradaException nao levantada");
	}

	@Test
	public void RemoverContaExistente() throws RepositorioException, ContaJaCadastradaException,
			ContaNaoEncontradaException, InicializacaoSistemaException, ClienteNaoPossuiContaException,
			ClienteJaCadastradoException, ClienteNaoCadastradoException {

		Conta co = new Conta("teste", 0);
		Cliente cl = new Cliente("teste", "teste");
		cl.contas.add(co.getNumero());
		banco.cadastrarCliente(cl);
		banco.cadastrar(co);

		banco.removerConta(cl, "teste");

		assertFalse(banco.procurarConta(co.numero).equals(co));

	}

	@Test
	public void AssociarClienteExistenteContaExistente() throws RepositorioException, ClienteJaCadastradoException,
			ClienteJaPossuiContaException, ContaJaAssociadaException, ClienteNaoCadastradoException,
			ContaJaCadastradaException, ContaNaoEncontradaException {

		Cliente cl = new Cliente("teste", "teste");
		Conta co = new Conta("teste", 0);
		banco.cadastrarCliente(cl);
		banco.cadastrar(co);
		banco.associarConta(cl.getCpf(), co.getNumero());
		assertFalse(!(cl.contas.contains(co.getNumero())));
	}

	@Test
	public void AssociarClienteExistenteContaInexistente()
			throws RepositorioException, ClienteJaCadastradoException, ClienteJaPossuiContaException,
			ContaJaAssociadaException, ClienteNaoCadastradoException, ContaNaoEncontradaException {

		Cliente cl = new Cliente("teste", "teste");
		banco.cadastrarCliente(cl);
		banco.associarConta(cl.getCpf(), null);

	}

	@Test
	public void AssociarClienteInexistente()
			throws RepositorioException, ClienteJaCadastradoException, ClienteJaPossuiContaException,
			ContaJaAssociadaException, ClienteNaoCadastradoException, ContaNaoEncontradaException {

		Cliente cl = new Cliente("teste", "teste");
		banco.associarConta(cl.getCpf(), null);

	}

	@Test(expected = ClienteNaoCadastradoException.class)
	public void RemoverClienteInexistente() throws RepositorioException, ContaJaCadastradaException,
			ContaNaoEncontradaException, InicializacaoSistemaException, ClienteNaoPossuiContaException,
			ClienteJaCadastradoException, ClienteNaoCadastradoException {

		Cliente c = new Cliente("saaaa", "3721039");
		c.contas = null;

		banco.removerCliente(c.getCpf());

		fail("Excecao ClienteNaoCadastradoException nao levantada");
	}

	@Test
	public void RemoverClienteExistenteSemContas() throws RepositorioException, ContaJaCadastradaException,
			ContaNaoEncontradaException, InicializacaoSistemaException, ClienteNaoPossuiContaException,
			ClienteJaCadastradoException, ClienteNaoCadastradoException {

		Cliente cl = new Cliente("test", "test");

		banco.cadastrarCliente(cl);

		banco.removerCliente(cl.getCpf());

		assertFalse(banco.procurarCliente(cl.getCpf()).equals(cl));

	}

	@Test
	public void RemoverClienteExistenteComContas() throws RepositorioException, ContaJaCadastradaException,
			ContaNaoEncontradaException, InicializacaoSistemaException, ClienteNaoPossuiContaException,
			ClienteJaCadastradoException, ClienteNaoCadastradoException {

		Conta co = new Conta("teste", 0);
		Conta co2 = new Conta("testee", 0);
		Cliente cl = new Cliente("teste", "teste");
		cl.contas.add(co.getNumero());
		cl.contas.add(co2.getNumero());
		banco.cadastrarCliente(cl);
		banco.cadastrar(co);

		banco.removerCliente(cl.getCpf());

		assertFalse(banco.procurarCliente(cl.getCpf()).equals(cl));

	}

	/**
	 * Verifica se o credito esta sendo executado corretamente em uma conta
	 * corrente.
	 * 
	 */
	@Test
	public void CreditarContaExistente() throws RepositorioException, ContaNaoEncontradaException,
			InicializacaoSistemaException, ContaJaCadastradaException, ValorInvalidoException {

		ContaAbstrata conta = new Conta("1", 100);
		banco.cadastrar(conta);
		banco.creditar(conta, 100);
		conta = banco.procurarConta("1");
		assertEquals(200, conta.getSaldo(), 0);
	}

	@Test(expected = ValorInvalidoException.class)
	public void CreditarValorInvalido() throws RepositorioException, ValorInvalidoException {

		banco.creditar(new Conta("teste", 32), -60);
		fail("Excecao ValorInvalidoException nao levantada");

	}

	@Test(expected = ValorInvalidoException.class)
	public void DebitarValorInvalido()
			throws RepositorioException, ValorInvalidoException, SaldoInsuficienteException {

		banco.debitar(new Conta("teste", 32), -60);
		fail("Excecao ValorInvalidoException nao levantada");

	}

	/**
	 * Verifica a excecao levantada na tentativa de creditar em uma conta que nao
	 * existe.
	 * 
	 */
	@Test(expected = ContaNaoEncontradaException.class)
	public void CreditarContaInexistente() throws RepositorioException, ContaNaoEncontradaException,
			InicializacaoSistemaException, ValorInvalidoException {

		banco.creditar(new Conta("", 0), 200);

		fail("Excecao ContaNaoEncontradaException nao levantada");
	}

	/**
	 * Verifica que a operacao de debito em conta corrente esta acontecendo
	 * corretamente.
	 * 
	 */
	@Test
	public void DebitarContaExistente()
			throws RepositorioException, ContaNaoEncontradaException, SaldoInsuficienteException,
			InicializacaoSistemaException, ContaJaCadastradaException, ValorInvalidoException {

		ContaAbstrata conta = new Conta("1", 50);
		banco.cadastrar(conta);
		banco.debitar(conta, 50);
		conta = banco.procurarConta("1");
		assertEquals(0, conta.getSaldo(), 0);
	}

	/**
	 * Verifica que tentantiva de debitar em uma conta que nao existe levante
	 * excecao.
	 * 
	 */
	@Test(expected = ContaNaoEncontradaException.class)
	public void DebitarContaInexistente() throws RepositorioException, ContaNaoEncontradaException,
			SaldoInsuficienteException, InicializacaoSistemaException, ValorInvalidoException {

		banco.debitar(new Conta("", 0), 50);
		fail("Excecao ContaNaoEncontradaException nao levantada");
	}

	/**
	 * Verifica que a transferencia entre contas correntes e realizada com sucesso.
	 * 
	 */
	@Test
	public void TransferirContaExistente()
			throws RepositorioException, ContaNaoEncontradaException, SaldoInsuficienteException,
			InicializacaoSistemaException, ContaJaCadastradaException, ValorInvalidoException {

		ContaAbstrata conta1 = new Conta("1", 100);
		ContaAbstrata conta2 = new Conta("2", 200);
		banco.cadastrar(conta1);
		banco.cadastrar(conta2);
		banco.transferir(conta1, conta2, 50);
		conta1 = banco.procurarConta("1");
		conta2 = banco.procurarConta("2");
		assertEquals(50, conta1.getSaldo(), 0);
		assertEquals(250, conta2.getSaldo(), 0);
	}

	/**
	 * Verifica que tentativa de transferir entre contas cujos numeros nao existe
	 * levanta excecao.
	 * 
	 */
	@Test(expected = ContaNaoEncontradaException.class)
	public void TransferirContaInexistente() throws RepositorioException, ContaNaoEncontradaException,
			SaldoInsuficienteException, InicializacaoSistemaException, ValorInvalidoException {

		banco.transferir(new Conta("", 0), new Conta("", 0), 50);
		fail("Excecao ContaNaoEncontradaException nao levantada)");
	}

	/**
	 * Verifica que render juros de uma conta poupanca funciona corretamente
	 * 
	 */

	@Test
	public void RenderJurosContaExistente() throws RepositorioException, ContaNaoEncontradaException,
			RenderJurosPoupancaException, InicializacaoSistemaException, ContaJaCadastradaException {

		Poupanca poupanca = new Poupanca("20", 100);
		banco.cadastrar(poupanca);
		double saldoSemJuros = poupanca.getSaldo();
		double saldoComJuros = saldoSemJuros + (saldoSemJuros * 0.008);
		banco.renderJuros(poupanca);
		assertEquals(saldoComJuros, poupanca.getSaldo(), 0);
	}

	/**
	 * Verifica que tentativa de render juros em conta inexistente levanta excecao.
	 * 
	 */

	@Test(expected = ContaNaoEncontradaException.class)
	public void RenderJurosContaInexistente() throws RepositorioException, ContaNaoEncontradaException,
			RenderJurosPoupancaException, InicializacaoSistemaException {

		banco.renderJuros(new Poupanca("20", 100));
		fail("Excecao ContaNaoEncontradaException nao levantada");

	}

	/**
	 * Verifica que tentativa de render juros em conta que nao e poupanca levanta
	 * excecao.
	 * 
	 */
	@Test(expected = RenderJurosPoupancaException.class)
	public void RenderJurosContaNaoEhPoupanca() throws RepositorioException, ContaNaoEncontradaException,
			RenderJurosPoupancaException, InicializacaoSistemaException, ContaJaCadastradaException {

		banco.renderJuros(new ContaEspecial("-13", 13));

		fail("Excecao RenderJurosPoupancaException nao levantada");
	}

	/**
	 * Verifica que render bonus de uma conta especial funciona corretamente.
	 * 
	 */

	@Test
	public void RenderBonusContaEspecialExistente()
			throws RepositorioException, ContaNaoEncontradaException, RenderBonusContaEspecialException,
			InicializacaoSistemaException, RenderJurosPoupancaException, ContaJaCadastradaException {

		ContaEspecial ce = new ContaEspecial("20", 210);
		double bonus = ce.getBonus() * ce.getSaldo();
		banco.cadastrar(ce);
		banco.renderBonus(ce);
		assertEquals(bonus, ce.getBonus(), 0);

	}

	/**
	 * Verifica que a tentativa de render bonus em inexistente levanta excecao.
	 * 
	 */

	@Test(expected = ContaNaoEncontradaException.class)
	public void RenderBonusContaEspecialNaoInexistente() throws RepositorioException, ContaNaoEncontradaException,
			RenderBonusContaEspecialException, InicializacaoSistemaException, RenderJurosPoupancaException {
		banco.renderBonus(new ContaEspecial("21", 245));
		fail("Excecao ContaNaoEncontradaException nao levantada");

	}

	/**
	 * Verifica que tentativa de render bonus em conta que nao e especial levante
	 * excecao.
	 */

	@Test(expected = RenderBonusContaEspecialException.class)
	public void RenderBonusContaNaoEspecial()
			throws RepositorioException, ContaNaoEncontradaException, RenderBonusContaEspecialException,
			InicializacaoSistemaException, RenderJurosPoupancaException, ContaJaCadastradaException {
		banco.renderBonus(new Conta("231", 124));

		fail("Excecao RenderBonusContaEspecialException nao levantada");

	}

	@Test
	public void AtualizarClienteExistente() throws ClienteNaoCadastradoException, RepositorioException,
			ClienteJaCadastradoException, AtualizacaoNaoRealizadaException {
		Cliente c = new Cliente("teste", "teste");
		banco.cadastrarCliente(c);
		c.setCpf("testee");
		banco.atualizarCliente(c);
		assertEquals(c, banco.clientes.procurar("testee"));
	}

	@Test
	public void AtualizarClienteInexistente() throws ClienteNaoCadastradoException, RepositorioException,
			ClienteJaCadastradoException, AtualizacaoNaoRealizadaException {

		banco.atualizarCliente(new Cliente("t123", "3t3"));
		fail("Excecao ClienteNaoCadastradoException nao levantada");

	}

	@Test
	public void getInstanceNaoNulo() throws RepositorioException, InicializacaoSistemaException {

		Banco b = null;

		try {
			b = new Banco(new RepositorioClientesArquivoBin(), new RepositorioContasArquivoBin());
		} catch (RepositorioException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		Banco b2 = Banco.getInstance();

	}

	@Test(expected = RepositorioException.class)
	public void getInstanceRepositorioVazio() throws RepositorioException, InicializacaoSistemaException {

		Banco b = null;

		Banco.instance.clientes = null;
		Banco.instance.contas = null;

		Banco b2 = Banco.getInstance();

		fail("Excecao RepositorioException nao levantada");

	}

}
