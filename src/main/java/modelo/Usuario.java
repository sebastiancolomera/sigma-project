package modelo;

import java.time.LocalDate;

public class Usuario {
    private String nombre;
    private String contrasena;
    private String rol;

    public Usuario() {
    }

    public Usuario(String nombre, String contrasena, String rol) {
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean esSuperusuario() {
        if (rol.equals("superusuario")) {
            return true;
        }
        return false;
    }

    public boolean esLider() {
        if (rol.equals("lider")) {
            return true;
        }
        return false;
    }

    public boolean esUsuario() {
        if (rol.equals("usuario")) {
            return true;
        }
        return false;
    }
}
