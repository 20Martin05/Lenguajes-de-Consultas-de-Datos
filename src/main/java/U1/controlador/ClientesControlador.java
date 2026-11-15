package U1.controlador;

import U1.modelo.Cliente;
import U1.modelo.Factura;
import U1.vista.VentanaEmpresa;
import U1.util.ConexionJPA;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

@SuppressWarnings("unchecked")
public class ClientesControlador {
    private final VentanaEmpresa vista;

    public ClientesControlador(VentanaEmpresa vista) {
        this.vista = vista;
    }


    private void mostrarResultado(String texto, String... etiquetas) {
        vista.mostrarResultado(vista.tbl_JPQLClientes, texto, etiquetas);
    }

    private DefaultTableModel crearModelo() {
        return new DefaultTableModel(
                new String[]{"ID", "Nombre", "Apellido", "Cédula", "Teléfono", "Correo"}, 0);
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) return "";
        String[] palabras = texto.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : palabras) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
        }
        return sb.toString();
    }

    private void llenar(DefaultTableModel modelo, List<Cliente> lista) {
        for (Cliente c : lista) {
            modelo.addRow(new Object[]{
                    c.getId(), c.getNombre(), c.getApellido(), c.getCedula(), c.getTelefono(), c.getCorreo()
            });
        }
    }

    public void CLlistar() {
        DefaultTableModel modelo = crearModelo();
        vista.tbl_CLClientes.setRowSorter(null);
        vista.tbl_CLClientes.setAutoCreateColumnsFromModel(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            llenar(modelo, em.createQuery("FROM Cliente c ORDER BY c.id ASC", Cliente.class).getResultList());
        }
        vista.tbl_CLClientes.setModel(modelo);
    }

    public void cargarComboFacturas() {
        if (vista.cmb_PRFactura == null) return;
        vista.cmb_PRFactura.removeAllItems();
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            for (Factura f : em.createQuery("FROM Factura", Factura.class).getResultList()) {
                vista.cmb_PRFactura.addItem(f.getNumero());
            }
        }
    }

    public List<Cliente> CLbuscarPorNombre(String nombre) {
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            return em.createQuery(
                            "SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE LOWER(:nombre) ORDER BY c.id ASC",
                            Cliente.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .getResultList();
        }
    }

    public void CLbuscarYMostrar(String texto) {
        DefaultTableModel modelo = crearModelo();
        vista.tbl_CLClientes.setRowSorter(null);
        vista.tbl_CLClientes.setAutoCreateColumnsFromModel(true);
        llenar(modelo, CLbuscarPorNombre(texto));
        vista.tbl_CLClientes.setModel(modelo);
    }

    private int idSeleccionado() {
        int filaVista = vista.tbl_CLClientes.getSelectedRow();
        if (filaVista < 0) {
            JOptionPane.showMessageDialog(vista, "Selecciona un cliente de la tabla primero");
            return -1;
        }
        int fila = vista.tbl_CLClientes.convertRowIndexToModel(filaVista);
        return ((Number) vista.tbl_CLClientes.getValueAt(fila, 0)).intValue();
    }

    private boolean camposValidos(String nombre, String apellido, String cedula) {
        if (nombre.isBlank() || apellido.isBlank() || cedula.isBlank()) {
            JOptionPane.showMessageDialog(vista, "Nombre, apellido y cédula son obligatorios.");
            return false;
        }
        return true;
    }

    public void CLguardar() {
        String nombre = capitalizar(vista.txt_CLNombre.getText());
        String apellido = capitalizar(vista.txt_CLApellido.getText());
        String cedula = vista.txt_CLCedula.getText().trim();
        String telefono = vista.txt_CLTelefono.getText().trim();
        String correo = vista.txt_CLCorreo.getText().trim().toLowerCase();

        if (!camposValidos(nombre, apellido, cedula)) return;

        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            Cliente nuevo = new Cliente();
            nuevo.setNombre(nombre);
            nuevo.setApellido(apellido);
            nuevo.setCedula(cedula);
            nuevo.setTelefono(telefono);
            nuevo.setCorreo(correo);

            em.getTransaction().begin();
            em.persist(nuevo);
            em.getTransaction().commit();
            JOptionPane.showMessageDialog(vista, "Cliente guardado con éxito");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(vista, "No se pudo guardar el cliente: " + ex.getMessage());
        }
        CLlistar();
        cargarComboFacturas();
    }

    public void CLactualizar() {
        int id = idSeleccionado();
        if (id == -1) return;

        String nuevoNombre = capitalizar(vista.txt_CLNombre.getText());
        String nuevoApellido = capitalizar(vista.txt_CLApellido.getText());
        String nuevaCedula = vista.txt_CLCedula.getText().trim();
        String nuevoTelefono = vista.txt_CLTelefono.getText().trim();
        String nuevoCorreo = vista.txt_CLCorreo.getText().trim().toLowerCase();

        if (!camposValidos(nuevoNombre, nuevoApellido, nuevaCedula)) return;

        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            em.getTransaction().begin();
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente != null) {
                cliente.setNombre(nuevoNombre);
                cliente.setApellido(nuevoApellido);
                cliente.setCedula(nuevaCedula);
                cliente.setTelefono(nuevoTelefono);
                cliente.setCorreo(nuevoCorreo);
            }
            em.getTransaction().commit();
            JOptionPane.showMessageDialog(vista, "Cliente actualizado con éxito");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(vista, "No se pudo actualizar el cliente: " + ex.getMessage());
        }
        CLlistar();
    }

    public void CLeliminar() {
        int id = idSeleccionado();
        if (id == -1) return;

        int confirmacion = JOptionPane.showConfirmDialog(vista,
                "¿Estás seguro de eliminar este cliente?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            long facturas = em.createQuery(
                            "SELECT COUNT(f) FROM Factura f WHERE f.cliente.id = :id", Long.class)
                    .setParameter("id", id)
                    .getSingleResult();
            if (facturas > 0) {
                JOptionPane.showMessageDialog(vista,
                        "No se puede eliminar: el cliente tiene " + facturas + " factura(s) registradas.");
                return;
            }

            em.getTransaction().begin();
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente != null) em.remove(cliente);
            em.getTransaction().commit();
            JOptionPane.showMessageDialog(vista, "Cliente eliminado con éxito");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(vista, "No se pudo eliminar el cliente: " + ex.getMessage());
        }
        CLlistar();
        cargarComboFacturas();
    }

    public void CLlimpiar() {
        vista.txt_CLNombre.setText("");
        vista.txt_CLApellido.setText("");
        vista.txt_CLCedula.setText("");
        vista.txt_CLTelefono.setText("");
        vista.txt_CLCorreo.setText("");
    }

    public void ClientesTotal() {
        vista.tbl_JPQLClientes.setEnabled(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Cliente> clientes = em.createQuery("SELECT c FROM Cliente c", Cliente.class
            ).getResultList();
            StringBuilder resultado = new StringBuilder();
            for (Cliente c : clientes) {
                resultado.append(" - ID: ")
                        .append(c.getId())
                        .append(" - NOMBRE: ")
                        .append(c.getNombre())
                        .append(" - APELLIDO: ")
                        .append(c.getApellido())
                        .append(" - CEDULA: ")
                        .append(c.getCedula())
                        .append(" - TELÉFONO: ")
                        .append(c.getTelefono())
                        .append(" - CORREO: ")
                        .append(c.getCorreo())
                        .append("\n");

            }
            mostrarResultado(resultado.toString(), "ID", "NOMBRE", "APELLIDO", "CEDULA", "TELÉFONO", "CORREO");
        }
    }

    public void ClientesConFactura() {
        vista.tbl_JPQLClientes.setEnabled(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Object[]> clientes = em.createQuery(
                    "SELECT DISTINCT c.id, f.numero, c.nombre, c.apellido FROM Cliente c " +
                            "JOIN c.facturas f ORDER BY c.id ASC",
                    Object[].class
            ).getResultList();
            StringBuilder resultado = new StringBuilder();
            for (Object[] c : clientes) {
                resultado.append(" - ID: ")
                        .append(c[0])
                        .append(" - FACTURA: ")
                        .append(c[1])
                        .append(" - NOMBRE: ")
                        .append(c[2])
                        .append(" - APELLIDO: ")
                        .append(c[3])
                        .append("\n");
            }
            mostrarResultado(resultado.toString(),   "ID:", "FACTURA:", "NOMBRE:", "APELLIDO:");
        }
    }

    public void ClientesPorNombre() {
        vista.tbl_JPQLClientes.setEnabled(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Object[]> clientes = em.createQuery(
                    "SELECT c.id, c.nombre FROM Cliente c " +
                            "ORDER BY c.id ASC", Object[].class
            ).getResultList();
            StringBuilder resultado = new StringBuilder();
            for (Object[] c : clientes) {
                resultado.append(" - ID: ")
                        .append(c[0])
                        .append(" - NOMBRE: ")
                        .append(c[1])
                        .append("\n");
            }
            mostrarResultado(resultado.toString(),  "ID:", "NOMBRE:");
        }
    }

    public void ClientesPorApellido() {
        vista.tbl_JPQLClientes.setEnabled(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Object[]> clientes = em.createQuery(
                    "SELECT c.id, c.apellido FROM Cliente c " +
                            "ORDER BY c.id ASC", Object[].class
            ).getResultList();
            StringBuilder resultado = new StringBuilder();
            for (Object[] c : clientes) {
                resultado.append(" - ID: ")
                        .append(c[0])
                        .append(" - APELLIDO: ")
                        .append(c[1])
                        .append("\n");
            }
            mostrarResultado(resultado.toString(),  "ID:", "APELLIDO:");
        }
    }

}
