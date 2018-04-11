package com.datecs.examples.PrinterSample.entity;

public class Produto {
	private String codproduto_pe;
	private double vrunit_pe = 0;
    private int nitem_pe =0 ;
    private double valor_pe = 0;
    private double qtde_pe = 0;
    private String obs_pe = "";
    private String descricao = "";
	
    public String getCodproduto_pe() {
		return codproduto_pe;
	}
	public void setCodproduto_pe(String codproduto_pe) {
		this.codproduto_pe = codproduto_pe;
	}
	public double getVrunit_pe() {
		return vrunit_pe;
	}
	public void setVrunit_pe(double vrunit_pe) {
		this.vrunit_pe = vrunit_pe;
	}
	public int getNitem_pe() {
		return nitem_pe;
	}
	public void setNitem_pe(int nitem_pe) {
		this.nitem_pe = nitem_pe;
	}
	public double getValor_pe() {
		return valor_pe;
	}
	public void setValor_pe(double valor_pe) {
		this.valor_pe = valor_pe;
	}
	public double getQtde_pe() {
		return qtde_pe;
	}
	public void setQtde_pe(double qtde_pe) {
		this.qtde_pe = qtde_pe;
	}
	public String getObs_pe() {
		return obs_pe;
	}
	public void setObs_pe(String obs_pe) {
		this.obs_pe = obs_pe;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	@Override
	public String toString() {
		return "Produto [codproduto_pe=" + codproduto_pe + ", vrunit_pe="
				+ vrunit_pe + ", valor_pe=" + valor_pe + ", qtde_pe=" + qtde_pe
				+ ", descricao=" + descricao + "]";
	}
	
	
    
}
