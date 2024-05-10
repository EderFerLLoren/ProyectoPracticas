package sidae.gestion.pagos;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sidae.jpa.beans.facturas.BdDFacturas;
import sidae.jpa.beans.pagos.BdDPagos;
import sidae.logica.facturas.Logicafacturas;
import sidae.logica.pagos.Logicapagos;
import sidae.serviciostecnicos.facturas.StDFacturas;
import sidae.serviciostecnicos.pagos.StDPagos;

@Named("Gestionpagos")
@SessionScoped
public class Gestionpagos implements Serializable{
	
	private static final long serialVersionUID = 1510486172065607690L;
	private String titulo = "Gestión pagos";
	//Pagos
	private List<BdDPagos> listaResultadosP;
	private List<BdDFacturas> listaFacturas;
	private List<BdDFacturas> listaFacturasSeleccionadas;
	private List<BdDFacturas> facturasEliminadas = new ArrayList<>();
	private String coPagos;
	private String dsPagos;
	private Date fePagos;
	private String dsTercero;
	private BigDecimal imPagos;
	private BigDecimal imPagosHasta;
	private String dsCuenta;
	private String usuarioBd;
	
	Logicapagos logica = new Logicapagos();
	BdDPagos objetoSeleccionado = new BdDPagos();
	
	
	public String buscar() throws SQLException, ParseException {
		BdDPagos pago = new BdDPagos();
		pago.setCoPagos(coPagos);
		pago.setDsPagos(dsPagos);
		pago.setDsTercero(dsTercero);
		pago.setImPagos(imPagos);
		pago.setImPagosHasta(imPagosHasta);

		listaResultadosP = logica.buscar(pago);
		if (listaResultadosP != null && !listaResultadosP.isEmpty()) {
			for (BdDPagos item : listaResultadosP) {
				System.out.println("Codigo: " + item.getCoPagos());
				System.out.println("Descripcion: " + item.getDsPagos());
				System.out.println("Fecha: " + item.getDsTercero());
				System.out.println("Importe: " + item.getImPagos());
				}
			System.out.println("todo listado correctamente");
		} else {
			System.out.println("Todo vacio");
			return "filtroPagos.xhtml";
		}
		return null;
	}
	
	
	public String detallePago() throws SQLException, ParseException {
		BdDPagos pagoItem = new BdDPagos();
		Integer idPagoItem = this.objetoSeleccionado.getIdPagos();
		pagoItem = new StDPagos().item(idPagoItem);
		listaFacturasSeleccionadas = new StDFacturas().itemFacturaPago(idPagoItem);
		// Comprobamos que la facturaItem tiene algun valor
		if (pagoItem == null) {
			System.out.println("Este pago no existe");
		} else {
			// Despues rellenamos los campos
			coPagos = pagoItem.getCoPagos();
			dsPagos = pagoItem.getDsPagos();
			fePagos = pagoItem.getFePagos();
			dsTercero = pagoItem.getDsTercero();
			dsCuenta = pagoItem.getDsCuenta();
			
			BigDecimal totalImporte = BigDecimal.ZERO;
	        for (BdDFacturas factura : listaFacturasSeleccionadas) {
	            totalImporte = totalImporte.add(factura.getImBaseImponible());
	        }
			objetoSeleccionado.setImPagos(totalImporte); 
			usuarioBd = pagoItem.getUsuarioBd();
		}
		return "altaPagos.xhtml";
	}
	

	public String grabar() throws SQLException, ParseException {
		
		if (objetoSeleccionado.getCoPagos().isEmpty() || objetoSeleccionado.getDsPagos().isEmpty()
				|| objetoSeleccionado.getFePagos() == null || objetoSeleccionado.getDsTercero().isEmpty()
				|| objetoSeleccionado.getDsTercero().isEmpty() || objetoSeleccionado.getImPagos() == null
				|| objetoSeleccionado.getUsuarioBd().isEmpty()) {
			PrimeFaces.current().ajax().update("mensajeError");
	        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                "Error", "Es necesario rellenar todos los campos"));
			return "";
		}
		if (!logica.validarCuentaIBAN(objetoSeleccionado.getDsCuenta())) {
			PrimeFaces.current().ajax().update("mensajeError");
	        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                "Error", "Los datos de la cuenta son invalidos."));
	        System.out.println("Cuenta invalida");
	        objetoSeleccionado.setDsCuenta(null);
	        return "";
	    }
		
		if (logica.verificarExistenciaCodigo(objetoSeleccionado)) {
			PrimeFaces.current().ajax().update("mensajeError");
	        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                "Error", "El código del pago ya existe en la base de datos."));
	        objetoSeleccionado.setCoPagos(null);
	        return "";
	    }
				
		if (objetoSeleccionado != null && objetoSeleccionado.getIdPagos() != null) {
			// Si hay un objeto seleccionado y su ID no es nulo, actualización.
			grabarDetallePago(objetoSeleccionado);
			// Asignar el idPagos del objeto seleccionado a las facturas asociadas
			actualizarFacturas(objetoSeleccionado, listaFacturasSeleccionadas, facturasEliminadas);		    		           
		} else {
			// Si no hay objeto seleccionado o su ID es nulo, alta.
			logica.grabar(objetoSeleccionado);
			
			List<BdDPagos> todosLosPagos = logica.buscar(objetoSeleccionado);
			// Obtener el último ID de pagos
			Integer idPagoNuevo = null;
			if (!todosLosPagos.isEmpty()) {
			    BdDPagos ultimoPago = todosLosPagos.get(todosLosPagos.size() - 1); // Último pago en la lista
			    idPagoNuevo = ultimoPago.getIdPagos();
			}
			System.out.println("Se ha dado de alta el pago con id: " + idPagoNuevo);
			for (BdDFacturas facturasPago : listaFacturasSeleccionadas) {
			    // Verificar si la factura no tiene un ID de pagos asignado previamente
			    if (facturasPago.getIdPagos() == null || facturasPago.getIdPagos() == 0) {
			        // Asignar el ID del nuevo pago a la factura
			        facturasPago.setIdPagos(idPagoNuevo);
			        // Grabar la factura actualizada
			        new Logicafacturas().grabar(facturasPago);
			    }
			}			
		}
		limpiar();
		buscar();		
		return "filtroPagos.xhtml";
	}
	
	private void grabarDetallePago(BdDPagos pago) throws SQLException, ParseException {		
		// Grabo el detalle del pago
		logica.grabar(objetoSeleccionado); // Llama al método grabar con el objeto seleccionado
		System.out.println("Se ha actualizado el pago con el ID: " + objetoSeleccionado.getIdPagos());

	}
	
	private void actualizarFacturas(BdDPagos pago, List<BdDFacturas> facturasSeleccionadas, List<BdDFacturas> facturasEliminadas) throws SQLException, ParseException {
		Integer idPagos = objetoSeleccionado.getIdPagos();
	    //Si listaseleccionadas no esta vacía añade idPago a facturas
	    if (!listaFacturasSeleccionadas.isEmpty()) {
	    	for(BdDFacturas facturasPago : listaFacturasSeleccionadas) {
		        if(facturasPago.getIdPagos() == null || facturasPago.getIdPagos() == 0) {
		            // Asignar el idPagos solo si la factura no tiene un idPagos asignado previamente
		            facturasPago.setIdPagos(idPagos);
		            // Grabar la factura actualizada
		            new Logicafacturas().grabar(facturasPago);
		        }    
		    }
		}else {
		    // Si listaFacturasSeleccionadas está vacía, asignar idPagos = 0 a las facturas en facturasEliminadas y grabarlas
		    for (BdDFacturas facturaEliminada : facturasEliminadas) {
		        facturaEliminada.setIdPagos(0);
		        new Logicafacturas().grabar(facturaEliminada);
		    }
		}
	}
	
	
	public String limpiar() throws SQLException, ParseException {
		this.setListaResultadosP(null);
		this.setCoPagos(null);
		this.setDsPagos(null);
		this.setDsTercero(null);
		this.setImPagos(null);
		this.setImPagosHasta(null);
		System.out.println("Campos para filtrar limpios");
		buscar();
		return "filtroPagos.xhtml";
	}
	
	public String limpiarAlta() {
		this.objetoSeleccionado.setIdPagos(null);
		this.objetoSeleccionado.setCoPagos(null);
		this.objetoSeleccionado.setDsPagos(null);
		this.objetoSeleccionado.setFePagos(null);
		this.objetoSeleccionado.setDsTercero(null);
		this.objetoSeleccionado.setDsCuenta(null);
		this.objetoSeleccionado.setImPagos(null);
		this.objetoSeleccionado.setUsuarioBd(null);
		//this.listaFacturasSeleccionadas.clear();
		System.out.println("Campos para dar de Alta limpios");
		return "altaPagos.xhtml";
	}
	
	public String volver() throws SQLException, ParseException {
		limpiar();
		limpiarFacturasSeleccionadas();
		buscar();
		return "filtroPagos.xhtml";
	}
	
	public String darDeAlta() throws SQLException, ParseException {
		limpiarAlta();	
		return "altaPagos.xhtml";
	}
	
	// Marcar las facturas en listaFacturas como seleccionadas para que aparezcan marcadas en la vista
	public String obtenerFacturasPopup() throws SQLException, ParseException {		
		listaFacturas = new StDFacturas().listarFacturasSinId();
		if (listaFacturasSeleccionadas != null && !listaFacturasSeleccionadas.isEmpty()) {
			for ( BdDFacturas itemFacturaSeleccionada : listaFacturasSeleccionadas) {
				for (BdDFacturas itemFacturasPopup : listaFacturas) {
					if (itemFacturaSeleccionada.getIdFacturas() == itemFacturasPopup.getIdFacturas() && !itemFacturasPopup.isSeleccionada()) {
						itemFacturasPopup.setSeleccionada(true);
					}
				}
			}
		}

		return null;
	}
	
	
	public void agregarFacturasSeleccionadas() {
		BigDecimal totalImporte = BigDecimal.ZERO;

		// Verificar si listaFacturasSeleccionadas es null y, si es así, inicializarla
		if (listaFacturasSeleccionadas == null) {
			listaFacturasSeleccionadas = new ArrayList<>();
		}

		// Calcular el total del importe de las facturas que ya estaban seleccionadas
		if (listaFacturasSeleccionadas != null) {
			for (BdDFacturas factura : listaFacturasSeleccionadas) {
				totalImporte = totalImporte.add(factura.getImBaseImponible());
			}
		}

		// Agregar las nuevas facturas seleccionadas y sumar su importe al totalImporte
		for (BdDFacturas factura : listaFacturas) {
			// Verificar si la factura ya está en listaFacturasSeleccionadas
			boolean facturaYaAgregada = false;
			for (BdDFacturas facturaAgregada : listaFacturasSeleccionadas) {
				if (facturaAgregada.getIdFacturas() == factura.getIdFacturas()) {
					facturaYaAgregada = true;
					break;
				}
			}
			// Si la factura no está en la lista y no tiene idPagos, agregarla
			if (!facturaYaAgregada && factura.getIdPagos() != null && factura.isSeleccionada()) {
				listaFacturasSeleccionadas.add(factura);
				totalImporte = totalImporte.add(factura.getImBaseImponible());
			}
		}

		System.out.println(totalImporte);
		objetoSeleccionado.setImPagos(totalImporte);
	}
	
	
	
     public void quitarFactura(BdDFacturas factura) throws SQLException, ParseException {
    	// Restar el importe de la factura que se va a quitar
    	    objetoSeleccionado.setImPagos(objetoSeleccionado.getImPagos().subtract(factura.getImBaseImponible()));    
    	    // Asignar el idPagos de la factura a null
    	    factura.setIdPagos(0);  

    	    // Verificar si la factura está en listaFacturasSeleccionadas antes de eliminarla
    	    if (listaFacturasSeleccionadas.contains(factura)) {
    	        // Eliminar la factura de la lista
    	        listaFacturasSeleccionadas.remove(factura);
    	        // Agregar la factura eliminada a la lista de facturas eliminadas solo si no estaba ya presente
    	        if (!facturasEliminadas.contains(factura)) {
    	            facturasEliminadas.add(factura);
    	            new Logicafacturas().grabar(factura);
    	        }
    	    } 
    	System.out.println("la factura tiene un idPagos de " +factura.getIdPagos());
     }
     
     public void limpiarFacturasSeleccionadas() {
    	    listaFacturasSeleccionadas = null;
    	}
     
     public void exportarTablaPagos() {
    	 if(this.listaResultadosP == null) {
    		PrimeFaces.current().ajax().update("mensajeError");
 	        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR,
 	                "Error", "No se ha ha filtrado ningun pago. Debes filtrar los pagos antes de exportarlos."));
    	 }else {
    		 try {
    			 PrimeFaces.current().ajax().update("mensajeExito");
    			 PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO,
    			     "Éxito", "Los pagos se han exportado correctamente."));
    			 
                 FileWriter archivo = new FileWriter("pagos.txt");             
                 archivo.write("------------------------------------------------------------------------------------------------\n");
                 String encabezado = String.format("| %-10s | %-30s | %-10s | %-20s | %-10s |%n", "Código", "Descripción", "Fecha", "Tercero", "Importe");
                 archivo.write(encabezado);
                 archivo.write("------------------------------------------------------------------------------------------------\n");
                 for (BdDPagos pago : listaResultadosP) {
                     String fila = String.format("| %-10s | %-30s | %-10s | %-20s | %-10s |%n", pago.getCoPagos(), pago.getDsPagos(), pago.getFePagosStr(), pago.getDsTercero(), String.valueOf(pago.getImPagos() +" €"));
                     archivo.write(fila);
                     archivo.write("------------------------------------------------------------------------------------------------\n");        
                     }

                 archivo.close();
             } catch (IOException e) {
                 // Manejar la excepción si ocurre algún error de E/S
                 e.printStackTrace();
             }
		}
         
     }
     
	//Getters y Setters
	
	public List<BdDPagos> getListaResultadosP() {
		return listaResultadosP;
	}

	public void setListaResultadosP(List<BdDPagos> listaResultadosP) {
		this.listaResultadosP = listaResultadosP;
	}

	public String getCoPagos() {
		return coPagos;
	}

	public void setCoPagos(String coPagos) {
		this.coPagos = coPagos;
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

	public String getDsTercero() {
		return dsTercero;
	}

	public void setDsTercero(String dsTercero) {
		this.dsTercero = dsTercero;
	}

	public BigDecimal getImPagos() {
		return imPagos;
	}

	public void setImPagos(BigDecimal imPagos) {
		this.imPagos = imPagos;
	}

	public BigDecimal getImPagosHasta() {
		return imPagosHasta;
	}

	public void setImPagosHasta(BigDecimal imPagosHasta) {
		this.imPagosHasta = imPagosHasta;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
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

	public BdDPagos getObjetoSeleccionado() {
		return objetoSeleccionado;
	}

	public void setObjetoSeleccionado(BdDPagos objetoSeleccionado) {
		this.objetoSeleccionado = objetoSeleccionado;
	}


	public List<BdDFacturas> getListaFacturas() {
		return listaFacturas;
	}


	public void setListaFacturas(List<BdDFacturas> listaFacturas) {
		this.listaFacturas = listaFacturas;
	}


	public List<BdDFacturas> getListaFacturasSeleccionadas() {
		return listaFacturasSeleccionadas;
	}


	public void setListaFacturasSeleccionadas(List<BdDFacturas> listaFacturasSeleccionadas) {
		this.listaFacturasSeleccionadas = listaFacturasSeleccionadas;
	}
	
	
	
}
