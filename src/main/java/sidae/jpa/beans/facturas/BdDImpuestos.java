package sidae.jpa.beans.facturas;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class BdDImpuestos {
	
	private Integer idImpuesto;
	private String coImpuesto;
	private String dsImpuesto;
	private BigDecimal pjImpuesto;
	private String tstbd;
	private String usuarioBd;
	
	public BdDImpuestos(){
			
		}
	
	public BdDImpuestos(Integer idImpuesto, String coImpuesto, String dsImpuesto, BigDecimal pjImpuesto, String tstbd,
			String usuarioBd) {
		
		this.setIdImpuesto(idImpuesto);
		this.setCoImpuesto(coImpuesto);
		this.setDsImpuesto(dsImpuesto);
		this.setPjImpuesto(pjImpuesto);
		this.setTstbd(tstbd);
		this.setUsuarioBd(usuarioBd);
	}

	public BdDImpuestos(ResultSet rs) throws SQLException, ParseException {
		this.setIdImpuesto(rs.getInt("ID_TIPOIMPUESTO"));
		this.setCoImpuesto(rs.getString("CO_TIPOIMPUESTO"));
		this.setDsImpuesto(rs.getString("DS_TIPOIMPUESTO"));
		this.setPjImpuesto(rs.getBigDecimal("PJ_TIPOIMPUESTO"));
		this.setTstbd(rs.getString("TSTBD"));
		this.setUsuarioBd(rs.getString("USUARIOBD"));	
	}
	
	
	//Getters y Setters
	public Integer getIdImpuesto() {
		return idImpuesto;
	}

	public void setIdImpuesto(Integer idImpuesto) {
		this.idImpuesto = idImpuesto;
	}

	public String getCoImpuesto() {
		return coImpuesto;
	}

	public void setCoImpuesto(String coImpuesto) {
		this.coImpuesto = coImpuesto;
	}

	public String getDsImpuesto() {
		return dsImpuesto;
	}

	public void setDsImpuesto(String dsImpuesto) {
		this.dsImpuesto = dsImpuesto;
	}

	public BigDecimal getPjImpuesto() {
		return pjImpuesto;
	}

	public void setPjImpuesto(BigDecimal pjImpuesto) {
		this.pjImpuesto = pjImpuesto;
	}

	public String getTstbd() {
		return tstbd;
	}

	public void setTstbd(String tstbd) {
		this.tstbd = tstbd;
	}

	public String getUsuarioBd() {
		return usuarioBd;
	}

	public void setUsuarioBd(String usuarioBd) {
		this.usuarioBd = usuarioBd;
	}

	
}
