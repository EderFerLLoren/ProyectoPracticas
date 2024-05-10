package sidae.jpa.beans.usuarios;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class BdDUsuarios {
	private Integer idUsuarios;	
	private String coUsuarios;
	private String dsUsuarios;
	private String dsPassword;
	private String dsPasswordR;
	private String dsEmail;
	private Integer boAdmin;
	private String usuarioBd;
	private String tstbd;
	
	
	public BdDUsuarios() {
			
	}
	
	public BdDUsuarios(Integer idUsuarios, String coUsuarios, String dsUsuarios, String dsPassword, String dsEmail,
			Integer boAdmin, String usuarioBd, String tstbd) {
		
		this.idUsuarios = idUsuarios;
		this.coUsuarios = coUsuarios;
		this.dsUsuarios = dsUsuarios;
		this.dsPassword = dsPassword;
		this.dsEmail = dsEmail;
		this.boAdmin = boAdmin;
		this.usuarioBd = usuarioBd;
		this.tstbd = tstbd;
	}
	
	public BdDUsuarios(ResultSet rs) throws SQLException, ParseException {
		this.setIdUsuarios(rs.getInt("ID_USUARIOS"));
		this.setCoUsuarios(rs.getString("CO_USUARIOS"));
		this.setDsUsuarios(rs.getString("DS_USUARIOS"));
		this.setDsPassword(rs.getString("DS_PASSWORD"));
		this.setDsEmail(rs.getString("DS_EMAIL"));
		this.setBoAdmin(rs.getInt("BO_ADMIN"));
		this.setUsuarioBd(rs.getString("USUARIOBD"));
		this.setTstbd(rs.getString("TSTBD"));
	}
	
	
	
	//Getters y Setters
	public Integer getIdUsuarios() {
		return idUsuarios;
	}

	public void setIdUsuarios(Integer idUsuarios) {
		this.idUsuarios = idUsuarios;
	}

	public String getCoUsuarios() {
		return coUsuarios;
	}

	public void setCoUsuarios(String coUsuarios) {
		this.coUsuarios = coUsuarios;
	}

	public String getDsUsuarios() {
		return dsUsuarios;
	}

	public void setDsUsuarios(String dsUsuarios) {
		this.dsUsuarios = dsUsuarios;
	}

	public String getDsPassword() {
		return dsPassword;
	}

	public void setDsPassword(String dsPassword) {
		this.dsPassword = dsPassword;
	}

	public String getDsEmail() {
		return dsEmail;
	}

	public void setDsEmail(String dsEmail) {
		this.dsEmail = dsEmail;
	}

	public Integer getBoAdmin() {
		return boAdmin;
	}

	public void setBoAdmin(Integer boAdmin) {
		this.boAdmin = boAdmin;
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

	public String getDsPasswordR() {
		return dsPasswordR;
	}

	public void setDsPasswordR(String dsPasswordR) {
		this.dsPasswordR = dsPasswordR;
	}

}
