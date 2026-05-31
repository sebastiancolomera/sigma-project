package sigma.modelo;

public class Usuario {
    private String nombre;
    private String contrasena;
    private RolUsuario rol;

    public Usuario(String nombre, String contrasena, RolUsuario rol) {
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public RolUsuario getRol() {
        return rol;
    }
    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public boolean esSuperusuario() {
       return this.rol == RolUsuario.SUPERUSUARIO;
    }
    public boolean esLider() {
        return this.rol == RolUsuario.LIDER;
    }
    public boolean esUsuario() {
        return this.rol == RolUsuario.USUARIO;
    }
}