package br.ufrpe.poo.banco.negocio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.ufrpe.poo.banco.exceptions.SaldoInsuficienteException;

public class TesteContaImposto {
	/**
	 * Testa se os parametros do construtor sao passados corretamente
	 */
	@Test
	public void construtorInicializaCorretamente() {
		ContaImposto c = new ContaImposto("1",100);
		assertEquals("Numero incorreto", "1", c.getNumero());
		assertEquals("Saldo incorreto", 100,  c.getSaldo(), 0);
	}
	
	/**
	 * Testa debito com valor menor que o saldo
	 * @throws SaldoInsuficienteException 
	 */
	@Test 
	public void debitarConsideraImposto() throws SaldoInsuficienteException {
		ContaImposto c = new ContaImposto("2",1000);
		c.debitar(100);
		assertEquals(899.62,c.getSaldo(),0);
	}	
	

	@Test 
	public void DebitarSaldoInsuficiente() throws SaldoInsuficienteException {
		Double saldoInicial = 100.0;
		ContaImposto contaImposto = new ContaImposto("123",saldoInicial);
		Double valorDebitar = saldoInicial + 1;
		try {
			contaImposto.debitar(valorDebitar);
		}finally {
			String saldoDepoisDebito = String.valueOf(contaImposto.getSaldo());//confere valor do saldo
			assertTrue( saldoDepoisDebito, contaImposto.getSaldo() == saldoInicial);
		}
	}	
		
}
