package com.astarlabs.spring.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers.DoubleSerializer;

public class Saldo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3929166658313094450L;
	private Double valor;

	@JsonSerialize(using=DoubleSerializer.class)
	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}
	
	

}
