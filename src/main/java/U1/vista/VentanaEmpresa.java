package U1.vista;

import U1.controlador.*;
import U1.vista.base.VentanaEmpresaUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
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

    public void mostrarResultado(JTextPane panel, String texto, String... etiquetas) {
        panel.setText(texto);
        StyledDocument documento = panel.getStyledDocument();
        SimpleAttributeSet negrita = new SimpleAttributeSet();
        StyleConstants.setBold(negrita, true);

        for (String etiqueta : etiquetas) {
            int inicio = texto.indexOf(etiqueta);
            while (inicio >= 0) {
                documento.setCharacterAttributes(inicio, etiqueta.length(), negrita, false);
                inicio = texto.indexOf(etiqueta, inicio + etiqueta.length());
            }
        }
    }

    private void configurarEventos() {

        // PRODUCTOS
        // CATEGORIA
        // FACTURA
        btn_FTodas.addActionListener(e -> {
            Fcontrolador.FacturasTotal();
        });
        btn_FTotalVentas.addActionListener(e -> {
            Fcontrolador.VentasTotal();
        });
        btn_FMayorTotal.addActionListener(e -> {
            Fcontrolador.MaximoYMinimo();
        });
        btn_FFechaConsulta.addActionListener(e -> {
            Fcontrolador.FacturasPorFecha();
        });
        btn_FClienteConsulta.addActionListener(e -> {
            Fcontrolador.FacturasPorCliente();
        });
        // CLIENTE
        btn_CLTodos.addActionListener(e -> {
            CLcontrolador.ClientesTotal();
        });
        btn_CLFacturaConsulta.addActionListener(e -> {
            CLcontrolador.ClientesConFactura();
        });
        btn_CLNombreConsulta.addActionListener(e -> {
            CLcontrolador.ClientesPorNombre();
        });
        btn_CLApellidoConsulta.addActionListener(e -> {
            CLcontrolador.ClientesPorApellido();
        });
        // MÉTODO DE PAGO

        // BUSCADORES
        agregarBuscador(txt_PBuscador, t -> {
            if (t.isEmpty()) Pcontrolador.Plistar();
            else Pcontrolador.PbuscarYMostrar(t); });
        agregarBuscador(txt_FBuscador, t -> {
            if (t.isEmpty()) Fcontrolador.Flistar();
            else Fcontrolador.FbuscarYMostrar(t); });
        agregarBuscador(txt_CLBuscador, t -> {
            if (t.isEmpty()) CLcontrolador.CLlistar();
            else CLcontrolador.CLbuscarYMostrar(t); });
        agregarBuscador(txt_MPBuscador, t -> {
            if (t.isEmpty()) MPcontrolador.MPlistar();
            else MPcontrolador.MPbuscarYMostrar(t); });
    }
}