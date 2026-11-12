package U1.controlador;

import U1.vista.VentanaEmpresa;
import U1.util.ConexionJPA;
import U1.modelo.*;
import jakarta.persistence.EntityManager;

import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.List;

public class FacturaControlador {
    private final VentanaEmpresa vista;

    public FacturaControlador(VentanaEmpresa vista) {
        this.vista = vista;
    }

    private void mostrarResultado(String texto, String... etiquetas) {
        vista.mostrarResultado(vista.tbl_JPQLFactura, texto, etiquetas);
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
        vista.tbl_FFacturas.setAutoCreateColumnsFromModel(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            llenar(m, em.createQuery("FROM Factura f ORDER BY f.id ASC", Factura.class).getResultList());
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
        vista.tbl_FFacturas.setAutoCreateColumnsFromModel(true);
        llenar(modelo, FbuscarPorCliente(texto));
        vista.tbl_FFacturas.setModel(modelo);
    }

    public void FacturasTotal() {
        vista.tbl_FFacturas.setEnabled(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Factura> facturas = em.createQuery(
                    "SELECT f FROM Factura f JOIN f.cliente ORDER BY f.id ASC", Factura.class)
                    .getResultList();
            StringBuilder resultado = new StringBuilder();
            for (Factura f : facturas) {
                resultado.append(" - ID: ")
                        .append(f.getId())
                        .append(" - Cliente: ")
                        .append(f.getCliente().getNombre())
                        .append(" - Número: ")
                        .append(f.getNumero())
                        .append(" - Fecha: ")
                        .append(f.getFecha())
                        .append(" - Total: ")
                        .append(f.getTotal())
                        .append("\n");
                
            }
            mostrarResultado(resultado.toString(),  "ID:", "Cliente", "Número:", "Fecha:", "Total:");
        }
    }
    public void VentasTotal() {
        vista.tbl_JPQLFactura.setEnabled(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Object[]> ventas = em.createQuery(
                    "SELECT f.id, f.subtotal, f.impuesto, f.total FROM Factura f ORDER BY f.id ASC",
                    Object[].class
            ).getResultList();
            BigDecimal sumaVentas = em.createQuery(
                    "SELECT COALESCE(SUM(f.total), 0) FROM Factura f", BigDecimal.class
            ).getSingleResult();
            StringBuilder resultado = new StringBuilder();
            for (Object[] f : ventas) {
                resultado.append(" - ID: ")
                        .append(f[0])
                        .append(" - SUBTOTAL: ")
                        .append(f[1])
                        .append(" - IMPUESTO: ")
                        .append(f[2])
                        .append(" - TOTAL: ")
                        .append(f[3])
                        .append("\n");
            }
            resultado.append("\nSuma total de ventas: ")
                    .append(sumaVentas);
            mostrarResultado(resultado.toString(),  "ID", "SUBTOTAL:", "IMPUESTO:", "TOTAL:",
                    "Suma total de ventas:");
        }
    }

    public void MaximoYMinimoTotal() {
        vista.tbl_JPQLFactura.setEnabled(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            Object[] total = em.createQuery(
                    "SELECT MAX(f.total), MIN(f.total) FROM Factura f", Object[].class
            ).getSingleResult();
            StringBuilder resultado = new StringBuilder();
            resultado.append("Máximo de ventas: ")
                    .append(total[0])
                    .append("\nMínimo de ventas: ")
                    .append(total[1]);
            mostrarResultado(resultado.toString(), "Máximo de ventas:", "Mínimo de ventas:");
        }
    }
    public void FacturasPorFecha() {
        vista.tbl_JPQLFactura.setEnabled(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Object[]> Fecha = em.createQuery(
                    "SELECT f.id, f.fecha FROM Factura f ORDER BY f.id ASC", Object[].class
            ).getResultList();
            StringBuilder resultado = new StringBuilder();
            int contador = 1;
            for (Object[] f : Fecha) {
                resultado.append("- ID:")
                        .append(f[0])
                        .append(" - Fecha: ")
                        .append(f[1])
                        .append("\n");
            }
            mostrarResultado(resultado.toString(), "ID", "Fecha");
        }
    }

    public void FacturasPorCliente() {
        vista.tbl_JPQLFactura.setEnabled(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Object[]> Clientes = em.createQuery(
                    "SELECT DISTINCT c.id, c.nombre, c.apellido FROM Cliente c ORDER BY c.id ASC", Object[].class
            ).getResultList();
            StringBuilder resultado = new StringBuilder();
            for (Object[] c : Clientes) {
                resultado.append(" - ID: ")
                        .append(c[0])
                        .append(" - Nombre: ")
                        .append(c[1])
                        .append(" - Apellido: ")
                        .append(c[2])
                        .append("\n");
            }
            mostrarResultado(resultado.toString(),  "ID:", "Nombre:",  "Apellido:");
        }
    }

}