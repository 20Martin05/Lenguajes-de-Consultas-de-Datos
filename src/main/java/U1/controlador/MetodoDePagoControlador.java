package U1.controlador;

import U1.modelo.Cliente;
import U1.modelo.Factura;
import U1.modelo.MetodoDePago;
import U1.util.ConexionJPA;
import U1.vista.VentanaEmpresa;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import javax.swing.JOptionPane;
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

    public void MPguardar() {
        String pago = String.valueOf(vista.cmb_MPPago.getSelectedItem()).trim();
        String descripcion = vista.txt_MPDescripcion.getText().trim();
        boolean estado = estadoSeleccionado();
        if (!camposValidos(pago, estado)) return;

        ejecutarTransaccion("guardar", em -> {
            MetodoDePago metodo = new MetodoDePago();
            metodo.setPago(pago);
            metodo.setDescripcion(descripcion);
            metodo.setEstado(estado);
            em.persist(metodo);
        });
        MPlistar();
    }

    public void MPactualizar() {
        int id = idSeleccionado();
        if (id == -1) return;

        String pago = String.valueOf(vista.cmb_MPPago.getSelectedItem()).trim();
        String descripcion = vista.txt_MPDescripcion.getText().trim();
        boolean estado = estadoSeleccionado();
        if (!camposValidos(pago, estado)) return;

        ejecutarTransaccion("actualizar", em -> {
            MetodoDePago metodo = em.find(MetodoDePago.class, id);
            if (metodo == null) throw new IllegalStateException("El método de pago ya no existe.");
            metodo.setPago(pago);
            metodo.setDescripcion(descripcion);
            metodo.setEstado(estado);
        });
        MPlistar();
    }

    public void MPeliminar() {
        int id = idSeleccionado();
        if (id == -1) return;

        if (JOptionPane.showConfirmDialog(vista,
                "¿Estás seguro de eliminar este método de pago?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        ejecutarTransaccion("eliminar", em -> {
            long facturas = em.createQuery(
                    "SELECT COUNT(f) FROM Factura f WHERE f.metodoDePago.id = :id", Long.class
            ).setParameter("id", id).getSingleResult();
            if (facturas > 0) {
                throw new IllegalStateException(
                        "No se puede eliminar: está asociado a " + facturas + " factura(s).");
            }
            MetodoDePago metodo = em.find(MetodoDePago.class, id);
            if (metodo != null) em.remove(metodo);
        });
        MPlistar();
    }

    public void MPlimpiar() {
        vista.cmb_MPPago.setSelectedItem("");
        vista.txt_MPDescripcion.setText("");
        vista.cmb_MPEstado.setSelectedItem("ACTIVO");
    }

    public void MPcargar() {
        int filaVista = vista.tbl_MPMetodoDePago.getSelectedRow();
        if (filaVista < 0 || vista.tbl_MPMetodoDePago.getRowCount() == 0) return;
        int fila = vista.tbl_MPMetodoDePago.convertRowIndexToModel(filaVista);

        vista.cmb_MPPago.setSelectedItem(
                String.valueOf(vista.tbl_MPMetodoDePago.getValueAt(fila, 1)));
        vista.txt_MPDescripcion.setText(String.valueOf(vista.tbl_MPMetodoDePago.getValueAt(fila, 2)));
        boolean estado = Boolean.parseBoolean(
                String.valueOf(vista.tbl_MPMetodoDePago.getValueAt(fila, 3)));
        vista.cmb_MPEstado.setSelectedItem(estado ? "ACTIVO" : "INACTIVO");
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

    private boolean camposValidos(String pago, Boolean estado) {
        if (pago.isBlank()) {
            JOptionPane.showMessageDialog(vista, "El tipo de pago es obligatorio.");
            return false;
        }
        return estado != null;
    }

    private boolean estadoSeleccionado() {
        return "ACTIVO".equalsIgnoreCase(
                String.valueOf(vista.cmb_MPEstado.getSelectedItem()));
    }

    private int idSeleccionado() {
        int filaVista = vista.tbl_MPMetodoDePago.getSelectedRow();
        if (filaVista < 0) {
            JOptionPane.showMessageDialog(vista,
                    "Selecciona un método de pago de la tabla primero.");
            return -1;
        }
        int fila = vista.tbl_MPMetodoDePago.convertRowIndexToModel(filaVista);
        return ((Number) vista.tbl_MPMetodoDePago.getValueAt(fila, 0)).intValue();
    }

    private void ejecutarTransaccion(String operacion, OperacionPersistencia accion) {
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                accion.ejecutar(em);
                tx.commit();
                JOptionPane.showMessageDialog(vista,
                        "Método de pago " + operacion + " con éxito.");
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                JOptionPane.showMessageDialog(vista,
                        "No se pudo " + operacion + " el método de pago: " + ex.getMessage());
            }
        }
    }

    @FunctionalInterface
    private interface OperacionPersistencia {
        void ejecutar(EntityManager em);
    }
}
