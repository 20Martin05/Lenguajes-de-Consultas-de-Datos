package U1.controlador;

import U1.modelo.Cliente;
import U1.modelo.Factura;
import U1.vista.VentanaEmpresa;
import U1.util.ConexionJPA;
import jakarta.persistence.EntityManager;

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

    private void llenar(DefaultTableModel modelo, List<Cliente> lista) {
        for (Cliente c : lista) {
            modelo.addRow(new Object[]{
                    c.getId(), c.getNombre(), c.getApellido(), c.getCedula(), c.getTelefono(), c.getCorreo()
            });
        }
    }

    public void CLlistar() {
        DefaultTableModel modelo = crearModelo();
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            llenar(modelo, em.createQuery("FROM Cliente", Cliente.class).getResultList());
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
                            "SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE LOWER(:nombre) ORDER BY c.nombre",
                            Cliente.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .getResultList();
        }
    }

    public void CLbuscarYMostrar(String texto) {
        DefaultTableModel modelo = crearModelo();
        llenar(modelo, CLbuscarPorNombre(texto));
        vista.tbl_CLClientes.setModel(modelo);
    }

    public void ClientesTotal() {
        vista.tbl_JPQLClientes.setEnabled(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Cliente> clientes = em.createQuery("SELECT c FROM Cliente c", Cliente.class
            ).getResultList();
            StringBuilder resultado = new StringBuilder();
            int contador = 1;
            for (Cliente c : clientes) {
                resultado.append("CLIENTE ")
                        .append(contador++)
                        .append(" - ID: ")
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
            mostrarResultado(resultado.toString(), "CLIENTE", "ID", "NOMBRE", "APELLIDO", "CEDULA", "TELÉFONO", "CORREO");
        }
    }

    public void ClientesConFactura() {
        vista.tbl_JPQLClientes.setEnabled(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Object[]> clientes = em.createQuery(
                    "SELECT DISTINCT c.id, f.id, c.nombre, c.apellido FROM Cliente c " +
                            "JOIN c.facturas f ORDER BY c.id ASC",
                    Object[].class
            ).getResultList();
            StringBuilder resultado = new StringBuilder();
            int contador = 1;
            for (Object[] c : clientes) {
                resultado.append("Cliente ")
                        .append(contador++)
                        .append(" - FACTURA ID: ").append(c[1])
                        .append(" - ID: ").append(c[0])
                        .append(" - NOMBRE: ").append(c[2])
                        .append(" - APELLIDO: ").append(c[3])
                        .append("\n");
            }
            mostrarResultado(resultado.toString(), "Cliente", "FACTURA ID", "ID:", "NOMBRE:", "APELLIDO:");
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
            int contador = 1;
            for (Object[] c : clientes) {
                resultado.append("Cliente ")
                        .append(contador++)
                        .append(" - ID: ")
                        .append(c[0])
                        .append(" - NOMBRE: ")
                        .append(c[1])
                        .append("\n");
            }
            mostrarResultado(resultado.toString(), "Cliente", "ID:", "NOMBRE:");
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
            int contador = 1;
            for (Object[] c : clientes) {
                resultado.append("Cliente ")
                        .append(contador++)
                        .append(" - ID: ")
                        .append(c[0])
                        .append(" - APELLIDO: ")
                        .append(c[1])
                        .append("\n");
            }
            mostrarResultado(resultado.toString(), "Cliente", "ID:", "APELLIDO:");
        }
    }

}
