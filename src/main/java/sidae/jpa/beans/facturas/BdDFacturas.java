package sidae.jpa.beans.facturas;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BdDFacturas {
	private Integer idFacturas;
	private String coFacturas;
	private String dsFacturas;
	private Date feFacturas;
	private BigDecimal imFacturas;
	private BigDecimal imFacturasHasta;
	private String tstbd;
	private String usuarioBd;
	private Integer idImpuesto;
	private String coImpuesto;
	private BigDecimal imBaseImponible;
	private Integer idPagos;	
	private boolean seleccionada;
	
	public BdDFacturas() {
		
	}
	
	public BdDFacturas(Integer idFacturas, String coFacturas, String dsFacturas, Date feFacturas,
			BigDecimal imFacturas, BigDecimal imFacturasHasta, String tstdb, String usuarioDb,
			Integer idImpuesto, String coImpuesto, BigDecimal imBaseImponible, Integer idPagos) {
		this.setIdFacturas(idFacturas);
		this.setCoFacturas(coFacturas);
		this.setDsFacturas(dsFacturas);
		this.setFeFacturas(feFacturas);
		this.setImFacturas(imFacturas);
		this.setImFacturasHasta(imFacturasHasta);
		this.setTstbd(tstdb);
		this.setUsuarioBd(usuarioDb);
		this.setCoImpuesto(coImpuesto);
		this.setIdImpuesto(idImpuesto);
		this.setImBaseImponible(imBaseImponible);
		this.setIdPagos(idPagos);
	}
	

	public BdDFacturas(ResultSet rs) throws SQLException, ParseException {
		this.setIdFacturas(rs.getInt("ID_FACTURAS"));
		this.setCoFacturas(rs.getString("CO_FACTURAS"));
		this.setDsFacturas(rs.getString("DS_FACTURAS"));
		String fecha =  rs.getString("FE_FACTURAS");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha2 = formatter.parse(fecha);
		this.setFeFacturas(fecha2);
		this.setImFacturas(rs.getBigDecimal("IM_FACTURAS"));
		this.setTstbd(rs.getString("TSTBD"));
		this.setUsuarioBd(rs.getString("USUARIOBD"));
		this.setIdImpuesto(rs.getInt("ID_TIPOIMPUESTO"));
		this.setCoImpuesto(rs.getString("CO_TIPOIMPUESTO"));
		this.setImBaseImponible(rs.getBigDecimal("IM_BASEIMPONIBLE"));
		this.setIdPagos(rs.getInt("ID_PAGOS"));
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "BdDFacturas("+idFacturas+ "," + coFacturas + ")" ;
	}

	
	
	//GETTERS Y SETTERS
	public Integer getIdFacturas() {
		return idFacturas;
	}

	public void setIdFacturas(Integer idFacturas) {
		this.idFacturas = idFacturas;
	}
	
	public String getCoFacturas() {
		return coFacturas;
	}

	public void setCoFacturas(String coFacturas) {
		this.coFacturas = coFacturas;
	}

	public String getDsFacturas() {
		return dsFacturas;
	}

	public void setDsFacturas(String dsFacturas) {
		this.dsFacturas = dsFacturas;
	}

	public Date getFeFacturas() {
		return feFacturas;
	}

	public void setFeFacturas(Date feFacturas) {
		this.feFacturas = feFacturas;
	}

	public BigDecimal getImFacturas() {
		return imFacturas;
	}

	public void setImFacturas(BigDecimal imFacturas) {
		this.imFacturas = imFacturas;
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

	public String getFeFacturasStr() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		if(feFacturas == null) {
			return "";
		}
		
		return formatter.format(feFacturas);
	}

	public void setFeFacturasStr(String feFacturas) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		if(feFacturas == null || feFacturas.isEmpty()) {
			this.feFacturas = null;
		}else {
			this.feFacturas = formatter.parse(feFacturas);
		}
	}
	
	public BigDecimal getImFacturasHasta() {
		return imFacturasHasta;
	}

	public void setImFacturasHasta(BigDecimal imFacturasHasta) {
		this.imFacturasHasta = imFacturasHasta;
	}

	public Integer getIdImpuesto() {
		return idImpuesto;
	}

	public void setIdImpuesto(Integer idImpuesto) {
		this.idImpuesto = idImpuesto;
	}

	public BigDecimal getImBaseImponible() {
		return imBaseImponible;
	}

	public void setImBaseImponible(BigDecimal imBaseImponible) {
		this.imBaseImponible = imBaseImponible;
	}

	public String getCoImpuesto() {
		return coImpuesto;
	}

	public void setCoImpuesto(String coImpuesto) {
		this.coImpuesto = coImpuesto;
	}

	public Integer getIdPagos() {
		return idPagos;
	}

	public void setIdPagos(Integer idPagos) {
		this.idPagos = idPagos;
	}

	public boolean isSeleccionada() {
		return seleccionada;
	}

	public void setSeleccionada(boolean seleccionada) {
		this.seleccionada = seleccionada;
	}
	
	
}
