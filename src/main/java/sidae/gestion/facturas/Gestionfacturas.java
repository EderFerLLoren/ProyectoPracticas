package sidae.gestion.facturas;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;
import javax.inject.Named;

import sidae.jpa.beans.facturas.BdDFacturas;
import sidae.jpa.beans.facturas.BdDImpuestos;
import sidae.logica.facturas.Logicafacturas;
import sidae.serviciostecnicos.facturas.StDFacturas;
import sidae.serviciostecnicos.facturas.StDImpuestos;

@Named("Gestionfacturas")
@SessionScoped
public class Gestionfacturas implements Serializable {

	private static final long serialVersionUID = 1510486172065607690L;

	private String titulo = "Gestión facturas";

	private List<BdDFacturas> listaResultados;
	private String coFacturas;
	private String dsFacturas;
	private Date feFacturas;
	private BigDecimal imFacturas;
	private BigDecimal imFacturasHasta;
	private String usuarioBd;
	private String coImpuesto;
	private BigDecimal imBaseImponible;
	private Integer idImpuesto;
	private Integer idPagos;

	private StDFacturas stFactura = new StDFacturas();
	Logicafacturas logica = new Logicafacturas();
	BdDFacturas objetoSeleccionado = new BdDFacturas();

	public String buscar() throws SQLException, ParseException {
		BdDFacturas factura = new BdDFacturas();
		factura.setCoFacturas(coFacturas);
		factura.setDsFacturas(dsFacturas);
		factura.setFeFacturas(feFacturas);
		factura.setImFacturas(imFacturas);
		factura.setImFacturasHasta(imFacturasHasta);
		factura.setIdImpuesto(idImpuesto);
		factura.setImBaseImponible(imBaseImponible);

		listaResultados = logica.buscar(factura);
		if (listaResultados != null && !listaResultados.isEmpty()) {
			for (BdDFacturas item : listaResultados) {
				System.out.println("Codigo: " + item.getCoFacturas());
				System.out.println("Descripcion: " + item.getDsFacturas());
				System.out.println("Fecha: " + item.getFeFacturasStr());
				System.out.println("Importe: " + item.getImFacturas());
				System.out.println("Tipo Impuesto: " + item.getCoImpuesto());
				System.out.println("Importe + Base imponible: " + item.getImBaseImponible());
			}
			System.out.println("todo listado correctamente");
		} else {
			System.out.println("Todo vacio");
		}
		return null;
	}

	public String limpiar() {
		this.setListaResultados(null);
		this.setCoFacturas(null);
		this.setDsFacturas(null);
		this.setFeFacturas(null);
		this.setImFacturas(null);
		this.setImFacturasHasta(null);
		this.setUsuarioBd(null);
		this.setIdImpuesto(null);
		this.setImBaseImponible(null);
		System.out.println("Campos para filtrar limpios");
		return "filtroFacturas.xhtml";
	}

	public String darDeAlta() {
		limpiarAlta();
		return "altaFacturas.xhtml";
	}

	public String filtroPagos() {
		return "filtroPagos.xhtml";
	}

	public String grabar() throws SQLException, ParseException {
		if (objetoSeleccionado.getCoFacturas().isEmpty() || objetoSeleccionado.getDsFacturas().isEmpty()
				|| objetoSeleccionado.getFeFacturas() == null || objetoSeleccionado.getImFacturas() == null
				|| objetoSeleccionado.getUsuarioBd().isEmpty() || objetoSeleccionado.getIdImpuesto() == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error. ", "Es necesario rellenar todos los campos"));
			return "";
		}
		

		if (objetoSeleccionado != null && objetoSeleccionado.getIdFacturas() != null) {
			// Si hay un objeto seleccionado y su ID no es nulo, actualización.
			logica.grabar(objetoSeleccionado); // Llama al método grabar con el objeto seleccionado
			System.out.println("Se ha actualizado la factura con ID: " + objetoSeleccionado.getIdFacturas());
			limpiar();
		} else {
			if (stFactura.isExisteCodigo(objetoSeleccionado.getCoFacturas())) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error. ", "Ese codigo de factura ya existe"));
				return "";
			}else {
				// Si no hay objeto seleccionado o su ID es nulo, alta.
				logica.grabar(objetoSeleccionado);
				System.out.println("Se ha dado de alta a la factura con codigo: " + objetoSeleccionado.getCoFacturas());
				buscar();
			}
			
		}

		buscar();
		return "filtroFacturas.xhtml";
	}

	public String detalleFactura() throws SQLException, ParseException {
		BdDFacturas facturaItem = new BdDFacturas();
		Integer idFacturaItem = this.objetoSeleccionado.getIdFacturas();
		facturaItem = new StDFacturas().item(idFacturaItem);

		// Comprobamos que la facturaItem tiene algun valor
		if (facturaItem == null) {
			System.out.println("Esta factura no existe");
		} else {
			// Despues rellenamos los campos
			coFacturas = facturaItem.getCoFacturas();
			dsFacturas = facturaItem.getDsFacturas();
			feFacturas = facturaItem.getFeFacturas();
			imFacturas = facturaItem.getImFacturas();
			usuarioBd = facturaItem.getUsuarioBd();
			idImpuesto = facturaItem.getIdImpuesto();
			imBaseImponible = facturaItem.getImBaseImponible();

		}
		return "altaFacturas.xhtml";
	}

	public String eliminarFactura() throws SQLException, ParseException {
		try {
			if (objetoSeleccionado == null || algunCampoEstaVacio(objetoSeleccionado)) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Error",
						"No se puede eliminar la factura porque algún campo está vacío o no se ha seleccionado ninguna factura."));
				return "altaFacturas.xhtml";

			} else if (objetoSeleccionado.getIdPagos() != 0 && objetoSeleccionado.getIdPagos() != null) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Error", "No se pueden eliminar facturas que ya esten pagadas."));
				return "altaFacturas.xhtml";
			} else {
				logica.baja(objetoSeleccionado);
				System.out.println("Factura eliminada correctamente.");
				limpiar();
			}

		} catch (Exception e) {
			System.out.println("Error al eliminar la factura: " + e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
					"Error al eliminar la factura: " + e.getMessage()));
		}
		// Actualizar la lista de facturas después de la eliminación
		buscar();
		return "filtroFacturas.xhtml";
	}

	private boolean algunCampoEstaVacio(BdDFacturas factura) {
		return factura.getCoFacturas() == null || factura.getCoFacturas().isEmpty() || factura.getDsFacturas() == null
				|| factura.getDsFacturas().isEmpty() || factura.getFeFacturas() == null
				|| factura.getImFacturas() == null;
	}

	public String volver() throws SQLException, ParseException {
		limpiar();
		buscar();
		return "filtroFacturas.xhtml";
	}

	public String limpiarAlta() {
		this.objetoSeleccionado.setIdFacturas(null);
		this.objetoSeleccionado.setCoFacturas(null);
		this.objetoSeleccionado.setDsFacturas(null);
		this.objetoSeleccionado.setFeFacturas(null);
		this.objetoSeleccionado.setImFacturas(null);
		this.objetoSeleccionado.setUsuarioBd(null);
		this.objetoSeleccionado.setIdImpuesto(null);
		this.objetoSeleccionado.setImBaseImponible(null);
		System.out.println("Campos para dar de Alta limpios");
		return "altaFacturas.xhtml";
	}

	public List<BdDImpuestos> obtenerTiposImpuesto() {
		try {
			// Crear una instancia de StDImpuestos
			StDImpuestos serviciosImpuestos = new StDImpuestos();
			// Llamar al método obtenerTiposImpuesto
			return serviciosImpuestos.obtenerTiposImpuesto();
		} catch (SQLException e) {
			// Manejar la excepción en caso de error al obtener los tipos de impuestos
			e.printStackTrace();
			return null; // o lanzar una excepción propia de la aplicación
		}
	}

	public String obtenerTiposJs() {
		String tipos = "";
		for (BdDImpuestos item : obtenerTiposImpuesto()) {
			tipos += "hasMap['" + item.getIdImpuesto() + "'] = " + item.getPjImpuesto() + ";";
		}

		return tipos;
	}

	// getters y setters

	public String getTitulo() {
		return titulo;
	}

	public String getCoFacturas() {
		return coFacturas;
	}

	public void setCoFacturas(String coFacturas) {
		this.coFacturas = coFacturas;
	}

	public List<BdDFacturas> getListaResultados() {
		return listaResultados;
	}

	public void setListaResultados(List<BdDFacturas> listaResultados) {
		this.listaResultados = listaResultados;
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

	public String getFeFacturasStr() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		if (feFacturas == null) {
			return "";
		}

		return formatter.format(feFacturas);
	}

	public void setFeFacturasStr(String feFacturas) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		if (feFacturas == null || feFacturas.isEmpty()) {
			this.feFacturas = null;
		} else {
			this.feFacturas = formatter.parse(feFacturas);
		}
	}

	public BigDecimal getImFacturas() {
		return imFacturas;
	}

	public void setImFacturas(BigDecimal imFacturas) {
		this.imFacturas = imFacturas;
	}

	public BigDecimal getImFacturasHasta() {
		return imFacturasHasta;
	}

	public void setImFacturasHasta(BigDecimal imFacturasHasta) {
		this.imFacturasHasta = imFacturasHasta;
	}

	public String getUsuarioBd() {
		return usuarioBd;
	}

	public void setUsuarioBd(String usuarioBd) {
		this.usuarioBd = usuarioBd;
	}

	public BdDFacturas getObjetoSeleccionado() {
		return objetoSeleccionado;
	}

	public void setObjetoSeleccionado(BdDFacturas objetoSeleccionado) {
		this.objetoSeleccionado = objetoSeleccionado;
	}

	public String getCoImpuesto() {
		return coImpuesto;
	}

	public void setCoImpuesto(String coImpuesto) {
		this.coImpuesto = coImpuesto;
	}

	public BigDecimal getImBaseImponible() {
		return imBaseImponible;
	}

	public void setImBaseImponible(BigDecimal imBaseImponible) {
		this.imBaseImponible = imBaseImponible;
	}

	public Integer getIdImpuesto() {
		return idImpuesto;
	}

	public void setIdImpuesto(Integer idImpuesto) {
		this.idImpuesto = idImpuesto;
	}

	public Integer getIdPagos() {
		return idPagos;
	}

	public void setIdPagos(Integer idPagos) {
		this.idPagos = idPagos;
	}
}
