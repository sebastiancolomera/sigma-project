package sigma.vista.gui;

import sigma.app.GestorSigma;
import javax.swing.*;
import java.awt.*;

public class MenuLiderFrame extends JFrame {
    private GestorSigma controlador;

    public MenuLiderFrame(GestorSigma controlador) {
        this.controlador = controlador;
        setTitle("SIGMA - Menú Líder");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 10, 10));

        JButton btnCrearMeta = new JButton("Crear Meta");
        btnCrearMeta.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog("Nombre del meta :");
            if (nombre != null) controlador.agregarMeta(nombre);
        });

        JButton btnAsignarTarea = new JButton("Asignar Tarea");
        btnAsignarTarea.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Asignar Tarea", true);
            dialog.add(new GestionTareasPanel(controlador));
            dialog.pack();
            dialog.setVisible(true);
        });

        JButton btnCerrar = new JButton("Cerrar Sesión");
        btnCerrar.addActionListener(e -> {
            this.dispose();
            new LoginFrame(controlador).setVisible(true);
        });

        add(btnCrearMeta);
        add(btnAsignarTarea);
        add(new JButton("Ver Tareas"));
        add(new JButton("Ver Usuarios"));
        add(btnCerrar);
    }
}