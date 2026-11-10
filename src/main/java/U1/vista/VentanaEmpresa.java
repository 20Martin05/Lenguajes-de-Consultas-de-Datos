package U1.vista;

import U1.controlador.*;
import U1.vista.base.VentanaEmpresaUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.util.Comparator;
import java.util.function.Consumer;

public class VentanaEmpresa extends VentanaEmpresaUI {

    private final ProductosControlador Pcontrolador;
    private final CategoriaControlador Ccontrolador;
    private final FacturaControlador Fcontrolador;
    private final ClientesControlador CLcontrolador;
    private final MetodoDePagoControlador MPcontrolador;

    public VentanaEmpresa() {
        this.Pcontrolador = new ProductosControlador(this);
        this.Ccontrolador = new CategoriaControlador(this);
        this.Fcontrolador = new FacturaControlador(this);
        this.CLcontrolador = new ClientesControlador(this);
        this.MPcontrolador = new MetodoDePagoControlador(this);

        setTitle("Gestión de Productos - Corporación Favorita");
        setContentPane(PanelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        iniciar();
    }

    public void iniciar() {
        cargarCombos();
        configurarEventos();

        Pcontrolador.Plistar();
        configurarSorter(tbl_PProductos);

        Ccontrolador.Clistar();

        Fcontrolador.cargarCombosFactura();
        Fcontrolador.Flistar();
        configurarSorter(tbl_FFacturas);

        CLcontrolador.CLlistar();
        CLcontrolador.cargarComboFacturas();
        configurarSorter(tbl_CLClientes);

        MPcontrolador.cargarCombosMetodoDePago();
        MPcontrolador.MPlistar();
        configurarSorter(tbl_MPMetodoDePago);

    }

    /**
     * Ordena por la columna 0 como número. Solo se aplica a tablas cuya
     * columna 0 es el ID (entero). Si el valor no es numérico, se trata como 0
     * para evitar NumberFormatException durante el ordenamiento.
     */
    private void configurarSorter(JTable tabla) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) tabla.getModel());
        sorter.setComparator(0, Comparator.comparingInt(valor -> {
            try {
                return Integer.parseInt(valor.toString());
            } catch (NumberFormatException e) {
                return 0;
            }
        }));
        tabla.setRowSorter(sorter);
    }

    private void cargarCombos() {
        String[] cats = {"LÁCTEOS", "CARNES", "FRUTAS", "VERDURAS", "GRANOS", "PANADERÍA", "BEBIDAS", "ENLATADOS", "SNACKS"};
        String[] pagos = {"", "EFECTIVO", "TARJETA", "TRANSFERENCIA"};
        String[] estados = {"ACTIVO", "INACTIVO"};
        String[] ests = {"DISPONIBLE", "AGOTADO"};
        cmb_CNombre.removeAllItems();
        cmb_PCategoria.removeAllItems();
        for (String c : cats) {
            cmb_CNombre.addItem(c);
            cmb_PCategoria.addItem(c);
        }

        cmb_CEstado.removeAllItems();
        for (String est : ests) {
            cmb_CEstado.addItem(est);
        }

        cmb_MPPago.removeAllItems();
        for (String pago : pagos) {
            cmb_MPPago.addItem(pago);
        }
        cmb_MPPago.setSelectedIndex(0);

        cmb_MPEstado.removeAllItems();
        for (String estado : estados) {
            cmb_MPEstado.addItem(estado);
        }
        cmb_MPEstado.setSelectedIndex(0);
    }

    private void agregarBuscador(JTextField campo, Consumer<String> accion) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { accion.accept(campo.getText().trim()); }
            public void removeUpdate(DocumentEvent e) { accion.accept(campo.getText().trim()); }
            public void changedUpdate(DocumentEvent e) { accion.accept(campo.getText().trim()); }
        });
    }

    private void configurarEventos() {
        conectarConsultas();

        // PRODUCTOS

        // CATEGORIA

        // FACTURA

        // CLIENTE

        // MÉTODO DE PAGO

        agregarBuscador(txt_MPBuscador, t -> {
            if (t.isEmpty()) MPcontrolador.MPlistar();
            else MPcontrolador.MPbuscarYMostrar(t);
        });

        // BUSCADORES
        agregarBuscador(txt_PBuscador, t -> { if (t.isEmpty()) Pcontrolador.Plistar(); else Pcontrolador.PbuscarYMostrar(t); });
        agregarBuscador(txt_FBuscador, t -> { if (t.isEmpty()) Fcontrolador.Flistar(); else Fcontrolador.FbuscarYMostrar(t); });
        agregarBuscador(txt_CLBuscador, t -> { if (t.isEmpty()) CLcontrolador.CLlistar(); else CLcontrolador.CLbuscarYMostrar(t); });
    }

    private void conectarConsultas() {
        conectar(btn_CLTodos, tbl_JPQLClientes);
        conectar(btn_CLNombreConsulta, tbl_JPQLClientes);
        conectar(btn_CLApellidoConsulta, tbl_JPQLClientes);
        conectar(btn_CLFacturaConsulta, tbl_JPQLClientes);

        conectar(btn_CTodas, tbl_JPQLCategoria);
        conectar(btn_CActivas, tbl_JPQLCategoria);
        conectar(btn_CInactivas, tbl_JPQLCategoria);
        conectar(btn_CProductos, tbl_JPQLCategoria);

        conectar(btn_PTodos, tbl_JPQLProductos);
        conectar(btn_PCategoriaConsulta, tbl_JPQLProductos);
        conectar(btn_PAgotados, tbl_JPQLProductos);
        conectar(btn_PMayorPrecio, tbl_JPQLProductos);
        conectar(btn_PExistencias, tbl_JPQLProductos);

        conectar(btn_FTodas, tbl_JPQLFactura);
        conectar(btn_FClienteConsulta, tbl_JPQLFactura);
        conectar(btn_FMayorTotal, tbl_JPQLFactura);
        conectar(btn_FFechaConsulta, tbl_JPQLFactura);
        conectar(btn_FTotalVentas, tbl_JPQLFactura);

        conectar(btn_MPTodos, tbl_JPQLMetodoDePago);
        conectar(btn_MPEfectivo, tbl_JPQLMetodoDePago);
        conectar(btn_MPTarjeta, tbl_JPQLMetodoDePago);
        conectar(btn_MPTransferencia, tbl_JPQLMetodoDePago);
        conectar(btn_MPEstadoConsulta, tbl_JPQLMetodoDePago);
    }

    private void conectar(JButton boton, JTextPane resultado) {
        boton.addActionListener(e -> resultado.setText(boton.getText()));
    }

}