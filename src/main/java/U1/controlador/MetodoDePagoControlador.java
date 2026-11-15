package U1.controlador;

import U1.modelo.Cliente;
import U1.modelo.Factura;
import U1.modelo.MetodoDePago;
import U1.util.ConexionJPA;
import U1.vista.VentanaEmpresa;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class MetodoDePagoControlador {
    private final VentanaEmpresa vista;

    public MetodoDePagoControlador(VentanaEmpresa vista) {
        this.vista = vista;
    }

    private void mostrarResultado(String texto, String... etiquetas) {
        vista.mostrarResultado(vista.tbl_JPQLMetodoDePago, texto, etiquetas);
    }

    private DefaultTableModel crearModelo() {
        return new DefaultTableModel(
                new String[]{"ID", "Factura ID", "Cliente ID", "Pago", "Descripción", "Estado"}, 0);
    }

    private void llenar(DefaultTableModel modelo, List<MetodoDePago> metodos) {
        for (MetodoDePago metodo : metodos) {
            if (metodo.getFacturas().isEmpty()) {
                agregarFila(modelo, metodo, null);
            } else {
                for (Factura factura : metodo.getFacturas()) {
                    agregarFila(modelo, metodo, factura);
                }
            }
        }
    }

    private void agregarFila(DefaultTableModel modelo, MetodoDePago metodo, Factura factura) {
        modelo.addRow(new Object[]{
                metodo.getId(),
                factura == null ? null : factura.getId(),
                factura == null || factura.getCliente() == null ? null : factura.getCliente().getId(),
                metodo.getPago(),
                metodo.getDescripcion(),
                metodo.isEstado()
        });
    }

    public void MPlistar() {
        DefaultTableModel modelo = crearModelo();
        vista.tbl_MPMetodoDePago.setRowSorter(null);
        vista.tbl_MPMetodoDePago.setAutoCreateColumnsFromModel(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            llenar(modelo, em.createQuery(
                    "SELECT m FROM MetodoDePago m ORDER BY m.id ASC", MetodoDePago.class
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
                            "WHERE LOWER(m.pago) LIKE LOWER(:pago) ORDER BY m.id ASC",
                    MetodoDePago.class
            ).setParameter("pago", "%" + pago + "%").getResultList();
        }
    }

    public void MPbuscarYMostrar(String texto) {
        DefaultTableModel modelo = crearModelo();
        vista.tbl_MPMetodoDePago.setRowSorter(null);
        vista.tbl_MPMetodoDePago.setAutoCreateColumnsFromModel(true);
        llenar(modelo, MPbuscarPorPago(texto));
        vista.tbl_MPMetodoDePago.setModel(modelo);
    }

    public void MPguardar() {
        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            MetodoDePago mp = new MetodoDePago();

            Object pagoObj = vista.cmb_MPPago.getSelectedItem();
            mp.setPago(pagoObj != null ? pagoObj.toString() : "");
            mp.setDescripcion(vista.txt_MPDescripcion.getText().trim());

            Object estadoObj = vista.cmb_MPEstado.getSelectedItem();
            boolean estado = estadoObj != null && estadoObj.toString().equalsIgnoreCase("ACTIVO");
            mp.setEstado(estado);

            em.persist(mp);
            tx.commit();
            JOptionPane.showMessageDialog(vista, "Método de pago guardado con éxito");
            MPlistar();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "No se pudo guardar el método de pago: " + e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void MPactualizar() {
        int filaVista = vista.tbl_MPMetodoDePago.getSelectedRow();
        if (filaVista < 0) {
            JOptionPane.showMessageDialog(vista, "Seleccione un método de pago de la tabla para actualizar.");
            return;
        }
        int filaModelo = vista.tbl_MPMetodoDePago.convertRowIndexToModel(filaVista);
        Object idObj = vista.tbl_MPMetodoDePago.getModel().getValueAt(filaModelo, 0);
        if (idObj == null) return;

        int id = Integer.parseInt(idObj.toString());

        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            MetodoDePago mp = em.find(MetodoDePago.class, id);
            if (mp != null) {
                Object pagoObj = vista.cmb_MPPago.getSelectedItem();
                mp.setPago(pagoObj != null ? pagoObj.toString() : "");
                mp.setDescripcion(vista.txt_MPDescripcion.getText().trim());

                Object estadoObj = vista.cmb_MPEstado.getSelectedItem();
                boolean estado = estadoObj != null && estadoObj.toString().equalsIgnoreCase("ACTIVO");
                mp.setEstado(estado);

                em.merge(mp);
                tx.commit();
                JOptionPane.showMessageDialog(vista, "Método de pago actualizado con éxito");
                MPlistar();
            }
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "No se pudo actualizar el método de pago: " + e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void MPeliminar() {
        int filaVista = vista.tbl_MPMetodoDePago.getSelectedRow();
        if (filaVista < 0) {
            JOptionPane.showMessageDialog(vista, "Seleccione un método de pago de la tabla para eliminar.");
            return;
        }
        int filaModelo = vista.tbl_MPMetodoDePago.convertRowIndexToModel(filaVista);
        Object idObj = vista.tbl_MPMetodoDePago.getModel().getValueAt(filaModelo, 0);
        if (idObj == null) return;

        int id = Integer.parseInt(idObj.toString());

        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            MetodoDePago mp = em.find(MetodoDePago.class, id);
            if (mp != null) {
                em.remove(mp);
                tx.commit();
                JOptionPane.showMessageDialog(vista, "Método de pago eliminado con éxito");
                MPlistar();
                MPlimpiar();
            }
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "No se pudo eliminar el método de pago: " + e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void MPlimpiar() {
        if (vista.cmb_MPFactura != null && vista.cmb_MPFactura.getItemCount() > 0) {
            vista.cmb_MPFactura.setSelectedIndex(0);
        }
        if (vista.cmb_MPCliente != null && vista.cmb_MPCliente.getItemCount() > 0) {
            vista.cmb_MPCliente.setSelectedIndex(0);
        }
        if (vista.cmb_MPPago != null && vista.cmb_MPPago.getItemCount() > 0) {
            vista.cmb_MPPago.setSelectedIndex(0);
        }
        if (vista.cmb_MPEstado != null && vista.cmb_MPEstado.getItemCount() > 0) {
            vista.cmb_MPEstado.setSelectedIndex(0);
        }
        if (vista.txt_MPDescripcion != null) {
            vista.txt_MPDescripcion.setText("");
        }
        if (vista.txt_MPBuscador != null) {
            vista.txt_MPBuscador.setText("");
        }
        vista.tbl_MPMetodoDePago.clearSelection();
    }

    public void MetodosPagoTotal() {
        vista.tbl_JPQLMetodoDePago.setEnabled(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Object[]> total = em.createQuery(
                    "SELECT m.id, f.numero, c.nombre, m.pago, m.descripcion, m.estado " +
                            "FROM MetodoDePago m LEFT JOIN m.facturas f LEFT JOIN f.cliente c " +
                            "ORDER BY m.id ASC, f.id ASC",
                    Object[].class
            ).getResultList();
            StringBuilder resultado = new StringBuilder();
            for (Object[] t : total) {
                resultado.append(" - ID: ")
                        .append(t[0])
                        .append(" - Factura: ")
                        .append(t[1])
                        .append(" - Cliente: ")
                        .append(t[2])
                        .append(" - Pago: ")
                        .append(t[3])
                        .append(" - Descripción: ")
                        .append(t[4])
                        .append(" - Estado: ")
                        .append(t[5])
                        .append("\n");
            }
            mostrarResultado(resultado.toString(), "ID:", "Factura:", "Cliente:", "Pago:", "Descripción:", "Estado:");
        }
    }

    public void MetodosPagoPorTipo(String tipo) {
        vista.tbl_JPQLMetodoDePago.setEnabled(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Object[]> metodos = em.createQuery(
                    "SELECT m.id, f.numero, c.nombre, m.pago, m.descripcion, m.estado " +
                            "FROM MetodoDePago m LEFT JOIN m.facturas f LEFT JOIN f.cliente c " +
                            "WHERE LOWER(m.pago) = LOWER(:tipo) ORDER BY m.id ASC, f.id ASC",
                    Object[].class
            ).setParameter("tipo", tipo).getResultList();
            StringBuilder resultado = new StringBuilder();
            for (Object[] m : metodos) {
                resultado.append(" - ID: ")
                        .append(m[0])
                        .append(" - Factura: ")
                        .append(m[1])
                        .append(" - Cliente: ")
                        .append(m[2])
                        .append(" - Pago: ")
                        .append(m[3])
                        .append(" - Descripción: ")
                        .append(m[4])
                        .append(" - Estado: ")
                        .append(m[5])
                        .append("\n");
            }
            mostrarResultado(resultado.toString(), "ID:", "Factura:", "Cliente: ", "Pago:", "Descripción:", "Estado:");
        }
    }
}
