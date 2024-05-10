package sidae.gestion.usuarios;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
import sidae.jpa.beans.usuarios.BdDUsuarios;
import sidae.logica.usuarios.Logicausuarios;
import sidae.serviciostecnicos.usuarios.StDUsuarios;

@Named("Gestionusuarios")
@SessionScoped
public class Gestionusuarios implements Serializable {
	private static final long serialVersionUID = 1510486172065607690L;

	private String coUsuarios;
	private String dsUsuarios;
	private String dsPassword;
	private String dsPasswordR;
	private String dsEmail;
	private Integer boAdmin;
	private String usuarioBd;
	private Boolean esBoolean;
	Logicausuarios logica = new Logicausuarios();
	BdDUsuarios objetoSeleccionado = new BdDUsuarios();
	BdDUsuarios nuevoUsuario = new BdDUsuarios();
	private List<BdDUsuarios> listaUsuarios;

	

	public String iniciarSesion() throws SQLException, ParseException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        if (logica.iniciarSesion(objetoSeleccionado.getCoUsuarios(), hashPassword(objetoSeleccionado.getDsPassword(), objetoSeleccionado.getCoUsuarios()))) {
            // Inicio de sesión exitoso
            externalContext.getSessionMap().put("usuario", objetoSeleccionado);
            objetoSeleccionado = logica.item(this.objetoSeleccionado.getCoUsuarios());
            return "filtroFacturas.xhtml"; // Página de inicio después de iniciar sesión
        } else {
            // Inicio de sesión fallido
        	PrimeFaces.current().ajax().update("mensajeError");
			PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
					"Inicio de sesión fallido. El nombre o la contraseña no coinciden"));
            return null; 
        }
    }

	public String cerrarSesion() {
	    // Limpiar las credenciales del usuario
	    objetoSeleccionado.setCoUsuarios(null);
	    objetoSeleccionado.setDsPassword(null); // Si es necesario limpiar la contraseña
	    
	    // Obtener el contexto de JSF y la sesión actual
	    FacesContext context = FacesContext.getCurrentInstance();
	    ExternalContext externalContext = context.getExternalContext();
	    HttpSession session = (HttpSession) externalContext.getSession(false);
	    
	    if (session != null) {
	        // Invalidar la sesión
	        session.invalidate();
	    }
	    
	    return "login.xhtml";
	}
    
    public String iniciarSesionAlRegistrarse(BdDUsuarios objetoSeleccionado) {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        if (logica.iniciarSesion(objetoSeleccionado.getCoUsuarios(), objetoSeleccionado.getDsPassword())) {
            // Inicio de sesión exitoso
            externalContext.getSessionMap().put("usuario", objetoSeleccionado);
            return "filtroFacturas.xhtml"; // Página de inicio después de iniciar sesión
        } else {
            // Inicio de sesión fallido
        	PrimeFaces.current().ajax().update("mensajeError");
			PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
					"Inicio de sesión fallido."));
            return null; // Permanecer en la misma página de inicio de sesión
        }
    }
	
    
    public String administrar() throws SQLException, ParseException {  	
    	limpiarNuevoUsuarioAdmin();
    	obtenerUsuariosRegistrados();
    	return "admin.xhtml";
    }
    
	public String grabar() throws SQLException, ParseException {		
		if (objetoSeleccionado != null) {
			if (objetoSeleccionado.getCoUsuarios().length() < 3 || objetoSeleccionado.getDsUsuarios().length() < 3) {
				PrimeFaces.current().ajax().update("mensajeError");
				PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
						"Los nombres deben contener almenos 3 caracteres."));
				return "";
			}
			if (logica.verificarExistenciaCodigo(objetoSeleccionado)) {
				PrimeFaces.current().ajax().update("mensajeError");
				PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
						"El nombre de usuario que has introducido ya existe, por favor elige otro nombre de usuario."));
				objetoSeleccionado.setCoUsuarios(null); 
		        return "";
		    }
			if(objetoSeleccionado.getDsPassword().length() < 5 || objetoSeleccionado.getDsPasswordR().length() < 5 ){
				PrimeFaces.current().ajax().update("mensajeError");
				PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
						"Las contraseñas deben tener más de 4 caracteres."));
				objetoSeleccionado.setDsPassword(null);
				objetoSeleccionado.setDsPasswordR(null);
				
				return "";
			}
			if(!objetoSeleccionado.getDsPassword().equals(objetoSeleccionado.getDsPasswordR())) {
				PrimeFaces.current().ajax().update("mensajeError");
				PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
						"Las contraseñas no coinciden."));
				objetoSeleccionado.setDsPassword(null);
				objetoSeleccionado.setDsPasswordR(null);
				return "";
			}
			if (!objetoSeleccionado.getDsEmail().contains("@") || !objetoSeleccionado.getDsEmail().contains(".")) {
				PrimeFaces.current().ajax().update("mensajeError");
				PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
						"Formato de correo invalido.."));
				return "";
			}
			
			if(objetoSeleccionado.getUsuarioBd() == null) {
				objetoSeleccionado.setUsuarioBd(objetoSeleccionado.getCoUsuarios());
			}
			
			// Generar hash de la contraseña antes de grabar
            String hashedPassword = hashPassword(objetoSeleccionado.getDsPassword(), objetoSeleccionado.getCoUsuarios());
            objetoSeleccionado.setDsPassword(hashedPassword);
			
			// Asignar valor entero a boAdmin antes de grabar
            if (esBoolean != null) {
            	if (esBoolean) {
    	            objetoSeleccionado.setBoAdmin(1);
    	        } else {
    	            objetoSeleccionado.setBoAdmin(0); 
    	        }
			}else {
				objetoSeleccionado.setBoAdmin(0);
			}
			
			
			
			logica.grabar(objetoSeleccionado);
			System.out.println("Grabamos el usuario: " + objetoSeleccionado.getDsUsuarios());
			
		
			// Iniciar sesión para el usuario registrado
	        return iniciarSesionAlRegistrarse(objetoSeleccionado);
		} else {
			PrimeFaces.current().ajax().update("mensajeError");
			PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
					"Rellena todos los campos."));
		}
		return "";
	}
	
	

	public String hashPassword(String password, String username) {
        String originalString = password + username;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // Manejar el error apropiadamente en tu aplicación
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String registro() {
    	limpiarNuevoUsuario();
		return "registro.xhtml";
	}

    
	public String perfil() throws SQLException, ParseException {
		objetoSeleccionado = logica.item(this.objetoSeleccionado.getCoUsuarios());
		
		return "perfil.xhtml";
	}
	
	public String editarPerfil() throws SQLException, ParseException{

		
		if (objetoSeleccionado.getDsUsuarios().length() < 3) {
			PrimeFaces.current().ajax().update("mensajeError");
			PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
					"El nombre debe contener almenos 3 caracteres."));
			return "";
		}
		
		if(!objetoSeleccionado.getDsPassword().isEmpty() && !objetoSeleccionado.getDsPasswordR().isEmpty()) {
			if(objetoSeleccionado.getDsPassword().length() < 5 || objetoSeleccionado.getDsPasswordR().length() < 5 ){
				PrimeFaces.current().ajax().update("mensajeError");
				PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
						"Las contraseñas deben tener más de 4 caracteres."));
				objetoSeleccionado.setDsPassword(null);
				objetoSeleccionado.setDsPasswordR(null);
				
				return "";
			}
		}
		
		if(!objetoSeleccionado.getDsPassword().equals(objetoSeleccionado.getDsPasswordR())) {
			PrimeFaces.current().ajax().update("mensajeError");
			PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
					"Las contraseñas no coinciden."));
			objetoSeleccionado.setDsPassword(null);
			objetoSeleccionado.setDsPasswordR(null);
			return "";
		}
		if (!objetoSeleccionado.getDsEmail().contains("@") || !objetoSeleccionado.getDsEmail().contains(".")) {
			PrimeFaces.current().ajax().update("mensajeError");
			PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
					"Formato de correo invalido.."));
			return "";
		}
		
		if (!objetoSeleccionado.getDsPassword().isEmpty() && !objetoSeleccionado.getDsPasswordR().isEmpty()) {
			String hashedPassword = hashPassword(objetoSeleccionado.getDsPassword(), objetoSeleccionado.getCoUsuarios());
	        objetoSeleccionado.setDsPassword(hashedPassword);
		}else {
			String paswordExistente = logica.item( objetoSeleccionado.getCoUsuarios()).getDsPassword();
			objetoSeleccionado.setDsPassword(paswordExistente);
		}
		
		 
        			
		logica.grabar(objetoSeleccionado);
		PrimeFaces.current().ajax().update("mensajeExito");
		PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Genial",
				"¡Has actualizado tú Perfil!"));
		
		return "perfil.xhtml";
	}

	public String grabarDesdeAdmin() throws SQLException, ParseException {	
		
		if (nuevoUsuario != null) {
			if (nuevoUsuario.getCoUsuarios().length() < 3 || nuevoUsuario.getDsUsuarios().length() < 3) {
				PrimeFaces.current().ajax().update("mensajeError");
				PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
						"Los nombres deben contener almenos 3 caracteres."));
				return "";
			}
			if (logica.verificarExistenciaCodigo(nuevoUsuario)) {
				PrimeFaces.current().ajax().update("mensajeError");
				PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
						"El nombre de usuario que has introducido ya existe, por favor elige otro nombre de usuario."));
				nuevoUsuario.setCoUsuarios(null); 
		        return "";
		    }
			if(nuevoUsuario.getDsPassword().length() < 5 || nuevoUsuario.getDsPasswordR().length() < 5 ){
				PrimeFaces.current().ajax().update("mensajeError");
				PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
						"Las contraseñas deben tener más de 4 caracteres."));
				nuevoUsuario.setDsPassword(null);
				nuevoUsuario.setDsPasswordR(null);
				
				return "";
			}
			if(!nuevoUsuario.getDsPassword().equals(nuevoUsuario.getDsPasswordR())) {
				PrimeFaces.current().ajax().update("mensajeError");
				PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
						"Las contraseñas no coinciden."));
				nuevoUsuario.setDsPassword(null);
				nuevoUsuario.setDsPasswordR(null);
				return "";
			}
			if (!nuevoUsuario.getDsEmail().contains("@") || !nuevoUsuario.getDsEmail().contains(".")) {
				PrimeFaces.current().ajax().update("mensajeError");
				PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
						"Formato de correo invalido.."));
				return "";
			}
			
			if(nuevoUsuario.getUsuarioBd() == null) {
				nuevoUsuario.setUsuarioBd(getCoUsuarios());
			}
			
			// Generar hash de la contraseña antes de grabar
            String hashedPassword = hashPassword(nuevoUsuario.getDsPassword(), nuevoUsuario.getCoUsuarios());
            nuevoUsuario.setDsPassword(hashedPassword);
			
			// Asignar valor entero a boAdmin antes de grabar
            if (esBoolean != null) {
            	if (esBoolean) {
            		nuevoUsuario.setBoAdmin(1);
    	        } else {
    	        	nuevoUsuario.setBoAdmin(0); 
    	        }
			}else {
				nuevoUsuario.setBoAdmin(0);
			}
			
			
			
			logica.grabar(nuevoUsuario);
			obtenerUsuariosRegistrados();
			System.out.println("Grabamos el usuario: " + nuevoUsuario.getDsUsuarios());
			limpiarNuevoUsuarioAdmin();
			PrimeFaces.current().ajax().update("mensajeExito");
			PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Genial",
					"¡Usuario Registrado con Exito!"));
	        return "";
		} else {
			PrimeFaces.current().ajax().update("mensajeError");
			PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
					"Rellena todos los campos."));
		}
		return "";
	}
	
	public String limpiarNuevoUsuarioAdmin() {
		this.nuevoUsuario.setCoUsuarios(null);
		this.nuevoUsuario.setDsUsuarios(null);
		this.nuevoUsuario.setDsPassword(null);
		this.nuevoUsuario.setDsPasswordR(null);
		this.nuevoUsuario.setDsEmail(null);
		this.nuevoUsuario.setUsuarioBd(null);
		this.setEsBoolean(null);
		

		System.out.println("Campos para dar de alta usuarios limpios");
		return "admin.xhtml";
	}
	
	public String limpiarNuevoUsuario() {
		this.objetoSeleccionado.setCoUsuarios(null);
		this.objetoSeleccionado.setDsUsuarios(null);
		this.objetoSeleccionado.setDsPassword(null);
		this.objetoSeleccionado.setDsPasswordR(null);
		this.objetoSeleccionado.setDsEmail(null);
		System.out.println("Campos para dar de alta usuarios limpios");
		return "registro.xhtml";
	}
	
	public List<BdDUsuarios> obtenerUsuariosRegistrados() throws SQLException, ParseException {		
		listaUsuarios = new StDUsuarios().buscarTodosUsuarios();
		return listaUsuarios;
	}
	
	private BdDUsuarios usuarioAEliminar;
	
	public static void mostrarPopupConfirmacion() {
        PrimeFaces.current().executeScript("PF('confirmDialog').show();");
    }

	public String quitarUsuario(BdDUsuarios usuario) throws SQLException, ParseException {
        usuarioAEliminar = usuario; // Almacena el usuario que se va a eliminar
        mostrarPopupConfirmacion();
        return "";
    }

	public String confirmarAccion() throws SQLException {
		if (usuarioAEliminar != null) {
			listaUsuarios.remove(usuarioAEliminar);
			logica.baja(usuarioAEliminar);
			PrimeFaces.current().ajax().update("mensajeExito");
			PrimeFaces.current().dialog().showMessageDynamic(
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Enhorabuena, usuario eliminado con éxito"));
		}

		PrimeFaces.current().executeScript("PF('confirmDialog').hide();");
		return "";
	}

	public String cancelarAccion() {
	    PrimeFaces.current().executeScript("PF('confirmDialog').hide();");
	    return "";
	}

	
	// Getters y Setters
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

	public String getDsPasswordR() {
		return dsPasswordR;
	}

	public void setDsPasswordR(String dsPasswordR) {
		this.dsPasswordR = dsPasswordR;
	}

	public String getDsEmail() {
		return dsEmail;
	}

	public void setDsEmail(String dsEmail) {
		this.dsEmail = dsEmail;
	}

	public String getUsuarioBd() {
		return usuarioBd;
	}

	public void setUsuarioBd(String usuarioBd) {
		this.usuarioBd = usuarioBd;
	}

	public Integer getBoAdmin() {
		return boAdmin;
	}

	public void setBoAdmin(Integer boAdmin) {
		this.boAdmin = boAdmin;
	}


	public Boolean getEsBoolean() {
		return esBoolean;
	}

	public void setEsBoolean(Boolean esBoolean) {
		this.esBoolean = esBoolean;
	}
	
	public Logicausuarios getLogica() {
		return logica;
	}

	public void setLogica(Logicausuarios logica) {
		this.logica = logica;
	}

	public BdDUsuarios getObjetoSeleccionado() {
		return objetoSeleccionado;
	}

	public void setObjetoSeleccionado(BdDUsuarios objetoSeleccionado) {
		this.objetoSeleccionado = objetoSeleccionado;
			
	}
	
	public BdDUsuarios getNuevoUsuario() {
		return nuevoUsuario;
	}

	public void setNuevoUsuario(BdDUsuarios nuevoUsuario) {
		this.nuevoUsuario = nuevoUsuario;
	}
	
	public List<BdDUsuarios> getListaUsuarios() {
		return listaUsuarios;
	}

	public void setListaUsuarios(List<BdDUsuarios> listaUsuarios) {
		this.listaUsuarios = listaUsuarios;
	}

	public BdDUsuarios getUsuarioAEliminar() {
		return usuarioAEliminar;
	}

	public void setUsuarioAEliminar(BdDUsuarios usuarioAEliminar) {
		this.usuarioAEliminar = usuarioAEliminar;
	}
}
