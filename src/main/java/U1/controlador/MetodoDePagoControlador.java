package U1.controlador;

import U1.modelo.Cliente;
import U1.modelo.Factura;
import U1.modelo.MetodoDePago;
import U1.util.ConexionJPA;
import U1.vista.VentanaEmpresa;
import jakarta.persistence.EntityManager;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class MetodoDePagoControlador {
    private final VentanaEmpresa vista;

    public MetodoDePagoControlador(VentanaEmpresa vista) {
        this.vista = vista;
    }

    private DefaultTableModel crearModelo() {
        return new DefaultTableModel(
                new String[]{"ID", "Pago", "Descripción", "Estado"}, 0);
    }

    private void llenar(DefaultTableModel modelo, List<MetodoDePago> metodos) {
        for (MetodoDePago metodo : metodos) {
            modelo.addRow(new Object[]{
                    metodo.getId(),
                    metodo.getPago(),
                    metodo.getDescripcion(),
                    metodo.isEstado()
            });
        }
    }

    public void MPlistar() {
        DefaultTableModel modelo = crearModelo();
        vista.tbl_MPMetodoDePago.setRowSorter(null);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            llenar(modelo, em.createQuery(
                    "SELECT m FROM MetodoDePago m ORDER BY m.pago", MetodoDePago.class
            ).getResultList());
        }
        vista.tbl_MPMetodoDePago.setModel(modelo);
    }

    public void cargarCombosMetodoDePago() {
        if (vista.cmb_MPCliente != null) {
            vista.cmb_MPCliente.removeAllItems();
        }
        if (vista.cmb_MPFactura != null) {
            vista.cmb_MPFactura.removeAllItems();
        }
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            if (vista.cmb_MPCliente != null) {
                for (Cliente c : em.createQuery(
                        "SELECT c FROM Cliente c ORDER BY c.nombre", Cliente.class
                ).getResultList()) {
                    vista.cmb_MPCliente.addItem(c.getNombre());
                }
            }
            if (vista.cmb_MPFactura != null) {
                for (Factura factura : em.createQuery(
                        "SELECT f FROM Factura f ORDER BY f.numero", Factura.class
                ).getResultList()) {
                    vista.cmb_MPFactura.addItem(factura.getNumero());
                }
            }
        }
    }


    public List<MetodoDePago> MPbuscarPorPago(String pago) {
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            return em.createQuery(
                    "SELECT m FROM MetodoDePago m " +
                            "WHERE LOWER(m.pago) LIKE LOWER(:pago) ORDER BY m.pago",
                    MetodoDePago.class
            ).setParameter("pago", "%" + pago + "%").getResultList();
        }
    }

    public void MPbuscarYMostrar(String texto) {
        DefaultTableModel modelo = crearModelo();
        vista.tbl_MPMetodoDePago.setRowSorter(null);
        llenar(modelo, MPbuscarPorPago(texto));
        vista.tbl_MPMetodoDePago.setModel(modelo);
    }


    @FunctionalInterface

}
