package com.datecs.examples.PrinterSample.entity;

import java.util.Date;
import java.util.List;

public class Pedido {
    private String nomeEmpresa;
	private double idPedido = 0;
    private String cod_pe;
    private String codcliente_pe;
    private String cliente = "";
    private String cidadeCliente;
    private String enderecoCliente;
    private String bairroCliente;
    private String numeroCliente;
    private String datavenda_pe;
    private int codvendedor_pe = 0;
    private String forpg_pe;
    private int dias_pe = 0;
    private int entrada_pe = 0;
    private int parcelas_pe=0;
    private int regis_pe=0;
    private Double valorTotal;
    private List<Produto> produtos;
    
    public Double getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}
	public String getBairroCliente() {
		return bairroCliente;
	}
	public void setBairroCliente(String bairroCliente) {
		this.bairroCliente = bairroCliente;
	}
	public String getNomeEmpresa() {
		return nomeEmpresa;
	}
	public void setNomeEmpresa(String nomeEmpresa) {
		this.nomeEmpresa = nomeEmpresa;
	}
	public String getNumeroCliente() {
		return numeroCliente;
	}
	public void setNumeroCliente(String numeroCliente) {
		this.numeroCliente = numeroCliente;
	}
	public List<Produto> getProdutos() {
		return produtos;
	}
	public void setProdutos(List<Produto> produtos) {
		this.produtos = produtos;
	}
	public String getCidadeCliente() {
		return cidadeCliente;
	}
	public void setCidadeCliente(String cidadeCliente) {
		this.cidadeCliente = cidadeCliente;
	}
	public String getEnderecoCliente() {
		return enderecoCliente;
	}
	public void setEnderecoCliente(String enderecoCliente) {
		this.enderecoCliente = enderecoCliente;
	}

	public double getIdPedido() {
		return idPedido;
	}
	public void setIdPedido(double idPedido) {
		this.idPedido = idPedido;
	}
	public String getCod_pe() {
		return cod_pe;
	}
	public void setCod_pe(String cod_pe) {
		this.cod_pe = cod_pe;
	}
	public String getCodcliente_pe() {
		return codcliente_pe;
	}
	public void setCodcliente_pe(String codcliente_pe) {
		this.codcliente_pe = codcliente_pe;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	public String getDatavenda_pe() {
		return datavenda_pe;
	}
	public void setDatavenda_pe(String datavenda_pe) {
		this.datavenda_pe = datavenda_pe;
	}
	public int getCodvendedor_pe() {
		return codvendedor_pe;
	}
	public void setCodvendedor_pe(int codvendedor_pe) {
		this.codvendedor_pe = codvendedor_pe;
	}
	public String getForpg_pe() {
		return forpg_pe;
	}
	public void setForpg_pe(String forpg_pe) {
		this.forpg_pe = forpg_pe;
	}
	public int getDias_pe() {
		return dias_pe;
	}
	public void setDias_pe(int dias_pe) {
		this.dias_pe = dias_pe;
	}
	public int getEntrada_pe() {
		return entrada_pe;
	}
	public void setEntrada_pe(int entrada_pe) {
		this.entrada_pe = entrada_pe;
	}
	public int getParcelas_pe() {
		return parcelas_pe;
	}
	public void setParcelas_pe(int parcelas_pe) {
		this.parcelas_pe = parcelas_pe;
	}
	public int getRegis_pe() {
		return regis_pe;
	}
	public void setRegis_pe(int regis_pe) {
		this.regis_pe = regis_pe;
	}
	
	@Override
	public String toString() {
		return "Pedido [nomeEmpresa=" + nomeEmpresa + ", cod_pe=" + cod_pe
				+ ", codcliente_pe=" + codcliente_pe + ", cliente=" + cliente
				+ ", cidadeCliente=" + cidadeCliente + ", enderecoCliente="
				+ enderecoCliente + ", bairroCliente=" + bairroCliente
				+ ", numeroCliente=" + numeroCliente + ", datavenda_pe="
				+ datavenda_pe + ", forpg_pe=" + forpg_pe + ", valorTotal=" + valorTotal +"]";
	}
    
	
	
}