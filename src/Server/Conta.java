package Server;

public class Conta {
	
	String nome;
	String agencia;
	String conta;
	double valorEmCaixa;
	String extrato;
	
	public String getExtrato() {
		return extrato;
	}

	public void setExtrato(String extrato) {
		this.extrato = extrato;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public double getValorEmCaixa() {
		return valorEmCaixa;
	}

	public void setValorEmCaixa(double d) {
		this.valorEmCaixa = d;
	}
		
}
