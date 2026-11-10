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

}
