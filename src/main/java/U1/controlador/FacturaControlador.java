package U1.controlador;

import U1.vista.VentanaEmpresa;
import U1.util.ConexionJPA;
import U1.modelo.*;
import jakarta.persistence.EntityManager;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class FacturaControlador {
    private final VentanaEmpresa vista;

    public FacturaControlador(VentanaEmpresa vista) {
        this.vista = vista;
    }

    private DefaultTableModel crearModelo() {
        return new DefaultTableModel(
                new String[]{"ID", "Cliente ID", "Cliente", "Número", "Fecha", "Subtotal", "Impuesto", "Total"}, 0);
    }

    private void llenar(DefaultTableModel m, List<Factura> facturas) {
        for (Factura f : facturas) {
            Integer clienteId = f.getCliente() != null ? f.getCliente().getId() : null;
            String clienteNombre = f.getCliente() != null ? f.getCliente().getNombre() : "";
            m.addRow(new Object[]{
                    f.getId(), clienteId, clienteNombre,
                    f.getNumero(), f.getFecha(), f.getSubtotal(), f.getImpuesto(), f.getTotal()
            });
        }
    }

    public void Flistar() {
        vista.tbl_FFacturas.setRowSorter(null);
        DefaultTableModel m = crearModelo();
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            llenar(m, em.createQuery("FROM Factura", Factura.class).getResultList());
        }
        vista.tbl_FFacturas.setModel(m);
    }

    public void cargarCombosFactura() {
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            if (vista.cmb_FCliente != null) {
                vista.cmb_FCliente.removeAllItems();
                for (Cliente c : em.createQuery("FROM Cliente", Cliente.class
                ).getResultList()) {
                    vista.cmb_FCliente.addItem(c.getNombre());
                }
            }
            if (vista.cmb_FProducto != null) {
                vista.cmb_FProducto.removeAllItems();
                for (Productos p : em.createQuery("FROM Productos", Productos.class
                ).getResultList()) {
                    vista.cmb_FProducto.addItem(p.getNombre());
                }
            }
        }
    }


    public List<Factura> FbuscarPorCliente(String cliente) {
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            return em.createQuery(
                            "SELECT f FROM Factura f JOIN f.cliente c WHERE LOWER(c.nombre) LIKE LOWER(:cliente) ORDER BY f.id",
                            Factura.class)
                    .setParameter("cliente", "%" + cliente + "%")
                    .getResultList();
        }
    }

    public void FbuscarYMostrar(String texto) {
        vista.tbl_FFacturas.setRowSorter(null);
        DefaultTableModel modelo = crearModelo();
        llenar(modelo, FbuscarPorCliente(texto));
        vista.tbl_FFacturas.setModel(modelo);
    }

}