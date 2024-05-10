package sidae.jpa.beans.pagos;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BdDPagos {
	private Integer idPagos;
	private String coPagos;
	private String dsPagos;
	private Date fePagos;
	private BigDecimal imPagos;
	private BigDecimal imBaseImponibleFacturas;
	private BigDecimal imPagosHasta;
	private String dsTercero;
	private String dsCuenta;
	private String usuarioBd;
	private String tstbd;
	
	public BdDPagos() {
		
	}
	
	public BdDPagos(Integer idPagos, String coPagos, String dsPagos, Date fePagos, BigDecimal imPagos,
			String dsTercero, String dsCuenta, String usuarioBd, String tstbd) {
		this.setIdPagos(idPagos);
		this.setCoPagos(coPagos);
		this.setDsPagos(dsPagos);
		this.setFePagos(fePagos);
		this.setImPagos(imPagos);
		this.setDsTercero(dsTercero);
		this.setDsCuenta(dsCuenta);
		this.setUsuarioBd(usuarioBd);
		this.setTstbd(tstbd);
	}
	
	public BdDPagos(ResultSet rs) throws SQLException, ParseException {
		this.setIdPagos(rs.getInt("ID_PAGOS"));
		this.setCoPagos(rs.getString("CO_PAGOS"));
		this.setDsPagos(rs.getString("DS_PAGOS"));
		String fecha =  rs.getString("FE_PAGOS");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha2 = formatter.parse(fecha);
		this.setFePagos(fecha2);
		this.setImPagos(rs.getBigDecimal("IM_PAGOS"));
		this.setDsTercero(rs.getString("DS_TERCERO"));
		this.setDsCuenta(rs.getString("DS_CUENTA"));
		this.setUsuarioBd(rs.getString("USUARIOBD"));
		this.setTstbd(rs.getString("TSTBD"));
	}
	
	public Integer getIdPagos() {
		return idPagos;
	}
	public void setIdPagos(Integer idPagos) {
		this.idPagos = idPagos;
	}
	public String getCoPagos() {
		return coPagos;
	}
	public void setCoPagos(String coFacturas) {
		this.coPagos = coFacturas;
	}
	public String getDsPagos() {
		return dsPagos;
	}
	public void setDsPagos(String dsPagos) {
		this.dsPagos = dsPagos;
	}
	public Date getFePagos() {
		return fePagos;
	}
	public void setFePagos(Date fePagos) {
		this.fePagos = fePagos;
	}
	
	public String getFePagosStr() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		if(fePagos == null) {
			return "";
		}
		
		return formatter.format(fePagos);
	}

	public void setFePagosStr(String fePagos) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		if(fePagos == null || fePagos.isEmpty()) {
			this.fePagos = null;
		}else {
			this.fePagos = formatter.parse(fePagos);
		}
	}
	
	public BigDecimal getImPagos() {
		return imPagos;
	}
	public void setImPagos(BigDecimal imPagos) {
		this.imPagos = imPagos;
	}
	public String getDsTercero() {
		return dsTercero;
	}
	public void setDsTercero(String dsTercero) {
		this.dsTercero = dsTercero;
	}
	public String getDsCuenta() {
		return dsCuenta;
	}
	public void setDsCuenta(String dsCuenta) {
		this.dsCuenta = dsCuenta;
	}
	public String getUsuarioBd() {
		return usuarioBd;
	}
	public void setUsuarioBd(String usuarioBd) {
		this.usuarioBd = usuarioBd;
	}
	public String getTstbd() {
		return tstbd;
	}
	public void setTstbd(String tstbd) {
		this.tstbd = tstbd;
	}

	public BigDecimal getImPagosHasta() {
		return imPagosHasta;
	}

	public void setImPagosHasta(BigDecimal imPagosHasta) {
		this.imPagosHasta = imPagosHasta;
	}

	public BigDecimal getImBaseImponibleFacturas() {
		return imBaseImponibleFacturas;
	}

	public void setImBaseImponibleFacturas(BigDecimal imBaseImponibleFacturas) {
		this.imBaseImponibleFacturas = imBaseImponibleFacturas;
	}

	
	


}
