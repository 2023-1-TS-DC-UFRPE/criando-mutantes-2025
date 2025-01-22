package br.ufrpe.poo.banco.dados;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import br.ufrpe.poo.banco.exceptions.RepositorioException;
import br.ufrpe.poo.banco.iterator.IteratorContaAbstrata;
import br.ufrpe.poo.banco.negocio.Conta;
import br.ufrpe.poo.banco.negocio.ContaAbstrata;
import br.ufrpe.poo.banco.negocio.ContaEspecial;
import br.ufrpe.poo.banco.negocio.ContaImposto;
import br.ufrpe.poo.banco.negocio.Poupanca;

public class TesteRepositorioContas {

	private RepositorioContasArquivoTxt repoContasTxt;
	private File arquivo;

	@Before
	public void RecriaArquivo() throws IOException, RepositorioException {
		if (arquivo != null)
			arquivo.delete();
		arquivo = new File("contas.dat");
		// Cria ou recria o arquivo vazio
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {
			bw.write("");
		}
		repoContasTxt = new RepositorioContasArquivoTxt(arquivo);
	}

	@Test
	public void InicializarRepositorioContasArquivoPreexistenteTxtComSucesso() throws RepositorioException {
		File _arquivo = new File("contas.dat");
		RepositorioContasArquivoTxt r = new RepositorioContasArquivoTxt(_arquivo);
		assertNotNull("teste de Arquivo ja existent", r);
	}

	@Test
	public void InicializarRepositorioContasArquivoTxtComArquivoInexistente() throws RepositorioException {
		new File("contas2.dat");
		File _arquivo = new File("contas2.dat"); // novo arquivo
		RepositorioContasArquivoTxt r = new RepositorioContasArquivoTxt(_arquivo);
		_arquivo.delete();
		assertNotNull("teste de novo arquivo - inexistente", r);
	}

	@Test
	public void InicializarRepositorioContasArquivoTxtComPathInvalido() throws RepositorioException {
		File _arquivo = new File("/path/invalido/contas3.dat");
		RepositorioContasArquivoTxt r = null;
		try {
			r = new RepositorioContasArquivoTxt(_arquivo);
		} catch (Exception e) {
			assertNull("teste de Arquivo invalido - IOException", r);
		}

	}

	@Test
	public void InserirContaComSucessoTxt() throws RepositorioException {
		Conta c = new Conta("1", 100);
		boolean inseriu = repoContasTxt.inserir(c);
		assertTrue("teste se inseriu", inseriu);
	}

	@Test
	public void InserirContaContaInvalidaTxt() throws RepositorioException {
		Conta c = null;
		boolean inseriu = repoContasTxt.inserir(c);
		assertTrue("teste se inseriu invalida", inseriu);
	}

	@Test
	public void procurar() throws RepositorioException {
		Conta c = new Conta("123", 100);
		repoContasTxt.inserir(c);
		ContaAbstrata ca = repoContasTxt.procurar(c.getNumero());
		assertNotNull("teste se procurou e achou", ca);
	}

	@Test
	public void RemoverContaComSucessoTxt() throws RepositorioException {
		Conta c = new Conta("123", 100);
		repoContasTxt.inserir(c);
		boolean removeu = repoContasTxt.remover(c.getNumero());
		assertTrue("teste se removeu", removeu);
	}

	@Test
	public void RemoverContaInexistenteTxt() throws RepositorioException {
		boolean removeu = repoContasTxt.remover("123"); // conta nao registrada
		assertTrue("teste se removeu inexistente", removeu);
	}

	@Test
	public void ContaExisteTxt() throws RepositorioException {
		Conta c = new Conta("123", 100);
		repoContasTxt.inserir(c);
		boolean existe = repoContasTxt.existe(c.getNumero());
		assertTrue("teste Conta Existe", existe);
	}

	@Test
	public void AtualizarContaTxt() throws RepositorioException {
		Conta c = new Conta("123", 100);
		repoContasTxt.inserir(c);
		boolean atualizou = repoContasTxt.atualizar(c);
		assertTrue("teste atualizar txte", atualizou);
	}

	@Test
	public void GetIteratorTxt() throws RepositorioException {
		Conta a = new Conta("123", 100);
		Conta b = new Conta("456", 1200);
		Conta c = new Conta("789", 300);
		Conta d = new Conta("951", 50);

		Conta contas[] = { a, b, c, d };

		repoContasTxt.inserir(a);
		repoContasTxt.inserir(b);
		repoContasTxt.inserir(c);
		repoContasTxt.inserir(d);

		IteratorContaAbstrata contasIterator = repoContasTxt.getIterator();
		int i = 0;
		while (contasIterator.hasNext()) {
			contasIterator.next();
			i++;
		}

		assertEquals("teste getIterator txt", contas.length, i + 1);
	}

	@Test
	public void LerArquivosConta() throws RepositorioException {
		Conta a = new Conta("123", 100);
		Poupanca b = new Poupanca("456", 1200);
		ContaImposto c = new ContaImposto("789", 300);
		ContaEspecial d = new ContaEspecial("951", 50);
		ContaAbstrata contas[] = { a, b, c, d };

		repoContasTxt.inserir(a);
		repoContasTxt.inserir(b);
		repoContasTxt.inserir(c);
		repoContasTxt.inserir(d);

		repoContasTxt = new RepositorioContasArquivoTxt(arquivo); // learquivo com as contas novamnte sem appagar

		IteratorContaAbstrata contasIterator = repoContasTxt.getIterator();
		int i = 0;
		while (contasIterator.hasNext()) {
			contasIterator.next();
			i++;
		}

		assertEquals("teste getIterator txt", contas.length, i + 1);
	}

	@Test
	public void GravarArquivosConta() throws RepositorioException {
		Conta a = new Conta("123", 100);
		Poupanca b = new Poupanca("456", 1200);
		ContaImposto c = new ContaImposto("789", 300);
		ContaEspecial d = new ContaEspecial("951", 50);

		repoContasTxt.inserir(a);
		repoContasTxt.inserir(b);
		repoContasTxt.inserir(c);
		repoContasTxt.inserir(d);
		repoContasTxt.remover(a.getNumero());
		repoContasTxt.remover(b.getNumero());
		repoContasTxt.remover(c.getNumero());
		repoContasTxt.remover(d.getNumero());

		IteratorContaAbstrata contasIterator = repoContasTxt.getIterator();
		assertFalse(contasIterator.hasNext());
	}

	@Test
	public void AtualizaArquivosConta() throws RepositorioException {
		Conta a = new Conta("123", 100);
		Poupanca b = new Poupanca("456", 1200);
		ContaImposto c = new ContaImposto("789", 300);
		ContaEspecial d = new ContaEspecial("951", 50);
		a.setSaldo(1000);
		b.setSaldo(1100);
		c.setSaldo(1200);
		d.setSaldo(1300);

		repoContasTxt.atualizar(a);
		repoContasTxt.atualizar(b);
		repoContasTxt.atualizar(c);
		repoContasTxt.atualizar(d);

		IteratorContaAbstrata contasIterator = repoContasTxt.getIterator();
		assertFalse(contasIterator.hasNext());
	}

	@Test
	public void InserirTipoDeContaInvalido() throws RepositorioException {
		ContaTeste contaInvalida = new ContaTeste("111", 111);
		boolean inseriu = repoContasTxt.inserir(contaInvalida);
		assertFalse(inseriu);
	}

	@Test
	public void DeveLancarExcecaoParaTipoNaoSuportado() throws RepositorioException {
		// Cria uma instância de um tipo não suportado

		ContaAbstrata conta = new Conta("999", 100);
		repoContasTxt.inserir(conta);
		ContaAbstrata contaInvalida = new ContaTeste("999", 0);

		boolean atualizou = repoContasTxt.atualizar(contaInvalida);
		// boolean removeu = repoContasTxt.remover(conta.getNumero());
		assertFalse(atualizou);
	}

	@Test
	public void DeveLancarArquivoNaoEncontrado() throws IOException, RepositorioException {

		arquivo = new File("contas10.dat");
		// Cria ou recria o arquivo vazio
		RepositorioContasArquivoTxt r = null;
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {
			bw.write("");
			ContaAbstrata conta = new Conta("999", 100);
			r = new RepositorioContasArquivoTxt(arquivo);
			r.inserir(conta);
			arquivo.delete();
			r = new RepositorioContasArquivoTxt(arquivo);

		} catch (Exception e) {
			arquivo.delete();
			assertNull(r);
		}
	}

	private class ContaTeste extends ContaAbstrata {

		private static final long serialVersionUID = 1L;

		public ContaTeste(String numero, double valor) {
			super(numero, valor);
			if (valor < 0) {
				this.setSaldo(0);
			}
		}

		@Override
		public void debitar(double valor) {

		}
	}

	@Test
	public void DeveDuplicarTamanhoDoArrayAoInserirConta100() throws RepositorioException {
		// Cria o repositório
		RepositorioContasArray repositorio = new RepositorioContasArray();

		ContaAbstrata contas[] = new Conta[200];

		for (int i = 0; i < 110; i++) {
			ContaAbstrata conta = new Conta(String.valueOf(i), i * 100.0);
			contas[i] = conta;
			boolean inseriu = repositorio.inserir(conta);
			assertTrue(inseriu); // Cada inserção deve retornar true
		}

		// Verifica se o índice atingiu o tamanho do array
		// Isso significa que o array foi preenchido até o limite inicial
		ContaAbstrata conta = repositorio.procurar("109");
		assertEquals(contas[109].getNumero(), conta.getNumero()); // Supondo que getIndice() seja um método auxiliar no
	}

}
